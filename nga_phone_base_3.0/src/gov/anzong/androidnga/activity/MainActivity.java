package gov.anzong.androidnga.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import gov.anzong.androidnga.NgaClientApp;
import gov.anzong.androidnga.R;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.ThemeManager;
import sp.phone.common.User;
import sp.phone.common.UserManagerImpl;
import sp.phone.fragment.BoardFragment;
import sp.phone.fragment.dialog.AboutClientDialogFragment;
import sp.phone.fragment.dialog.ProfileSearchDialogFragment;
import sp.phone.fragment.dialog.UrlInputDialogFragment;
import sp.phone.fragment.dialog.VersionUpgradeDialogFragment;
import sp.phone.mvp.contract.BoardContract;
import sp.phone.mvp.presenter.BoardPresenter;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.PermissionUtils;

public class MainActivity extends BaseActivity {

    private BoardContract.Presenter mPresenter;

    private boolean mIsNightMode;

    private PhoneConfiguration mConfig = PhoneConfiguration.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideActionBar();
        super.onCreate(savedInstanceState);
        prepare();
        initView();
        mIsNightMode = ThemeManager.getInstance().isNightMode();

    }

    private void prepare() {
        checkNewVersion();
        if (!PermissionUtils.hasStoragePermission(this)) {
            PermissionUtils.requestStoragePermission(this);
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

    //OK
    private void checkNewVersion() {
        NgaClientApp app = (NgaClientApp) getApplication();
        if (app.isNewVersion() || true) {
            app.setNewVersion(false);
            new VersionUpgradeDialogFragment().show(getSupportFragmentManager(), null);
        }
    }

    private void initView() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(BoardFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new BoardFragment();
            fm.beginTransaction().replace(android.R.id.content, fragment, BoardFragment.class.getSimpleName()).commit();
        }
        mPresenter = new BoardPresenter((BoardContract.View) fragment);
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
                jumpToSetting();
                break;
            case R.id.menu_bookmark:
                jumpToBookmark();
                break;
            case R.id.menu_msg:
                myMessage();
                break;
            case R.id.menu_post:
                jumpToMyPost(false);
                break;
            case R.id.menu_reply:
                jumpToMyPost(true);
                break;
            case R.id.menu_about:
                aboutNgaClient();
                break;
            case R.id.menu_noname:
                noname();
                break;
            case R.id.menu_search:
                searchProfile();
                break;
            case R.id.menu_location:
                jumpToNearby();
                break;
            case R.id.menu_forward:
                new UrlInputDialogFragment().show(getSupportFragmentManager());
                break;
            case R.id.menu_gun:
                jumpToRecentReply();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void searchProfile() {
        new ProfileSearchDialogFragment().show(getSupportFragmentManager());
    }

    private void myMessage() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, mConfig.messageActivityClass);
        startActivity(intent);
    }

    private void noname() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, mConfig.nonameActivityClass);
        startActivity(intent);
    }

    private void aboutNgaClient() {
        new AboutClientDialogFragment().show(getSupportFragmentManager());
    }

    private void jumpToSetting() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SettingsActivity.class);
        startActivityForResult(intent, ActivityUtils.REQUEST_CODE_SETTING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ActivityUtils.REQUEST_CODE_SETTING && resultCode == Activity.RESULT_OK) {
            mPresenter.notifyDataSetChanged();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void jumpToNearby() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, NearbyUserActivity.class);
        startActivity(intent);
    }

    private void jumpToRecentReply() {
        Intent intent = new Intent();
        intent.putExtra("recentmode", "recentmode");
        intent.setClass(MainActivity.this, mConfig.recentReplyListActivityClass);
        startActivity(intent);
    }

    private void jumpToMyPost(boolean isReply) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, mConfig.topicActivityClass);
        User user = UserManagerImpl.getInstance().getActiveUser();

        if (user == null) {
            showToast("你还没有登录");
            return;
        }
        String userName = user.getNickName();
        if (isReply) {
            intent.putExtra("author", userName);
            intent.putExtra("searchpost", 1);
        } else {
            intent.putExtra("author", userName);
        }
        startActivity(intent);
    }

    private void jumpToBookmark() {
        Intent intent_bookmark = new Intent(this, mConfig.topicActivityClass);
        intent_bookmark.putExtra("favor", 1);
        startActivity(intent_bookmark);
    }


}
