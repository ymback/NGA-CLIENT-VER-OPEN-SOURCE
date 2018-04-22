package com.noname.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.noname.R;
import com.noname.activity.NonamePostActivity;
import com.noname.adapter.NonameThreadFragmentAdapter;
import com.noname.common.PreferenceKey;
import com.noname.gson.parse.NonameReadResponse;
import com.noname.interfaces.OnChildFragmentRemovedListener;
import com.noname.interfaces.OnNonameThreadPageLoadFinishedListener;
import com.noname.interfaces.PagerOwner;
import com.noname.util.ActivityUtils;
import com.noname.util.NLog;
import com.noname.util.StringUtils;


public class NonameArticleContainerFragment extends Fragment implements
        OnNonameThreadPageLoadFinishedListener, PreferenceKey, PagerOwner {
    private static final String TAG = "NonameArtContainFrag";
    private static final String GOTO_TAG = "goto";
    // TabHost tabhost;
    ViewPager mViewPager;
    NonameThreadFragmentAdapter mTabsAdapter;
    int tid;
    String title;
    String url;
    ViewGroup mcontainer;
    OnNonameArticleContainerFragmentListener mCallback;

    public NonameArticleContainerFragment() {
        super();
    }

    public static NonameArticleContainerFragment create(int tid) {
        NonameArticleContainerFragment f = new NonameArticleContainerFragment();
        Bundle args = new Bundle();
        args.putInt("tid", tid);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.noname_article_viewpager, container, false);
        mViewPager = (ViewPager) v.findViewById(R.id.pager);

        int pageFromUrl = 0;
        url = getArguments().getString("url");
        if (null != url) {
            tid = this.getUrlParameter(url, "tid");
            pageFromUrl = this.getUrlParameter(url, "page");
        } else {
            tid = this.getArguments().getInt("tid", 0);
        }

        mcontainer = container;

        mTabsAdapter = new NonameThreadFragmentAdapter(getActivity(),
                getChildFragmentManager(), mViewPager,
                NonameArticleListFragment.class);

        mTabsAdapter.setArgument("id", tid);

        // ActivityUtils.getInstance().noticeSaying(getActivity());

        if (savedInstanceState != null) {
            int pageCount = savedInstanceState.getInt("pageCount");
            if (pageCount != 0) {
                mTabsAdapter.setCount(pageCount);
                mViewPager.setCurrentItem(savedInstanceState.getInt("tab"));
            }
        } else if (pageFromUrl != 0) {
            mTabsAdapter.setCount(pageFromUrl + 1);
            mViewPager.setCurrentItem(pageFromUrl);
        } else {
            mTabsAdapter.setCount(1);
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pageCount", mTabsAdapter.getCount());
        outState.putInt("tab", mViewPager.getCurrentItem());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnNonameArticleContainerFragmentListener) activity;
        } catch (ClassCastException e) {
        }
    }

    public void changemode() {
        if (mTabsAdapter != null) {
            try {
                ((NonameArticleListFragment) mTabsAdapter.getRegisteredFragment(mViewPager.getCurrentItem())).modechange();
                ((NonameArticleListFragment) mTabsAdapter.getRegisteredFragment(mViewPager.getCurrentItem() + 1)).modechange();
                ((NonameArticleListFragment) mTabsAdapter.getRegisteredFragment(mViewPager.getCurrentItem() - 1)).modechange();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.nonamearticlelist_menu, menu);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        int i = item.getItemId();
        if (i == R.id.article_menuitem_reply) {
            String tid = String.valueOf(this.tid);
            intent.putExtra("prefix", "");
            intent.putExtra("tid", tid);
            intent.putExtra("action", "reply");
            intent.setClass(getActivity(),
                    NonamePostActivity.class);
            startActivity(intent);

        } else if (i == R.id.article_menuitem_refresh) {
            int current = mViewPager.getCurrentItem();
            ActivityUtils.getInstance().noticeSaying(getActivity());
            mViewPager.setAdapter(mTabsAdapter);
            mViewPager.setCurrentItem(current);

        } else if (i == R.id.article_menuitem_lock) {
            handleLockOrientation(item);

        } else if (i == R.id.goto_floor) {
            createGotoDialog();

        } else if (i == R.id.night_mode) {
            nightMode(item);

        } else {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .remove(this).commit();
            OnChildFragmentRemovedListener father = null;
            try {
                father = (OnChildFragmentRemovedListener) getActivity();
                father.OnChildFragmentRemoved(getId());
            } catch (ClassCastException e) {
                NLog.e(TAG, "father activity does not implements interface " + OnChildFragmentRemovedListener.class.getName());

            }

        }
        return true;
    }

    private void nightMode(final MenuItem menu) {
        //changeNightMode(menu);
        if (mTabsAdapter != null) {
        }
        try {
            ((NonameArticleListFragment) mTabsAdapter.getRegisteredFragment(mViewPager.getCurrentItem())).modechange();
            ((NonameArticleListFragment) mTabsAdapter.getRegisteredFragment(mViewPager.getCurrentItem() + 1)).modechange();
            ((NonameArticleListFragment) mTabsAdapter.getRegisteredFragment(mViewPager.getCurrentItem() - 1)).modechange();
        } catch (Exception e) {

        }
        if (mCallback != null)
            mCallback.onModeChanged();
        if (mcontainer != null) {
        }

    }

    @SuppressWarnings({"unused", "deprecation"})
    private void handleLockOrientation(MenuItem item) {

    }

    @Override
    public void finishLoad(NonameReadResponse data) {
        int exactCount = data.data.totalpage;
        if (mTabsAdapter.getCount() != exactCount) {
            mTabsAdapter.setCount(exactCount);
        }
        title = data.data.posts[0].title;

    }

    private void createGotoDialog() {

        int count = mTabsAdapter.getCount();
        Bundle args = new Bundle();
        args.putInt("count", count);
//
//        DialogFragment df = new GotoDialogFragment();
//        df.setArguments(args);
//
//        FragmentManager fm = getActivity().getSupportFragmentManager();
//
//        Fragment prev = fm.findFragmentByTag(GOTO_TAG);
//        if (prev != null) {
//            fm.beginTransaction().remove(prev).commit();
//        }
//        df.show(fm, GOTO_TAG);

    }

    @Override
    public int getCurrentPage() {
        return mViewPager.getCurrentItem() + 1;

    }

    @Override
    public void setCurrentItem(int index) {
        mViewPager.setCurrentItem(index);

    }

    // Container Activity must implement this interface
    public interface OnNonameArticleContainerFragmentListener {
        public void onModeChanged();
    }

}
