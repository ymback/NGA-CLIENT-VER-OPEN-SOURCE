package com.noname.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.noname.R;
import com.noname.adapter.NonameTopicListAdapter;
import com.noname.fragment.NonameArticleContainerFragment;
import com.noname.fragment.NonameTopiclistContainer;
import com.noname.gson.parse.NonameReadResponse;
import com.noname.gson.parse.NonameThreadBody;
import com.noname.gson.parse.NonameThreadResponse;
import com.noname.interfaces.EnterJsonNonameArticle;
import com.noname.interfaces.OnChildFragmentRemovedListener;
import com.noname.interfaces.OnNonameThreadPageLoadFinishedListener;
import com.noname.interfaces.OnNonameTopListLoadFinishedListener;
import com.noname.interfaces.PagerOwner;
import com.noname.interfaces.PullToRefreshAttacherOwner;
import com.noname.util.NLog;
import com.noname.util.ReflectionUtil;
import com.noname.util.StringUtils;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;

public class FlexibleNonameTopicListActivity extends AppCompatActivity
        implements
        OnNonameTopListLoadFinishedListener,
        OnItemClickListener,
        OnNonameThreadPageLoadFinishedListener,
        PagerOwner,
        OnChildFragmentRemovedListener,
        PullToRefreshAttacherOwner,
        NonameArticleContainerFragment.OnNonameArticleContainerFragmentListener,
        NonameTopiclistContainer.OnNonameTopiclistContainerListener {

    boolean dualScreen = true;
    int flags = 31;
    NonameThreadResponse result = null;
    View view;
    String guidtmp;
    private String TAG = FlexibleNonameTopicListActivity.class.getSimpleName();
    private PullToRefreshAttacher mPullToRefreshAttacher;
    private OnItemClickListener onItemClickNewActivity = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        view = LayoutInflater.from(this).inflate(R.layout.noname_topiclist_activity,
                null);
        getSupportActionBar().setTitle("大漩涡匿名版");
        this.setContentView(view);
        PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
        options.refreshScrollDistance = 0.3f;
        options.refreshOnUp = true;
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this, options);

        if (null == findViewById(R.id.item_detail_container)) {
            dualScreen = false;
        }
        FragmentManager fm = getSupportFragmentManager();
        Fragment f1 = fm.findFragmentById(R.id.item_list);
        if (f1 == null) {
            f1 = new NonameTopiclistContainer();
            Bundle args = new Bundle();// (getIntent().getExtras());
            if (null != getIntent().getExtras()) {
                args.putAll(getIntent().getExtras());
            }
            f1.setArguments(args);
            FragmentTransaction ft = fm.beginTransaction().add(R.id.item_list,
                    f1);
            // .add(R.id.item_detail_container, f);
            ft.commit();
        }
        Fragment f2 = fm.findFragmentById(R.id.item_detail_container);
        if (null == f2) {
            f1.setHasOptionsMenu(true);
        } else if (!dualScreen) {
            fm.beginTransaction().remove(f2).commit();
            f1.setHasOptionsMenu(true);
        } else {
            f1.setHasOptionsMenu(false);
            f2.setHasOptionsMenu(true);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Fragment f1 = getSupportFragmentManager().findFragmentById(
                R.id.item_list);
        Fragment f2 = getSupportFragmentManager().findFragmentById(
                R.id.item_detail_container);
        f1.onPrepareOptionsMenu(menu);
        if (f2 != null && dualScreen)
            f2.onPrepareOptionsMenu(menu);
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
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void jsonfinishLoad(NonameThreadResponse result) {
        Fragment topicContainer = getSupportFragmentManager().findFragmentById(
                R.id.item_list);
        OnNonameTopListLoadFinishedListener listener = null;
        try {
            listener = (OnNonameTopListLoadFinishedListener) topicContainer;
            if (listener != null)
                listener.jsonfinishLoad(result);
        } catch (ClassCastException e) {
            NLog.e(TAG,
                    "topicContainer should implements "
                            + OnNonameTopListLoadFinishedListener.class
                            .getCanonicalName());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        if (!dualScreen) {// 非平板
            if (null == onItemClickNewActivity) {
                onItemClickNewActivity = new EnterJsonNonameArticle(this);
            }
            onItemClickNewActivity.onItemClick(parent, view, position, id);

        } else {
            int stid = 0;
            String guid = "";
            if (parent.getItemAtPosition(position) instanceof NonameThreadBody) {
                stid = ((NonameThreadBody) parent.getItemAtPosition(position)).tid;
                if (stid == 0) {
                    guid = (String) parent.getItemAtPosition(position);
                    if (StringUtils.isEmpty(guid))
                        return;
                } else {
                    guid = "tid=" + String.valueOf(stid);
                }
            } else {
                guid = (String) parent.getItemAtPosition(position);
                if (StringUtils.isEmpty(guid))
                    return;
            }

            guidtmp = guid;

            int tid = StringUtils.getUrlParameter(guid, "tid");
            NonameArticleContainerFragment f = NonameArticleContainerFragment
                    .create(tid);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            ft.replace(R.id.item_detail_container, f);
            Fragment f1 = fm.findFragmentById(R.id.item_list);
            f1.setHasOptionsMenu(false);
         //   f.setHasOptionsMenu(true);
            ft.commit();

            ListView listview = (ListView) parent;
            Object a = parent.getAdapter();
            NonameTopicListAdapter adapter = null;
            if (a instanceof NonameTopicListAdapter) {
                adapter = (NonameTopicListAdapter) a;
            } else if (a instanceof HeaderViewListAdapter) {
                HeaderViewListAdapter ha = (HeaderViewListAdapter) a;
                adapter = (NonameTopicListAdapter) ha.getWrappedAdapter();
                position -= ha.getHeadersCount();
            }
            adapter.setSelected(position);
            listview.setItemChecked(position, true);

        }

    }

    @Override
    public int getCurrentPage() {
        PagerOwner child = null;
        try {

            Fragment articleContainer = getSupportFragmentManager()
                    .findFragmentById(R.id.item_detail_container);
            child = (PagerOwner) articleContainer;
            if (null == child)
                return 0;
            return child.getCurrentPage();
        } catch (ClassCastException e) {
            NLog.e(TAG, "fragment in R.id.item_detail_container does not implements interface " + PagerOwner.class.getName());
            return 0;
        }
    }

    @Override
    public void setCurrentItem(int index) {
        PagerOwner child = null;
        try {
            Fragment articleContainer = getSupportFragmentManager().findFragmentById(R.id.item_detail_container);
            child = (PagerOwner) articleContainer;
            child.setCurrentItem(index);
        } catch (ClassCastException e) {
            NLog.e(TAG, "fragment in R.id.item_detail_container does not implements interface " + PagerOwner.class.getName());
        }
    }

    @Override
    public void OnChildFragmentRemoved(int id) {
        if (id == R.id.item_detail_container) {
            FragmentManager fm = getSupportFragmentManager();
            Fragment f1 = fm.findFragmentById(R.id.item_list);
            f1.setHasOptionsMenu(true);
            getSupportActionBar().setTitle("大漩涡匿名版");
            guidtmp = "";
        }

    }

    @Override
    public PullToRefreshAttacher getAttacher() {
        return mPullToRefreshAttacher;
    }

    @Override
    public void finishLoad(NonameReadResponse data) {
        // TODO Auto-generated method stub

        Fragment articleContainer = getSupportFragmentManager()
                .findFragmentById(R.id.item_detail_container);

        OnNonameThreadPageLoadFinishedListener listener = null;
        try {
            listener = (OnNonameThreadPageLoadFinishedListener) articleContainer;
            if (listener != null) {
                listener.finishLoad(data);
                getSupportActionBar().setTitle(
                        StringUtils.unEscapeHtml(data.data.title));
            }
        } catch (ClassCastException e) {
            NLog.e(TAG,
                    "detailContainer should implements OnThreadPageLoadFinishedListener");
        }
    }

    @Override
    public void onModeChanged() {
        // TODO Auto-generated method stub
        Fragment f1 = getSupportFragmentManager().findFragmentById(
                R.id.item_list);
        if (f1 != null) {
            ((NonameTopiclistContainer) f1).changedmode();
        }
    }

    @Override
    public void onAnotherModeChanged() {
    }

}
