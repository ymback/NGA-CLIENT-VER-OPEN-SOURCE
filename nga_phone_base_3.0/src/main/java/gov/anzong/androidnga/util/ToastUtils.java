package gov.anzong.androidnga.util;

import android.annotation.SuppressLint;
import androidx.annotation.StringRes;
import android.widget.Toast;

import gov.anzong.androidnga.base.util.ContextUtils;;

/**
 * Created by Justwen on 2018/8/11.
 */
@Deprecated
public class ToastUtils {

    private static Toast sToast;

    public static void showShortToast(String text) {
//        initToast();
//        sToast.setDuration(Toast.LENGTH_SHORT);
//        sToast.setText(text);
//        sToast.show();
        gov.anzong.androidnga.base.util.ToastUtils.showToast(text);
    }

    public static void showShortToast(@StringRes int strId) {
//        initToast();
//        sToast.setDuration(Toast.LENGTH_SHORT);
//        sToast.setText(strId);
//        sToast.show();

        gov.anzong.androidnga.base.util.ToastUtils.showToast(strId);
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
            sToast = Toast.makeText(ContextUtils.getContext(), "", Toast.LENGTH_SHORT);
        }
    }

}
