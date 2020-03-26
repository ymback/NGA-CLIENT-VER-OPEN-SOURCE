package gov.anzong.androidnga.base.util;

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

import java.lang.ref.WeakReference;

public class ContextUtils {

    private static WeakReference<Context> sContext;

    private static Application sApplication;

    public static void setContext(Context context) {
        sContext = new WeakReference<>(context);
    }

    public static Context getContext() {
        if (sContext == null || sContext.get() == null) {
            return sApplication;
        } else {
            return sContext.get();
        }
    }

    public static void setApplication(Application application) {
        sApplication = application;
    }

    public static Application getApplication() {
        return sApplication;
    }

    public static Drawable getDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(getContext(), id);
    }

    @ColorInt
    public static int getColor(@ColorRes int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    public static String getString(@StringRes int id) {
        return getContext().getString(id);
    }

    public static int getDimension(@DimenRes int id) {
        return getContext().getResources().getDimensionPixelSize(id);
    }

    public static Resources getResources() {
        return getContext().getResources();
    }

    public static SharedPreferences getDefaultSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    public static SharedPreferences getSharedPreferences(String name) {
        return getContext().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

}
