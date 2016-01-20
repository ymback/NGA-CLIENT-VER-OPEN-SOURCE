package gov.anzong.meizi;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import gov.anzong.androidnga.R;
import gov.anzong.meizi.MeiziCategory.MeiziCategoryItem;
import gov.anzong.meizi.MeiziCategoryFragment.OnMeiziSelectedListener;
import sp.phone.interfaces.OnChildFragmentRemovedListener;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ThemeManager;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;

public class MeiziMainActivity extends ActionBarActivity implements
        OnMeiziSelectedListener,
        OnChildFragmentRemovedListener, PullToRefreshAttacherOnwer {

    int flags = 7;
    boolean dualScreen = true;
    MeiziCategoryFragment mMeiziCategoryFragment;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private PullToRefreshAttacher mPullToRefreshAttacher;
    private MeiziCategoryItem mCategoryItem;
    private MeiziCategoryFragment mCategoryFragment;
    private View view;
    private MeiziDrawerAdapter mAdapter;
    private ListView mDrawerList;
    private int mActiveposition = 0;
    private FragmentManager fm;
    private MeiziTopicFragment mTopicFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        Toast.makeText(this, "本软件是浏览器,所有内容均来自网络,和本软件无关,也不代表开发者支持该内容,",
                Toast.LENGTH_LONG).show();
    }

    private void initView() {
        mMeiziCategoryFragment = new MeiziCategoryFragment();
        fm = getSupportFragmentManager();
        view = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        setContentView(view);
        PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this, options);

        if (null == view.findViewById(R.id.left_drawer)) {

            dualScreen = false;
        }
        mCategoryFragment = (MeiziCategoryFragment) fm
                .findFragmentById(R.id.content_frame);
        if (mCategoryFragment == null) {
            mCategoryFragment = mMeiziCategoryFragment.setInitData(
                    MeiziCategory.ITEMS[0], this);
            FragmentTransaction ft = fm.beginTransaction().add(
                    R.id.content_frame, mCategoryFragment);
            // .add(R.id.item_detail_container, f);
            ft.commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(MeiziCategory.ITEMS[0].getName());
        mTopicFragment = (MeiziTopicFragment) fm
                .findFragmentById(R.id.left_drawer);
        if (null == mTopicFragment) {
            mCategoryFragment.setHasOptionsMenu(true);
        } else if (!dualScreen) {
            getSupportActionBar().setTitle("主题列表");
            fm.beginTransaction().remove(mTopicFragment).commit();
            mCategoryFragment.setHasOptionsMenu(true);
        } else {
            mCategoryFragment.setHasOptionsMenu(false);
            mTopicFragment.setHasOptionsMenu(true);
        }

        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        mDrawerLayout.setScrimColor(Color.argb(100, 0, 0, 0));
        mDrawerList = (ListView) view.findViewById(R.id.listView);


        mDrawerList.setItemChecked(0, true);
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mDrawerList.setItemChecked(position, true);
                mActiveposition = position;
                setCategory(mAdapter.getItem(position));
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open,
                R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("赞美JJMIMMYS");
                if (ActivityUtil.isLessThan_3_0())
                    supportInvalidateOptionsMenu();
                else
                    invalidateOptionsMenu();
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                getSupportActionBar().setTitle(mAdapter.getItem(mActiveposition).getName());
                if (ActivityUtil.isLessThan_3_0())
                    supportInvalidateOptionsMenu();
                else
                    invalidateOptionsMenu();
            }

        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    protected void onResume() {
        int orentation = ThemeManager.getInstance().screenOrentation;
        if (orentation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                || orentation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(orentation);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        if (PhoneConfiguration.getInstance().fullscreen) {
            ActivityUtil.getInstance().setFullScreen(view);
        }
        mAdapter = new MeiziDrawerAdapter(this, mDrawerList);
        mDrawerList.setAdapter(mAdapter);
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        Glide.get(this).clearMemory();
        super.onDestroy();
    }

    public void setCategory(MeiziCategoryItem categoryItem) {
        if (categoryItem == null || mCategoryItem == categoryItem) {
            return;
        }

        mCategoryItem = categoryItem;

        mCategoryFragment = mMeiziCategoryFragment.setInitData(
                categoryItem, this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, mCategoryFragment).commit();
        mDrawerLayout.closeDrawer(mDrawerList);
        mCategoryFragment.setHasOptionsMenu(true);
    }

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // ReflectionUtil.actionBar_setDisplayOption(this, flags);
    // return true;
    // }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    public void onMeiziSelect(int position) {
        // The user selected the headline of an article from the
        // HeadlinesFragment
        // Do something here to display that article
    }

    @Override
    public void onMeiziSelect(MeiziUrlData meiziM) {
        // TODO Auto-generated method stub
        if (meiziM != null) {
            String topicUrl = meiziM.TopicUrl;
            if (!dualScreen) {// 非平板
                MeiziNavigationUtil.startTopicActivity(this, topicUrl);
            } else {
                mTopicFragment = new MeiziTopicFragment();
                Bundle bundle = new Bundle();
                bundle.putString(MeiziTopicFragment.ARG_KEY_URL, topicUrl);
                mTopicFragment.setArguments(bundle);
                fm.beginTransaction().replace(R.id.left_drawer, mTopicFragment)
                        .commit();
                mCategoryFragment.setHasOptionsMenu(false);
                mTopicFragment.setHasOptionsMenu(true);
            }
        }
    }

    @Override
    public void OnChildFragmentRemoved(int id) {
        // TODO Auto-generated method stub
        if (id == R.id.left_drawer) {
            mCategoryFragment = (MeiziCategoryFragment) fm
                    .findFragmentById(R.id.content_frame);
            mCategoryFragment.setHasOptionsMenu(true);
            getSupportActionBar().setTitle(
                    MeiziCategory.ITEMS[0].getName());
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    @Override
    public PullToRefreshAttacher getAttacher() {
        return mPullToRefreshAttacher;
    }

}
