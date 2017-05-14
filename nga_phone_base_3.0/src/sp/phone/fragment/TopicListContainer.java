package sp.phone.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.activity.MainActivity;
import sp.phone.adapter.AppendableTopicAdapter;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.NextJsonTopicListLoader;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.task.JsonTopicListLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;

/**
 * 帖子列表分页
 */
public class TopicListContainer extends BaseFragment implements OnTopListLoadFinishedListener, NextJsonTopicListLoader, PerferenceConstant {
    final String TAG = TopicListContainer.class.getSimpleName();
    int fid;
    int authorid;
    int searchpost;
    int favor;
    int content;
    String key;
    String fidgroup;
    String author;

    private PullToRefreshAttacher attacher = null;
    private AppendableTopicAdapter adapter;
    boolean canDismiss = true;
    int category = 0;
    int fromreplyactivity = 0;
    boolean searchmode = false;
    private OnTopicListContainerListener mCallback;

    private ListView listView;
    private FloatingActionButton mFab;
    private TopicListInfo mTopicListInfo;
    private int mListPosition;
    private int mListFirstTop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void changedMode() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnTopicListContainerListener) activity;
        } catch (ClassCastException e) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            category = savedInstanceState.getInt("category", 0);
        }

        try {
            PullToRefreshAttacherOnwer attacherOnwer = (PullToRefreshAttacherOnwer) getActivity();
            attacher = attacherOnwer.getAttacher();

        } catch (ClassCastException e) {
            Log.e(TAG, "father activity should implement PullToRefreshAttacherOnwer");
        }
        View view = inflater.inflate(R.layout.fragment_topic_list_container, container, false);
        listView = (ListView) view.findViewById(R.id.topic_list);
        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listView.setNestedScrollingEnabled(true);
        }
        adapter = new AppendableTopicAdapter(this.getActivity(), attacher, this);
        listView.setAdapter(adapter);
        try {
            OnItemClickListener listener = (OnItemClickListener) getActivity();
            listView.setOnItemClickListener(listener);
        } catch (ClassCastException e) {
            Log.e(TAG, "father activity should implenent OnItemClickListener");
        }
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

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
//			table = StringUtil.getStringBetween(url, 0, "table=", "&").result;
            fidgroup = StringUtil.getStringBetween(url, 0, "fidgroup=", "&").result;
            searchmode = false;
            content = getUrlParameter(url, "content");
        } else {
            fid = getArguments().getInt("fid", 0);
            authorid = getArguments().getInt("authorid", 0);
            content = getArguments().getInt("content", 0);
            searchpost = getArguments().getInt("searchpost", 0);
            favor = getArguments().getInt("favor", 0);
            key = getArguments().getString("key");
            author = getArguments().getString("author");
            fidgroup = getArguments().getString("fidgroup");
            if (!StringUtil.isEmpty(getArguments().getString("searchmode"))) {
                if (getArguments().getString("searchmode").equals("true"))
                    searchmode = true;
            }
        }

        if (favor != 0) {
            Toast.makeText(getActivity(), "长按可删除收藏的帖子", Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof OnItemLongClickListener) {
                listView.setLongClickable(true);
                listView.setOnItemLongClickListener((OnItemLongClickListener) getActivity());
            }
        }
        if (!StringUtil.isEmpty(key) || !StringUtil.isEmpty(author)
                || !StringUtil.isEmpty(fidgroup)
                || searchpost != 0 || authorid != 0 || favor != 0) {//|| !StringUtil.isEmpty(table)
            fromreplyactivity = 1;
        }

        return view;
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
        if (searchmode)
            handleSearch();
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

    private void refresh_saying() {
        DefaultHeaderTransformer transformer = null;

        if (attacher != null) {
            uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.HeaderTransformer headerTransformer;
            headerTransformer = attacher.getHeaderTransformer();
            if (headerTransformer != null && headerTransformer instanceof DefaultHeaderTransformer)
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
        JsonTopicListLoadTask task = new JsonTopicListLoadTask(getActivity(), this);
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
        if (this.content != 0) {
            sb.append("content=");
            sb.append(searchpost);
            sb.append('&');
        }
        return sb.toString();
    }

    public String getUrl(int page, boolean isend, boolean restart) {
        String jsonUri = HttpUtil.Server + "/thread.php?";
        if (0 != authorid)
            jsonUri += "authorid=" + authorid + "&";
        if (searchpost != 0)
            jsonUri += "searchpost=" + searchpost + "&";
        if (favor != 0)
            jsonUri += "favor=" + favor + "&";
        if (content != 0)
            jsonUri += "content=" + content + "&";

        if (!StringUtil.isEmpty(author)) {
            try {
                if (author.endsWith("&searchpost=1")) {
                    jsonUri += "author="
                            + URLEncoder.encode(
                            author.substring(0, author.length() - 13),
                            "GBK") + "&searchpost=1&";
                } else {
                    jsonUri += "author="
                            + URLEncoder.encode(author, "GBK") + "&";
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            if (0 != fid)
                jsonUri += "fid=" + fid + "&";
            if (!StringUtil.isEmpty(key)) {
                jsonUri += "key=" + StringUtil.encodeUrl(key, "UTF-8") + "&";
            }
            if (!StringUtil.isEmpty(fidgroup)) {
                jsonUri += "fidgroup=" + fidgroup + "&";
            }
        }
        jsonUri += "page=" + page + "&lite=js&noprefix";
        switch (category) {
            case 2:
                jsonUri += "&recommend=1&order_by=postdatedesc&admin=1";
                break;
            case 1:
                jsonUri += "&recommend=1&order_by=postdatedesc&user=1";
                break;
            case 0:
            default:
        }

        return jsonUri;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int menuId;
        if (PhoneConfiguration.getInstance().HandSide == 1) {// lefthand
            int flag = PhoneConfiguration.getInstance().getUiFlag();
            if (flag == 1 || flag == 3 || flag == 5 || flag == 7) {// 主题列表，UIFLAG为1或者1+2或者1+4或者1+2+4
                menuId = R.menu.threadlist_menu_left;
            } else {
                menuId = R.menu.threadlist_menu;
            }
        } else {
            menuId = R.menu.threadlist_menu;
        }
        inflater.inflate(menuId, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (menu.findItem(R.id.night_mode) != null) {
            if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
                menu.findItem(R.id.night_mode).setIcon(R.drawable.ic_action_brightness_high);
                menu.findItem(R.id.night_mode).setTitle(R.string.change_daily_mode);
            } else {
                menu.findItem(R.id.night_mode).setIcon(R.drawable.ic_action_bightness_low);
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
            /*
             * int current = this.mViewPager.getCurrentItem();
			 * ActivityUtil.getInstance().noticeSaying(this);
			 * this.mViewPager.setAdapter(this.mTabsAdapter);
			 * this.mViewPager.setCurrentItem(current, true);
			 */
                this.refresh();
                break;
            case R.id.goto_bookmark_item:
                Intent intent_bookmark = new Intent(getActivity(), PhoneConfiguration.getInstance().topicActivityClass);
                intent_bookmark.putExtra("favor", 1);
                startActivity(intent_bookmark);
                break;
            case R.id.night_mode://OK
                nightMode(item);
                break;
            case R.id.search:
                Intent intentsearch = new Intent();
                intentsearch.putExtra("fid", fid);
                intentsearch.putExtra("action", "search");
                if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
                    handleSearch();
                } else {
                    intentsearch.setClass(getActivity(), PhoneConfiguration.getInstance().loginActivityClass);
                    startActivity(intentsearch);
                    if (PhoneConfiguration.getInstance().showAnimation) {
                        getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                    }
                }
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
        if (adapter != null)
            adapter.notifyDataSetChanged();
        if (mCallback != null)
            mCallback.onAnotherModeChanged();
    }

    private boolean handlePostThread(MenuItem item) {
        Intent intent = new Intent();
        intent.putExtra("fid", fid);
        intent.putExtra("action", "new");
        if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
            intent.setClass(getActivity(), PhoneConfiguration.getInstance().postActivityClass);
        } else {
            intent.setClass(getActivity(), PhoneConfiguration.getInstance().loginActivityClass);
        }
        startActivity(intent);
        if (PhoneConfiguration.getInstance().showAnimation) {
            getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("category", category);
        canDismiss = false;
        super.onSaveInstanceState(outState);
    }

    private void handleSearch() {
        Bundle arg = new Bundle();
        arg.putInt("id", fid);
        arg.putInt("authorid", authorid);
        DialogFragment df = new SearchDialogFragment();
        df.setArguments(arg);
        final String dialogTag = "search_dialog";
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(dialogTag);
        if (prev != null) {
            ft.remove(prev);
        }
        try {
            df.show(ft, dialogTag);
        } catch (Exception e) {
            Log.e(TopicListContainer.class.getSimpleName(), Log.getStackTraceString(e));
        }
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
    public void jsonfinishLoad(TopicListInfo result) {
        if (attacher != null)
            attacher.setRefreshComplete();

        if (result == null)
            return;

        mTopicListInfo = result;
        if (result.get__SEARCHNORESULT()) {
            if (getActivity() != null) {
                Toast.makeText(getActivity(), "结果已搜索完毕",
                        Toast.LENGTH_SHORT).show();
            }
            return;
        }
        int lines = 35;
        if (authorid != 0)
            lines = 20;
        int pageCount = result.get__ROWS() / lines;
        if (pageCount * lines < result.get__ROWS())
            pageCount++;

        if (searchpost != 0)// can not get exact row counts
        {
            int page = result.get__ROWS();
            pageCount = page;
            if (result.get__T__ROWS() == lines)
                pageCount++;
        }

        adapter.clear();
        adapter.jsonfinishLoad(result);
        listView.setAdapter(adapter);
        if (canDismiss)
            ActivityUtil.getInstance().dismiss();
    }

    @TargetApi(11)
    private void RunParallen(JsonTopicListLoadTask task) {
        task.executeOnExecutor(JsonTopicListLoadTask.THREAD_POOL_EXECUTOR, getUrl(adapter.getNextPage(), adapter.getIsEnd(), false));
    }

    @Override
    public void loadNextPage(OnTopListLoadFinishedListener callback) {
        JsonTopicListLoadTask task = new JsonTopicListLoadTask(getActivity(), callback);
        refresh_saying();
        if (ActivityUtil.isGreaterThan_2_3_3())
            RunParallen(task);
        else
            task.execute(getUrl(adapter.getNextPage(), adapter.getIsEnd(), false));
    }

    // Container Activity must implement this interface
    public interface OnTopicListContainerListener {
        public void onAnotherModeChanged();
    }

    class ListRefreshListener implements PullToRefreshAttacher.OnRefreshListener {

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
