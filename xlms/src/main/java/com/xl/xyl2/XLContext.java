package com.xl.xyl2;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.baidu.mapapi.SDKInitializer;
import com.gdswlw.library.http.GDSHttpClient;
import com.gdswlw.library.toolkit.DateUtil;
import com.gdswlw.library.toolkit.FileUtil;
import com.gdswlw.library.toolkit.NetUtil;
import com.gdswlw.library.toolkit.PreferenceHelper;
import com.gdswlw.library.toolkit.StrUtil;
import com.gdswlw.library.toolkit.UIKit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.xl.xyl2.bean.Device;
import com.xl.xyl2.bean.Loc;
import com.xl.xyl2.mvp.view.activity.ActivityRestart;
import com.xl.xyl2.play.Data;
import com.xl.xyl2.play.DeviceTerminal;
import com.xl.xyl2.play.PlayArea;
import com.xl.xyl2.play.PlayList;
import com.xl.xyl2.play.PlayProgram;
import com.xl.xyl2.play.PlayUnit;
import com.xl.xyl2.play.Setting;
import com.xl.xyl2.utils.AutoOc;
import com.xl.xyl2.utils.BDLocationUtil;
import com.xl.xyl2.utils.CrashHandler;
import com.xl.xyl2.utils.FileUtils;
import com.xl.xyl2.utils.WindowUtils;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.media.AudioManager.FLAG_PLAY_SOUND;
import static android.media.AudioManager.STREAM_MUSIC;

/**
 *
 * @author shihuanzhang
 */
public class XLContext {
    public static Context mContext;
    public static XLContext instance;
    //设备批次号
    public static String lotCode;
    public static String URL_IP;
    public static int URL_PORT;
    public static String API_URL;
    public static PreferenceHelper config;
    public static final String META_KEY_IP = "com.xl.KEY_IP";
    public static final String META_KEY_PORT = "com.xl.KEY_PORT";
    public static final String META_KEY_LOT_CODE = "com.xl.KEY_LOT_CODE";


    public static boolean isInit(){
        return mContext!= null && !TextUtils.isEmpty(URL_IP) &&
                 URL_PORT > 0 && !TextUtils.isEmpty(lotCode);
    }

