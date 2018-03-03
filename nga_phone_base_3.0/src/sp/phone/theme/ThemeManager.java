package sp.phone.theme;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import sp.phone.common.PhoneConfiguration;
import sp.phone.common.PreferenceKey;

public class ThemeManager {

    private PhoneConfiguration mConfig;

    @Deprecated
    public static final int MODE_NORMAL = 0;

    @Deprecated
    public static final int MODE_NIGHT = 1;

    @Deprecated
    public int screenOrentation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

    private List<ITheme> mThemeList;

    private ITheme mCurrentTheme;

    private boolean mNightMode;

    private static class ThemeManagerHolder {

        private static ThemeManager sInstance = new ThemeManager();
    }

    private ThemeManager() {
        mConfig = PhoneConfiguration.getInstance();
        init();
    }

    public static ThemeManager getInstance() {
        return ThemeManagerHolder.sInstance;
    }

    private void init() {
        mThemeList = new ArrayList<>();
        mThemeList.add(new DefaultTheme());
        mThemeList.add(new GreenTheme());
        mThemeList.add(new BlackTheme());
        updateTheme();
    }

    public void updateTheme() {
        if (isNightMode()) {
            mCurrentTheme = mThemeList.get(0);
        } else {
            int index = mConfig.getInt(PreferenceKey.MATERIAL_THEME);
            mCurrentTheme = mThemeList.get(index);
        }
    }

    public int getForegroundColor() {
        return mCurrentTheme.getForegroundColor();
    }

    public int getBackgroundColor() {
        return getBackgroundColor(0);
    }

    public int getBackgroundColor(int position) {
        return position % 2 == 1 ? mCurrentTheme.getBackgroundColor2() : mCurrentTheme.getBackgroundColor();
    }

    @Deprecated
    public int getMode() {
        return mNightMode ? MODE_NIGHT : MODE_NORMAL;
    }


    public void setNighMode(boolean nighMode) {
        mNightMode = nighMode;
        updateTheme();
    }

    public boolean isNightMode() {
        return mNightMode;
    }

    @ColorInt
    public int getPrimaryColor(Context context) {
        return ContextCompat.getColor(context, mCurrentTheme.getPrimaryColor());
    }

    @ColorInt
    public int getAccentColor(Context context) {
        return ContextCompat.getColor(context, mCurrentTheme.getAccentColor());
    }

    public int getNoActionBarTheme() {
        return mCurrentTheme.getNoActionBarTheme();
    }

    public int getActionBarTheme() {
        return mCurrentTheme.getActionBarTheme();
    }
}
