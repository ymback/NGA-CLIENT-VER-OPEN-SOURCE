package sp.phone.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.MainActivity;
import sp.phone.adapter.AppendableMessageAdapter;
import sp.phone.bean.MessageListInfo;
import sp.phone.bean.PerferenceConstant;
import sp.phone.interfaces.NextJsonMessageListLoader;
import sp.phone.interfaces.OnMessageListLoadFinishedListener;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.task.JsonMessageListLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;

public class MessageListContainer extends BaseFragment implements
        OnMessageListLoadFinishedListener, NextJsonMessageListLoader, PerferenceConstant {
    static final int MESSAGE_SENT = 1;
    final String TAG = MessageListContainer.class.getSimpleName();
    PullToRefreshAttacher attacher = null;
    AppendableMessageAdapter adapter;
    boolean canDismiss = true;
    int category = 0;
    OnMessagelistContainerListener mCallback;
    private ListView listView;
    private ViewGroup mcontainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            category = savedInstanceState.getInt("category", 0);
        }
        mcontainer = container;
        if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
            if (mcontainer != null)
                mcontainer.setBackgroundResource(R.color.night_bg_color);
        }

        try {
            PullToRefreshAttacherOnwer attacherOnwer = (PullToRefreshAttacherOnwer) getActivity();
            attacher = attacherOnwer.getAttacher();

        } catch (ClassCastException e) {
            Log.e(TAG,
                    "father activity should implement PullToRefreshAttacherOnwer");
        }

        listView = new ListView(getActivity());
        listView.setDivider(null);
        adapter = new AppendableMessageAdapter(this.getActivity(), attacher, this);
        listView.setAdapter(adapter);
        // mPullRefreshListView.setAdapter(adapter);
        try {
            OnItemClickListener listener = (OnItemClickListener) getActivity();
            // mPullRefreshListView.setOnItemClickListener(listener);
            listView.setOnItemClickListener(listener);
        } catch (ClassCastException e) {
            Log.e(TAG, "father activity should implenent OnItemClickListener");
        }

        // mPullRefreshListView.setOnRefreshListener(new
        // ListRefreshListener());\
        if (attacher != null)
            attacher.addRefreshableView(listView, new ListRefreshListener());

        // JsonTopicListLoadTask task = new
        // JsonTopicListLoadTask(getActivity(),this);
        // task.execute(getUrl(1));

        return listView;
    }

    public void changedmode() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnMessagelistContainerListener) activity;
        } catch (ClassCastException e) {
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        canDismiss = true;
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        this.refresh();
        super.onViewCreated(view, savedInstanceState);
    }

    private void refresh_saying() {
        DefaultHeaderTransformer transformer = null;
        if (attacher != null) {
            uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.HeaderTransformer headerTransformer;
            headerTransformer = attacher.getHeaderTransformer();
            if (headerTransformer != null
                    && headerTransformer instanceof DefaultHeaderTransformer)
                transformer = (DefaultHeaderTransformer) headerTransformer;
        }

        if (transformer == null)
            ActivityUtil.getInstance().noticeSaying(this.getActivity());
        else
            transformer.setRefreshingText(ActivityUtil.getSaying());
        if (attacher != null)
            attacher.setRefreshing(true);
    }

    void refresh() {
        JsonMessageListLoadTask task = new JsonMessageListLoadTask(getActivity(),
                this);
        // ActivityUtil.getInstance().noticeSaying(this.getActivity());
        refresh_saying();
        task.execute(getUrl(1, true, true));
    }

    public String getUrl(int page, boolean isend, boolean restart) {

        String jsonUri = HttpUtil.Server + "/nuke.php?__lib=message&__act=message&act=list&";

        jsonUri += "page=" + page + "&lite=js&noprefix";

        return jsonUri;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int menuId;
        if (PhoneConfiguration.getInstance().HandSide == 1) {// lefthand
            int flag = PhoneConfiguration.getInstance().getUiFlag();
            if (flag == 1 || flag == 3 || flag == 5 || flag == 7) {// 主题列表，UIFLAG为1或者1+2或者1+4或者1+2+4
                menuId = R.menu.messagelist_menu_left;
            } else {
                menuId = R.menu.messagelist_menu;
            }
        } else {
            menuId = R.menu.messagelist_menu;
        }
        inflater.inflate(menuId, menu);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (menu.findItem(R.id.night_mode) != null) {
            if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
                menu.findItem(R.id.night_mode).setIcon(
                        R.drawable.ic_action_brightness_high);
                menu.findItem(R.id.night_mode).setTitle(R.string.change_daily_mode);
            } else {
                menu.findItem(R.id.night_mode).setIcon(
                        R.drawable.ic_action_bightness_low);
                menu.findItem(R.id.night_mode).setTitle(R.string.change_night_mode);
            }
        }
        // getSupportMenuInflater().inflate(R.menu.book_detail, menu);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.threadlist_menu_item2:
                this.refresh();
                break;
            case R.id.threadlist_menu_newthread:
                Intent intent_bookmark = new Intent();
                intent_bookmark.putExtra("action", "new");
                intent_bookmark.putExtra("messagemode", "yes");
                if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
                    intent_bookmark.setClass(getActivity(),
                            PhoneConfiguration.getInstance().messagePostActivityClass);
                } else {
                    intent_bookmark.setClass(getActivity(),
                            PhoneConfiguration.getInstance().loginActivityClass);
                }
                startActivityForResult(intent_bookmark, 321);
                break;
            case R.id.night_mode://OK
                nightMode(item);
                break;
            case R.id.threadlist_menu_item3:
            default:
                // case android.R.id.home:
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void nightMode(final MenuItem menu) {
        changeNightMode(menu);
        if (mcontainer != null) {
            if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
                mcontainer.setBackgroundResource(R.color.night_bg_color);
            } else {
                mcontainer.setBackgroundResource(R.color.shit1);
            }
        }
        if (mCallback != null)
            mCallback.onAnotherModeChanged();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("category", category);
        canDismiss = false;
        super.onSaveInstanceState(outState);
    }

    public void onCategoryChanged(int position) {
        if (position != category) {
            category = position;
            refresh();
        }
    }

    @Override
    public void jsonfinishLoad(MessageListInfo result) {
        if (attacher != null)
            attacher.setRefreshComplete();

        if (result == null)
            return;

        adapter.clear();
        adapter.jsonfinishLoad(result);
        listView.setAdapter(adapter);
        if (canDismiss)
            ActivityUtil.getInstance().dismiss();
    }

    @TargetApi(11)
    private void RunParallen(JsonMessageListLoadTask task) {
        task.executeOnExecutor(JsonMessageListLoadTask.THREAD_POOL_EXECUTOR,
                getUrl(adapter.getNextPage(), adapter.getIsEnd(), false));
    }

    @Override
    public void loadNextPage(OnMessageListLoadFinishedListener callback) {
        JsonMessageListLoadTask task = new JsonMessageListLoadTask(getActivity(),
                callback);
        refresh_saying();
        if (ActivityUtil.isGreaterThan_2_3_3())
            RunParallen(task);
        else
            task.execute(getUrl(adapter.getNextPage(), adapter.getIsEnd(),
                    false));
    }

    // Container Activity must implement this interface
    public interface OnMessagelistContainerListener {
        public void onAnotherModeChanged();
    }

    class ListRefreshListener implements
            PullToRefreshAttacher.OnRefreshListener {

		/*
         * @Override public void onPullDownToRefresh(
		 * PullToRefreshBase<ListView> refreshView) { refresh();
		 * 
		 * }
		 * 
		 * @Override public void onPullUpToRefresh( PullToRefreshBase<ListView>
		 * refreshView) { JsonTopicListLoadTask task = new
		 * JsonTopicListLoadTask(getActivity(), new
		 * OnTopListLoadFinishedListener(){
		 * 
		 * @Override public void jsonfinishLoad( TopicListInfo result) {
		 * mPullRefreshListView.onRefreshComplete(); if(result == null) return;
		 * ActivityUtil.getInstance().dismiss(); adapter.jsonfinishLoad(result);
		 * 
		 * }
		 * 
		 * } ); ActivityUtil.getInstance().noticeSaying(getActivity());
		 * task.execute(getUrl(adapter.getNextPage()));
		 * 
		 * }
		 */

        @Override
        public void onRefreshStarted(View view) {

            refresh();
        }
    }
}
