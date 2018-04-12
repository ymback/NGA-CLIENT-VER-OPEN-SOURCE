package noname.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.SwipeBackAppCompatActivity;
import noname.adapter.TabsAdapter;
import noname.adapter.ThreadFragmentAdapter;
import noname.fragment.NonameArticleListFragment;
import noname.gson.parse.NonameReadResponse;
import noname.interfaces.OnNonameThreadPageLoadFinishedListener;
import noname.interfaces.PagerOwner;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.PreferenceKey;
import sp.phone.fragment.dialog.GotoDialogFragment;
import sp.phone.interfaces.PullToRefreshAttacherOwner;
import sp.phone.theme.ThemeManager;
import sp.phone.util.ActivityUtils;
import sp.phone.util.NLog;
import sp.phone.util.StringUtils;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;

public class NonameArticleListActivity extends SwipeBackAppCompatActivity
        implements PagerOwner, OnNonameThreadPageLoadFinishedListener,
        PullToRefreshAttacherOwner, PreferenceKey {
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
        return R.layout.noname_pagerview_article_list;
        // return R.layout.noname_article_viewpager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getViewId());

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
            if (tabhost != null) {
                tabhost.setup();
                mTabsAdapter = new TabsAdapter(this, tabhost, mViewPager,
                        NonameArticleListFragment.class);
            } else {
                mTabsAdapter = new ThreadFragmentAdapter(this,
                        getSupportFragmentManager(), mViewPager,
                        NonameArticleListFragment.class);
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
            PullToRefreshAttacherOwner attacherOwner = (PullToRefreshAttacherOwner) this;
            attacher = attacherOwner.getAttacher();

        } catch (ClassCastException e) {
            NLog.e(TAG,
                    "father activity should implement PullToRefreshAttacherOwner");
        }

        PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
        options.refreshScrollDistance = 0.3f;
        options.refreshOnUp = true;
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this, options);
        try {
            PullToRefreshAttacherOwner attacherOwner = (PullToRefreshAttacherOwner) this;
            attacher = attacherOwner.getAttacher();

        } catch (ClassCastException e) {
            NLog.e(TAG, "father activity should implement PullToRefreshAttacherOwner");
        }

        ActivityUtils.getInstance().noticeSaying(this);

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
            ActivityUtils.getInstance().noticeSaying(this);
        else
            transformer.setRefreshingText(ActivityUtils.getSaying());
        if (attacher != null)
            attacher.setRefreshing(true);
    }

    private int getUrlParameter(String url, String paraName) {
        if (StringUtils.isEmpty(url)) {
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
            NLog.e(TAG, "invalid url:" + url);
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
        inflater.inflate(R.menu.nonamearticlelist_menu, menu);
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
                break;
            case R.id.article_menuitem_refresh:
                int current = mViewPager.getCurrentItem();
                ActivityUtils.getInstance().noticeSaying(this);
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
               // changeNightMode(item);
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
        NLog.i(TAG, String.valueOf(exactCount));
        if (mTabsAdapter.getCount() != exactCount) {
            mTabsAdapter.setCount(exactCount);
        }

        title = data.data.title;
        if (!StringUtils.isEmpty(title)) {
            getSupportActionBar().setTitle(StringUtils.unEscapeHtml(title));
        } else {
            getSupportActionBar().setTitle("无题");

        }

        attacher.setRefreshComplete();
    }

}
