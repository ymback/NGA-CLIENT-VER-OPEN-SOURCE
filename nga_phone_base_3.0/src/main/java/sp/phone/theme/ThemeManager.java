package sp.phone.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.ColorInt;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import gov.anzong.androidnga.R;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.common.PreferenceKey;

public class ThemeManager implements SharedPreferences.OnSharedPreferenceChangeListener {

    private int[] mAppThemes = {
            R.style.AppThemeDayNightBrown_NoActionBar,
            R.style.AppThemeDayNightGreen_NoActionBar,
            R.style.AppThemeDayNightBlack_NoActionBar,
    };

    private int[] mAppThemesActionBar = {
            R.style.AppThemeDayNightBrown,
            R.style.AppThemeDayNightGreen,
            R.style.AppThemeDayNightBlack,
    };

    private int mThemeIndex;

    private boolean mNightMode;

    private Context mContext;

    private WebViewTheme mWebViewTheme;

    private TypedValue mTypedValue = new TypedValue();

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        if (key.equals(PreferenceKey.NIGHT_MODE)) {
            mNightMode = sp.getBoolean(key, false);
        } else if (key.equals(PreferenceKey.MATERIAL_THEME)) {
            mThemeIndex = Integer.parseInt(sp.getString(key, "0"));
        }
        mWebViewTheme = null;
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
    }

    public static ThemeManager getInstance() {
        return ThemeManagerHolder.sInstance;
    }


    public void initializeWebTheme(Context context) {
        if (mWebViewTheme == null) {
            mWebViewTheme = new WebViewTheme(context);
        }
    }

    public int getForegroundColor() {
        return R.color.foreground_color;
    }

    public int getBackgroundColor() {
        return getBackgroundColor(0);
    }

    public int getBackgroundColor(int position) {
        return position % 2 == 1 ? R.color.background_color2 : R.color.background_color;
    }

    public boolean isNightMode() {
        return mNightMode;
    }

    @ColorInt
    public int getPrimaryColor(Context context) {
        context.getTheme().resolveAttribute(android.R.attr.colorPrimary, mTypedValue, true);
        return ContextCompat.getColor(context, mTypedValue.resourceId);
    }

    @ColorInt
    public int getAccentColor(Context context) {
        context.getTheme().resolveAttribute(android.R.attr.colorAccent, mTypedValue, true);
        return ContextCompat.getColor(context, mTypedValue.resourceId);
    }


    @ColorInt
    public int getWebTextColor() {
        return mWebViewTheme.getWebTextColor();
    }

    @ColorInt
    public int getWebQuoteBackgroundColor() {
        return mWebViewTheme.getQuoteBackgroundColor();
    }

    @StyleRes
    public int getTheme(boolean actionbarEnabled) {
        int index = isNightMode() ? 0 : mThemeIndex;
        return actionbarEnabled ? mAppThemesActionBar[index] : mAppThemes[index];
    }
}
