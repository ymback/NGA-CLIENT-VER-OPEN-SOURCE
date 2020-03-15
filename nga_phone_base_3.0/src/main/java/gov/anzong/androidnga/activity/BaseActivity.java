package gov.anzong.androidnga.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.common.SwipeBackHelper;
import gov.anzong.androidnga.base.util.ContextUtils;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.PreferenceKey;
import sp.phone.theme.ThemeManager;
import sp.phone.util.NLog;

/**
 * Created by liuboyu on 16/6/28.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected PhoneConfiguration mConfig;

    private boolean mToolbarEnabled;

    private boolean mHardwareAcceleratedEnabled = true;

    private SwipeBackHelper mSwipeBackHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initBaseModule();
        mConfig = PhoneConfiguration.getInstance();
        updateWindowFlag();
        updateThemeUi();
        setSwipeBackEnable(getSharedPreferences(PreferenceKey.PREFERENCE_SETTINGS, Context.MODE_PRIVATE).getBoolean(PreferenceKey.KEY_SWIPE_BACK, false));
        onCreateBeforeSuper(savedInstanceState);
        super.onCreate(savedInstanceState);
        onCreateAfterSuper(savedInstanceState);
        ThemeManager.getInstance().initializeWebTheme(this);

        if (mSwipeBackHelper != null) {
            mSwipeBackHelper.onCreate(this);
        }

        if (getResources().getBoolean(R.bool.night_mode) != ThemeManager.getInstance().isNightMode()) {
            new WebView(this);
        }

        try {
            if (ThemeManager.getInstance().isNightMode()) {
                getWindow().setNavigationBarColor(ContextUtils.getColor(R.color.background_color));
            }
        } catch (Exception e) {
            NLog.e("set navigation bar color exception occur: " + e);
        }
    }

    private void initBaseModule() {
        ContextUtils.setContext(this);
        ApplicationContextHolder.setContext(this);
    }

    protected void setSwipeBackEnable(boolean enable) {
        if (!enable) {
            mSwipeBackHelper = null;
        } else if (mSwipeBackHelper == null) {
            mSwipeBackHelper = new SwipeBackHelper();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mSwipeBackHelper != null) {
            mSwipeBackHelper.onPostCreate();
        }
    }

    @Override
    public <T extends View> T findViewById(int id) {
        T t = super.findViewById(id);
        if (t == null && mSwipeBackHelper != null) {
            t = mSwipeBackHelper.findViewById(id);
        }
        return t;
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }
}
