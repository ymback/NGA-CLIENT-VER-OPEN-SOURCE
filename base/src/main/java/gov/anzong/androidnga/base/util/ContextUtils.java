package gov.anzong.androidnga.base.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

public class ContextUtils {

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    public static void setContext(Context context) {
        sContext = context.getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }

    // 临时使用
    @ColorInt
    @Deprecated
    public static int getPrimaryColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
        return ContextCompat.getColor(context, typedValue.resourceId);
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
}
