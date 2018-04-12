package noname.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import sp.phone.util.NLog;

public class TabsAdapter extends ThreadFragmentAdapter implements
        TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
    static final String TAG = TabsAdapter.class.getSimpleName();
    static final int MAX_TAB = 5;
    private final Context mContext;
    private final TabHost mTabHost;
    private final ViewPager mViewPager;
    //private final int id;//fid for topiclist, tid for topic list.
    //private int pid = 0;
    //private int authorid = 0;
    private final Class<?> clss;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    //private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
    private int offset = 0;
    private int pageCount = 0;
    private Bundle arguments = new Bundle();

    public TabsAdapter(FragmentActivity activity, TabHost tabHost,
                       ViewPager pager, Class<?> FragmentClass) {
        super(activity, activity.getSupportFragmentManager(), pager, FragmentClass);
        mContext = activity;
        mTabHost = tabHost;
        mViewPager = pager;
        setCount(1);
        mTabHost.setOnTabChangedListener(this);
        mViewPager.setOnPageChangeListener(this);
        //this.id = id;
        this.clss = FragmentClass;

        mViewPager.setAdapter(this);
    }

    public void addTab(TabHost.TabSpec tabSpec) {
        tabSpec.setContent(new DummyTabFactory(mContext));
        mTabHost.addTab(tabSpec);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return pageCount;//mTabs.size();
    }

    public void setCount(int pageCount) {
        //NLog.i(TAG, "setCount current page count:" + this.pageCount );
        this.pageCount = pageCount;
        int tabCount = mTabHost.getTabWidget().getChildCount();
        //NLog.i(TAG, "setCount current tab count:" + tabCount );
        int tabsToDisplay = MAX_TAB < pageCount ? MAX_TAB : pageCount;
        //NLog.i(TAG, "setCount set page count  Count to:" + pageCount);
        if (tabCount < tabsToDisplay) {
            for (int i = tabCount; i < tabsToDisplay; ++i) {
                TextView tv = new TextView(mContext);
                tv.setTextSize(20);
                //	NLog.i(TAG, "add tab:" + (i+1));
                String tag = String.valueOf(i + 1);
                tv.setText(tag);
                tv.setGravity(Gravity.CENTER);
                this.addTab(mTabHost.newTabSpec(tag).setIndicator(tv));
            }
        }

        if (pageCount == 1) {
            TextView v = (TextView) mTabHost.getTabWidget().getChildAt(0);
            v.setTextColor(mContext.getResources().getColor(R.color.holo_blue_light));
        }

        this.notifyDataSetChanged();
    }

    public void setArgument(String key, int value) {
        arguments.putInt(key, value);
    }

    public void setArgument(String key, String value) {
        arguments.putString(key, value);
    }


    @Override
    public Fragment getItem(int position) {
        NLog.i(TAG, "getItem " + position + "current offset=" + offset);
        Bundle args = new Bundle(arguments);
        args.putInt("page", position);
        NLog.i(TAG, "again+" + String.valueOf(position));
        Fragment f = Fragment.instantiate(mContext, clss.getName(), args);

        return f;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void onTabChanged(String tabId) {
        int position = mTabHost.getCurrentTab();

        NLog.d(TAG, "onTabChanged:" + tabId + ",current offset=" + offset);


        TextView v = (TextView) mTabHost.getCurrentTabView();
        int defaultColor = mContext.getResources().getColor(R.color.hint_foreground_holo_light);
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            v = (TextView) mTabHost.getTabWidget().getChildAt(i);
            //v.setText(String.valueOf(i+offset+1));
            if (mTabHost.getCurrentTab() == i) {
                NLog.d(TAG, "set tab:" + (i + offset + 1) + "to holo blue");
                v.setTextColor(mContext.getResources().getColor(R.color.holo_blue_light));
            } else {
                NLog.d(TAG, "set tab:" + (i + offset + 1) + "to default color");
                v.setTextColor(defaultColor);
            }
        }
        mViewPager.setCurrentItem(position + offset);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        // Unfortunately when TabHost changes the current tab, it kindly
        // also takes care of putting focus on it when not in touch mode.
        // The jerk.
        // This hack tries to prevent this from pulling focus out of our
        // ViewPager.
        NLog.d(TAG, "onPageSelected:" + position);
        TabWidget widget = mTabHost.getTabWidget();
        int oldFocusability = widget.getDescendantFocusability();
        widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        offset = position / MAX_TAB * MAX_TAB;
        NLog.d(TAG, "onPageSelected current offset=" + offset);
        if (offset + MAX_TAB > pageCount && offset > 0) {
            offset = pageCount - MAX_TAB;
            NLog.i(TAG, "onPageSelected current offset=" + offset);

        }

        TextView v = null;
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            v = (TextView) mTabHost.getTabWidget().getChildAt(i);
            v.setText(String.valueOf(i + offset + 1));
        }

        mTabHost.setCurrentTab(position - offset);
        widget.setDescendantFocusability(oldFocusability);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    static class DummyTabFactory implements TabHost.TabContentFactory {
        private final Context mContext;

        public DummyTabFactory(Context context) {
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }


}