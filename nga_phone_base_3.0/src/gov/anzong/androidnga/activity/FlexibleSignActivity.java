package gov.anzong.androidnga.activity;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import gov.anzong.androidnga.R;
import sp.phone.adapter.ActionBarUserListAdapter;
import sp.phone.adapter.SpinnerUserListAdapter;
import sp.phone.bean.SignData;
import sp.phone.common.UserManagerImpl;
import sp.phone.fragment.SignContainer;
import sp.phone.interfaces.OnChildFragmentRemovedListener;
import sp.phone.interfaces.OnSignPageLoadFinishedListener;
import sp.phone.interfaces.PullToRefreshAttacherOwner;
import sp.phone.utils.NLog;
import sp.phone.utils.ReflectionUtil;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;

public class FlexibleSignActivity extends SwipeBackAppCompatActivity implements
        OnSignPageLoadFinishedListener,
        OnChildFragmentRemovedListener, PullToRefreshAttacherOwner {

    boolean dualScreen = true;
    int flags = 7;
    ArrayAdapter<String> categoryAdapter;
    private String TAG = TopicListActivity.class.getSimpleName();
    private PullToRefreshAttacher mPullToRefreshAttacher;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        this.setContentView(R.layout.sign_activity);// OK
        if (null == findViewById(R.id.item_mission_container)) {
            dualScreen = false;
        }// ok

        PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this, options);
        FragmentManager fm = getSupportFragmentManager();
        Fragment f1 = fm.findFragmentById(R.id.sign_list);// ok
        if (f1 == null) {
            f1 = new SignContainer();
            Bundle args = new Bundle();// (getIntent().getExtras());
            if (null != getIntent().getExtras()) {
                args.putAll(getIntent().getExtras());
            }
            args.putString("url", getIntent().getDataString());
            f1.setArguments(args);
            FragmentTransaction ft = fm.beginTransaction().add(R.id.sign_list,
                    f1);
            // .add(R.id.item_detail_container, f);
            ft.commit();
        }// 生成左边
        Fragment f2 = fm.findFragmentById(R.id.item_mission_container);
        if (null == f2) {
            f1.setHasOptionsMenu(true);
        } else if (!dualScreen) {
            getSupportActionBar().setTitle("签到任务");
            fm.beginTransaction().remove(f2).commit();
            f1.setHasOptionsMenu(true);
        } else {
            f1.setHasOptionsMenu(false);
            f2.setHasOptionsMenu(true);
        }
        setNavigation();
    }

    @TargetApi(11)
    private void setNavigation() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        final SpinnerUserListAdapter categoryAdapter = new ActionBarUserListAdapter(this);

        OnNavigationListener callback = new OnNavigationListener() {

            @Override
            public boolean onNavigationItemSelected(int itemPosition,
                                                    long itemId) {
                UserManagerImpl.getInstance().setActiveUser(itemPosition);
                SignContainer f1 = (SignContainer) getSupportFragmentManager().findFragmentById(R.id.sign_list);
                if (f1 != null) {
                    f1.onCategoryChanged(itemPosition);
                }
                return true;
            }

        };
        actionBar.setListNavigationCallbacks(categoryAdapter, callback);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Fragment f1 = getSupportFragmentManager().findFragmentById(R.id.sign_list);
        f1.onPrepareOptionsMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        ReflectionUtil.actionBar_setDisplayOption(this, flags);
        return false;// super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();// 关闭activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public PullToRefreshAttacher getAttacher() {
        return mPullToRefreshAttacher;
    }

    @Override
    public void OnChildFragmentRemoved(int id) {
        if (id == R.id.item_mission_container) {
            FragmentManager fm = getSupportFragmentManager();
            Fragment f1 = fm.findFragmentById(R.id.sign_list);
            f1.setHasOptionsMenu(true);
            getSupportActionBar().setTitle("签到任务");
        }

    }// 竖屏变横屏就干这个

    @Override
    public void jsonfinishLoad(SignData result) {// 给左边SIGN信息用的
        Fragment SignContainer = getSupportFragmentManager().findFragmentById(
                R.id.sign_list);

        OnSignPageLoadFinishedListener listener = null;
        try {
            listener = (OnSignPageLoadFinishedListener) SignContainer;
            if (listener != null)
                listener.jsonfinishLoad(result);
        } catch (ClassCastException e) {
            NLog.e(TAG, "topicContainer should implements " + OnSignPageLoadFinishedListener.class.getCanonicalName());
        }
    }
}
