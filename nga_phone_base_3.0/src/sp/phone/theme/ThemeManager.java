package sp.phone.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import sp.phone.common.PreferenceKey;
import sp.phone.utils.ApplicationContextHolder;

public class ThemeManager implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Deprecated
    public static final int MODE_NORMAL = 0;

    @Deprecated
    public static final int MODE_NIGHT = 1;

    private List<ITheme> mThemeList;

    private ITheme mCurrentTheme;

    private int mThemeIndex;

    private boolean mNightMode;

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        if (key.equals(PreferenceKey.NIGHT_MODE)) {
            mNightMode = sp.getBoolean(key, false);
            updateTheme();
        } else if (key.equals(PreferenceKey.MATERIAL_THEME)) {
            mThemeIndex = Integer.parseInt(sp.getString(key, "0"));
            updateTheme();
        }
    }

    private static class ThemeManagerHolder {

        private static ThemeManager sInstance = new ThemeManager();
    }

    private ThemeManager() {
        SharedPreferences sp = ApplicationContextHolder.getContext().getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
        sp.registerOnSharedPreferenceChangeListener(this);
        mNightMode = sp.getBoolean(PreferenceKey.NIGHT_MODE, false);
        mThemeIndex = Integer.parseInt(sp.getString(PreferenceKey.MATERIAL_THEME, "0"));
        initThemeList();
    }

    public static ThemeManager getInstance() {
        return ThemeManagerHolder.sInstance;
    }

    private void initThemeList() {
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
            mCurrentTheme = mThemeList.get(mThemeIndex);
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
