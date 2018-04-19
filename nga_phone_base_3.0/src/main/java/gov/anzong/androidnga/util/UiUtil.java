package gov.anzong.androidnga.util;

import android.content.Context;
import android.widget.Toast;

public class UiUtil {
    public static int dip2px(Context context, float dpValue) {
        if (context != null) {
            float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5F);
        } else {
            return (int) dpValue;
        }
    }

    public static void showToast(Context context, String msg) {
        showToast(context, msg, 0);
    }

    public static void showToast(Context context, String msg, int duration) {
        Toast.makeText(context, msg, duration).show();
    }
}
