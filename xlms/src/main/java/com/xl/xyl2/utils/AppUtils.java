package com.xl.xyl2.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;

public class AppUtils {

    /**
     * 获取包信息
     */
    public static PackageInfo getPackageInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回当前app版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return versionName;
    }

    /**
     * 获取当前APP版本号
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return versionCode;
    }

    //判断当前系统是否时中文环境
    public static boolean isCN(Context context) {
        if (context.getResources().getConfiguration().locale.getCountry().equals("CN"))
            return true;
        else
            return false;
    }

    /**
     * 获得终端编号
     */
    public static String getTernimalNum() {
        String mCPUSerial = getCPUSerial();
        if (!TextUtils.isEmpty(mCPUSerial) && noAllNumbers0(mCPUSerial)) {
            return mCPUSerial;
        }

        String mSN = android.os.Build.SERIAL;
        if (!TextUtils.isEmpty(mSN)) {
            return mSN;
        }
        return getRandom();
    }

    /**
     * 获取CPU序列号
     */
    private static String getCPUSerial() {
        String str = "", strCPU = "", cpuAddress = "";
        try {
            // 读取CPU信息
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            // 查找CPU序列号
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    // 查找到序列号所在行
                    if (str.indexOf("Serial") > -1) {
                        // 提取序列号
                        strCPU = str.substring(str.indexOf(":") + 1, str.length());
                        // 去空格
                        cpuAddress = strCPU.trim();
                        break;
                    }
                } else {
                    // 文件结尾
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return cpuAddress;
    }

    /**
     * 判断字符串不是全为数字0
     */
    private static boolean noAllNumbers0(String str) {
        if (TextUtils.isEmpty(str) || str.length() < 2) {
            return false;
        }
        char first = '0'; //str.charAt(0);
        for (int i = 1; i < str.length(); i++) {
            if (str.charAt(i) != first) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得10位的随机整数
     */
    private static String getRandom() {
        Random random = new Random();
        String result = "";
        for (int i = 0; i < 10; i++) {
            result += random.nextInt(10);
        }

        return result;
    }

    //执行adb shell指令
    public static void shellCmds(List<String> cmds) throws Exception {
        Process process = Runtime.getRuntime().exec("su");
        DataOutputStream os = new DataOutputStream(process.getOutputStream());

        for (String tmpCmd : cmds) {
            os.writeBytes(tmpCmd+"\n");
        }

        os.writeBytes("exit\n");
        os.flush();
        os.close();
        process.waitFor();
    }

    /**
     * 执行具体的静默安装逻辑，需要手机ROOT。
     *
     * @param apkPath 要安装的apk文件的路径
     * @return 安装成功返回true，安装失败返回false。
     */
    public static boolean installSilent(String apkPath) {
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader inputStream = null;
        try {
            //LogUtils.d("pm install -r " + apkPath);
            // 申请su权限
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            String command = "pm install -r " + apkPath + "\n";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = inputStream.readLine()) != null) {
                msg += line;
            }
            //LogUtils.d("pm install msg-->" + msg);
            if (msg.contains("Success")) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //LogUtils.d("pm install finally()");
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 判断手机是否拥有Root权限。
     *
     * @return 有root权限返回true，否则返回false。
     */
    public static boolean isRoot() {
        boolean bool = false;
        try {
            bool = new File("/system/bin/su").exists() || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool;
    }
}
