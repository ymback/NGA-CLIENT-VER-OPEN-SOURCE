package gov.anzong.androidnga.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TabHost;

import gov.anzong.androidnga.R;
import noname.gson.parse.NonameReadResponse;
import sp.phone.adapter.TabsAdapter;
import sp.phone.adapter.ThreadFragmentAdapter;
import sp.phone.bean.PerferenceConstant;
import sp.phone.fragment.GotoDialogFragment;
import sp.phone.fragment.NonameArticleListFragment;
import sp.phone.fragment.NonameArticleListFragmentNew;
import sp.phone.interfaces.OnNonameThreadPageLoadFinishedListener;
import sp.phone.interfaces.PagerOwnner;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;

public class NonameArticleListActivity extends SwipeBackAppCompatActivity
        implements PagerOwnner, OnNonameThreadPageLoadFinishedListener,
        PullToRefreshAttacherOnwer, PerferenceConstant {
    private static final String TAG = "ArticleListActivity";
    private static final String GOTO_TAG = "goto";
    TabHost tabhost;
    ViewPager mViewPager;
    ThreadFragmentAdapter mTabsAdapter;
    int tid;
    String title;
    PullToRefreshAttacher attacher = null;
    private PullToRefreshAttacher mPullToRefreshAttacher;

    protected int getViewId() {
        return R.layout.pagerview_article_list;
        // return R.layout.article_viewpager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getViewId());

        if (PhoneConfiguration.getInstance().uploadLocation
                && PhoneConfiguration.getInstance().location == null) {
            ActivityUtil.reflushLocation(this);
        }

        mViewPager = (ViewPager) findViewById(R.id.pager);
        int pageFromUrl = 0;
        String url = this.getIntent().getDataString();
        if (null != url) {
            tid = this.getUrlParameter(url, "tid");
            pageFromUrl = this.getUrlParameter(url, "page");
        } else {
            tid = this.getIntent().getIntExtra("tid", 0);
        }

        tabhost = (TabHost) findViewById(android.R.id.tabhost);
        if (PhoneConfiguration.getInstance().kitwebview) {
            if (tabhost != null) {
                tabhost.setup();
                mTabsAdapter = new TabsAdapter(this, tabhost, mViewPager,
                        NonameArticleListFragmentNew.class);
            } else {
                mTabsAdapter = new ThreadFragmentAdapter(this,
                        getSupportFragmentManager(), mViewPager,
                        NonameArticleListFragmentNew.class);
            }
        } else {
            if (tabhost != null) {
                tabhost.setup();
                mTabsAdapter = new TabsAdapter(this, tabhost, mViewPager,
                        NonameArticleListFragment.class);
            } else {
                mTabsAdapter = new ThreadFragmentAdapter(this,
                        getSupportFragmentManager(), mViewPager,
                        NonameArticleListFragment.class);
            }
        }

        mTabsAdapter.setArgument("id", tid);

        if (savedInstanceState != null) {
            int pageCount = savedInstanceState.getInt("pageCount");
            if (pageCount != 0) {
                mTabsAdapter.setCount(pageCount);
                mViewPager.setCurrentItem(savedInstanceState.getInt("tab"));
            }

        } else if (0 != getUrlParameter(url, "page")) {

            mTabsAdapter.setCount(pageFromUrl);
            mViewPager.setCurrentItem(pageFromUrl);
        }
        try {
            PullToRefreshAttacherOnwer attacherOnwer = (PullToRefreshAttacherOnwer) this;
            attacher = attacherOnwer.getAttacher();

        } catch (ClassCastException e) {
            Log.e(TAG,
                    "father activity should implement PullToRefreshAttacherOnwer");
        }

        PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
        options.refreshScrollDistance = 0.3f;
        options.refreshOnUp = true;
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this, options);
        try {
            PullToRefreshAttacherOnwer attacherOnwer = (PullToRefreshAttacherOnwer) this;
            attacher = attacherOnwer.getAttacher();

        } catch (ClassCastException e) {
            Log.e(TAG,
                    "father activity should implement PullToRefreshAttacherOnwer");
        }

        if (PhoneConfiguration.getInstance().fullscreen) {
            refresh_saying();
        } else {
            ActivityUtil.getInstance().noticeSaying(this);
        }

    }

    private void refresh_saying() {
        DefaultHeaderTransformer transformer = null;

        if (attacher != null) {
            uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.HeaderTransformer headerTransformer;
            headerTransformer = attacher.getHeaderTransformer();
            if (headerTransformer != null
                    && headerTransformer instanceof DefaultHeaderTransformer)
                transformer = (DefaultHeaderTransformer) headerTransformer;
        } else {
        }

        if (transformer == null)
            ActivityUtil.getInstance().noticeSaying(this);
        else
            transformer.setRefreshingText(ActivityUtil.getSaying());
        if (attacher != null)
            attacher.setRefreshing(true);
    }

    private int getUrlParameter(String url, String paraName) {
        if (StringUtil.isEmpty(url)) {
            return 0;
        }
        final String pattern = paraName + "=";
        int start = url.indexOf(pattern);
        if (start == -1)
            return 0;
        start += pattern.length();
        int end = url.indexOf("&", start);
        if (end == -1)
            end = url.length();
        String value = url.substring(start, end);
        int ret = 0;
        try {
            ret = Integer.parseInt(value);
        } catch (Exception e) {
            Log.e(TAG, "invalid url:" + url);
        }

        return ret;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putInt("pageCount", mTabsAdapter.getCount());
        outState.putInt("tab", mViewPager.getCurrentItem());
        // outState.putInt("tid",tid);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (PhoneConfiguration.getInstance().HandSide == 1) {// lefthand
            int flag = PhoneConfiguration.getInstance().getUiFlag();
            if (flag == 1 || flag == 3 || flag == 5 || flag == 7) {// 文章列表，UIFLAG为1或者1+2或者1+4或者1+2+4
                inflater.inflate(R.menu.nonamearticlelist_menu_left, menu);
            } else {
                inflater.inflate(R.menu.nonamearticlelist_menu, menu);
            }
        } else {
            inflater.inflate(R.menu.nonamearticlelist_menu, menu);
        }
        final int flags = ThemeManager.ACTION_BAR_FLAG;

        MenuItem lock = menu.findItem(R.id.article_menuitem_lock);
        int orentation = ThemeManager.getInstance().screenOrentation;
        if (orentation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                || orentation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            lock.setTitle(R.string.unlock_orientation);
            lock.setIcon(R.drawable.ic_menu_always_landscape_portrait);

        }

        ReflectionUtil.actionBar_setDisplayOption(this, flags);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu.findItem(R.id.night_mode) != null) {
            if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
                menu.findItem(R.id.night_mode).setIcon(
                        R.drawable.ic_action_brightness_high);
                menu.findItem(R.id.night_mode).setTitle(
                        R.string.change_daily_mode);
            } else {
                menu.findItem(R.id.night_mode).setIcon(
                        R.drawable.ic_action_bightness_low);
                menu.findItem(R.id.night_mode).setTitle(
                        R.string.change_night_mode);
            }
        }
        // getSupportMenuInflater().inflate(R.menu.book_detail, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.article_menuitem_reply:
                // if(articleAdpater.getData() == null)
                // return false;
                String tid = String.valueOf(this.tid);
                intent.putExtra("prefix", "");
                intent.putExtra("tid", tid);
                intent.putExtra("action", "reply");
                intent.setClass(this,
                        PhoneConfiguration.getInstance().nonamePostActivityClass);
                startActivity(intent);
                if (PhoneConfiguration.getInstance().showAnimation) {
                    overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                }
                break;
            case R.id.article_menuitem_refresh:
                int current = mViewPager.getCurrentItem();
                if (PhoneConfiguration.getInstance().fullscreen) {
                    refresh_saying();
                } else {
                    ActivityUtil.getInstance().noticeSaying(this);
                }
                mViewPager.setAdapter(mTabsAdapter);
                mViewPager.setCurrentItem(current);

                break;
            case R.id.article_menuitem_lock:
                handleLockOrientation(item);
                break;
            case R.id.goto_floor:
                createGotoDialog();
                break;
            case R.id.night_mode://OK
                changeNightMode(item);
                break;
            case R.id.article_menuitem_back:
            default:
                finish();
                break;
        }
        return true;

    }

    @SuppressWarnings({"unused", "deprecation"})
    private void handleLockOrientation(MenuItem item) {
        int preOrentation = ThemeManager.getInstance().screenOrentation;
        int newOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        ImageButton compat_item = null;// getActionItem(R.id.actionbar_compat_item_lock);

        if (preOrentation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                || preOrentation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            // restore
            // int newOrientation = ActivityInfo.SCREEN_ORIENTATION_USER;
            ThemeManager.getInstance().screenOrentation = newOrientation;

            setRequestedOrientation(newOrientation);
            item.setTitle(R.string.lock_orientation);
            item.setIcon(R.drawable.ic_lock_screen);
            if (compat_item != null)
                compat_item.setImageResource(R.drawable.ic_lock_screen);

        } else {
            newOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            Display dis = getWindowManager().getDefaultDisplay();
            // Point p = new Point();
            // dis.getSize(p);
            if (dis.getWidth() < dis.getHeight()) {
                newOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            }

            ThemeManager.getInstance().screenOrentation = newOrientation;
            setRequestedOrientation(newOrientation);
            item.setTitle(R.string.unlock_orientation);
            item.setIcon(R.drawable.ic_menu_always_landscape_portrait);
            if (compat_item != null)
                compat_item
                        .setImageResource(R.drawable.ic_menu_always_landscape_portrait);
        }

        SharedPreferences share = getSharedPreferences(PERFERENCE,
                MODE_MULTI_PROCESS);
        Editor editor = share.edit();
        editor.putInt(SCREEN_ORENTATION, newOrientation);
        editor.apply();

    }

	/*
     * private ImageButton getActionItem(int id){ View actionbar_compat =
	 * findViewById(R.id.actionbar_compat); View ret = null; if(actionbar_compat
	 * != null) { ret = actionbar_compat.findViewById(id); } return
	 * (ImageButton) ret; }
	 */

    private void createGotoDialog() {

        int count = mTabsAdapter.getCount();
        Bundle args = new Bundle();
        args.putInt("count", count);

        DialogFragment df = new GotoDialogFragment();
        df.setArguments(args);

        FragmentManager fm = getSupportFragmentManager();

        Fragment prev = fm.findFragmentByTag(GOTO_TAG);
        if (prev != null) {
            fm.beginTransaction().remove(prev).commit();
        }
        df.show(fm, GOTO_TAG);

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
            ActivityUtil.getInstance().setFullScreen(mViewPager);
        }
        super.onResume();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        return super.onContextItemSelected(item);
    }

	/*
     * public ThreadFragmentAdapter getmTabsAdapter() { return mTabsAdapter; }
	 */

    @Override
    public int getCurrentPage() {

        return mViewPager.getCurrentItem() + 1;
    }

    @Override
    public void setCurrentItem(int index) {
        mViewPager.setCurrentItem(index);
    }

    @Override
    public PullToRefreshAttacher getAttacher() {
        // TODO Auto-generated method stub
        return mPullToRefreshAttacher;
    }

    @Override
    public void finishLoad(NonameReadResponse data) {
        // TODO Auto-generated method stub

        int exactCount = data.data.totalpage;
        Log.i(TAG, String.valueOf(exactCount));
        if (mTabsAdapter.getCount() != exactCount) {
            mTabsAdapter.setCount(exactCount);
        }

        title = data.data.title;
        if (!StringUtil.isEmpty(title)) {
            getSupportActionBar().setTitle(StringUtil.unEscapeHtml(title));
        } else {
            getSupportActionBar().setTitle("无题");

        }

        attacher.setRefreshComplete();
    }

}
