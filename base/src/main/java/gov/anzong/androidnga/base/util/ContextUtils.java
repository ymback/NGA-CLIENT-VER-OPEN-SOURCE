package gov.anzong.androidnga.base.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

public class ContextUtils {

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    public static void setContext(Context context) {
        sContext = context.getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }

    public static Drawable getDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(sContext, id);
    }

    @ColorInt
    public static int getColor(@ColorRes int id) {
        return ContextCompat.getColor(sContext, id);
    }

    public static String getString(@StringRes int id) {
        return sContext.getString(id);
    }

    public static int getDimension(@DimenRes int id) {
        return sContext.getResources().getDimensionPixelSize(id);
    }

    public static SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(sContext);
    }

    public static SharedPreferences getSharedPreferences(String name) {
        return sContext.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

}
