package com.xl.xyl2.mvp.view.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gdswlw.library.http.Callback;
import com.gdswlw.library.http.GDSHttpClient;
import com.gdswlw.library.toolkit.UIKit;
import com.xl.xyl2.XLContext;
import com.xl.xyl2.R;
import com.xl.xyl2.play.PlayArea;
import com.xl.xyl2.play.Weather;
import com.xl.xyl2.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 天气视图
 * Created by Afun on 2019/10/9.
 */

public class XLWeather extends LinearLayout {

    TextView tvWeather;
    View view;

    public XLWeather(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_weather,this);
        tvWeather = findViewById(R.id.tv_weather);
        view = findViewById(R.id.v_outer);
    }

    public XLWeather(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 获取天气
     * @param playArea
     */
    public void getWeather(PlayArea playArea,double scaleValue){

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) (playArea.getWidth() * scaleValue),
                (int) (playArea.getHeight() * scaleValue));
        view.setLayoutParams(layoutParams);
        view.setBackgroundColor(Color.parseColor(playArea.getBgColor()));
        view.setAlpha(playArea.getBgOpacity());

        tvWeather.setTextColor(Color.parseColor(playArea.getFontColor()== null?"#000000":playArea.getFontColor()));
        tvWeather.setTextSize(TypedValue.COMPLEX_UNIT_PX,playArea.getFontSize()==0?15:playArea.getFontSize());
        final JSONObject json = new JSONObject();
        try {
            json.put("lan", AppUtils.isCN(getContext()) ? "cn" : "en");
            json.put("dcode", AppUtils.getTernimalNum());
            JSONObject data = new JSONObject();
            if(playArea.getCity() != null){
                data.put("region", playArea.getCity());
            }else if(playArea.getProvince()!=null){
                data.put("region", playArea.getProvince());
            }else if(playArea.getCountry()!= null){
                data.put("region", playArea.getCountry());
            }else{
                data.put("region", "");
            }
            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GDSHttpClient.postWithJsonBody(getContext(), XLContext.API_URL + "/main/device/api/weather", json, new Callback() {
            @Override
            public void onSuccess(String url, JSONObject jsonObject, int type) {
                if(jsonObject.optInt("success") == 1){
                    Weather weather =  XLContext.getGson().fromJson(jsonObject.optJSONObject("data").toString(),Weather.class);
                    tvWeather.setText(weather.getWeather()+"\t"+weather.getMintemp()+"℃-"+weather.getMaxtemp()+"℃");
                }
            }

            @Override
            public void onFailure(String url, Throwable throwable) {
                UIKit.eLog(throwable.getMessage());
                tvWeather.setText("Weather Err");
            }

        }, null);
    }

}
