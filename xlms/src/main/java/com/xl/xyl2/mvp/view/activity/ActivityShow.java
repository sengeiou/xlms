package com.xl.xyl2.mvp.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.gdswlw.library.activity.GDSBaseActivity;
import com.gdswlw.library.http.Callback;
import com.gdswlw.library.http.CallbackAdapter;
import com.gdswlw.library.http.GDSHttpClient;
import com.gdswlw.library.toolkit.FileUtil;
import com.gdswlw.library.toolkit.UIKit;
import com.xl.xyl2.XLContext;
import com.xl.xyl2.R;
import com.xl.xyl2.mvp.view.dialog.DialogPassword;
import com.xl.xyl2.mvp.view.ui.terminal.TerminalView;
import com.xl.xyl2.net.udp.UDPServer;
import com.xl.xyl2.play.Data;
import com.xl.xyl2.play.DeviceTerminal;
import com.xl.xyl2.play.LogsProcessor;
import com.xl.xyl2.play.PlayList;
import com.xl.xyl2.play.PlayProgram;
import com.xl.xyl2.play.Setting;
import com.xl.xyl2.utils.AppUtils;
import com.xl.xyl2.utils.ScreenShot;
import com.xl.xyl2.utils.ZXingUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.xl.xyl2.utils.Constant.PASSWORD_TIME;

/**
 * Created by Afun on 2019/9/12.
 */

public class ActivityShow extends GDSBaseActivity{
    HashMap<Integer,TerminalView> terminalViews;
    FrameLayout flParent;
    public static final String ACTIVITY_SHOW_RECEIVER = "activity_show_receiver";
    public static final String ACTION_VAL = "action_vlaue";
    public static final int ACTION_UPDATE_PROGRAM =  0x123;
    public static final int ACTION_UPDATE_PROGRAM_TERMINAL_ID =  0x128;
    public static final int ACTION_UPDATE_SETTING =  0x129;
    public static final int ACTION_UPDATE_LOCATION = 0x456;
    public static final int ACTION_VALUE_PRINT_SCREEN = 0X100;
    public static final int ACTION_VALUE_HIDDEN_QRCDODE = 0X300;
    public static final int ACTION_PLAY = 0X500;
    Receiver receiver;
    public static ActivityShow instatnce;
    /**
     * 接受UDP发过来等通知
     */
    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if(intent.getAction()!= null){
                if(intent.getIntExtra(ACTION_VAL,0) == ACTION_UPDATE_PROGRAM){
                    //更新播放列表
                    UIKit.toastShort("Update Data");
                    if(intent.getStringExtra("json") != null){
                            //保存到本地
                            Data data = XLContext.getGson().fromJson(intent.getStringExtra("json"), Data.class);
                            XLContext.saveSettingData(data);
                            List<DeviceTerminal> deviceTerminalList = data.getDeviceTerminalList();
                            //先检测旧的终端id是否可用，先把新的终端id存入list
                            ArrayList<Integer> newIds = new ArrayList<>();
                            for (int i = 0; i < deviceTerminalList.size(); i++) {
                                newIds.add(deviceTerminalList.get(i).getId());
                            }
                            //移除不存在的终端id
                            Iterator<Integer> oldIds = terminalViews.keySet().iterator();
                            while (oldIds.hasNext()){
                                Integer oldId = oldIds.next();
                                if(! newIds.contains(oldId)){//如果旧的id不存新的list，则移除View
                                    flParent.removeView(terminalViews.get(oldId));
                                    terminalViews.remove(oldId);
                                }
                            }
                            for (int i = 0; i < deviceTerminalList.size(); i++) {
                                DeviceTerminal terminal = deviceTerminalList.get(i);
                                if(terminal.getPlayList() == null){//终端可能不存在节目
                                    continue;
                                }
                                if(terminalViews.containsKey(terminal.getId())){//如果ID存在刷新视图
                                    //获取新数据的identification
                                    String identification =  terminal.getPlayList().getIdentification();
                                    TerminalView terminalView = terminalViews.get(terminal.getId());
                                    //identification判断播放列表是否改变了，不一致则更新数据
                                    if(! identification.equals(terminalView.getIdentification())){
                                        terminalView.loadTerminalData(terminal,true);
                                    }
                                }else{//添加新的视图
                                    TerminalView terminalView = new TerminalView(context,terminal.getMain());
                                    terminalView.loadTerminalData(terminal,false);
                                    terminalView.setTag(terminal);//绑定数据
                                    flParent.addView(terminalView);
                                    terminalViews.put(terminal.getId(),terminalView);
                                }
                            }
                    }
                }else  if(intent.getIntExtra(ACTION_VAL,0) == ACTION_UPDATE_LOCATION){
                    UIKit.dLog("update location");
                    Iterator<Integer> oldIds = terminalViews.keySet().iterator();
                    while (oldIds.hasNext()){
                        Integer oldId = oldIds.next();
                        if(terminalViews.get(oldId) != null){
                            terminalViews.get(oldId).updateLocation();
                        }
                    }
                }else if(intent.getIntExtra(ACTION_VAL,0) == ACTION_UPDATE_PROGRAM_TERMINAL_ID){//根据终端id来更新终端视图
                    if(intent.getIntExtra("id",0) > 0){
                        int id = intent.getIntExtra("id",0);
                        syncPlayListByTerminalId(id);//根据终端列表更新节目
                    }
                }else if(intent.getIntExtra(ACTION_VAL,0) == ACTION_VALUE_PRINT_SCREEN){
                    //截图
                     shotImage(intent.getIntExtra("id",0));
                }else if(intent.getIntExtra(ACTION_VAL,0) == ACTION_UPDATE_SETTING){
                    synSetting();
                }else if(intent.getIntExtra(ACTION_VAL,0) == ACTION_VALUE_HIDDEN_QRCDODE){
                    id(R.id.iv_qr_code).gone();
                }else if(intent.getIntExtra(ACTION_VAL,0) == ACTION_PLAY){
                      play(intent.getIntExtra("tid",0),
                              intent.getIntExtra("action",1) == 1);
                }
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        play(false);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        play(true);
    }


