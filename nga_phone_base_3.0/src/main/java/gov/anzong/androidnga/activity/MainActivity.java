package gov.anzong.androidnga.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;

import gov.anzong.androidnga.NgaClientApp;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.arouter.ARouterConstants;
import sp.phone.common.UserManagerImpl;
import sp.phone.forumoperation.ParamKey;
import sp.phone.fragment.BoardFragment;
import sp.phone.fragment.dialog.AboutClientDialogFragment;
import sp.phone.fragment.dialog.ProfileSearchDialogFragment;
import sp.phone.fragment.dialog.UrlInputDialogFragment;
import sp.phone.fragment.dialog.VersionUpgradeDialogFragment;
import sp.phone.mvp.contract.BoardContract;
import sp.phone.mvp.presenter.BoardPresenter;
import sp.phone.theme.ThemeManager;
import sp.phone.util.ActivityUtils;
import sp.phone.util.PermissionUtils;

public class MainActivity extends BaseActivity {

    private boolean mIsNightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setActionBarEnabled(false);
        super.onCreate(savedInstanceState);
        checkPermission();
        checkNewVersion();
        initView();
        mIsNightMode = ThemeManager.getInstance().isNightMode();
        setSwipeBackEnable(false);
    }

    private void checkPermission() {
        if (!PermissionUtils.hasStoragePermission(this)) {
            PermissionUtils.requestStoragePermission(this);
        }
    }

    private void checkNewVersion() {
        NgaClientApp app = (NgaClientApp) getApplication();
        if (app.isNewVersion()) {
            app.setNewVersion(false);
            new VersionUpgradeDialogFragment().show(getSupportFragmentManager(), null);
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
        Fragment fragment = fm.findFragmentByTag(BoardFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new BoardFragment();
            fm.beginTransaction().replace(android.R.id.content, fragment, BoardFragment.class.getSimpleName()).commit();
        }
        new BoardPresenter((BoardContract.View) fragment);
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
                searchProfile();
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

    private void searchProfile() {
        new ProfileSearchDialogFragment().show(getSupportFragmentManager());
    }

    private void startMessageActivity() {
        ARouter.getInstance()
                .build(ARouterConstants.ACTIVITY_MESSAGE_LIST)
                .navigation(this);
    }

    private void aboutNgaClient() {
        new AboutClientDialogFragment().show(getSupportFragmentManager());
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
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startNotificationActivity() {
        ARouter.getInstance()
                .build(ARouterConstants.ACTIVITY_NOTIFICATION)
                .navigation(this);
    }

    // NPE问题
    private void startPostActivity(boolean isReply) {
        String userName = UserManagerImpl.getInstance().getActiveUser().getNickName();
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
