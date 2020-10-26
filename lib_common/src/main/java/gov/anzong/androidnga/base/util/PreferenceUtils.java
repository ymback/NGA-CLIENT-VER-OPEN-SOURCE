package gov.anzong.androidnga.base.util;

import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Justwen
 */
public class PreferenceUtils {

    private static SharedPreferences sPreferences;

    static {
        sPreferences = ContextUtils.getDefaultSharedPreferences();
    }

    public static void putData(String key, String value) {
        sPreferences.edit().putString(key, value).apply();
    }

    public static void putData(String key, int value) {
        sPreferences.edit().putInt(key, value).apply();
    }

    public static void putData(String key, boolean value) {
        sPreferences.edit().putBoolean(key, value).apply();
    }

    public static void putData(String key, float value) {
        sPreferences.edit().putFloat(key, value).apply();
    }

    public static void putData(String key, long value) {
        sPreferences.edit().putLong(key, value).apply();
    }

    public static void putData(String key, Set<String> value) {
        sPreferences.edit().putStringSet(key, value).apply();
    }

    public static String getData(String key, String defValue) {
        return sPreferences.getString(key, defValue);
    }

    public static boolean getData(String key, boolean defValue) {
        return sPreferences.getBoolean(key, defValue);
    }

    public static float getData(String key, float defValue) {
        return sPreferences.getFloat(key, defValue);
    }

    public static long getData(String key, long defValue) {
        return sPreferences.getLong(key, defValue);
    }

    public static int getData(String key, int defValue) {
        return sPreferences.getInt(key, defValue);
    }

    public static Set<String> getData(String key, Set<String> defValue) {
        return sPreferences.getStringSet(key, defValue);
    }

    public static void putData(String key, List list) {
        sPreferences.edit().putString(key, JSON.toJSONString(list)).apply();
    }

    public static <T> List<T> getData(String key, Class<T> clz) {
        try {
            String value = sPreferences.getString(key, null);
            if (value == null) {
                return new ArrayList<>();
            } else {
                return JSON.parseArray(value, clz);
            }
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public static SharedPreferences.Editor edit() {
        return sPreferences.edit();
    }

}
