package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.activity.SwipeBackActivity;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.common.PhoneConfiguration;
import sp.phone.theme.ThemeManager;

/**
 * Created by liuboyu on 16/6/28.
 */
public abstract class BaseActivity extends SwipeBackActivity {

    protected PhoneConfiguration mConfig;

    private boolean mToolbarEnabled;

    private boolean mHardwareAcceleratedEnabled = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (ApplicationContextHolder.getContext() == null) {
            ApplicationContextHolder.setContext(this);
        }
        mConfig = PhoneConfiguration.getInstance();
        updateWindowFlag();
        updateThemeUi();
        onCreateBeforeSuper(savedInstanceState);
        super.onCreate(savedInstanceState);
        ThemeManager.getInstance().initializeWebTheme(this);
        onCreateAfterSuper(savedInstanceState);
    }

    protected void onCreateBeforeSuper(@Nullable Bundle savedInstanceState) {

    }

    protected void onCreateAfterSuper(@Nullable Bundle savedInstanceState) {

    }

    protected void setToolbarEnabled(boolean enabled) {
        mToolbarEnabled = enabled;
    }

    protected void setHardwareAcceleratedEnabled(boolean enabled) {
        mHardwareAcceleratedEnabled = enabled;
    }

    public void setupToolbar(Toolbar toolbar) {
        if (toolbar != null && getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    public void setupToolbar() {
        setupToolbar((Toolbar) findViewById(R.id.toolbar));
    }

    public void setupActionBar() {
        if (mToolbarEnabled) {
            setupToolbar();
        } else {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    protected void updateThemeUi() {
        ThemeManager tm = ThemeManager.getInstance();
        setTheme(tm.getTheme(mToolbarEnabled));
        if (tm.isNightMode()) {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    protected void updateWindowFlag() {
        int flag = 0;

        if (mHardwareAcceleratedEnabled) {
            flag = flag | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        }
        getWindow().addFlags(flag);
    }

    @Deprecated
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
