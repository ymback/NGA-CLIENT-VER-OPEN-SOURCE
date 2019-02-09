package gov.anzong.androidnga.util;

import android.annotation.SuppressLint;
import android.support.annotation.StringRes;
import android.widget.Toast;

import sp.phone.common.ApplicationContextHolder;

/**
 * Created by Justwen on 2018/8/11.
 */
public class ToastUtils {

    private static Toast sToast;

    public static void showShortToast(String text) {
        initToast();
        sToast.setDuration(Toast.LENGTH_SHORT);
        sToast.setText(text);
        sToast.show();
    }

    public static void showShortToast(@StringRes int strId) {
        initToast();
        sToast.setDuration(Toast.LENGTH_SHORT);
        sToast.setText(strId);
        sToast.show();
    }

    public static void showToast(String text) {
        showShortToast(text);
    }

    public static void showToast(@StringRes int strId) {
        showShortToast(strId);
    }

    @SuppressLint("ShowToast")
    private static void initToast() {
        if (sToast == null) {
            sToast = Toast.makeText(ApplicationContextHolder.getContext(), "", Toast.LENGTH_SHORT);
        }
    }

}
