package gov.anzong.androidnga.activity;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import gov.anzong.androidnga.R;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.ThemeManager;
import sp.phone.utils.ActivityUtils;

import static sp.phone.common.PreferenceKey.NIGHT_MODE;
import static sp.phone.common.PreferenceKey.PERFERENCE;

/**
 * Created by liuboyu on 16/6/28.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Toast mToast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        updateFullScreen();
        updateOrientation();
        if (PhoneConfiguration.getInstance().isMaterialMode() && ActivityUtils.supportMaterialMode(this) || ActivityUtils.supportNewUi(this)) {
            updateThemeUi();
        }
        super.onCreate(savedInstanceState);
    }

    protected void updateThemeUi(){
        ThemeManager tm = ThemeManager.getInstance();
        setTheme(tm.getTheme());
        if (tm.isNightMode()){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void updateFullScreen(){
        int flag = 0;
        if (PhoneConfiguration.getInstance().fullscreen){
            flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }

        if (PhoneConfiguration.getInstance().getHardwareAcceleratedMode()) {
            flag = flag | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        }
        getWindow().addFlags(flag);
    }

    private void updateOrientation(){
        int orientation = ThemeManager.getInstance().screenOrentation;
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    protected void setupActionBar(Toolbar toolbar){
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    public void setupActionBar() {
        setupActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    protected void showToast(int res) {
        String str = getString(res);
        showToast(str);
    }

    protected void showToast(String res) {
        if (mToast != null) {
            mToast.setText(res);
            mToast.setDuration(Toast.LENGTH_SHORT);
        } else {
            mToast = Toast.makeText(this, res, Toast.LENGTH_SHORT);
        }
        mToast.show();
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
