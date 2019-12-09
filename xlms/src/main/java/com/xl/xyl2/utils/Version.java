package com.xl.xyl2.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import com.gdswlw.library.http.Callback;
import com.gdswlw.library.http.CallbackDownload;
import com.gdswlw.library.http.GDSHttpClient;
import com.gdswlw.library.toolkit.AppUtil;
import com.gdswlw.library.toolkit.UIKit;
import com.xl.xyl2.XLContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Created by Afun on 2019/10/8.
 */

public class Version {
    static boolean isDownload = false;
    /**
     * 检查软件版本
     */
    public static void checkVersion(@NonNull final Context context, final boolean isSync) {
        if(isDownload){
            return;
        }
        final JSONObject json = new JSONObject();
        try {
            json.put("lan", AppUtils.isCN(context) ? "cn" : "en");
            json.put("dcode", AppUtils.getTernimalNum());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GDSHttpClient.postWithJsonBody(context, XLContext.API_URL + "/main/device/api/versionCheck", json, new Callback() {
            @Override
            public void onSuccess(String url, JSONObject jsonObject, int type) {
                JSONObject data = jsonObject.optJSONObject("data");
                if (data != null) {
                    //判断是否比当前版本号高 如果高就下载文件
                    if (AppUtil.getVersionCode(context) < data.optInt("versionCode", 0)) {
                        String downloadUrl = data.optString("url");
                        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/")+1);
                        UIKit.dLog("need Update:" + downloadUrl);
                        File downloadFile =  new File(FileUtils.getRootFile().getPath()+"/"+fileName);
                        if(downloadFile.exists() && AppUtil.getVersionCode(context) <
                                getVersionCodeFromApk(context,downloadFile.getPath())){
                            silentInstall(downloadFile.getPath());
                        }else{
                            if(downloadFile.exists()){
                                downloadFile.delete();
                            }
                            isDownload = true;
                            GDSHttpClient.download(downloadUrl,downloadFile, new CallbackDownload() {
                                @Override
                                public void onSuccess(File file) {
                                    silentInstall(file.getPath());
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    UIKit.dLog("download update fail:" + throwable.getMessage());
                                    isDownload = false;
                                }

                                @Override
                                public void progress(long bytesWritten, long totalbytes) {
                                    UIKit.dLog("download update:" + bytesWritten);
                                    isDownload = false;
                                }
                            },isSync);
                        }
                    }

                }
            }

            @Override
            public void onFailure(String url, Throwable throwable) {
                UIKit.eLog(throwable.getMessage());
            }

        }, null,isSync);
    }

    /**
     * 静默安装
     * @param apkPath
     * @return
     */
    public static boolean silentInstall(String apkPath) {
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        BufferedReader successStream = null;
        Process process = null;
        try {
            //执行 pm install 命令
            String command;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH){
                 command = "pm install -r -d " + apkPath + "\n";
            }else{
                command = "pm install -r -d -i packageName --user 0 " + apkPath + "\n";
            }
            process = Runtime.getRuntime().exec(command);
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.write(command.getBytes(Charset.forName("UTF-8")));
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorMsg = new StringBuilder();
            String line;
            while ((line = errorStream.readLine()) != null) {
                errorMsg.append(line);
            }
            StringBuilder successMsg = new StringBuilder();
            successStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
            // 读取命令执行结果
            while ((line = successStream.readLine()) != null) {
                successMsg.append(line);
            }
            // 如果执行结果中包含 Failure 字样就认为是操作失败，否则就认为安装成功
            if (!(errorMsg.toString().contains("Failure") || successMsg.toString().contains("Failure"))) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (process != null) {
                    process.destroy();
                }
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
                if (successStream != null) {
                    successStream.close();
                }
            } catch (Exception e) {
                // ignored
            }
        }
        return result;
    }


    /**
     * 从apk文件获取版本码
     * @param apkPath
     * @return
     */
    private static int getVersionCodeFromApk(Context context,String apkPath){
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(apkPath,PackageManager.GET_ACTIVITIES);
        return pkgInfo == null ? -1 : pkgInfo.versionCode;
    }
}
