package gov.anzong.androidnga.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import gov.anzong.androidnga.R;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ThemeManager;

import static sp.phone.bean.PreferenceConstant.NIGHT_MODE;
import static sp.phone.bean.PreferenceConstant.PERFERENCE;

/**
 * Created by liuboyu on 16/6/28.
 */
public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (PhoneConfiguration.getInstance().isMaterialMode() && ActivityUtil.supportMaterialMode(this)) {
            updateThemeUi();
            updateFullScreen();
        }
        super.onCreate(savedInstanceState);
    }

    protected void updateThemeUi(){
        if (ThemeManager.getInstance().isNightMode()){
            setTheme(R.style.MaterialThemeDarkNoActionBar);
        } else {
            setTheme(R.style.MaterialThemeNoActionBar);
        }
    }

    private void updateFullScreen(){
        int flag;
        if (PhoneConfiguration.getInstance().fullscreen){
            flag = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        } else {
            flag = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        }
        getWindow().addFlags(flag);
    }

    protected Toast toast;

    protected void showToast(int res) {
        String str = getString(res);
        showToast(str);
    }

    protected void showToast(String res) {
        if (toast != null) {
            toast.setText(res);
            toast.setDuration(Toast.LENGTH_SHORT);
        } else {
            toast = Toast.makeText(this, res, Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public void changeNightMode(final MenuItem menu) {
        ThemeManager tm = ThemeManager.getInstance();
        SharedPreferences share = getSharedPreferences(PERFERENCE, MODE_PRIVATE);
        int mode = ThemeManager.MODE_NORMAL;
        if (tm.getMode() == ThemeManager.MODE_NIGHT) {// 是晚上模式，改白天的
            menu.setIcon(R.drawable.ic_action_bightness_low);
            menu.setTitle(R.string.change_night_mode);
            SharedPreferences.Editor editor = share.edit();
            editor.putBoolean(NIGHT_MODE, false);
            editor.apply();
        } else {
            menu.setIcon(R.drawable.ic_action_brightness_high);
            menu.setTitle(R.string.change_daily_mode);
            SharedPreferences.Editor editor = share.edit();
            editor.putBoolean(NIGHT_MODE, true);
            editor.apply();
            mode = ThemeManager.MODE_NIGHT;
        }
        ThemeManager.getInstance().setMode(mode);
    }
}
