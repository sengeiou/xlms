package com.xl.xyl2.callback;

import android.content.Context;
import android.content.Intent;

import com.google.gson.internal.LinkedTreeMap;
import com.xl.xyl2.XLContext;
import com.xl.xyl2.download.DownLoadManager;
import com.xl.xyl2.mvp.view.activity.XLMain;
import com.xl.xyl2.mvp.view.activity.ActivityShow;
import com.xl.xyl2.net.udp.IcmdHandler;
import com.xl.xyl2.net.udp.UDPServer;

import java.util.HashMap;


/**
 * 命令处理器
 * Created by Afun on 2019/9/10.
 */

public class CmdHandler implements IcmdHandler {
    private Context context;

    public CmdHandler(Context context) {
        this.context = context;
    }

    @Override
    public Object Execute(String c, Object v, UDPServer us) {
        Object result=null;
        switch (c) {
            case "printscreen"://截屏
                float terminalId  =  Float.parseFloat(String.valueOf(v));
                Intent intent = new Intent(ActivityShow.ACTIVITY_SHOW_RECEIVER);
                intent.putExtra(ActivityShow.ACTION_VAL, ActivityShow.ACTION_VALUE_PRINT_SCREEN);
                intent.putExtra("id", (int)terminalId);
                context.sendBroadcast(intent);
                XLContext.showLogMessage("收到指令【截屏】，终端id="+terminalId);
                result =  1;
                break;
            case "restart"://重启
                context.sendBroadcast(new Intent("android.xl.reboot"));
                XLContext.showLogMessage("收到指令【重启】");
                result =  1;
                break;
            case "playState":
                XLContext.showLogMessage("收到指令【上传播放状态】");
                int tid  =  (int)Float.parseFloat(String.valueOf(v));
                HashMap<String,Object> data = new HashMap<>();
                //判断是否正在下载中
                DownLoadManager downLoadManager = DownLoadManager.getCur(tid);
                if(downLoadManager != null && !downLoadManager.isFinish()){
                    data.put("state",0);//下载中
                    data.put("pid",downLoadManager.getPid());
                    data.put("pro",downLoadManager.getProgress());
                }else if(ActivityShow.instatnce!= null){
                    data.put("state",ActivityShow.instatnce.isPlayTermainalId(tid)?1:2);//播放或者暂停
                    data.put("pid",ActivityShow.instatnce.getPidByTermainalId(tid));//节目标识
                    data.put("pro",100);
                }else{
                    //找不到对应的终端或其他异常
                    data.put("state",-1);
                    data.put("pid","");
                    data.put("pro",0);
                }
                result =  data;
                break;
            case "newSetting"://更新设置
                XLContext.showLogMessage("收到指令【更新设置】");
                Intent updateSettingIntent = new Intent(ActivityShow.ACTIVITY_SHOW_RECEIVER);
                updateSettingIntent.putExtra(ActivityShow.ACTION_VAL, ActivityShow.ACTION_UPDATE_SETTING);
                context.sendBroadcast(updateSettingIntent);
                result =  1;
                break;
            case "newPlayList"://通过终端ID更新节目
                XLContext.showLogMessage("收到指令【更新播放列表】");
                float userTerminalId  =  Float.parseFloat(String.valueOf(v));
                Intent updateTermainalIntent = new Intent(ActivityShow.ACTIVITY_SHOW_RECEIVER);
                updateTermainalIntent.putExtra(ActivityShow.ACTION_VAL, ActivityShow.ACTION_UPDATE_PROGRAM_TERMINAL_ID);
                updateTermainalIntent.putExtra("id", (int)userTerminalId);
                context.sendBroadcast(updateTermainalIntent);

                //如果第一次初始化没有节目，则通知加载
                Intent main = new Intent(XLMain.ACTION_MAIN_RECEIVER);
                main.putExtra(XLMain.ACTION_KEY, XLMain.ACTION_VALUE_GET_DATA);
                context.sendBroadcast(main);
                result = 1;
                break;
            case "allBind"://隐藏二维码
                XLContext.showLogMessage("收到指令【已绑定终端】");
                Intent hidden = new Intent(ActivityShow.ACTIVITY_SHOW_RECEIVER);
                hidden.putExtra(ActivityShow.ACTION_VAL, ActivityShow.ACTION_VALUE_HIDDEN_QRCDODE);
                context.sendBroadcast(hidden);
                result =  1;
                break;
            case "poState"://获取播放状态
                int id  = (int) Float.parseFloat(String.valueOf(v));
                XLContext.showLogMessage("收到指令【获取播放状态】");
                boolean isPlay = ActivityShow.instatnce!= null && ActivityShow.instatnce.isPlayTermainalId(id);
                result = ( isPlay ? 1:0);
                break;
            case "setPO"://播放暂停
                LinkedTreeMap linkedTreeMap = (LinkedTreeMap)v;
                Integer termainalid = (int) Float.parseFloat(String.valueOf(linkedTreeMap.get("tid")));
                Integer action = (int) Float.parseFloat(String.valueOf(linkedTreeMap.get("action")));//0暂停 1播放
                XLContext.showLogMessage("收到指令【"+(action==0?"暂停":"播放")+"】，终端id="+termainalid);
                if( ActivityShow.instatnce!= null && ActivityShow.instatnce.hasTermainalId(termainalid)){
                    Intent play = new Intent(ActivityShow.ACTIVITY_SHOW_RECEIVER);
                    play.putExtra("tid",termainalid);
                    play.putExtra("action",action);
                    play.putExtra(ActivityShow.ACTION_VAL, ActivityShow.ACTION_PLAY);
                    context.sendBroadcast(play);
                    //播放或暂停
                    result =  1;//1成功 0失败
                }else{
                    result =  0;
                }
                break;
            case "test":
                result =  "test";
                break;
        }
        return result;
    }


}