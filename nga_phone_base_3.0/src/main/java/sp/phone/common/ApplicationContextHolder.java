package sp.phone.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

/**
 * Created by Justwen on 2017/10/15.
 */

public class ApplicationContextHolder {

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    public static void setContext(Context context) {
        sContext = context.getApplicationContext();
        if (sContext == null) {
            sContext = context;
        }
    }

    public static String getString(@StringRes int resId) {
        return sContext.getString(resId);
    }

    public static int getColor(@ColorRes int resId) {
        return ContextCompat.getColor(sContext, resId);
    }

    public static Drawable getDrawable(@DrawableRes int resId) {
        return ContextCompat.getDrawable(sContext, resId);
    }

    public static Context getContext() {
        return sContext;
    }

    public static Resources getResources() {
        return sContext.getResources();
    }


}
