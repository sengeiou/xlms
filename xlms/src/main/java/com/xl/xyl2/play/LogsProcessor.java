package com.xl.xyl2.play;

import com.gdswlw.library.http.Callback;
import com.gdswlw.library.http.GDSHttpClient;
import com.gdswlw.library.toolkit.DateUtil;
import com.gdswlw.library.toolkit.FileUtil;
import com.gdswlw.library.toolkit.UIKit;
import com.xl.xyl2.XLContext;
import com.xl.xyl2.utils.AppUtils;
import com.xl.xyl2.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Afun on 2019/11/12.
 */

public class LogsProcessor {
    private static HashMap<String, PlayLog> playLogHashMap = new HashMap<>();//播放日志缓存
    private static ArrayList<String> checks = new ArrayList<>();//已检查队列
    private static boolean isUpload = false;//是否上传

    public static void release() {
        playLogHashMap.clear();
        checks.clear();
        isUpload = false;
    }


    /**
     * 处理日志线程
     */
    private static class HandleLogThread extends Thread {
        private int tid, maxPlayTime;

        /**
         *
         * @param playProgram 节目
         * @param tid 终端id
         * @param maxPlayTime 最大播放时间
         */
        public HandleLogThread(PlayProgram playProgram,int tid,int maxPlayTime) {
            this.playProgram = playProgram;
            this.tid  = tid;
            this.maxPlayTime = maxPlayTime;
        }

        private PlayProgram playProgram;

        @Override
        public void run() {
            super.run();
            if (playProgram != null) {
                process(playProgram,tid,maxPlayTime);
            }
        }
    }


    public static void handleLogs(PlayProgram program,int tid,int maxPlayTime) {
        new HandleLogThread(program,tid,maxPlayTime).start();
    }


    /**
     * 处理日志
     *
     * @param program
     */
    private static void process(PlayProgram program,int tid,int maxPlayTime) {
        Date currentDate = XLContext.getLocalServerDate();
        String currentYMD = DateUtil.getDateStrYmd(currentDate);
        //获取本地文件，如果存在并且为之前的上传
        //如果没有检测过并且不是当前日期
        if (!checks.contains(currentYMD) && !isUpload) {
            //获取目录下同个节目之前的日志
            File logDir = new File(FileUtils.getLogDir().getPath());
            File[] files = logDir.listFiles();
            final ArrayList<File> fileArrayList = new ArrayList<>();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    final File temp = files[i];
                    if (temp.isFile()) {
                        long modifiedTime = temp.lastModified();//获取文件最后修改时间
                        String modifiedYMD = DateUtil.getDateStrYmd(new Date(modifiedTime));
                        if (currentYMD.equals(modifiedYMD)) {//同一天不不上传
                            continue;
                        } else {
                            fileArrayList.add(temp);
                        }
                    }
                }
            }

            checks.add(currentYMD);//添加已检测
            if (fileArrayList.size() > 0) {
                uploadPlayLog(fileArrayList);
            }
        }

        //检测当前的
        PlayLog playLog;
        if (playLogHashMap.containsKey(program.getIdentification())) {
            playLog = playLogHashMap.get(program.getIdentification());
        } else {
            //判断当前是否存在日志
            String currentPath = FileUtils.getLogDir().getPath() + "/" + DateUtil.getStringWithDate(currentDate) + "_" + program.getIdentification();
            Object data = FileUtil.getSerilizeData(currentPath);//获取当前日志
            if (data != null) {
                playLog = (PlayLog) data;
            } else {
                playLog = new PlayLog();
                playLog.setIndef(program.getIdentification());
                playLog.setLogDate(XLContext.getLocalServerDate());
                playLog.setTerminalId(tid);
            }
            playLogHashMap.put(program.getIdentification(), playLog);
        }
        playLog.update(DateUtil.getDateStrHm(), maxPlayTime, XLContext.getGson().toJson(program));
        //播放日志
        if (playLog.getIncre() == 5) {//每更新5次写入文件
            playLog.setIncre(0);
            //写入文件
            FileUtil.serilizenData(FileUtils.getLogDir(), DateUtil.getStringWithDate(currentDate) + "_" + program.getIdentification(), playLog);
        }
        UIKit.dLog("Identification=" + program.getIdentification() + ",logtimes=" + playLog.getLogTime() + ",times=" + playLog.getTimes());
    }


    /**
     * 上传播放日志
     *
     * @param files
     */
    private static  void uploadPlayLog(final ArrayList<File> files) {
        final JSONObject json = new JSONObject();
        try {
            json.put("lan", AppUtils.isCN(XLContext.mContext) ? "cn" : "en");
            json.put("dcode", AppUtils.getTernimalNum());
            JSONObject data = new JSONObject();
            data.put("mode", 0);////模式 0覆盖 1追加

            JSONArray list = new JSONArray();
            for (int i = 0; i < files.size(); i++) {
                Object object = FileUtil.getSerilizeData(files.get(i).getPath());
                if(object == null){
                    continue;
                }
                PlayLog playLog = (PlayLog)object;
                JSONObject playlog = new JSONObject();
                playlog.put("logDate", DateUtil.getStringWithDate(playLog.getLogDate()));
                playlog.put("logTime", playLog.getLogTime());
                playlog.put("duration", playLog.getDuration());
                playlog.put("times", playLog.getTimes());
                playlog.put("terminalId", playLog.getTerminalId() == 0?-1:playLog.getTerminalId());
                playlog.put("program", playLog.getProgram());
                list.put(playlog);
            }
            data.put("list", list);
            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        isUpload = true;
        GDSHttpClient.postWithJsonBody(XLContext.mContext, XLContext.API_URL + "/main/device/api/playLog", json, new Callback() {
            @Override
            public void onSuccess(String url, JSONObject jsonObject, int type) {
                isUpload = false;
                if (jsonObject.optInt("success") == 1) {
                    for (int i = 0; i < files.size(); i++) {
                        FileUtil.delFiles(files.get(i).getPath());//删除日志文件
                    }
                }
            }

            @Override
            public void onFailure(String url, Throwable throwable) {
                isUpload = false;
                UIKit.eLog(throwable.getMessage());
            }

        }, null,true);
    }
}
