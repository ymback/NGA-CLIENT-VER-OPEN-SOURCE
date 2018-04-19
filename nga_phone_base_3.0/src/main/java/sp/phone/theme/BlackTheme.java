package sp.phone.theme;

import gov.anzong.androidnga.R;

/**
 * Created by Justwen on 2018/3/3.
 */

public class BlackTheme extends DefaultTheme {
    @Override
    public int getPrimaryColor() {
        return R.color.colorPrimaryBlack;
    }

    @Override
    public int getAccentColor() {
        return R.color.colorAccentBlack;
    }

    @Override
    public int getActionBarTheme() {
        return R.style.AppThemeDayNightBlack;
    }

    @Override
    public int getNoActionBarTheme() {
        return R.style.AppThemeDayNightBlack_NoActionBar;
    }

}
