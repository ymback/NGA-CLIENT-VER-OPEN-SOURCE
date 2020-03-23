package gov.anzong.androidnga.base.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

public class ContextUtils {

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    private static Application sApplication;

    public static void setContext(Context context) {
        sContext = context.getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }

    public static void setApplication(Application application) {
        sApplication = application;
        sContext = application.getApplicationContext();
    }

    public static Application getApplication() {
        return sApplication;
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

    public static Resources getResources() {
        return sContext.getResources();
    }

    public static SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(sContext);
    }

    public static SharedPreferences getSharedPreferences(String name) {
        return sContext.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

}
