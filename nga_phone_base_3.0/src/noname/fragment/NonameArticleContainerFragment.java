package noname.fragment;

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

import gov.anzong.androidnga.R;
import noname.adapter.NonameThreadFragmentAdapter;
import noname.gson.parse.NonameReadResponse;
import noname.interfaces.OnNonameThreadPageLoadFinishedListener;
import noname.interfaces.PagerOwner;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.PreferenceKey;
import sp.phone.fragment.BaseFragment;
import sp.phone.fragment.dialog.GotoDialogFragment;
import sp.phone.interfaces.OnChildFragmentRemovedListener;
import sp.phone.theme.ThemeManager;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.NLog;
import sp.phone.utils.StringUtils;

public class NonameArticleContainerFragment extends BaseFragment implements
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
        View v = inflater.inflate(R.layout.article_viewpager, container, false);
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
        if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
            if (mcontainer != null)
                mcontainer.setBackgroundResource(R.color.night_bg_color);
        }

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
        if (mcontainer != null) {
            if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
                mcontainer.setBackgroundResource(R.color.night_bg_color);
            } else {
                mcontainer.setBackgroundResource(R.color.shit1);
            }
        }
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
    public void onPrepareOptionsMenu(Menu menu) {
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
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.article_menuitem_reply:
                String tid = String.valueOf(this.tid);
                intent.putExtra("prefix", "");
                intent.putExtra("tid", tid);
                intent.putExtra("action", "reply");
                intent.setClass(getActivity(),
                        PhoneConfiguration.getInstance().nonamePostActivityClass);
                startActivity(intent);
                break;
            case R.id.article_menuitem_refresh:
                int current = mViewPager.getCurrentItem();
                ActivityUtils.getInstance().noticeSaying(getActivity());
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
                nightMode(item);
                break;
            case R.id.article_menuitem_back:
            default:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(this).commit();
                OnChildFragmentRemovedListener father = null;
                try {
                    father = (OnChildFragmentRemovedListener) getActivity();
                    father.OnChildFragmentRemoved(getId());
                } catch (ClassCastException e) {
                    NLog.e(TAG, "father activity does not implements interface " + OnChildFragmentRemovedListener.class.getName());

                }
                break;
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
            if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
                mcontainer.setBackgroundResource(R.color.night_bg_color);
            } else {
                mcontainer.setBackgroundResource(R.color.shit1);
            }
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

        DialogFragment df = new GotoDialogFragment();
        df.setArguments(args);

        FragmentManager fm = getActivity().getSupportFragmentManager();

        Fragment prev = fm.findFragmentByTag(GOTO_TAG);
        if (prev != null) {
            fm.beginTransaction().remove(prev).commit();
        }
        df.show(fm, GOTO_TAG);

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
