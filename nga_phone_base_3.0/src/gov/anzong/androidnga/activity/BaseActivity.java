package gov.anzong.androidnga.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
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
        updateFullScreen();
        if (PhoneConfiguration.getInstance().isMaterialMode() && ActivityUtil.supportMaterialMode(this) || ActivityUtil.supportNewUi(this)) {
            updateThemeUi();
        }
        super.onCreate(savedInstanceState);
    }

    protected void updateThemeUi(){
        setTheme(R.style.AppThemeDayNight);
        if (ThemeManager.getInstance().isNightMode()){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void updateFullScreen(){
        int flag;
        if (PhoneConfiguration.getInstance().fullscreen){
            flag = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        } else {
            flag = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        }
        getWindow().addFlags(flag);
    }

    protected void setupActionBar(Toolbar toolbar){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
