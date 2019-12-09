package com.xl.xyl2.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

public class JsonUtils {
    /**
     * json 转 bean
     *
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> Object jsonToBean(String json, Class<T> tClass) {
        try {
            return new Gson().fromJson(json, tClass);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 对象转json
     *
     * @param object
     * @return
     */
    public static String beanToJson(Object object) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(object);
    }

    /**
     * 向json中添加数据
     *
     * @param object
     * @param name
     * @param value
     * @return
     */
    public static JSONObject putJSON(JSONObject object, String name, Object value) {
        try {
            object.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static JSONArray putJSONArray(JSONArray jsonArray, Object val) {
        jsonArray.put(val);
        return jsonArray;
    }


    public static int getJSONInt(JSONArray jsonArray, int index) {
        if (jsonArray != null && jsonArray.length() > 0) {
            try {
                return jsonArray.getInt(index);
            } catch (JSONException e) {
                return 0;
            }
        }
        return 0;
    }


    /**
     * 得到格式化json数据  退格用\t 换行用\r
     */
    public static String format(String jsonStr) {
        int level = 0;
        StringBuffer jsonForMatStr = new StringBuffer();
        for (int i = 0; i < jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);
            if (level > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                jsonForMatStr.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    jsonForMatStr.append(c + "\n");
                    level++;
                    break;
                case ',':
                    jsonForMatStr.append(c + "\n");
                    break;
                case '}':
                case ']':
                    jsonForMatStr.append("\n");
                    level--;
                    jsonForMatStr.append(getLevelStr(level));
                    jsonForMatStr.append(c);
                    break;
                default:
                    jsonForMatStr.append(c);
                    break;
            }
        }
        return jsonForMatStr.toString();

    }

    private static String getLevelStr(int level) {
        StringBuffer levelStr = new StringBuffer();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }

    public static float[] str2floatList(String json) {
        Type type = new TypeToken<Float>() {
        }.getType();
        JsonArray jsonArray = new JsonParser().parse(json).getAsJsonArray();
        float[] faceVec = null;
        if (jsonArray != null && jsonArray.size() == 512) {
            faceVec = new float[jsonArray.size()];
            for (int k = 0; k < jsonArray.size(); k++) {
                faceVec[k] = jsonArray.get(k).getAsFloat();
            }
            return faceVec;
        }
        return null;
    }

}
