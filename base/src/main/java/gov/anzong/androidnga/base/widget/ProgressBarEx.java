package gov.anzong.androidnga.base.widget;

import android.app.Activity;
import android.graphics.Color;

import net.steamcrafted.loadtoast.LoadToast;

import gov.anzong.androidnga.base.util.ThemeUtils;

public class ProgressBarEx {

    private LoadToast mLoadToast;

    public ProgressBarEx(Activity activity) {
        mLoadToast = new LoadToast(activity);
        mLoadToast.setBackgroundColor(ThemeUtils.getAccentColor());
        mLoadToast.setTextColor(Color.WHITE);
        mLoadToast.setTranslationY(activity.getResources().getDisplayMetrics().heightPixels * 3 / 4);
    }

    public void show(String text) {
        mLoadToast.setText(text);
        mLoadToast.show();
    }

    public void success() {
        mLoadToast.success();
    }

    public void error() {
        mLoadToast.error();
    }

    public void hide() {
        mLoadToast.hide();
    }
}
