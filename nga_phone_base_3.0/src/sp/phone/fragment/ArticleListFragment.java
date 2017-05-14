package sp.phone.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import java.util.Set;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.activity.MyApp;
import sp.phone.adapter.ArticleListAdapter;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.interfaces.PagerOwnner;
import sp.phone.interfaces.ResetableArticle;
import sp.phone.task.JsonThreadLoadTask;
import sp.phone.task.ReportTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.FunctionUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

/**
 * 帖子详情分页
 */
public class ArticleListFragment extends BaseFragment implements
        OnThreadPageLoadFinishedListener, PerferenceConstant {
    final static private String TAG = ArticleListFragment.class.getSimpleName();
    /*
     * static final int QUOTE_ORDER = 0; static final int REPLY_ORDER = 1;
     * static final int COPY_CLIPBOARD_ORDER = 2; static final int
     * SHOW_THISONLY_ORDER = 3; static final int SHOW_MODIFY_ORDER = 4; static
     * final int SHOW_ALL = 5; static final int POST_COMMENT = 6; static final
     * int SEARCH_POST = 7; static final int SEARCH_SUBJECT = 8;
     */
    private ListView listview = null;
    private ArticleListAdapter articleAdpater;
    // private JsonThreadLoadTask task;
    private int page = 0;
    private int tid;
    private String title;
    private int pid;
    private int authorid;
    private boolean needLoad = true;
    private Object mActionModeCallback = null;

    private ThreadData mData;
    private int mListPosition;
    private int mListFirstTop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        PhoneConfiguration.getInstance().setRefreshAfterPost(
                false);
        Log.d(TAG, "onCreate");
        page = getArguments().getInt("page") + 1;
        tid = getArguments().getInt("id");
        pid = getArguments().getInt("pid", 0);
        authorid = getArguments().getInt("authorid", 0);
        articleAdpater = new ArticleListAdapter(this.getActivity());
        super.onCreate(savedInstanceState);
        String fatheractivityclassname = getActivity().getClass()
                .getSimpleName();
        if (!StringUtil.isEmpty(fatheractivityclassname)) {
            if (fatheractivityclassname.indexOf("TopicListActivity") < 0)
                setRetainInstance(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listview = new ListView(this.getActivity());

        listview.setBackgroundResource(ThemeManager.getInstance().getBackgroundColor());
        listview.setDivider(null);

        activeActionMode();
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) parent;
                lv.setItemChecked(position, true);
                if (mActionModeCallback != null) {
                    ((ActionBarActivity) getActivity()).startSupportActionMode((Callback) mActionModeCallback);
                    return true;
                }
                return false;
            }

        });

        listview.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);
        return listview;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        listview.setAdapter(articleAdpater);
        super.onActivityCreated(savedInstanceState);
    }

    @TargetApi(11)
    private void activeActionMode() {
        mActionModeCallback = new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                if (pid == 0) {
                    inflater.inflate(R.menu.articlelist_context_menu, menu);
                } else {
                    inflater.inflate(R.menu.articlelist_context_menu_with_tid,
                            menu);
                }
                int position = listview.getCheckedItemPosition();
                ThreadRowInfo row = new ThreadRowInfo();
                if (position < listview.getCount())
                    row = (ThreadRowInfo) listview.getItemAtPosition(position);

                MenuItem mi = (MenuItem) menu.findItem(R.id.ban_thisone);
                if (mi != null && row != null) {
                    if (row.get_isInBlackList()) {// 处于屏蔽列表，需要去掉
                        mi.setTitle(R.string.cancel_ban_thisone);
                    } else {
                        mi.setTitle(R.string.ban_thisone);
                    }
                }
                MenuItem votemenu = (MenuItem) menu.findItem(R.id.vote_dialog);
                if (votemenu != null && StringUtil.isEmpty(row.getVote())) {
                    menu.removeItem(R.id.vote_dialog);
                }
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
    public void onResume() {
        Log.d(TAG, "onResume pid=" + pid + "&page=" + page);
        // setHasOptionsMenu(true);

        if (PhoneConfiguration.getInstance().refresh_after_post_setting_mode) {
            if (PhoneConfiguration.getInstance().isRefreshAfterPost()) {

                PagerOwnner father = null;
                try {
                    father = (PagerOwnner) getActivity();
                    if (father.getCurrentPage() == page) {
                        PhoneConfiguration.getInstance().setRefreshAfterPost(
                                false);
                        // this.task = null;
                        this.needLoad = true;
                    }
                } catch (ClassCastException e) {
                    Log.e(TAG, "father activity does not implements interface "
                            + PagerOwnner.class.getName());

                }

            }
        }
        loadPage();
        if (mData != null) {
            ((OnThreadPageLoadFinishedListener) getActivity())
                    .finishLoad(mData);
        }
        super.onResume();
        listview.setSelectionFromTop(mListPosition, mListFirstTop);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (listview.getChildCount() >= 1) {
            mListPosition = listview.getFirstVisiblePosition();
            mListFirstTop = listview.getChildAt(0).getTop();
        }
    }

    @TargetApi(11)
    private void RunParallen(JsonThreadLoadTask task, String url) {
        task.executeOnExecutor(JsonThreadLoadTask.THREAD_POOL_EXECUTOR, url);
    }

    @TargetApi(11)
    private void RunParallen(ReportTask task, String url) {
        task.executeOnExecutor(JsonThreadLoadTask.THREAD_POOL_EXECUTOR, url);
    }

    private void loadPage() {
        if (needLoad) {
            Log.d(TAG, "loadPage" + page);
            Activity activity = getActivity();
            JsonThreadLoadTask task = new JsonThreadLoadTask(activity, this);
            String url = HttpUtil.Server + "/read.php?" + "&page=" + page
                    + "&lite=js&noprefix&v2";
            if (tid != 0)
                url = url + "&tid=" + tid;
            if (pid != 0) {
                url = url + "&pid=" + pid;
            }

            if (authorid != 0) {
                url = url + "&authorid=" + authorid;
            }
            if (ActivityUtil.isGreaterThan_2_3_3())
                RunParallen(task, url);
            else
                task.execute(url);
        } else {
            ActivityUtil.getInstance().dismiss();
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        if (this.pid == 0) {
            inflater.inflate(R.menu.articlelist_context_menu, menu);

        } else {
            inflater.inflate(R.menu.articlelist_context_menu_with_tid, menu);
        }
        int position = listview.getCheckedItemPosition();
        ThreadRowInfo row = new ThreadRowInfo();
        if (position < listview.getCount())
            row = (ThreadRowInfo) listview.getItemAtPosition(position);

        MenuItem mi = (MenuItem) menu.findItem(R.id.ban_thisone);
        if (mi != null && row != null) {
            if (row.get_isInBlackList()) {// 处于屏蔽列表，需要去掉
                mi.setTitle(R.string.cancel_ban_thisone);
            } else {
                mi.setTitle(R.string.ban_thisone);
            }
        }
        MenuItem votemenu = (MenuItem) menu.findItem(R.id.vote_dialog);
        if (votemenu != null && StringUtil.isEmpty(row.getVote())) {
            menu.removeItem(R.id.vote_dialog);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Log.d(TAG, "onContextItemSelected,tid=" + tid + ",page=" + page);
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

        if (father.getCurrentPage() != page) {
            return false;
        }

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        int position = this.listview.getCheckedItemPosition();
        if (info != null) {
            position = info.position;
        }
        if (position < 0 || position >= listview.getAdapter().getCount()) {
            showToast(R.string.floor_error);
            position = 0;
        }
        StringBuffer postPrefix = new StringBuffer();
        String tidStr = String.valueOf(this.tid);

        ThreadRowInfo row = (ThreadRowInfo) listview
                .getItemAtPosition(position);
        if (row == null) {
            showToast(R.string.unknow_error);
            return true;
        }
        String content = row.getContent();
        final String name = row.getAuthor();
        final String uid = String.valueOf(row.getAuthorid());
        boolean isanonymous = row.getISANONYMOUS();
        String mention = null;
        Intent intent = new Intent();
        switch (item.getItemId())
        // if( REPLY_POST_ORDER ==item.getItemId())
        {
            case R.id.quote_subject:

                final String quote_regex = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
                final String replay_regex = "\\[b\\]Reply to \\[pid=\\d+,\\d+,\\d+\\]Reply\\[/pid\\] Post by .+?\\[/b\\]";
                content = content.replaceAll(quote_regex, "");
                content = content.replaceAll(replay_regex, "");
                final String postTime = row.getPostdate();

                content = FunctionUtil.checkContent(content);
                content = StringUtil.unEscapeHtml(content);
                if (row.getPid() != 0) {
                    mention = name;
                    postPrefix.append("[quote][pid=");
                    postPrefix.append(row.getPid());
                    postPrefix.append(',').append(tidStr).append(",").append(page);
                    postPrefix.append("]");// Topic
                    postPrefix.append("Reply");
                    if (row.getISANONYMOUS()) {// 是匿名的人
                        postPrefix.append("[/pid] [b]Post by [uid=");
                        postPrefix.append("-1");
                        postPrefix.append("]");
                        postPrefix.append(name);
                        postPrefix.append("[/uid][color=gray](");
                        postPrefix.append(row.getLou());
                        postPrefix.append("楼)[/color] (");
                    } else {
                        postPrefix.append("[/pid] [b]Post by [uid=");
                        postPrefix.append(uid);
                        postPrefix.append("]");
                        postPrefix.append(name);
                        postPrefix.append("[/uid] (");
                    }
                    postPrefix.append(postTime);
                    postPrefix.append("):[/b]\n");
                    postPrefix.append(content);
                    postPrefix.append("[/quote]\n");
                }

                // case R.id.r:

                if (!StringUtil.isEmpty(mention))
                    intent.putExtra("mention", mention);
                intent.putExtra("prefix", StringUtil.removeBrTag(postPrefix.toString()));
                intent.putExtra("tid", tidStr);
                intent.putExtra("action", "reply");
                if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
                    intent.setClass(getActivity(), PhoneConfiguration.getInstance().postActivityClass);
                } else {
                    intent.setClass(getActivity(), PhoneConfiguration.getInstance().loginActivityClass);
                }
                startActivity(intent);
                if (PhoneConfiguration.getInstance().showAnimation)
                    getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                break;

            case R.id.signature_dialog:
                if (isanonymous) {
                    FunctionUtil.errordialog(getActivity(), listview);
                } else {
                    FunctionUtil.Create_Signature_Dialog(row, getActivity(),
                            listview);
                }
                break;
            case R.id.vote_dialog:
                FunctionUtil.Create_Vote_Dialog(row, getActivity(), listview, toast);
                break;

            case R.id.ban_thisone:
                if (isanonymous) {
                    showToast(R.string.cannot_add_to_blacklist_cause_anony);
                } else {
                    Set<Integer> blacklist = PhoneConfiguration.getInstance().blacklist;
                    String blickliststring = "";
                    if (row.get_isInBlackList()) {// 在屏蔽列表中，需要去除
                        row.set_IsInBlackList(false);
                        blacklist.remove(row.getAuthorid());
                        showToast(R.string.remove_from_blacklist_success);
                    } else {
                        row.set_IsInBlackList(true);
                        blacklist.add(row.getAuthorid());
                        showToast(R.string.add_to_blacklist_success);
                    }
                    PhoneConfiguration.getInstance().blacklist = blacklist;
                    blickliststring = blacklist.toString();
                    SharedPreferences share = getActivity().getSharedPreferences(
                            PERFERENCE, Context.MODE_PRIVATE);
                    Editor editor = share.edit();
                    editor.putString(BLACK_LIST, blickliststring);
                    editor.apply();
                    if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().uid)) {
                        MyApp app = (MyApp) getActivity().getApplication();
                        app.upgradeUserdata(blacklist.toString());
                    } else {
                        showToast(R.string.cannot_add_to_blacklist_cause_logout);
                    }
                }
                break;
            case R.id.show_profile:
                if (isanonymous) {
                    FunctionUtil.errordialog(getActivity(), listview);
                } else {
                    intent.putExtra("mode", "username");
                    intent.putExtra("username", row.getAuthor());
                    intent.setClass(getActivity(), PhoneConfiguration.getInstance().profileActivityClass);
                    startActivity(intent);
                    if (PhoneConfiguration.getInstance().showAnimation)
                        getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                }
                break;
            case R.id.avatar_dialog:
                if (isanonymous) {
                    FunctionUtil.errordialog(getActivity(), listview);
                } else {
                    FunctionUtil.Create_Avatar_Dialog(row, getActivity(), listview);
                }
                break;
            case R.id.edit:
                if (FunctionUtil.isComment(row)) {
                    showToast(R.string.cannot_eidt_comment);
                    break;
                }
                Intent intentModify = new Intent();
                intentModify.putExtra("prefix", StringUtil.unEscapeHtml(StringUtil.removeBrTag(content)));
                intentModify.putExtra("tid", tidStr);
                String pid = String.valueOf(row.getPid());// getPid(map.get("url"));
                intentModify.putExtra("pid", pid);
                intentModify.putExtra("title", StringUtil.unEscapeHtml(row.getSubject()));
                intentModify.putExtra("action", "modify");
                if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
                    intentModify.setClass(getActivity(), PhoneConfiguration.getInstance().postActivityClass);
                } else {
                    intentModify.setClass(getActivity(), PhoneConfiguration.getInstance().loginActivityClass);
                }
                startActivity(intentModify);
                if (PhoneConfiguration.getInstance().showAnimation)
                    getActivity().overridePendingTransition(R.anim.zoom_enter,  R.anim.zoom_exit);
                break;
            case R.id.copy_to_clipboard:
                FunctionUtil.CopyDialog(row.getFormated_html_data(), getActivity(), listview);
                break;
            case R.id.show_this_person_only:

                if (null == getActivity().findViewById(R.id.item_detail_container)) {
                    Intent intentThis = new Intent();
                    intentThis.putExtra("tab", "1");
                    intentThis.putExtra("tid", tid);
                    intentThis.putExtra("authorid", row.getAuthorid());
                    intentThis.putExtra("fromreplyactivity", 1);
                    intentThis.setClass(getActivity(), PhoneConfiguration.getInstance().articleActivityClass);
                    startActivity(intentThis);
                    if (PhoneConfiguration.getInstance().showAnimation)
                        getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                } else {
                    int tid1 = tid;
                    int authorid1 = row.getAuthorid();
                    ArticleContainerFragment f = ArticleContainerFragment
                            .createshowonly(tid1, authorid1);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.addToBackStack(null);
                    f.setHasOptionsMenu(true);
                    ft.replace(R.id.item_detail_container, f);
                    ft.commit();

                }

                // restNotifier.reset(0, row.getAuthorid());
                // ActivityUtil.getInstance().noticeSaying(getActivity());

                break;
            case R.id.show_whole_thread:
                if (null == getActivity().findViewById(R.id.item_detail_container)) {
                    ResetableArticle restNotifier = null;
                    try {
                        restNotifier = (ResetableArticle) getActivity();
                    } catch (ClassCastException e) {
                        Log.e(TAG, "father activity does not implements interface "
                                + ResetableArticle.class.getName());
                        return true;
                    }
                    restNotifier.reset(0, 0, row.getLou());
                    ActivityUtil.getInstance().noticeSaying(getActivity());
                } else {
                    int tid1 = tid;
                    ArticleContainerFragment f = ArticleContainerFragment
                            .createshowall(tid1);
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.addToBackStack(null);
                    f.setHasOptionsMenu(true);
                    ft.replace(R.id.item_detail_container, f);
                    ft.commit();
                }
                break;
            case R.id.send_message:
                if (isanonymous) {
                    FunctionUtil.errordialog(getActivity(), listview);
                } else {
                    FunctionUtil.start_send_message(getActivity(), row);
                }
                break;
            case R.id.post_comment:

                final String quote_regex1 = "\\[quote\\]([\\s\\S])*\\[/quote\\]";
                final String replay_regex1 = "\\[b\\]Reply to \\[pid=\\d+,\\d+,\\d+\\]Reply\\[/pid\\] Post by .+?\\[/b\\]";
                content = content.replaceAll(quote_regex1, "");
                content = content.replaceAll(replay_regex1, "");
                final String postTime1 = row.getPostdate();

                content = FunctionUtil.checkContent(content);
                content = StringUtil.unEscapeHtml(content);
                if (row.getPid() != 0) {
                    mention = name;
                    postPrefix.append("[quote][pid=");
                    postPrefix.append(row.getPid());
                    postPrefix.append(',').append(tidStr).append(",").append(page);
                    postPrefix.append("]");// Topic
                    postPrefix.append("Reply");
                    if (row.getISANONYMOUS()) {// 是匿名的人
                        postPrefix.append("[/pid] [b]Post by [uid=");
                        postPrefix.append("-1");
                        postPrefix.append("]");
                        postPrefix.append(name);
                        postPrefix.append("[/uid][color=gray](");
                        postPrefix.append(row.getLou());
                        postPrefix.append("楼)[/color] (");
                    } else {
                        postPrefix.append("[/pid] [b]Post by [uid=");
                        postPrefix.append(uid);
                        postPrefix.append("]");
                        postPrefix.append(name);
                        postPrefix.append("[/uid] (");
                    }
                    postPrefix.append(postTime1);
                    postPrefix.append("):[/b]\n");
                    postPrefix.append(content);
                    postPrefix.append("[/quote]\n");
                }

                final String dialog_tag = "post comment";
                FragmentTransaction ft = getActivity().getSupportFragmentManager()
                        .beginTransaction();
                Fragment prev = getActivity().getSupportFragmentManager()
                        .findFragmentByTag(dialog_tag);
                if (prev != null) {
                    ft.remove(prev);
                }
                DialogFragment df = new PostCommentDialogFragment();
                Bundle b = new Bundle();
                b.putInt("pid", row.getPid());
                b.putInt("fid", row.getFid());
                b.putInt("tid", this.tid);
                String prefix = StringUtil.removeBrTag(postPrefix.toString());
                if (!StringUtil.isEmpty(prefix)) {
                    prefix = prefix + "\n";
                }
                intent.putExtra("prefix", prefix
                );
                df.setArguments(b);
                df.show(ft, dialog_tag);

                break;
            case R.id.report:
                FunctionUtil.handleReport(row, tid, getFragmentManager());
                break;
            case R.id.search_post:
                intent.putExtra("searchpost", 1);
            case R.id.search_subject:
                intent.putExtra("authorid", row.getAuthorid());
                intent.setClass(getActivity(),
                        PhoneConfiguration.getInstance().topicActivityClass);
                startActivity(intent);
                if (PhoneConfiguration.getInstance().showAnimation)
                    getActivity().overridePendingTransition(R.anim.zoom_enter,
                            R.anim.zoom_exit);

                break;
            case R.id.item_share:
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareUrl = Utils.getNGAHost() + "read.php?";
                if (row.getPid() != 0) {
                    shareUrl = shareUrl + "pid=" + row.getPid() + " (分享自NGA安卓客户端开源版)";
                } else {
                    shareUrl = shareUrl + "tid=" + tid + " (分享自NGA安卓客户端开源版)";
                }
                if (!StringUtil.isEmpty(this.title)) {
                    shareUrl = "《" + this.title + "》 - 艾泽拉斯国家地理论坛，地址：" + shareUrl;
                }
                intent.putExtra(Intent.EXTRA_TEXT, shareUrl);
                String text = getResources().getString(R.string.share);
                getActivity().startActivity(Intent.createChooser(intent, text));
                break;

        }
        return true;
    }

    public void modechange() {
        listview.setBackgroundResource(ThemeManager.getInstance().getBackgroundColor());
        if (mData != null) {
            for (int i = 0; i < mData.getRowList().size(); i++) {
                FunctionUtil.fillFormated_html_data(mData.getRowList().get(i), i, getActivity());
            }
            finishLoad(mData);
        }
    }

    @Override
    public void finishLoad(ThreadData data) {
        Log.d(TAG, "finishLoad");
        if (null != data) {
            mData = data;
            articleAdpater.setData(data);
            articleAdpater.notifyDataSetChanged();

            if (0 != data.getThreadInfo().getQuote_from())
                tid = data.getThreadInfo().getQuote_from();
            if (!StringUtil.isEmpty(data.getThreadInfo().getSubject())) {
                title = data.getThreadInfo().getSubject();
            }
            OnThreadPageLoadFinishedListener father = null;
            try {
                father = (OnThreadPageLoadFinishedListener) getActivity();
                if (father != null)
                    father.finishLoad(data);
            } catch (ClassCastException e) {
                Log.e(TAG, "father activity should implements OnThreadPageLoadFinishedListener");
            }

        }
        ActivityUtil.getInstance().dismiss();
        this.needLoad = false;
    }
}
