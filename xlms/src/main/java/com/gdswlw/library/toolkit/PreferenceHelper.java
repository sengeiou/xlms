package com.gdswlw.library.toolkit;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 偏好设置工具
 * 
 * @author shi
 * 
 */
public class PreferenceHelper {
	protected SharedPreferences setting;
	/**
	 * 获取一个实例
	 * @param context 上下文对象
	 * @param configName 配置文件名字
	 * @return
	 */
	public PreferenceHelper(Context context,String configName){
		if(configName==null || configName.trim().equals("")){
			throw new NullPointerException("please support the config name!");
		}
		setting = context.getSharedPreferences(configName, Context.MODE_PRIVATE);
	}
	

	public void  remove(String key){
		setting.edit().remove(key).commit();
	}
	public void clear(){
		if(setting.edit()!=null){
			setting.edit().clear().commit();
		}
	}
	public void save(String key, String value) {
		setting.edit().putString(key, value).commit();
	}

	public  String getString(String key) {
		return setting.getString(key, "");
	}

	public JSONObject getJson(String key) {
		JSONObject json = null;
		if( !setting.getString(key, "").equals("")){
			try {
				json = new JSONObject(setting.getString(key, ""));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	public void save(String key,JSONObject jsonObject){
		if(jsonObject != null){
			setting.edit().putString(key, jsonObject.toString()).commit();
		}
	}
	
	public void save(String key, int value) {
		setting.edit().putInt(key, value).commit();
	}
	
	public  int getInt(String key) {
		return setting.getInt(key, 0);
	}

	
	public void save(String key,boolean value) {
		setting.edit().putBoolean(key, value).commit();
	}


	public  boolean getBoolean(String key) {
		return setting.getBoolean(key, false);
	}

	public void save(String key,float value) {
		setting.edit().putFloat(key, value).commit();
	}


	public  float getFloat(String key) {
		return setting.getFloat(key, 0f);
	}
	public void save(String key,long value) {
		setting.edit().putLong(key, value).commit();
	}

	public  long getLong(String key) {
		return setting.getLong(key, 0l);
	}
}
