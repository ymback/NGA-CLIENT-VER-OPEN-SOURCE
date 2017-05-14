package sp.phone.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import gov.anzong.androidnga.R;
import sp.phone.adapter.AppendableMessageDetialAdapter;
import sp.phone.bean.MessageArticlePageInfo;
import sp.phone.bean.MessageDetialInfo;
import sp.phone.bean.PerferenceConstant;
import sp.phone.interfaces.NextJsonMessageDetialLoader;
import sp.phone.interfaces.OnChildFragmentRemovedListener;
import sp.phone.interfaces.OnMessageDetialLoadFinishedListener;
import sp.phone.interfaces.PagerOwnner;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.task.JsonMessageDetialLoadTask;
import sp.phone.task.JsonMessageListLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.FunctionUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;

public class MessageDetialListContainer extends BaseFragment implements
        OnMessageDetialLoadFinishedListener, NextJsonMessageDetialLoader,
        PerferenceConstant {
    static final int MESSAGE_SENT = 1;
    final String TAG = MessageDetialListContainer.class.getSimpleName();
    PullToRefreshAttacher attacher = null;
    AppendableMessageDetialAdapter adapter;
    boolean canDismiss = true;
    int mid;
    String title, to;
    String url;
    OnMessageDetialListContainerListener mCallback;
    private ListView listView;
    private Object mActionModeCallback = null;
    private ViewGroup mcontainer;

    public MessageDetialListContainer() {
        super();
    }

    public static MessageDetialListContainer create(int mid) {
        MessageDetialListContainer f = new MessageDetialListContainer();
        Bundle args = new Bundle();
        args.putInt("mid", mid);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        activeActionMode();
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @TargetApi(11)
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                ListView lv = (ListView) parent;
                lv.setItemChecked(position, true);
                if (mActionModeCallback != null) {
                    ((ActionBarActivity) getActivity())
                            .startSupportActionMode((Callback) mActionModeCallback);
                    return true;
                }
                return false;
            }
        });
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

        url = getArguments().getString("url");

        if (url != null) {
            String tmp = StringUtil.getStringBetween(url, 0, "mid=", "&").result;
            if (!StringUtil.isEmpty(tmp)) {
                mid = Integer.parseInt(tmp, 0);
            }
        } else {
            mid = getArguments().getInt("mid", 0);
        }
        // JsonTopicListLoadTask task = new
        // JsonTopicListLoadTask(getActivity(),this);
        // task.execute(getUrl(1));
        return listView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        canDismiss = true;
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        this.refresh();
        super.onViewCreated(view, savedInstanceState);
    }

    public void changemode() {
        if (mcontainer != null) {
            if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
                mcontainer.setBackgroundResource(R.color.night_bg_color);
            } else {
                mcontainer.setBackgroundResource(R.color.shit1);
            }
        }
        if (adapter != null)
            adapter.notifyDataSetChangedWithModChange();
    }

    @TargetApi(11)
    private void activeActionMode() {
        mActionModeCallback = new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.messagedetail_context_menu, menu);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                onContextItemSelected(item);
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // int position = listview.getCheckedItemPosition();
                // listview.setItemChecked(position, false);

            }

        };
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
    public boolean onContextItemSelected(MenuItem item) {

        PagerOwnner father = null;
        try {
            father = (PagerOwnner) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "father activity does not implements interface "
                    + PagerOwnner.class.getName());
            return true;
        }

        if (father == null)
            return false;

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        int position = this.listView.getCheckedItemPosition();
        if (info != null) {
            position = info.position;
        }
        if (position < 0 || position >= listView.getAdapter().getCount()) {
            Toast.makeText(getActivity(), R.string.floor_error,
                    Toast.LENGTH_LONG).show();
            position = 0;
        }
        StringBuffer postPrefix = new StringBuffer();

        boolean isadmin = false;
        MessageArticlePageInfo row = (MessageArticlePageInfo) listView
                .getItemAtPosition(position);
        if (row == null) {
            Toast.makeText(getActivity(), R.string.unknow_error,
                    Toast.LENGTH_LONG).show();
            return true;
        }
        if (row.getFrom().trim().equals("0")) {
            isadmin = true;
        }
        String content = row.getContent();
        final String name = row.getAuthor();
        final String uid = String.valueOf(row.getFrom());
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.signature_dialog:
                if (isadmin) {
                    FunctionUtil.errordialogadmin(getActivity(), listView);
                } else {
                    FunctionUtil.Create_Signature_Dialog_Message(row, getActivity(), listView);
                }
                break;
            case R.id.avatar_dialog:
                if (isadmin) {
                    FunctionUtil.errordialogadmin(getActivity(), listView);
                } else {
                    FunctionUtil.Create_Avatar_Dialog_Meaasge(row, getActivity(), listView);
                }
                break;
            case R.id.send_message:
                if (isadmin) {
                    FunctionUtil.errordialogadmin(getActivity(), listView);
                } else {
                    start_send_message(row);
                }
                break;
            case R.id.show_profile:
                if (isadmin) {
                    FunctionUtil.errordialogadmin(getActivity(), listView);
                } else {
                    intent.putExtra("mode", "username");
                    intent.putExtra("username", row.getAuthor());
                    intent.setClass(getActivity(),
                            PhoneConfiguration.getInstance().profileActivityClass);
                    startActivity(intent);
                    if (PhoneConfiguration.getInstance().showAnimation)
                        getActivity().overridePendingTransition(R.anim.zoom_enter,
                                R.anim.zoom_exit);
                }
                break;
            case R.id.search_post:
                if (isadmin) {
                    FunctionUtil.errordialogadmin(getActivity(), listView);
                } else {
                    intent.putExtra("searchpost", 1);
                    try {
                        intent.putExtra("authorid", Integer.parseInt(row.getFrom()));
                    } catch (Exception e) {

                    }
                    intent.setClass(getActivity(),
                            PhoneConfiguration.getInstance().topicActivityClass);
                    startActivity(intent);
                    if (PhoneConfiguration.getInstance().showAnimation)
                        getActivity().overridePendingTransition(R.anim.zoom_enter,
                                R.anim.zoom_exit);
                }
                break;
            case R.id.search_subject:
                if (isadmin) {
                    FunctionUtil.errordialogadmin(getActivity(), listView);
                } else {
                    try {
                        intent.putExtra("authorid", Integer.parseInt(row.getFrom()));
                    } catch (Exception e) {

                    }
                    intent.setClass(getActivity(),
                            PhoneConfiguration.getInstance().topicActivityClass);
                    startActivity(intent);
                    if (PhoneConfiguration.getInstance().showAnimation)
                        getActivity().overridePendingTransition(R.anim.zoom_enter,
                                R.anim.zoom_exit);
                }
                break;
            case R.id.copy_to_clipboard:
                FunctionUtil.CopyDialog(content, getActivity(), listView);
                break;

            case R.id.quote_subject:

                final String quote_regex = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
                final String replay_regex = "\\[b\\]Reply to \\[pid=\\d+,\\d+,\\d+\\]Reply\\[/pid\\] Post by .+?\\[/b\\]";
                content = content.replaceAll(quote_regex, "");
                content = content.replaceAll(replay_regex, "");
                final String postTime = row.getTime();

                content = FunctionUtil.checkContent(content);
                content = StringUtil.unEscapeHtml(content);
                postPrefix.append("[quote]");
                postPrefix.append(" [b]Post by [uid=");
                postPrefix.append(uid);
                postPrefix.append("]");
                postPrefix.append(name);
                postPrefix.append("[/uid] (");
                postPrefix.append(postTime);
                postPrefix.append("):[/b]\n");
                postPrefix.append(content);
                postPrefix.append("[/quote]\n");

                // case R.id.r:

                intent.putExtra("prefix",
                        StringUtil.removeBrTag(postPrefix.toString()));
                intent.putExtra("mid", mid);
                intent.putExtra("action", "reply");
                intent.putExtra("title", title);
                intent.putExtra("to", to);
                intent.putExtra("messagemode", "yes");
                if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
                    intent.setClass(
                            getActivity(),
                            PhoneConfiguration.getInstance().messagePostActivityClass);
                } else {
                    intent.setClass(getActivity(),
                            PhoneConfiguration.getInstance().loginActivityClass);
                }
                startActivityForResult(intent, 123);
                if (PhoneConfiguration.getInstance().showAnimation)
                    getActivity().overridePendingTransition(R.anim.zoom_enter,
                            R.anim.zoom_exit);
                break;

        }
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnMessageDetialListContainerListener) activity;
        } catch (ClassCastException e) {
        }
    }

    private void nightMode(final MenuItem menu) {
        changeNightMode(menu);
        refresh_saying();
        if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
            mcontainer.setBackgroundResource(R.color.night_bg_color);
        } else {
            mcontainer.setBackgroundResource(R.color.shit1);
        }
        if (adapter != null)
            adapter.notifyDataSetChangedWithModChange();
        if (mCallback != null)
            mCallback.onModeChanged();
        if (attacher != null)
            attacher.setRefreshComplete();
    }

    private void start_send_message(MessageArticlePageInfo row) {
        Intent intent_bookmark = new Intent();
        intent_bookmark.putExtra("to", row.getAuthor());
        intent_bookmark.putExtra("action", "new");
        intent_bookmark.putExtra("messagemode", "yes");
        if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
            intent_bookmark.setClass(getActivity(),
                    PhoneConfiguration.getInstance().messagePostActivityClass);
        } else {
            intent_bookmark.setClass(getActivity(),
                    PhoneConfiguration.getInstance().loginActivityClass);
        }
        startActivity(intent_bookmark);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.articlelist_context_menu, menu);

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
        else {
            transformer.setRefreshingText(ActivityUtil.getSaying());
        }
        if (attacher != null)
            attacher.setRefreshing(true);
    }

    void refresh() {
        JsonMessageDetialLoadTask task = new JsonMessageDetialLoadTask(
                getActivity(), this);
        // ActivityUtil.getInstance().noticeSaying(this.getActivity());
        if (this.getActivity() != null) {
            adapter = new AppendableMessageDetialAdapter(this.getActivity(),
                    attacher, this);
            refresh_saying();
            task.execute(getUrl(1, mid, true, true));
        }
    }

    public String getUrl(int page, int mid, boolean isend, boolean restart) {

        String jsonUri = HttpUtil.Server
                + "/nuke.php?__lib=message&__act=message&act=read&";

        jsonUri += "page=" + page + "&mid=" + String.valueOf(mid)
                + "&lite=js&noprefix";

        return jsonUri;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int menuId;
        if (PhoneConfiguration.getInstance().HandSide == 1) {// lefthand
            int flag = PhoneConfiguration.getInstance().getUiFlag();
            if (flag == 1 || flag == 3 || flag == 5 || flag == 7) {// 主题列表，UIFLAG为1或者1+2或者1+4或者1+2+4
                menuId = R.menu.messagedetail_menu_left;
            } else {
                menuId = R.menu.messagedetail_menu;
            }
        } else {
            menuId = R.menu.messagedetail_menu;
        }
        inflater.inflate(menuId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.article_menuitem_refresh:
                this.refresh();
                break;
            case R.id.night_mode://OK
                nightMode(item);
                break;
            case R.id.article_menuitem_reply:
                Intent intent_bookmark = new Intent();
                intent_bookmark.putExtra("mid", mid);
                intent_bookmark.putExtra("title", title);
                intent_bookmark.putExtra("to", to);
                intent_bookmark.putExtra("action", "reply");
                intent_bookmark.putExtra("messagemode", "yes");
                if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
                    intent_bookmark
                            .setClass(
                                    getActivity(),
                                    PhoneConfiguration.getInstance().messagePostActivityClass);
                } else {
                    intent_bookmark.setClass(getActivity(),
                            PhoneConfiguration.getInstance().loginActivityClass);
                }
                startActivityForResult(intent_bookmark, 123);
                break;
            case R.id.article_menuitem_back:
            default:
                // case android.R.id.home:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(this).commit();
                OnChildFragmentRemovedListener father = null;
                try {
                    father = (OnChildFragmentRemovedListener) getActivity();
                    father.OnChildFragmentRemoved(getId());
                } catch (ClassCastException e) {
                    Log.e(TAG, "father activity does not implements interface "
                            + OnChildFragmentRemovedListener.class.getName());

                }
                break;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        canDismiss = false;
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 123) {
            refresh();
        }
    }

    @Override
    public void finishLoad(MessageDetialInfo result) {
        if (attacher != null)
            attacher.setRefreshComplete();

        if (result == null) {
            return;
        }

        title = result.get_Title();
        to = result.get_Alluser();
        adapter.finishLoad(result);
        listView.setAdapter(adapter);
        if (canDismiss)
            ActivityUtil.getInstance().dismiss();

    }

    @TargetApi(11)
    private void RunParallen(JsonMessageDetialLoadTask task) {
        task.executeOnExecutor(JsonMessageListLoadTask.THREAD_POOL_EXECUTOR,
                getUrl(adapter.getNextPage(), mid, adapter.getIsEnd(), false));
    }

    @Override
    public void loadNextPage(OnMessageDetialLoadFinishedListener callback) {
        JsonMessageDetialLoadTask task = new JsonMessageDetialLoadTask(
                getActivity(), callback);
        refresh_saying();
        if (ActivityUtil.isGreaterThan_2_3_3())
            RunParallen(task);
        else
            task.execute(getUrl(adapter.getNextPage(), mid, adapter.getIsEnd(),
                    false));
    }

    // Container Activity must implement this interface
    public interface OnMessageDetialListContainerListener {
        public void onModeChanged();
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
