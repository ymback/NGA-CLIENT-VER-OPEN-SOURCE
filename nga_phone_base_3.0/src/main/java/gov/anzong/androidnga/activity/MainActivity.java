package gov.anzong.androidnga.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;

import gov.anzong.androidnga.NgaClientApp;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.arouter.ARouterConstants;
import gov.anzong.androidnga.base.util.ThemeUtils;
import sp.phone.common.User;
import sp.phone.common.UserManagerImpl;
import sp.phone.param.ParamKey;
import sp.phone.ui.fragment.BaseFragment;
import sp.phone.ui.fragment.NavigationDrawerFragment;
import sp.phone.ui.fragment.dialog.UrlInputDialogFragment;
import sp.phone.ui.fragment.dialog.VersionUpgradeDialogFragment;
import sp.phone.theme.ThemeManager;
import sp.phone.util.ActivityUtils;
import gov.anzong.androidnga.base.util.PermissionUtils;

public class MainActivity extends BaseActivity {

    private boolean mIsNightMode;

    private BaseFragment mBoardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setToolbarEnabled(true);
        setSwipeBackEnable(false);
        super.onCreate(savedInstanceState);
        ThemeUtils.init(this);
        checkPermission();
        checkNewVersion();
        initView();
        mIsNightMode = ThemeManager.getInstance().isNightMode();
        setTitle(R.string.start_title);
    }

    @Override
    protected void onCreateAfterSuper(@Nullable Bundle savedInstanceState) {
        setSwipeBackEnable(false);
    }

    private void checkPermission() {
        PermissionUtils.request(this, null, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void checkNewVersion() {
        Application app = getApplication();
        if (app instanceof NgaClientApp) {
            if (((NgaClientApp) app).isNewVersion()) {
                ((NgaClientApp) app).setNewVersion(false);
                new VersionUpgradeDialogFragment().show(getSupportFragmentManager(), null);
            }
        }
    }

    @Override
    protected void onResume() {
        if (mIsNightMode != ThemeManager.getInstance().isNightMode()) {
            finish();
            startActivity(getIntent());
        }
        super.onResume();
    }

    private void initView() {
        FragmentManager fm = getSupportFragmentManager();
        mBoardFragment = (BaseFragment) fm.findFragmentByTag(NavigationDrawerFragment.class.getSimpleName());
        if (mBoardFragment== null) {
            mBoardFragment = new NavigationDrawerFragment();
            fm.beginTransaction().replace(android.R.id.content, mBoardFragment, NavigationDrawerFragment.class.getSimpleName()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.menu_setting:
                startSettingActivity();
                break;
            case R.id.menu_bookmark:
                startFavoriteTopicActivity();
                break;
            case R.id.menu_msg:
                startMessageActivity();
                break;
            case R.id.menu_post:
                startPostActivity(false);
                break;
            case R.id.menu_reply:
                startPostActivity(true);
                break;
            case R.id.menu_about:
                aboutNgaClient();
                break;
            case R.id.menu_search:
                startSearchActivity();
                break;
            case R.id.menu_forward:
                new UrlInputDialogFragment().show(getSupportFragmentManager());
                break;
            case R.id.menu_gun:
                startNotificationActivity();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void startSearchActivity() {
        ARouter.getInstance()
                .build(ARouterConstants.ACTIVITY_SEARCH)
                .navigation(this);
    }

    private void startMessageActivity() {
        ARouter.getInstance()
                .build(ARouterConstants.ACTIVITY_MESSAGE_LIST)
                .navigation(this);
    }

    private void aboutNgaClient() {
       // new AboutClientDialogFragment().show(getSupportFragmentManager());
        startActivity(new Intent(this,AboutActivity.class));
    }

    private void startSettingActivity() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SettingsActivity.class);
        startActivityForResult(intent, ActivityUtils.REQUEST_CODE_SETTING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ActivityUtils.REQUEST_CODE_SETTING && resultCode == Activity.RESULT_OK) {
            recreate();
        } else {
            mBoardFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startNotificationActivity() {
        ARouter.getInstance()
                .build(ARouterConstants.ACTIVITY_NOTIFICATION)
                .navigation(this);
    }

    // NPE问题
    private void startPostActivity(boolean isReply) {
        User user = UserManagerImpl.getInstance().getActiveUser();
        String userName = user != null ? user.getNickName() : "";
        Postcard postcard = ARouter.getInstance()
                .build(ARouterConstants.ACTIVITY_TOPIC_LIST)
                .withString(ParamKey.KEY_AUTHOR, userName);
        if (isReply) {
            postcard.withInt(ParamKey.KEY_SEARCH_POST, 1);
        }
        postcard.navigation(this);
    }

    private void startFavoriteTopicActivity() {
        ARouter.getInstance()
                .build(ARouterConstants.ACTIVITY_TOPIC_LIST)
                .withInt(ParamKey.KEY_FAVOR, 1)
                .navigation(this);
    }


}
