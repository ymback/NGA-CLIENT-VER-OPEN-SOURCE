package sp.phone.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.ColorInt;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;

import gov.anzong.androidnga.R;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.common.PreferenceKey;

public class ThemeManager implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Deprecated
    private ITheme[] mThemes = {
            new DefaultTheme(),
            new GreenTheme(),
            new BlackTheme()
    };

    private ITheme mCurrentTheme;

    private int mThemeIndex;

    private boolean mNightMode;

    private Context mContext;

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
        mContext = ApplicationContextHolder.getContext();
        SharedPreferences sp = mContext.getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
        sp.registerOnSharedPreferenceChangeListener(this);
        mNightMode = sp.getBoolean(PreferenceKey.NIGHT_MODE, false);
        mThemeIndex = Integer.parseInt(sp.getString(PreferenceKey.MATERIAL_THEME, "0"));
        updateTheme();
    }

    public static ThemeManager getInstance() {
        return ThemeManagerHolder.sInstance;
    }

    public void updateTheme() {
        if (isNightMode()) {
            mCurrentTheme = mThemes[0];
        } else {
            mCurrentTheme = mThemes[mThemeIndex];
        }
    }

    public ITheme getCurrentTheme() {
        return mCurrentTheme;
    }

    public int getForegroundColor() {
        return mCurrentTheme.getForegroundColor();
    }

    public int getSecondTextColor() {
        return mCurrentTheme.getSecondTextColor();
    }

    public int getBackgroundColor() {
        return getBackgroundColor(0);
    }

    public int getBackgroundColor(int position) {
        return position % 2 == 1 ? mCurrentTheme.getBackgroundColor2() : mCurrentTheme.getBackgroundColor();
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

    @ColorInt
    public int getActiveColor() {
        return ContextCompat.getColor(mContext, R.color.color_state_active);
    }

    @ColorInt
    public int getInactiveColor() {
        return ContextCompat.getColor(mContext, R.color.color_state_inactive);
    }

    @ColorInt
    public int getNukedColor() {
        return ContextCompat.getColor(mContext, R.color.color_state_nuked);
    }

    @ColorInt
    public int getMutedColor() {
        return ContextCompat.getColor(mContext, R.color.color_state_muted);
    }

    @ColorInt
    public int getWebTextColor() {
        return ContextCompat.getColor(mContext, mCurrentTheme.getWebTextColor());
    }

    @StyleRes
    public int getNoActionBarTheme() {
        return mCurrentTheme.getNoActionBarTheme();
    }

    @StyleRes
    public int getActionBarTheme() {
        return mCurrentTheme.getActionBarTheme();
    }
}
