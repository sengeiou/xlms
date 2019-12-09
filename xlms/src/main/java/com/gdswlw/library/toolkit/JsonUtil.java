package com.gdswlw.library.toolkit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class JsonUtil {
	private JsonUtil() {
	};

	/**
	 * 将jsonArray转换为ArrayList<HashMap<String, String>>
	 * @param arrays
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> jsonArray2HashMapList(
			JSONArray arrays) {
		ArrayList<HashMap<String, String>> maps = new ArrayList<HashMap<String, String>>();
		if (arrays != null && arrays.length() > 0) {
			JSONObject json = null;
			HashMap<String, String> map = null;
			for (int i = 0; i < arrays.length(); i++) {
				try {
					json = arrays.getJSONObject(i);
					map = new HashMap<String, String>();
					Iterator<String> it = json.keys();
					while (it.hasNext()) {
						String key = it.next();
						map.put(key, json.getString(key));
					}
					maps.add(map);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return maps;
	}

	/**
	 * 将jsonArray转换为ArrayList<JSONObject>
	 * @param arrays
	 * @return
	 */
	public static ArrayList<JSONObject> array2List(
			JSONArray arrays) {
		ArrayList<JSONObject> list = new ArrayList<>();
		if (arrays != null && arrays.length() > 0) {
			for (int i = 0; i < arrays.length(); i++) {
				try {
					list.add(arrays.getJSONObject(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, String> jsonObject2HashMap(JSONObject json) {
		HashMap<String, String> map = null;
		if (json != null) {
			try {
				map = new HashMap<String, String>();
				Iterator<String> it = json.keys();
				while (it.hasNext()) {
					String key = it.next();
					map.put(key, json.getString(key));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}
		return map;
	}
}
