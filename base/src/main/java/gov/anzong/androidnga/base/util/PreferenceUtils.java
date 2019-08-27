package gov.anzong.androidnga.base.util;

import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PreferenceUtils {

    private static SharedPreferences sPreferences;

    private static void createIfNull() {
        if (sPreferences == null) {
            sPreferences = ContextUtils.getDefaultSharedPreferences();
        }
    }

    public static void putData(String key, String value) {
        createIfNull();
        sPreferences.edit().putString(key, value).apply();
    }

    public static void putData(String key, int value) {
        createIfNull();
        sPreferences.edit().putInt(key, value).apply();
    }

    public static void putData(String key, boolean value) {
        createIfNull();
        sPreferences.edit().putBoolean(key, value).apply();
    }

    public static void putData(String key, float value) {
        createIfNull();
        sPreferences.edit().putFloat(key, value).apply();
    }


    public static void putData(String key, long value) {
        createIfNull();
        sPreferences.edit().putLong(key, value).apply();
    }

    public static void putData(String key, Set<String> value) {
        createIfNull();
        sPreferences.edit().putStringSet(key, value).apply();
    }

    public static String getData(String key, String defValue) {
        createIfNull();
        return sPreferences.getString(key, defValue);
    }

    public static boolean getData(String key, boolean defValue) {
        createIfNull();
        return sPreferences.getBoolean(key, defValue);
    }

    public static float getData(String key, float defValue) {
        createIfNull();
        return sPreferences.getFloat(key, defValue);
    }

    public static long getData(String key, long defValue) {
        createIfNull();
        return sPreferences.getLong(key, defValue);
    }

    public static int getData(String key, int defValue) {
        createIfNull();
        return sPreferences.getInt(key, defValue);
    }

    public static Set<String> getData(String key, Set<String> defValue) {
        createIfNull();
        return sPreferences.getStringSet(key, defValue);
    }

    public static void putData(String key, List list) {
        createIfNull();
        sPreferences.edit().putString(key, JSON.toJSONString(list)).apply();
    }

    public static <T> List<T> getData(String key, Class<T> clz) {
        createIfNull();
        String value = sPreferences.getString(key, null);
        if (value == null) {
            return new ArrayList<>();
        } else {
            return JSON.parseArray(value, clz);
        }
    }

    public static SharedPreferences.Editor edit() {
        createIfNull();
        return sPreferences.edit();
    }

}
