package sp.phone.common;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v4.content.ContextCompat;

import gov.anzong.androidnga.R;

public class ThemeManager {

    public static final int MODE_NORMAL = 0;
    public static final int MODE_NIGHT = 1;

    private int mThemeIndex;

    private int mMode;

    private int[] mPrimaryColors = new int[]{R.color.colorPrimaryBrown, R.color.colorPrimaryGreen, R.color.colorPrimaryBlack};

    private int[] mAccentColors = new int[]{R.color.colorAccentBrown, R.color.colorAccentGreen, R.color.colorAccentBlack};

    private int[] mThemes = new int[]{R.style.AppThemeDayNightBrown, R.style.AppThemeDayNightGreen, R.style.AppThemeDayNightBlack};

    static final public int ACTION_BAR_FLAG = 31;
    public int mode = 0;
    public int screenOrentation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

    private int foregroundColor[] = {R.color.black, R.color.night_fore_color};
    private int backgroundColor[] = {R.color.shit2, R.color.night_bg_color};

    private int[] mSwitchBackground = {R.color.switch_bg_selector, R.color.switch_bg_selector};

    private static class ThemeManagerHolder {

        private static ThemeManager sInstance = new ThemeManager();
    }

    public static ThemeManager getInstance() {
        return ThemeManagerHolder.sInstance;
    }

    public static void SetContextTheme(Context context) {
        context.setTheme(android.R.style.Theme_Holo);
    }

    public int getForegroundColor() {
        return foregroundColor[mode];
    }

    public int getBackgroundColor() {
        return getBackgroundColor(0);
    }

    public int getSwitchBackground() {
        return mSwitchBackground[mode];
    }

    public int getBackgroundColor(int position) {
        int ret = backgroundColor[mode];

        if (MODE_NORMAL == mode && position % 2 == 1) {
            ret = R.color.shit1;
        }

        return ret;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        mMode = mode;
        this.mode = mode;
    }

    public boolean isNightMode() {
        return mMode == MODE_NIGHT;
    }

    public void setTheme(int theme) {
        mThemeIndex = theme;
    }

    public int getPrimaryColor(Context context) {
        return ContextCompat.getColor(context, mPrimaryColors[mThemeIndex]);
    }

    public int getAccentColor(Context context) {
        return ContextCompat.getColor(context, mAccentColors[mThemeIndex]);
    }

    public int getTheme() {
        return mThemes[mThemeIndex];
    }
}
