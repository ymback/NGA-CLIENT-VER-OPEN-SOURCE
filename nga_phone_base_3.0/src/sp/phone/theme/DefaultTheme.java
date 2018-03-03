package sp.phone.theme;

import gov.anzong.androidnga.R;

/**
 * Created by Justwen on 2018/3/2.
 */

public class DefaultTheme implements ITheme {

    @Override
    public int getPrimaryColor() {
        return R.color.colorPrimaryBrown;
    }

    @Override
    public int getAccentColor() {
        return R.color.colorAccentBrown;
    }

    @Override
    public int getActionBarTheme() {
        return R.style.AppThemeDayNightBrown;
    }

    @Override
    public int getNoActionBarTheme() {
        return R.style.AppThemeDayNightBrown_NoActionBar;
    }

    @Override
    public int getBackgroundColor() {
        return R.color.background_color;
    }

    @Override
    public int getBackgroundColor2() {
        return R.color.background_color2;
    }

    @Override
    public int getForegroundColor() {
        return R.color.foreground_color;
    }
}
