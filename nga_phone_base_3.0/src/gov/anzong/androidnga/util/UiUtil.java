package gov.anzong.androidnga.util;

import android.content.Context;
import android.widget.TextView;
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
        if (context != null) {
            context = context.getApplicationContext();
            Toast mToast = new Toast(context);
            TextView mTextView = new TextView(context);
            mTextView.setGravity(17);
            mTextView.setTextColor(context.getResources().getColor(android.R.color.white));
            mToast.setView(mTextView);
            mTextView.setText(msg);
            mToast.setDuration(duration);
            mToast.show();
        }
    }
}
