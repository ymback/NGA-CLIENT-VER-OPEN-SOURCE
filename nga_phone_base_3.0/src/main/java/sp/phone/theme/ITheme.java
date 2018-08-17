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

    @ColorRes
    int getSecondTextColor();

    @ColorRes
    int getActiveColor();

    @ColorRes
    int getMutedColor();

    @ColorRes
    int getInactiveColor();

    @ColorRes
    int getNukedColor();

    @ColorRes
    int getWebTextColor();
}
