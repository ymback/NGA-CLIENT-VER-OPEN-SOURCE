package gov.anzong.androidnga.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import gov.anzong.androidnga.R;
import sp.phone.adapter.AppendableTopicAdapter;
import sp.phone.adapter.TopicListAdapter;
import sp.phone.bean.BoardHolder;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.TopicListInfo;
import sp.phone.fragment.ArticleContainerFragment;
import sp.phone.fragment.TopicListContainer;
import sp.phone.interfaces.EnterJsonArticle;
import sp.phone.interfaces.OnChildFragmentRemovedListener;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.interfaces.PagerOwnner;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.task.CheckReplyNotificationTask;
import sp.phone.task.DeleteBookmarkTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;

/**
 * 帖子列表
 */
public class FlexibleTopicListActivity extends SwipeBackAppCompatActivity
        implements OnTopListLoadFinishedListener, OnItemClickListener,
        OnThreadPageLoadFinishedListener, PagerOwnner,
        OnChildFragmentRemovedListener, PullToRefreshAttacherOnwer,
        OnItemLongClickListener,
        ArticleContainerFragment.OnArticleContainerFragmentListener,
        TopicListContainer.OnTopicListContainerListener {

    boolean dualScreen = true;
    String strs[] = {"全部", "精华", "推荐"};
    ArrayAdapter<String> categoryAdapter;
    int flags = 7;
    int toDeleteTid = 0;
    TopicListInfo result = null;
    View view;
    int nightmode;
    String guidtmp;
    int authorid;
    int searchpost;
    int favor;
    int content;
    String key;
    //	String table;
    String fidgroup;
    String author;
    boolean fromreplyactivity = false;
    private String TAG = FlexibleTopicListActivity.class.getSimpleName();
    private CheckReplyNotificationTask asynTask;
    private PullToRefreshAttacher mPullToRefreshAttacher;
    private OnItemClickListener onItemClickNewActivity = null;

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
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        view = LayoutInflater.from(this).inflate(R.layout.topiclist_activity, null);
        Intent intent = getIntent();
        boolean isfullScreen = intent.getBooleanExtra("isFullScreen", false);
        if (isfullScreen) {
            ActivityUtil.getInstance().setFullScreen(view);
        }
        this.setContentView(view);
        nightmode = ThemeManager.getInstance().getMode();
        PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
        options.refreshScrollDistance = 0.3f;
        options.refreshOnUp = true;
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this, options);

        if (ActivityUtil.isNotLessThan_4_0())
            setNfcCallBack();

        if (null == findViewById(R.id.item_detail_container)) {
            dualScreen = false;
        }
        FragmentManager fm = getSupportFragmentManager();
        Fragment f1 = fm.findFragmentById(R.id.item_list);

        String url = getIntent().getDataString();
        if (url != null) {
            authorid = getUrlParameter(url, "authorid");
            searchpost = getUrlParameter(url, "searchpost");
            favor = getUrlParameter(url, "favor");
            key = StringUtil.getStringBetween(url, 0, "key=", "&").result;
            author = StringUtil.getStringBetween(url, 0, "author=", "&").result;
//			table = StringUtil.getStringBetween(url, 0, "table=", "&").result;
            fidgroup = StringUtil.getStringBetween(url, 0, "fidgroup=", "&").result;
            content = getUrlParameter(url, "content");
        } else {
            if (null != getIntent().getExtras()) {
                authorid = getIntent().getExtras().getInt("authorid", 0);
                content = getIntent().getExtras().getInt("content", 0);
                searchpost = getIntent().getExtras().getInt("searchpost", 0);
                favor = getIntent().getExtras().getInt("favor", 0);
                key = getIntent().getExtras().getString("key");
                author = getIntent().getExtras().getString("author");
                if (!StringUtil.isEmpty(author))
                    if (author.indexOf("&searchpost=1") > 0) {
                        author = author.replace("&searchpost=1", "");
                        searchpost = 1;
                    }
//				table = getIntent().getExtras().getString("table");
                fidgroup = getIntent().getExtras().getString("fidgroup");
            }
        }
        if (authorid > 0 || searchpost > 0 || favor > 0
                || !StringUtil.isEmpty(key) || !StringUtil.isEmpty(author)
                || !StringUtil.isEmpty(fidgroup)) {//!StringUtil.isEmpty(table) ||
            fromreplyactivity = true;
        }
        if (f1 == null) {
            f1 = new TopicListContainer();
            Bundle args = new Bundle();// (getIntent().getExtras());
            if (null != getIntent().getExtras()) {
                args.putAll(getIntent().getExtras());
            }
            args.putString("url", getIntent().getDataString());
            f1.setArguments(args);
            FragmentTransaction ft = fm.beginTransaction().add(R.id.item_list, f1);
            ft.commit();
        }
        Fragment f2 = fm.findFragmentById(R.id.item_detail_container);
        if (null == f2) {
            f1.setHasOptionsMenu(true);
        } else if (!dualScreen) {
            getSupportActionBar().setTitle("主题列表");
            fm.beginTransaction().remove(f2).commit();
            f1.setHasOptionsMenu(true);
        } else {
            f1.setHasOptionsMenu(false);
            f2.setHasOptionsMenu(true);
        }

        int fid = getIntent().getIntExtra("fid", 0);
        if (fid != 0) {
            String boardName = BoardHolder.boardNameMap.get(fid);
            if (null != boardName) {
                strs[0] = boardName;
            }
        }
        int favor = getIntent().getIntExtra("favor", 0);
        String key = getIntent().getStringExtra("key");
        String fidgroup = getIntent().getStringExtra("fidgroup");
        int authorid = getIntent().getIntExtra("authorid", 0);

        if (favor == 0 && authorid == 0 && StringUtil.isEmpty(key)
                && StringUtil.isEmpty(author)) {
            setNavigation();
        } else {
            flags = ThemeManager.ACTION_BAR_FLAG;
        }
        if (favor != 0) {
            getSupportActionBar().setTitle(R.string.bookmark_title);
        }
        if (!StringUtil.isEmpty(key)) {
            flags = ThemeManager.ACTION_BAR_FLAG;
            if (content == 1) {
                if (!StringUtil.isEmpty(fidgroup)) {
                    final String title = "搜索全站(包含正文):" + key;
                    getSupportActionBar().setTitle(title);
                } else {
                    final String title = "搜索(包含正文):" + key;
                    getSupportActionBar().setTitle(title);
                }
            } else {
                if (!StringUtil.isEmpty(fidgroup)) {
                    final String title = "搜索全站:" + key;
                    getSupportActionBar().setTitle(title);
                } else {
                    final String title = "搜索:" + key;
                    getSupportActionBar().setTitle(title);
                }
            }
        } else {
            if (!StringUtil.isEmpty(author)) {
                flags = ThemeManager.ACTION_BAR_FLAG;
                if (searchpost > 0) {
                    final String title = "搜索" + author + "的回复";
                    getSupportActionBar().setTitle(title);
                } else {
                    final String title = "搜索" + author + "的主题";
                    getSupportActionBar().setTitle(title);
                }
            }
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

    @TargetApi(11)
    private void setNavigation() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strs);
        OnNavigationListener callback = new OnNavigationListener() {

            @Override
            public boolean onNavigationItemSelected(int itemPosition,
                                                    long itemId) {
                TopicListContainer f1 = (TopicListContainer) getSupportFragmentManager()
                        .findFragmentById(R.id.item_list);
                if (f1 != null) {
                    f1.onCategoryChanged(itemPosition);
                }
                return true;
            }

        };
        actionBar.setListNavigationCallbacks(categoryAdapter, callback);

    }

    @TargetApi(14)
    void setNfcCallBack() {
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        CreateNdefMessageCallback callback = new CreateNdefMessageCallback() {

            @Override
            public NdefMessage createNdefMessage(NfcEvent event) {
                FragmentManager fm = getSupportFragmentManager();
                TopicListContainer f1 = (TopicListContainer) fm
                        .findFragmentById(R.id.item_list);
                final String url = f1.getNfcUrl();
                NdefMessage msg = new NdefMessage(
                        new NdefRecord[]{NdefRecord.createUri(url)});
                return msg;
            }

        };
        if (adapter != null) {
            adapter.setNdefPushMessageCallback(callback, this);
        }
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
    protected void onResume() {
        if (nightmode != ThemeManager.getInstance().getMode()) {
            onModeChanged();
            invalidateOptionsMenu();
            nightmode = ThemeManager.getInstance().getMode();
        }
        int orientation = ThemeManager.getInstance().screenOrentation;
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(orientation);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }

        if (asynTask != null) {
            asynTask.cancel(true);
            asynTask = null;
        }
        long now = System.currentTimeMillis();
        PhoneConfiguration config = PhoneConfiguration.getInstance();
        if (now - config.lastMessageCheck > 30 * 1000 && config.notification) {// 30秒才爽啊艹
            Log.d(TAG, "start to check Reply Notification");
            asynTask = new CheckReplyNotificationTask(this);
            asynTask.execute(config.getCookie());
        }
        if (PhoneConfiguration.getInstance().fullscreen) {
            ActivityUtil.getInstance().setFullScreen(view);
        }
        super.onResume();
    }

    @Override
    public void jsonfinishLoad(TopicListInfo result) {
        Fragment topicContainer = getSupportFragmentManager().findFragmentById(R.id.item_list);
        if (!result.get__SEARCHNORESULT()) {
            this.result = result;
        }
        OnTopListLoadFinishedListener listener = null;
        try {
            listener = (OnTopListLoadFinishedListener) topicContainer;
            if (listener != null)
                listener.jsonfinishLoad(result);
        } catch (ClassCastException e) {
            Log.e(TAG, "topicContainer should implements " + OnTopListLoadFinishedListener.class.getCanonicalName());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!dualScreen) {// 非平板
            if (null == onItemClickNewActivity) {
                onItemClickNewActivity = new EnterJsonArticle(this, fromreplyactivity);
            }
            onItemClickNewActivity.onItemClick(parent, view, position, id);
        } else {
            String guid = (String) parent.getItemAtPosition(position);
            if (StringUtil.isEmpty(guid))
                return;

            guid = guid.trim();
            guidtmp = guid;

            int pid = StringUtil.getUrlParameter(guid, "pid");
            int tid = StringUtil.getUrlParameter(guid, "tid");
            int authorid = StringUtil.getUrlParameter(guid, "authorid");
            ArticleContainerFragment f = ArticleContainerFragment.create(tid,
                    pid, authorid);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            ft.replace(R.id.item_detail_container, f);
            Fragment f1 = fm.findFragmentById(R.id.item_list);
            f1.setHasOptionsMenu(false);
            f.setHasOptionsMenu(true);
            ft.commit();

            ListView listview = (ListView) parent;
            Object a = parent.getAdapter();
            TopicListAdapter adapter = null;
            if (a instanceof TopicListAdapter) {
                adapter = (TopicListAdapter) a;
            } else if (a instanceof HeaderViewListAdapter) {
                HeaderViewListAdapter ha = (HeaderViewListAdapter) a;
                adapter = (TopicListAdapter) ha.getWrappedAdapter();
                position -= ha.getHeadersCount();
            }
            adapter.setSelected(position);
            listview.setItemChecked(position, true);
        }
    }

    @Override
    public void finishLoad(ThreadData data) {
        /*
         * int exactCount = 1 + data.getThreadInfo().getReplies()/20;
		 * if(father.getmTabsAdapter().getCount() != exactCount &&this.authorid
		 * == 0){ father.getmTabsAdapter().setCount(exactCount); }
		 * father.setTitle
		 * (StringUtil.unEscapeHtml(data.getThreadInfo().getSubject()));
		 */

        Fragment articleContainer = getSupportFragmentManager()
                .findFragmentById(R.id.item_detail_container);

        OnThreadPageLoadFinishedListener listener = null;
        try {
            listener = (OnThreadPageLoadFinishedListener) articleContainer;
            if (listener != null) {
                listener.finishLoad(data);
                getSupportActionBar().setTitle(
                        StringUtil.unEscapeHtml(data.getThreadInfo()
                                .getSubject()));
            }
        } catch (ClassCastException e) {
            Log.e(TAG, "detailContainer should implements OnThreadPageLoadFinishedListener");
        }
    }

    @Override
    public int getCurrentPage() {
        PagerOwnner child = null;
        try {
            Fragment articleContainer = getSupportFragmentManager().findFragmentById(R.id.item_detail_container);
            child = (PagerOwnner) articleContainer;
            if (null == child)
                return 0;
            return child.getCurrentPage();
        } catch (ClassCastException e) {
            Log.e(TAG, "fragment in R.id.item_detail_container does not implements interface " + PagerOwnner.class.getName());
            return 0;
        }
    }

    @Override
    public void setCurrentItem(int index) {
        PagerOwnner child = null;
        try {
            Fragment articleContainer = getSupportFragmentManager().findFragmentById(R.id.item_detail_container);
            child = (PagerOwnner) articleContainer;
            child.setCurrentItem(index);
        } catch (ClassCastException e) {
            Log.e(TAG, "fragment in R.id.item_detail_container does not implements interface " + PagerOwnner.class.getName());
            return;
        }
    }

    @Override
    public void OnChildFragmentRemoved(int id) {
        if (id == R.id.item_detail_container) {
            FragmentManager fm = getSupportFragmentManager();
            Fragment f1 = fm.findFragmentById(R.id.item_list);
            f1.setHasOptionsMenu(true);
            getSupportActionBar().setTitle("主题列表");
            guidtmp = "";
        }
    }

    @Override
    public PullToRefreshAttacher getAttacher() {
        return mPullToRefreshAttacher;
    }

    public ThreadPageInfo getEntry(int position) {
        if (result != null)
            return result.getArticleEntryList().get(position);
        return null;
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, final View view, int position, long id) {
        Object a = parent.getAdapter();
        AppendableTopicAdapter adapter = null;
        if (a instanceof AppendableTopicAdapter) {
            adapter = (AppendableTopicAdapter) a;
        } else if (a instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter ha = (HeaderViewListAdapter) a;
            adapter = (AppendableTopicAdapter) ha.getWrappedAdapter();
            position -= ha.getHeadersCount();
        }
        final int positiona = position;
        final String deladd = adapter.gettidarray(positiona);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        DeleteBookmarkTask task = new DeleteBookmarkTask(
                                FlexibleTopicListActivity.this, parent, positiona);
                        task.execute(deladd);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // Do nothing
                        break;
                }
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getString(R.string.delete_favo_confirm_text))
                .setPositiveButton(R.string.confirm, dialogClickListener)
                .setNegativeButton(R.string.cancle, dialogClickListener);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface arg0) {
                dialog.dismiss();
                if (PhoneConfiguration.getInstance().fullscreen) {
                    ActivityUtil.getInstance().setFullScreen(view);
                }
            }

        });
        return true;
    }

    @Override
    public void onModeChanged() {
        Fragment f1 = getSupportFragmentManager().findFragmentById(R.id.item_list);
        if (f1 != null) {
            ((TopicListContainer) f1).changedMode();
        }
    }

    @Override
    public void onAnotherModeChanged() {
        nightmode = ThemeManager.getInstance().getMode();
        Fragment f2 = getSupportFragmentManager().findFragmentById(R.id.item_detail_container);
        if (f2 != null) {
            ((ArticleContainerFragment) f2).changemode();
        } else {
            FrameLayout v = (FrameLayout) view.findViewById(R.id.item_detail_container);
            if (v != null) {
                if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
                    v.setBackgroundResource(R.color.night_bg_color);
                } else {
                    v.setBackgroundResource(R.color.shit1);
                }
            }
        }
    }
}