    /**
     * 初始化
     * @param application
     */
    public static void init(Application application) {
        mContext = application;
        UIKit.init(application);
        ApplicationInfo ai = null;
        try {
            ai = mContext.getPackageManager().
                    getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            URL_IP =   bundle.getString(META_KEY_IP);
            URL_PORT =  bundle.getInt(META_KEY_PORT);
            lotCode =  bundle.getString(META_KEY_LOT_CODE);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            UIKit.eLog("Failed to load meta-data, NameNotFound: " + e.getMessage());
        }
        if(URL_IP == null || URL_PORT <= 0 || lotCode == null){
            UIKit.eLog("请在你的app build.gradle文件中manifestPlaceholders节点是否在[xl_key_ip,xl_key_port,xl_key_lotcode]参数中传入正确的值");
            return;
        }
        API_URL = URL_IP + ":" + URL_PORT + "/service/";
        AutoOc.init(mContext);
        config = new PreferenceHelper(mContext, mContext.getPackageName());//create config file

        //从配置读取批次编号，忽略硬编码
        Object object = FileUtil.getSerilizeData(FileUtils.getRootFile().getPath() + "/other/device.info");
        if (object != null) {
            Device device = (Device) object;
            lotCode = device.getLotCode();
        } else {
            //初始化，固化到本地
            if (!lotCode.equals("00000")) {//如果默认批次编号，不保存
                Device device = new Device();
                device.setLotCode(lotCode);
                FileUtil.serilizenData(new File(FileUtils.getRootFile().getPath() + "/other/"), "device.info", device);
            }
        }


        GDSHttpClient.setCacheDir(mContext.getExternalCacheDir());
        GDSHttpClient.registerParseCondition(new GDSHttpClient.ParseCondition() {
            @Override
            public boolean isSuccee(JSONObject jsonObject) {
                return jsonObject.optInt("success") == 1;
            }

            @Override
            public String errorMessage(JSONObject jsonObject) {
                UIKit.eLog(jsonObject.optString("errMsg"));
                return jsonObject.optString("errMsg");
            }

            @Override
            public void onRequestTimeout(String message) {
                UIKit.toastShort(message);
            }
        });
        GDSHttpClient.registerNetWorkCheck(new GDSHttpClient.NetWorkCheck() {
            @Override
            public boolean isConnect() {
                return NetUtil.CheckNetworkAvailable(mContext);
            }
        });

        //百度地图初始化
        SDKInitializer.initialize(application);

        //未捕捉异常日志处理
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(FileUtils.getCrashLogDir().getPath(),new CrashHandler.Callback(){

            @Override
            public void onHandled(String logPath) {
                //重启
               Intent intent =  new Intent(mContext, ActivityRestart.class);
               intent.putExtra("path",logPath);
               intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
               mContext.startActivity(intent);
               android.os.Process.killProcess(android.os.Process.myPid());
            }
        }));
    }


    /**
     * get XLContext instance
     *
     * @return
     */
    public static XLContext getInstance() {
        return instance;
    }


    //------------------------------------Data save start-------------------------------------------


    /**
     * get setting data
     *
     * @return
     */
    public static Setting getSettingData() {
        Data  data = getData();
        return data == null ? null : data.getSetting();
    }

    /**
     * saveSetting
     *
     * @param setting
     * @return
     */
    public static void saveSetting(Setting setting) {
        if (setting != null) {
            //判断当前的设置是否一致
            Data data = getData();
            if(data!= null){
                if (setting.getIdentification()!= null && !setting.getIdentification().equals(data.getSetting().getIdentification())) {
                    data.setSetting(setting);
                    saveData(data);
                    //更新设置，覆盖数据 并更新终端设置
                    XLContext.useSetting(setting);
                }
            }
        }
    }

    private static Gson gson;

    /**
     * 获取Gson对象
     *
     * @return
     */
    public static Gson getGson() {
        if (gson == null) {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    try {
                        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(json.getAsJsonPrimitive().getAsString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
            gson = builder.create();
        }
        return gson;

    }


    /**
     * 获取设备终端
     *
     * @return
     */
    public static List<DeviceTerminal> getDeviceTerminals() {
        Data data = (Data) FileUtil.getSerilizeData(FileUtils.getDataDir().getPath() + "/" + FileUtils.DATA_NAME);
        return  data == null ? null :data.getDeviceTerminalList();
    }

    /**
     * 保存数据
     *
     * @param jsonStrData
     */
    public static void saveData(String jsonStrData) {
        Data data = XLContext.getGson().fromJson(jsonStrData, Data.class);
        FileUtil.serilizenData(FileUtils.getDataDir(), FileUtils.DATA_NAME, data);
    }

    /**
     * 保存数据 同步数据的接口只保存终端列表和设置
     * @param jsonStrData
     */
    public static void saveDataSync(String jsonStrData) {
        Data data = XLContext.getGson().fromJson(jsonStrData, Data.class);
        Data localData = getData();
        localData.setSetting(data.getSetting());//更新设置
        localData.setDeviceTerminalList(data.getDeviceTerminalList());//更新终端列表
        //如果设置有更新使用新的设置
        Setting setting = data.getSetting();
        if(setting.getIdentification()!= null && !setting.getIdentification().equals(localData.getSetting().getIdentification())){
            useSetting(data.getSetting());
        }
        saveData(localData);//保存新的数据
    }


    public static void saveSettingData(Data data) {
        if(data != null){
            Data localData = getData();
            Setting setting = data.getSetting();
            if(setting.getIdentification()!= null && !setting.getIdentification().equals(localData.getSetting().getIdentification())){
                //更新设置
                localData.setSetting(data.getSetting());
                useSetting(data.getSetting());
                saveData(localData);//保存新的数据
            }
        }
    }

    public static void saveData(Data data) {
        FileUtil.serilizenData(FileUtils.getDataDir(), FileUtils.DATA_NAME, data);
    }

    /**
     * 获取数据
     *
     * @return
     */
    public static Data getData() {
        Data data = (Data) FileUtil.getSerilizeData(FileUtils.getDataDir().getPath() + "/" + FileUtils.DATA_NAME);
        return data;
    }


    /**
     * 获取有效的节目列表
     *
     * @return
     */
    public static List<PlayProgram> getEffectivityPrograms(Data data) {
        return getPrograms(data, true);
    }

    public static List<PlayProgram> getEffectivityPrograms() {
        return getPrograms(getData(), true);
    }


    public static List<PlayProgram> getAllPrograms(Data data) {
        return getPrograms(data, false);
    }

    public static List<PlayProgram> getAllPrograms() {
        return getPrograms(getData(), false);
    }

    /**
     * 获取节目列表
     *
     * @param data
     * @param isExcludeEffectivity 是否排除有效性,获取有效性的节目
     * @return
     */
    public static List<PlayProgram> getPrograms(Data data, boolean isExcludeEffectivity) {
        ArrayList<PlayProgram> effectivityPrograms = new ArrayList<>();
        if(data == null){
            return effectivityPrograms;
        }
        List<DeviceTerminal> deviceTerminalList = data.getDeviceTerminalList();
        if (deviceTerminalList != null && deviceTerminalList.size() > 0) {
            //获取当前时间
            for (int i = 0; i < deviceTerminalList.size(); i++) {
                DeviceTerminal deviceTerminal = deviceTerminalList.get(i);//终端
                PlayList playList = deviceTerminal.getPlayList();//播放列表
                if (playList == null) {
                    continue;
                }
                List<PlayProgram> programs = playList.getPrograms();
                //节目里的每个area，轮播
                for (int j = 0; j < programs.size(); j++) {
                    PlayProgram program = programs.get(j);
                    program.setTid(deviceTerminal.getId());//tid
                    if (isExcludeEffectivity) {
                        //日期是否在区间内 && 是否在开始时间之前
                        if (isProgramInDate(program)) {
                            effectivityPrograms.add(program);
                        }
                    } else {
                        effectivityPrograms.add(program);
                    }
                }
            }
        }
        return effectivityPrograms;
    }


    /**
     * 从节目列表获取下载列表
     *
     * @param programs 节目列表
     * @return
     */
    public static HashMap<String, PlayUnit> getDownloadList(List<PlayProgram> programs) {
        HashMap<String, PlayUnit> downloadList = new HashMap<>();
        if (programs == null) {
            return downloadList;
        }
        for (int j = 0; j < programs.size(); j++) {
            List<PlayArea> areas =  programs.get(j).getAreas();
            List<PlayUnit> playUnits = programs.get(j).getLinkUnits();
            for (int k = 0; k < areas.size(); k++) {
                PlayArea area  = areas.get(k);
                //判断每个area的type
                switch (area.getType()) { //1-视频 2-图片 3-时间日期 4-天气 5-网页 6-直播流 7-字幕 8文本
                    case 1:
                    case 2:
                        List<PlayUnit> units = area.getUnits();
                        for (int l = 0; l < units.size(); l++) {
                            PlayUnit unit = units.get(l);
                            unit.setTid(programs.get(j).getTid());//tid
                            //添加需要下载的文件
                            downloadList.put(unit.getSourceId(), unit);
                        }
                        break;

                }
            }

            //联动单元文件
            if(playUnits != null && playUnits.size() > 0){
                for (int i = 0; i < playUnits.size(); i++) {
                    PlayUnit unit = playUnits.get(i);
                    unit.setTid(programs.get(j).getTid());//tid
                    downloadList.put(unit.getSourceId(), unit);
                }
            }
        }
        return downloadList;
    }

    /**
     * 获取下载列表
     * @return
     */
    public static HashMap<String, PlayUnit> getDownloadList() {
        return  getDownloadList(getAllPrograms());
    }

    /**
     * 判断定时节目是否在当前时间范围内
     *
     * @param timeprogram 定时节目
     */
    public static boolean isBetweenTime(JSONObject timeprogram) {
        if (timeprogram == null) {
            return false;
        }
        String timeFrames = timeprogram.optString("timeFrames");
        String[] times;
        //获取时间段
        if (timeFrames.contains(";")) {//可能存在多个时间段00:00-00:00;00:00-00:00
            times = timeFrames.split(";");
        } else {
            times = new String[]{timeFrames};
        }
        for (int j = 0; j < times.length; j++) {
            //解析多个时间判断是否成立
            if (DateUtil.isInTime(XLContext.getLocalServerTime(), times[j])) {
                return true;
            }
        }
        return false;
    }


    /**
     * 节目是否在有效期内
     *
     * @param program
     * @return
     */
    public static boolean isProgramInDate(PlayProgram program) {
        if (program != null) {
            Date now  = getLocalServerDate();//本地和服务器时间
            //不在有效期限内
            if ((program.getEffectiveDate() != null && now.getTime() <program.getEffectiveDate().getTime())
                    || (program.getExpiryDate() != null && now.getTime() > program.getExpiryDate().getTime()))
                return false;
        }
        return true;
    }

    /**
     * 获取本地服务器时间
     *
     * @return
     */
    public static long getLocalServerTime() {
        String now = config.getString("now");
        if ("".equals(now)) {
            now = String.valueOf(0);
        }
        return System.currentTimeMillis() + Long.parseLong(now);
    }

    /**
     * 获取本地服务器时间日期
     * @return
     */
    public static Date getLocalServerDate() {
        return new Date(getLocalServerTime());
    }



    /**
     * 获取资源服务器主机地址
     *
     * @return
     */
    public static String getResSeverHost() {
        return getData().getResSeverHost();
    }


    /**
     * 获取定位到的经纬度
     *
     * @return
     */
    public static String getLatLng() {
        String latlng = null;
        Loc loc = BDLocationUtil.getInstance().getSerilizeLocationData();
        if (loc != null) {
            latlng = StrUtil.appendString(loc.getLat(), ",", loc.getLng());
        }
        return latlng;
    }

    //-------------------------------------Data save end--------------------------------------------

    public static void lauchApp(Class<? extends Activity> activity) {
        mContext.startActivity(new Intent(mContext, activity).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        UIKit.dLog("start app");
    }

    /**
     * 使用设置，设置亮度和音量等
     */
    public static void useSetting(){
        useSetting(getSettingData());
    }

    /**
     * 使用设置，设置亮度和音量等
     */
    public static void useSetting(Setting setting){
        if(setting != null){
            //设置亮度
            int progress = (int)(((setting.getBrightness() * 1.0) /100) * 255);
            UIKit.dLog("brightness="+progress);
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,progress);
            showLogMessage("亮度调节为:"+setting.getBrightness()+"%");
            //设置音量
            AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

            if(mAudioManager.getStreamVolume(STREAM_MUSIC) == 0){
                try{
                    String keyCommand = "input keyevent " + KeyEvent.KEYCODE_VOLUME_MUTE;
                    Runtime runtime = Runtime.getRuntime();
                    runtime.exec(keyCommand);
                } catch(IOException e){
                }
            }
            int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int volume = (int)(((setting.getVol() * 1.0) /100) * maxVolume);
            UIKit.dLog("volume="+volume);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,volume, FLAG_PLAY_SOUND);
            AutoOc.syncOcSettings(setting);
            showLogMessage("音量调节为:"+setting.getVol()+"%");
        }
    }


    public static void shutdown(){
        Intent shutdown = new Intent("android.xl.shutdown");
        mContext.sendBroadcast(shutdown);
    }


    public static  void showLogMessage(String messsage){
        if(config.getBoolean("debug")){
            WindowUtils.setMessage(messsage,true);
        }
    }
}
