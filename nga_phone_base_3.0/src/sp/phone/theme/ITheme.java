package sp.phone.theme;

import android.support.annotation.ColorRes;
import android.support.annotation.StyleRes;

public interface ITheme {

    @ColorRes
    int getPrimaryColor();

    @ColorRes
    int getAccentColor();

    @StyleRes
    int getActionBarTheme();

    @StyleRes
    int getNoActionBarTheme();

    @ColorRes
    int getBackgroundColor();

    @ColorRes
    int getBackgroundColor2();

    @ColorRes
    int getForegroundColor();
}