    /**
     * 播放或者暂停
     * @param isPlay
     */
    public void play(boolean isPlay){
        if(terminalViews != null && terminalViews.keySet().size()>0 ){
            Iterator<Integer> iterator = terminalViews.keySet().iterator();
            while (iterator.hasNext()){
                TerminalView terminalView = terminalViews.get(iterator.next());
                if(terminalView != null){
                    terminalView.play(isPlay);
                }
            }
        }
    }



    @Override
    public void initUI() {
        instatnce = this;
        flParent = viewId(R.id.fl_parent);
        List<DeviceTerminal> deviceTerminalList = XLContext.getDeviceTerminals();
        if(deviceTerminalList != null){
            terminalViews = new HashMap<>();
            for (int i = 0; i < deviceTerminalList.size(); i++) {
                DeviceTerminal terminal = deviceTerminalList.get(i);
                final TerminalView terminalView = new TerminalView(context,terminal.getMain());
                if(terminal.getMain()){
                    XLContext.config.save("main",terminal.getId());
                }
                terminalView.loadTerminalData(terminal,false);
                terminalView.setTag(terminal);//绑定数据
                flParent.addView(terminalView);
                terminalViews.put(terminal.getId(),terminalView);
                terminalView.getViewTreeObserver().addOnPreDrawListener(
                        new ViewTreeObserver.OnPreDrawListener() {

                            @Override
                            public boolean onPreDraw() {
                                //调试 测试视图宽高
                                terminalView.getViewTreeObserver().removeOnPreDrawListener(this);
                                UIKit.dLog("x:"+terminalView.getX()+",y:"+ terminalView.getY()+",width:"+ terminalView.getWidth()+",height:"+ terminalView.getHeight());
                                return true;
                            }
                        });
            }
        }
        //接受播放列表更新的广播
        receiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter(ACTIVITY_SHOW_RECEIVER);
        registerReceiver(receiver, intentFilter);
        hideBottomUIMenu(true);
        XLContext.useSetting();//使用设置
        id(R.id.clickView).click(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputPassword(true);//菜单
            }
        });
        bindQrCode();

    }



    protected void hideBottomUIMenu(boolean isHidden){
        context.sendBroadcast(isHidden ? new Intent("com.xl.hide_nav_bar") : new Intent("com.xl.show_nav_bar"));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        instatnce = null;
        if(receiver != null){
            unregisterReceiver(receiver);
        }
        LogsProcessor.release();
    }

    /**
     * 根据终端ID移除终端View
     * @param terminalId
     */
    private void removeTerminalById(int terminalId){
        if(terminalViews.containsKey(terminalId)){
            flParent.removeView(terminalViews.get(terminalId));
            terminalViews.remove(terminalId);
        }
    }


    /**
     * 是否存在终端
     * @param terminalId
     * @return
     */
    public boolean hasTermainalId(int terminalId){
        return terminalViews.containsKey(terminalId) ;
    }

    /**
     * 播放或暂停终端
     * @param terminalId
     * @param isPlay
     */
    public boolean play(final int terminalId, final boolean isPlay){
        if(terminalViews.containsKey(terminalId)){
            terminalViews.get(terminalId).play(isPlay);
           return  true;
        }
        return false ;
    }

    /**
     * 终端是否在播放
     * @param terminalId
     * @return
     */
    public boolean isPlayTermainalId(int terminalId){
        return terminalViews.containsKey(terminalId) && terminalViews.get(terminalId).isPlay() ;
    }





    public String  getPidByTermainalId(int terminalId){
        if(terminalViews.containsKey(terminalId)){
            return  terminalViews.get(terminalId).getIdentification();
        }
        return "unknow";
    }
    @Override
    public void regUIEvent() {

    }

    @Override
    public int getLayout() {
        return R.layout.activity_show;
    }




    /**
     * 截图
     */
    private void shotImage(final int termainalId) {
        if(! terminalViews.containsKey(termainalId)){
            return;
        }
        showProgressDialog("Screen shot",false);
        TerminalView terminalView = terminalViews.get(termainalId);
        ScreenShot.screenshot(context, new ScreenShot.ShotCallback() {
            @Override
            public void onSucess(Bitmap bitmap) {
                dismissProgressDialog();
                if (XLContext.getResSeverHost().equals("")) {
                    UIKit.toastShort("resSeverHost为空");
                    return;
                }
                uploadImageData(bitmap, termainalId);
            }

            @Override
            public void onFailed(String message) {
                dismissProgressDialog();
                UIKit.toastShort(message);
            }
        },terminalView.getWidth(),terminalView.getHeight(),terminalView.getX(),terminalView.getY());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
//                UIKit.toastShort("KEYCODE_MENU");
                showInputPassword(true);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 显示输入密码
     * @param isSetting 是否打开设置，否返回桌面
     */
    private void showInputPassword(final boolean isSetting){
        if((System.currentTimeMillis() - XLContext.config.getLong("passwordTime")) <= PASSWORD_TIME){
            startActivityForResult(new Intent(context,ActivitySetting.class),12345);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            return;
        }
        Setting setting = XLContext.getSettingData();
        if(setting != null){
            if(setting.getIsOpPsd() && setting.getOpPsd()!=null && !"".equals(setting.getOpPsd().trim())){
                DialogPassword dialogPassword = new DialogPassword(this, new DialogPassword.Callback() {
                    @Override
                    public void onSucess() {
                        if(isSetting){
                            startActivityForResult(new Intent(context,ActivitySetting.class),12345);
                            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        }else{
                            finish();
                        }

                    }

                    @Override
                    public void onError(String msg) {
                        UIKit.toastShort(msg);
                    }
                });
                dialogPassword.show();
            }else{
                if(isSetting){
                    startActivityForResult(new Intent(context,ActivitySetting.class),12345);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }else{
                    finish();
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //退出系统
        if(requestCode == 12345 && resultCode == RESULT_OK){
            XLContext.config.remove("passwordTime");
            finish();
            UDPServer.stop();
            System.exit(0);
        }
    }

    /**
     * 上传图片数据
     *
     * @param image
     * @param terminalId
     */
    public void uploadImageData(Bitmap image, final Integer terminalId) {
        String content = FileUtil.getImageBase64Content(image, 100);
        final JSONObject json = new JSONObject();
        try {
            json.put("lan", AppUtils.isCN(getBaseContext()) ? "cn" : "en");
            json.put("dcode", AppUtils.getTernimalNum());
            JSONObject data = new JSONObject();
            data.put("terminalId", terminalId);
            data.put("content", content);
            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GDSHttpClient.postWithJsonBody(getBaseContext(), XLContext.getResSeverHost() + "/uploader/printscreen", json, new Callback() {
            @Override
            public void onSuccess(String url, JSONObject data, int type) {
                if (data.optInt("success") == 1) {
                    UIKit.toastShort("terminalId："+terminalId+" shot image upload success");
                } else {
                    UIKit.toastShort(data.optString("errMsg"));
                }

                dismissProgressDialog();
            }

            @Override
            public void onFailure(String url, Throwable throwable) {
                dismissProgressDialog();
                UIKit.eLog(throwable.getMessage());
            }

        }, new CallbackAdapter() {
            @Override
            public void requestStart(String url) {
                showProgressDialog("",true);
            }
        });


    }

    /**
     * 同步设置
     */
    private void synSetting() {
        final JSONObject json = new JSONObject();
        try {
            json.put("lan", AppUtils.isCN(context) ? "cn" : "en");
            json.put("dcode", AppUtils.getTernimalNum());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GDSHttpClient.postWithJsonBody(context, XLContext.API_URL + "/main/device/api/synSetting", json, new Callback() {
            @Override
            public void onSuccess(String url, JSONObject settingData, int type) {
                if (settingData != null) {
                    Setting setting = XLContext.getGson().fromJson(settingData.optJSONObject("data").toString(),Setting.class);
                    XLContext.saveSetting(setting);
                }

            }

            @Override
            public void onFailure(String url, Throwable throwable) {
                UIKit.eLog(throwable.getMessage());
            }

        }, null);
    }

    /**
     * 生成绑定二维码
     */
    private void bindQrCode(){
        final JSONObject json = new JSONObject();
        try {
            json.put("lan", AppUtils.isCN(context) ? "cn" : "en");
            json.put("dcode", AppUtils.getTernimalNum());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GDSHttpClient.postWithJsonBody(context, XLContext.API_URL + "/main/device/api/bindQrCode", json, new Callback() {
            @Override
            public void onSuccess(String url,final JSONObject jsonObject, int type) {
                JSONObject data = jsonObject.optJSONObject("data");
                if(data != null && ! data.optBoolean("allBind",false)) {
                    id(R.id.iv_qr_code).visible();
                    //生成二维码
                    ((ImageView)viewId(R.id.iv_qr_code)).setImageBitmap(ZXingUtils.createQRImage(data.optString("bindUrl"), 160,  160));
                }
            }

            @Override
            public void onFailure(String url, Throwable throwable) {
                UIKit.eLog(throwable.getMessage());
            }
        }, null);
    }

    /**
     * 根据终端id同步播放列表
     *
     * @param userTerminalId
     */
    private void syncPlayListByTerminalId(final Integer userTerminalId) {
        if(userTerminalId == null){
            return;
        }
        final JSONObject json = new JSONObject();
        try {
            json.put("lan", AppUtils.isCN(context) ? "cn" : "en");
            json.put("dcode", AppUtils.getTernimalNum());
            JSONObject data = new JSONObject();
            data.put("userTerminalId", userTerminalId);
            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GDSHttpClient.postWithJsonBody(context, XLContext.API_URL + "main/device/api/synPlayList", json, new Callback() {
            @Override
            public void onSuccess(String url,final JSONObject data, int type) {
                if(data != null){
                    //获取播放列表
                    final PlayList playList = XLContext.getGson().fromJson(data.optJSONObject("data").toString(),PlayList.class);
                    List<PlayProgram> programs = playList.getPrograms();
                    //设置tid
                    for (PlayProgram p :programs) {
                        p.setTid(userTerminalId);
                    }
                    if(terminalViews.containsKey(userTerminalId)) {
                        TerminalView terminalView = terminalViews.get(userTerminalId);
                        terminalView.update(playList);
                    }
                }

            }

            @Override
            public void onFailure(String url, Throwable throwable) {
                UIKit.eLog(throwable.getMessage());
            }
        }, null);
    }

}
