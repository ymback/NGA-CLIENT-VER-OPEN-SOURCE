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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.activity.MainActivity;
import noname.gson.parse.NonameThreadResponse;
import sp.phone.adapter.AppendableNonameTopicAdapter;
import sp.phone.bean.PerferenceConstant;
import sp.phone.interfaces.NextJsonNonameTopicListLoader;
import sp.phone.interfaces.OnNonameTopListLoadFinishedListener;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.task.JsonNonameTopicListLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;

public class NonameTopiclistContainer extends BaseFragment implements
        OnNonameTopListLoadFinishedListener, NextJsonNonameTopicListLoader, PerferenceConstant {
    static final int MESSAGE_SENT = 1;
    final String TAG = NonameTopiclistContainer.class.getSimpleName();
    int fid;
    int authorid;
    int searchpost;
    int favor;
    String key;
    String table;
    String fidgroup;
    String author;

    PullToRefreshAttacher attacher = null;
    AppendableNonameTopicAdapter adapter;
    boolean canDismiss = true;
    int category = 0;
    OnNonameTopiclistContainerListener mCallback;
    private ListView listView;
    private NonameThreadResponse mTopicListInfo;
    private int mListPosition;
    private int mListFirstTop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            category = savedInstanceState.getInt("category", 0);
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
        adapter = new AppendableNonameTopicAdapter(this.getActivity(), attacher, this);
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

        fid = 0;
        authorid = 0;
        String url = getArguments().getString("url");

        if (url != null) {

            fid = getUrlParameter(url, "fid");
            authorid = getUrlParameter(url, "authorid");
            searchpost = getUrlParameter(url, "searchpost");
            favor = getUrlParameter(url, "favor");
            key = StringUtil.getStringBetween(url, 0, "key=", "&").result;
            author = StringUtil.getStringBetween(url, 0, "author=", "&").result;
            table = StringUtil.getStringBetween(url, 0, "table=", "&").result;
            fidgroup = StringUtil.getStringBetween(url, 0, "fidgroup=", "&").result;
        } else {
            fid = getArguments().getInt("fid", 0);
            authorid = getArguments().getInt("authorid", 0);
            searchpost = getArguments().getInt("searchpost", 0);
            favor = getArguments().getInt("favor", 0);
            key = getArguments().getString("key");
            author = getArguments().getString("author");
            table = getArguments().getString("table");
            fidgroup = getArguments().getString("fidgroup");
        }

        if (favor != 0) {
            Toast.makeText(
                    getActivity(),
                    "长按可删除收藏的帖子", Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof OnItemLongClickListener) {
                listView.setLongClickable(true);
                listView.setOnItemLongClickListener((OnItemLongClickListener)
                        getActivity());
            }
        }

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        canDismiss = true;
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if (mTopicListInfo == null) {
            refresh();
        } else {
            jsonfinishLoad(mTopicListInfo);
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        listView.setSelectionFromTop(mListPosition, mListFirstTop);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (listView.getChildCount() >= 1) {
            mListPosition = listView.getFirstVisiblePosition();
            mListFirstTop = listView.getChildAt(0).getTop();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnNonameTopiclistContainerListener) activity;
        } catch (ClassCastException e) {
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
        }

        if (transformer == null)
            ActivityUtil.getInstance().noticeSaying(this.getActivity());
        else
            transformer.setRefreshingText(ActivityUtil.getSaying());
        if (attacher != null)
            attacher.setRefreshing(true);
    }

    void refresh() {
        JsonNonameTopicListLoadTask task = new JsonNonameTopicListLoadTask(getActivity(),
                this);
        // ActivityUtil.getInstance().noticeSaying(this.getActivity());
        refresh_saying();
        task.execute(getUrl(1, true, true));
    }

    public String getNfcUrl() {
        final String scheme = getResources().getString(R.string.myscheme);
        final StringBuilder sb = new StringBuilder(scheme);
        sb.append("://" + Utils.getNGADomain() + "/thread.php?");
        if (fid != 0) {
            sb.append("fid=");
            sb.append(fid);
            sb.append('&');
        }
        if (authorid != 0) {
            sb.append("authorid=");
            sb.append(authorid);
            sb.append('&');
        }
        if (this.searchpost != 0) {
            sb.append("searchpost=");
            sb.append(searchpost);
            sb.append('&');
        }

        return sb.toString();
    }

    public String getUrl(int page, boolean isend, boolean restart) {

        String jsonUri = HttpUtil.NonameServer + "/thread.php?";
        jsonUri += "page=" + page;

        return jsonUri;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int menuId;
        if (PhoneConfiguration.getInstance().HandSide == 1) {// lefthand
            int flag = PhoneConfiguration.getInstance().getUiFlag();
            if (flag == 1 || flag == 3 || flag == 5 || flag == 7) {// 主题列表，UIFLAG为1或者1+2或者1+4或者1+2+4
                menuId = R.menu.nonamethreadlist_menu_left;
            } else {
                menuId = R.menu.nonamethreadlist_menu;
            }
        } else {
            menuId = R.menu.nonamethreadlist_menu;
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
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.threadlist_menu_newthread:
                handlePostThread(item);
                break;
            case R.id.threadlist_menu_item2:
                this.refresh();
                break;
            case R.id.night_mode://OK
                nightMode(item);
                break;
            case R.id.threadlist_menu_item3:
            default:
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void nightMode(final MenuItem menu) {
        changeNightMode(menu);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (mCallback != null) {
            mCallback.onAnotherModeChanged();
        }
    }

    private boolean handlePostThread(MenuItem item) {
        Intent intent = new Intent();
        intent.putExtra("fid", fid);
        intent.putExtra("action", "new");
        intent.setClass(getActivity(),
                PhoneConfiguration.getInstance().nonamePostActivityClass);
        startActivity(intent);
        if (PhoneConfiguration.getInstance().showAnimation) {
            getActivity().overridePendingTransition(R.anim.zoom_enter,
                    R.anim.zoom_exit);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("category", category);
        canDismiss = false;
        super.onSaveInstanceState(outState);
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

    public void onCategoryChanged(int position) {
        if (position != category) {
            category = position;
            refresh();
        }
    }

    @Override
    public void jsonfinishLoad(NonameThreadResponse result) {
        if (attacher != null)
            attacher.setRefreshComplete();

        if (result == null)
            return;
        mTopicListInfo = result;
        adapter.clear();
        adapter.jsonfinishLoad(result);
        listView.setAdapter(adapter);
        if (canDismiss)
            ActivityUtil.getInstance().dismiss();

    }

    @TargetApi(11)
    private void RunParallen(JsonNonameTopicListLoadTask task) {
        task.executeOnExecutor(JsonNonameTopicListLoadTask.THREAD_POOL_EXECUTOR,
                getUrl(adapter.getNextPage(), adapter.getIsEnd(), false));
    }

    @Override
    public void loadNextPage(OnNonameTopListLoadFinishedListener callback) {
        JsonNonameTopicListLoadTask task = new JsonNonameTopicListLoadTask(getActivity(),
                callback);
        refresh_saying();
        if (ActivityUtil.isGreaterThan_2_3_3())
            RunParallen(task);
        else
            task.execute(getUrl(adapter.getNextPage(), adapter.getIsEnd(),
                    false));
    }

    // Container Activity must implement this interface
    public interface OnNonameTopiclistContainerListener {
        public void onAnotherModeChanged();
    }

    class ListRefreshListener implements
            PullToRefreshAttacher.OnRefreshListener {

        @Override
        public void onRefreshStarted(View view) {

            refresh();
        }
    }


}
