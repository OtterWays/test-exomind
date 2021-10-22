package com.otterways.kata;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utils {

    public static String getStringValueFromJSON(JSONObject jsonObject, String key) {
        String value = "";
        try {
            value = jsonObject.has(key) ? jsonObject.getString(key) : "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static JSONObject getJSONObjectFromJSON(JSONObject jsonObject, String key) {
        JSONObject object = null;
        try {
            object = jsonObject.has(key) ? jsonObject.getJSONObject(key) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

    public static double getDoubleValueFromJSON(JSONObject jsonObject, String key) {
        double value = 0d;
        try {
            value = jsonObject.has(key) ? jsonObject.getInt(key) : 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static JSONArray getJSONArrayFromJSON(JSONObject jsonObject, String key) {
        JSONArray array = null;
        try {
            array = jsonObject.has(key) ? jsonObject.getJSONArray(key) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }
}
