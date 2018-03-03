package sp.phone.theme;

import gov.anzong.androidnga.R;

/**
 * Created by Justwen on 2018/3/2.
 */

public class GreenTheme extends DefaultTheme {
    
    @Override
    public int getPrimaryColor() {
        return R.color.colorPrimaryGreen;
    }

    @Override
    public int getAccentColor() {
        return R.color.colorAccentGreen;
    }

    @Override
    public int getActionBarTheme() {
        return R.style.AppThemeDayNightGreen;
    }

    @Override
    public int getNoActionBarTheme() {
        return R.style.AppThemeDayNightGreen_NoActionBar;
    }


}