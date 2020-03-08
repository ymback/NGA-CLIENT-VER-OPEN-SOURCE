package gov.anzong.androidnga.base.util;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

public class ThemeUtils {

    private static int sAccentColor;

    private static int sPrimaryColor;

    public static void init(Activity activity) {
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
        sPrimaryColor = ContextCompat.getColor(activity, typedValue.resourceId);

        activity.getTheme().resolveAttribute(android.R.attr.colorAccent, typedValue, true);
        sAccentColor = ContextCompat.getColor(activity, typedValue.resourceId);
    }

    public static int getAccentColor() {
        return sAccentColor;
    }

    public static int getPrimaryColor() {
        return sPrimaryColor;
    }
}
