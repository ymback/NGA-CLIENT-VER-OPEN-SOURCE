package sp.phone.utils;

import android.content.Context;
import android.content.pm.ActivityInfo;

import gov.anzong.androidnga.R;

public class ThemeManager {
    //private int cseq = 0;
    static final public int MODE_NORMAL = 0;
    static final public int MODE_NIGHT = 1;
    static final public int ACTION_BAR_FLAG = 31;
    static final public int ACTION_IF_ROOM = 1;//SHOW_AS_ACTION_IF_ROOM
    private static ThemeManager instance = null;
    public int mode = 0;
    public int screenOrentation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

	/*ActionBar.DISPLAY_SHOW_HOME;//2
	flags |= ActionBar.DISPLAY_USE_LOGO;//1
	flags |= ActionBar.DISPLAY_SHOW_TITLE;//8
	flags |= ActionBar.DISPLAY_HOME_AS_UP;//4*/
    int foregroundColor[] = {R.color.black, R.color.night_fore_color};
    //static final public int Theme_Holo = 16973931;//android.R$style.class
    //public static final int Theme = 16973829;//android.R$style.class
    int backgroundColor[] = {R.color.shit2, R.color.night_bg_color};
    
    int[] mSwitchBackground = {R.color.switch_bg_selector,R.color.switch_bg_selector};

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();

        }
        return instance;
    }

    public static void setInstance(ThemeManager instance) {
        ThemeManager.instance = instance;
    }

    public static void SetContextTheme(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            context.setTheme(android.R.style.Theme_Holo);
    }

    public int getForegroundColor() {
        return foregroundColor[mode];
    }

    public int getBackgroundColor() {
        return getBackgroundColor(0);
    }
    
    public int getSwitchBackground(){
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
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }


}
