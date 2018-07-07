package gov.anzong.androidnga.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import gov.anzong.androidnga.R;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.PreferenceKey;
import sp.phone.theme.ThemeManager;

/**
 * Created by liuboyu on 16/6/28.
 */
public abstract class BaseActivity extends SwipeBackActivity {

    protected Toast mToast;

    protected PhoneConfiguration mConfig;

    private boolean mNeedActionBar = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mConfig = PhoneConfiguration.getInstance();
        updateWindowFlag();
        updateThemeUi();
        super.onCreate(savedInstanceState);
        initSwipeBack();
    }

    protected void hideActionBar() {
        mNeedActionBar = false;
    }

    protected void updateThemeUi() {
        ThemeManager tm = ThemeManager.getInstance();
        if (mNeedActionBar) {
            setTheme(tm.getActionBarTheme());
        } else {
            setTheme(tm.getNoActionBarTheme());
        }
        if (tm.isNightMode()) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    protected void updateWindowFlag() {
        int flag = 0;
        if (mConfig.isFullScreenMode()) {
            flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }

        if (mConfig.isHardwareAcceleratedEnabled()) {
            flag = flag | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        }
        getWindow().addFlags(flag);
    }

    private void initSwipeBack() {
        if (getSharedPreferences(PreferenceKey.PREFERENCE_SETTINGS, Context.MODE_PRIVATE).getBoolean(PreferenceKey.KEY_SWIPE_BACK, true)) {
            final float density = getResources().getDisplayMetrics().density;// 获取屏幕密度PPI
            getSwipeBackLayout().setEdgeSize((int) (10 * density + 0.5f));// 10dp
            int pos = SwipeBackLayout.EDGE_LEFT | SwipeBackLayout.EDGE_RIGHT;
            getSwipeBackLayout().setEdgeTrackingEnabled(pos);
            setSwipeBackEnable(true);
        } else {
            setSwipeBackEnable(false);
        }
    }

    public void setupActionBar(Toolbar toolbar) {
        if (toolbar != null && getSupportActionBar() == null) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;

    }
}
