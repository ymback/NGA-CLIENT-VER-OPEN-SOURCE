package sp.phone.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.activity.MyApp;
import gov.anzong.androidnga.util.NetUtil;
import sp.phone.bean.AvatarTag;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.interfaces.AvatarLoadCompleteCallBack;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.interfaces.PagerOwnner;
import sp.phone.interfaces.ResetableArticle;
import sp.phone.listener.MyListenerForClient;
import sp.phone.listener.MyListenerForReply;
import sp.phone.task.AvatarLoadTask;
import sp.phone.task.JsonThreadLoadTask;
import sp.phone.task.ReportTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ArticleListWebClient;
import sp.phone.utils.FunctionUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

public class ArticleListFragmentNew extends Fragment implements
        OnThreadPageLoadFinishedListener, PerferenceConstant,
        AvatarLoadCompleteCallBack {
    final static private String TAG = ArticleListFragmentNew.class
            .getSimpleName();
    private final HashSet<String> urlSet = new HashSet<String>();
    private final Object lock = new Object();
    WebViewClient client;
    /*
     * static final int QUOTE_ORDER = 0; static final int REPLY_ORDER = 1;
     * static final int COPY_CLIPBOARD_ORDER = 2; static final int
     * SHOW_THISONLY_ORDER = 3; static final int SHOW_MODIFY_ORDER = 4; static
     * final int SHOW_ALL = 5; static final int POST_COMMENT = 6; static final
     * int SEARCH_POST = 7; static final int SEARCH_SUBJECT = 8;
     */
    private ScrollView scrollview = null;
    private LinearLayout linear = null;
    // private JsonThreadLoadTask task;
    private int page = 0;
    private int tid;
    private int pid;
    private int authorid;
    private boolean needLoad = true;
    private Toast toast;
    private ThreadData mData;
    /**
     * 头像处理开始
     **/
    private Bitmap defaultAvatar = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        PhoneConfiguration.getInstance().setRefreshAfterPost(
                false);
        Log.d(TAG, "onCreate");
        page = getArguments().getInt("page") + 1;
        tid = getArguments().getInt("id");
        pid = getArguments().getInt("pid", 0);
        authorid = getArguments().getInt("authorid", 0);
        super.onCreate(savedInstanceState);
        String fatheractivityclassname = getActivity().getClass()
                .getSimpleName();
        if (!StringUtil.isEmpty(fatheractivityclassname)) {
            if (fatheractivityclassname.indexOf("TopicListActivity") < 0)
                setRetainInstance(true);
        }
        client = new ArticleListWebClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        scrollview = new ScrollView(this.getActivity());
        linear = (LinearLayout) LayoutInflater.from(getActivity()).inflate(
                R.layout.article_scrollview, null, false);
        scrollview.addView(linear);
        scrollview.setBackgroundResource(ThemeManager.getInstance()
                .getBackgroundColor());

        scrollview
                .setDescendantFocusability(ScrollView.FOCUS_AFTER_DESCENDANTS);

        return scrollview;
    }

    private Object activeActionMode(final ThreadData data, final int position) {
        Object mActionModeCallback = new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                if (pid == 0) {
                    inflater.inflate(R.menu.articlelist_context_menu, menu);
                } else {
                    inflater.inflate(R.menu.articlelist_context_menu_with_tid,
                            menu);
                }
                ThreadRowInfo row = data.getRowList().get(position);

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
                onContextItemSelected(item, data.getRowList().get(position),
                        position);
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // int position = listview.getCheckedItemPosition();
                // listview.setItemChecked(position, false);

            }

        };
        return mActionModeCallback;
    }

    public boolean onContextItemSelected(MenuItem item, ThreadRowInfo row,
                                         int position) {

        StringBuffer postPrefix = new StringBuffer();
        String tidStr = String.valueOf(row.getTid());
        String content = row.getContent();
        final String name = row.getAuthor();
        final String uid = String.valueOf(row.getAuthorid());
        boolean isanonymous = row.getISANONYMOUS();
        String mention = null;
        Intent intent = new Intent();
        switch (item.getItemId()) {
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
                intent.putExtra("prefix",
                        StringUtil.removeBrTag(postPrefix.toString()));
                intent.putExtra("tid", tidStr);
                intent.putExtra("action", "reply");
                if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
                    intent.setClass(getActivity(),
                            PhoneConfiguration.getInstance().postActivityClass);
                } else {
                    intent.setClass(getActivity(),
                            PhoneConfiguration.getInstance().loginActivityClass);
                }
                startActivity(intent);
                if (PhoneConfiguration.getInstance().showAnimation)
                    getActivity().overridePendingTransition(R.anim.zoom_enter,
                            R.anim.zoom_exit);
                break;
            case R.id.signature_dialog:
                if (isanonymous) {
                    FunctionUtil.errordialog(getActivity(), scrollview);
                } else {
                    FunctionUtil.Create_Signature_Dialog(row, getActivity(),
                            scrollview);
                }
                break;
            case R.id.vote_dialog:
                FunctionUtil.Create_Vote_Dialog(row, getActivity(), scrollview,
                        toast);
                break;
            case R.id.ban_thisone:
                if (isanonymous) {
                    if (toast != null) {
                        toast.setText(R.string.cannot_add_to_blacklist_cause_anony);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        toast = Toast.makeText(getActivity(),
                                R.string.cannot_add_to_blacklist_cause_anony,
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    Set<Integer> blacklist = PhoneConfiguration.getInstance().blacklist;
                    String blickliststring = "";
                    if (row.get_isInBlackList()) {// 在屏蔽列表中，需要去除
                        row.set_IsInBlackList(false);
                        blacklist.remove(row.getAuthorid());
                        if (toast != null) {
                            toast.setText(R.string.remove_from_blacklist_success);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            toast = Toast.makeText(getActivity(),
                                    R.string.remove_from_blacklist_success,
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } else {
                        row.set_IsInBlackList(true);
                        blacklist.add(row.getAuthorid());
                        if (toast != null) {
                            toast.setText(R.string.add_to_blacklist_success);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            toast = Toast.makeText(getActivity(),
                                    R.string.add_to_blacklist_success,
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
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
                        if (toast != null) {
                            toast.setText(R.string.cannot_add_to_blacklist_cause_logout);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            toast = Toast.makeText(getActivity(),
                                    R.string.cannot_add_to_blacklist_cause_logout,
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
                break;
            case R.id.show_profile:
                if (isanonymous) {
                    FunctionUtil.errordialog(getActivity(), scrollview);
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
            case R.id.avatar_dialog:
                if (isanonymous) {
                    FunctionUtil.errordialog(getActivity(), scrollview);
                } else {
                    FunctionUtil.Create_Avatar_Dialog(row, getActivity(),
                            scrollview);
                }
                break;
            case R.id.edit:
                if (FunctionUtil.isComment(row)) {
                    if (toast != null) {
                        toast.setText(R.string.cannot_eidt_comment);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        toast = Toast.makeText(getActivity(),
                                R.string.cannot_eidt_comment, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    break;
                }
                Intent intentModify = new Intent();
                intentModify.putExtra("prefix",
                        StringUtil.unEscapeHtml(StringUtil.removeBrTag(content)));
                intentModify.putExtra("tid", tidStr);
                String mpid = String.valueOf(row.getPid());// getPid(map.get("url"));
                intentModify.putExtra("pid", mpid);
                intentModify.putExtra("title",
                        StringUtil.unEscapeHtml(row.getSubject()));
                intentModify.putExtra("action", "modify");
                if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
                    intentModify.setClass(getActivity(),
                            PhoneConfiguration.getInstance().postActivityClass);
                } else {
                    intentModify.setClass(getActivity(),
                            PhoneConfiguration.getInstance().loginActivityClass);
                }
                startActivity(intentModify);
                if (PhoneConfiguration.getInstance().showAnimation)
                    getActivity().overridePendingTransition(R.anim.zoom_enter,
                            R.anim.zoom_exit);
                break;
            case R.id.copy_to_clipboard:
                FunctionUtil.CopyDialog(row.getFormated_html_data(), getActivity(),
                        scrollview);
                break;
            case R.id.show_this_person_only:

                if (null == getActivity().findViewById(R.id.item_detail_container)) {
                    Intent intentThis = new Intent();
                    intentThis.putExtra("tab", "1");
                    intentThis.putExtra("tid", tid);
                    intentThis.putExtra("authorid", row.getAuthorid());
                    intentThis.putExtra("fromreplyactivity", 1);
                    intentThis.setClass(getActivity(),
                            PhoneConfiguration.getInstance().articleActivityClass);
                    startActivity(intentThis);
                    if (PhoneConfiguration.getInstance().showAnimation)
                        getActivity().overridePendingTransition(R.anim.zoom_enter,
                                R.anim.zoom_exit);
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
                    FunctionUtil.errordialog(getActivity(), scrollview);
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
                if (pid == 0) {
                    shareUrl = shareUrl + "pid=" + row.getPid()
                            + " (分享自NGA安卓客户端开源版)";
                } else {
                    shareUrl = shareUrl + "tid=" + tid + " (分享自NGA安卓客户端开源版)";
                }
                if (!StringUtil.isEmpty(row.getSubject())) {
                    shareUrl = "《" + row.getSubject() + "》 - 艾泽拉斯国家地理论坛，地址："
                            + shareUrl;
                }
                intent.putExtra(Intent.EXTRA_TEXT, shareUrl);
                String text = getResources().getString(R.string.share);
                getActivity().startActivity(Intent.createChooser(intent, text));
                break;

        }
        return true;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume pid=" + pid + "&page=" + page);

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
                        linear.removeAllViewsInLayout();
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

    public void modechange() {
        scrollview.setBackgroundResource(ThemeManager.getInstance()
                .getBackgroundColor());
        if (mData != null) {
            for (int i = 0; i < mData.getRowList().size(); i++) {
                FunctionUtil.fillFormated_html_data(mData.getRowList().get(i),
                        i, getActivity());
            }
            linear.removeAllViewsInLayout();
            finishLoad(mData);
        }
    }

    @Override
    public void finishLoad(ThreadData data) {
        Log.d(TAG, "finishLoad");
        // ArticleListActivity father = (ArticleListActivity)
        // this.getActivity();
        if (null != data) {
            mData = data;
            if (0 != data.getThreadInfo().getQuote_from())
                tid = data.getThreadInfo().getQuote_from();
            create_pageview(data);
            OnThreadPageLoadFinishedListener father = null;
            try {
                father = (OnThreadPageLoadFinishedListener) getActivity();
                if (father != null)
                    father.finishLoad(data);
            } catch (ClassCastException e) {
                Log.e(TAG,
                        "father activity should implements OnThreadPageLoadFinishedListener");
            }
        }
        ActivityUtil.getInstance().dismiss();
        this.needLoad = false;
    }

    private ViewHolder initHolder(final View view) {
        final ViewHolder holder = new ViewHolder();
        holder.articlelistrelativelayout = (RelativeLayout) view
                .findViewById(R.id.articlelistrelativelayout);
        holder.nickNameTV = (TextView) view.findViewById(R.id.nickName);
        holder.avatarIV = (ImageView) view.findViewById(R.id.avatarImage);
        holder.floorTV = (TextView) view.findViewById(R.id.floor);
        holder.postTimeTV = (TextView) view.findViewById(R.id.postTime);
        holder.contentTV = (WebView) view.findViewById(R.id.content);
        holder.contentTV.setHorizontalScrollBarEnabled(false);
        holder.viewBtn = (ImageButton) view.findViewById(R.id.listviewreplybtn);
        holder.clientBtn = (ImageButton) view.findViewById(R.id.clientbutton);
        return holder;
    }

    public void create_pageview(ThreadData data) {
        if (getActivity() != null) {
            for (int i = 0; i < data.getRowList().size(); i++) {
                View view = LayoutInflater.from(getActivity()).inflate(
                        R.layout.relative_aritclelist, null, false);
                linear.addView(childview(view, data, i), i);
            }
        }
    }

    public View childview(View view, final ThreadData data, final int position) {
        ViewHolder holder = initHolder(view);
        final ThreadRowInfo row = data.getRowList().get(position);
        int lou = -1;
        if (row != null)
            lou = row.getLou();
        if (!PhoneConfiguration.getInstance().showReplyButton) {
            holder.viewBtn.setVisibility(View.GONE);
        } else {
            MyListenerForReply myListenerForReply = new MyListenerForReply(
                    position, data, getActivity());
            holder.viewBtn.setOnClickListener(myListenerForReply);
        }
        ThemeManager theme = ThemeManager.getInstance();
        int colorId = theme.getBackgroundColor(position);
        view.setBackgroundResource(colorId);
        if (row == null) {
            return view;
        }
        handleAvatar(holder.avatarIV, row);
        int fgColorId = ThemeManager.getInstance().getForegroundColor();
        final int fgColor = getActivity().getResources().getColor(fgColorId);

        FunctionUtil.handleNickName(row, fgColor, holder.nickNameTV,
                getActivity());
        final String floor = String.valueOf(lou);
        TextView floorTV = holder.floorTV;
        floorTV.setText("[" + floor + " 楼]");
        floorTV.setTextColor(fgColor);
        if (!StringUtil.isEmpty(row.getFromClientModel())) {
            MyListenerForClient myListenerForClient = new MyListenerForClient(
                    position, data, getActivity(), scrollview);
            String from_client_model = row.getFromClientModel();
            if (from_client_model.equals("ios")) {
                holder.clientBtn.setImageResource(R.drawable.ios);// IOS
            } else if (from_client_model.equals("wp")) {
                holder.clientBtn.setImageResource(R.drawable.wp);// WP
            } else if (from_client_model.equals("unknown")) {
                holder.clientBtn.setImageResource(R.drawable.unkonwn);// 未知orBB
            }
            holder.clientBtn.setVisibility(View.VISIBLE);
            holder.clientBtn.setOnClickListener(myListenerForClient);
        }
        TextView postTimeTV = holder.postTimeTV;
        postTimeTV.setText(row.getPostdate());
        postTimeTV.setTextColor(fgColor);
        final int bgColor = getActivity().getResources().getColor(colorId);
        final WebView contentTV = holder.contentTV;
        final Callback mActionModeCallback = (Callback) activeActionMode(data,
                position);
        FunctionUtil.handleContentTV(contentTV, row, bgColor, fgColor,
                getActivity(), mActionModeCallback, client);
        holder.articlelistrelativelayout
                .setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        // TODO Auto-generated method stub
                        ((ActionBarActivity) getActivity())
                                .startSupportActionMode(mActionModeCallback);
                        return false;
                    }

                });
        return view;
    }

    private void handleAvatar(ImageView avatarIV, ThreadRowInfo row) {

        final int lou = row.getLou();
        final String avatarUrl = FunctionUtil.parseAvatarUrl(row
                .getJs_escap_avatar());//
        final String userId = String.valueOf(row.getAuthorid());
        if (PhoneConfiguration.getInstance().nikeWidth < 3) {
            avatarIV.setImageBitmap(null);
            return;
        }
        if (defaultAvatar == null
                || defaultAvatar.getWidth() != PhoneConfiguration.getInstance().nikeWidth) {
            Resources res = avatarIV.getContext().getResources();
            InputStream is = res.openRawResource(R.raw.default_avatar);
            InputStream is2 = res.openRawResource(R.raw.default_avatar);
            this.defaultAvatar = ImageUtil.loadAvatarFromStream(is, is2);
        }

        Object tagObj = avatarIV.getTag();
        if (tagObj instanceof AvatarTag) {
            AvatarTag origTag = (AvatarTag) tagObj;
            if (origTag.isDefault == false) {
                ImageUtil.recycleImageView(avatarIV);
                // Log.d(TAG, "recycle avatar:" + origTag.lou);
            } else {
                // Log.d(TAG, "default avatar, skip recycle");
            }
        }

        AvatarTag tag = new AvatarTag(lou, true);
        avatarIV.setImageBitmap(defaultAvatar);
        avatarIV.setTag(tag);
        if (!StringUtil.isEmpty(avatarUrl)) {
            final String avatarPath = ImageUtil.newImage(avatarUrl, userId);
            if (avatarPath != null) {
                File f = new File(avatarPath);
                if (f.exists() && !isPending(avatarUrl)) {

                    Bitmap bitmap = ImageUtil.loadAvatarFromSdcard(avatarPath);
                    if (bitmap != null) {
                        avatarIV.setImageBitmap(bitmap);
                        tag.isDefault = false;
                    } else
                        f.delete();
                    long date = f.lastModified();
                    if ((System.currentTimeMillis() - date) / 1000 > 30 * 24 * 3600) {
                        f.delete();
                    }

                } else {
                    final boolean downImg = NetUtil.getInstance().isInWifi()
                            || PhoneConfiguration.getInstance().isDownAvatarNoWifi();

                    new AvatarLoadTask(avatarIV, null, downImg, lou, this)
                            .execute(avatarUrl, avatarPath, userId);
                }
            }
        }

    }

    private boolean isPending(String url) {
        boolean ret = false;
        synchronized (lock) {
            ret = urlSet.contains(url);
        }
        return ret;
    }

    @Override
    public void OnAvatarLoadStart(String url) {
        synchronized (lock) {
            this.urlSet.add(url);
        }
    }

    @Override
    public void OnAvatarLoadComplete(String url) {
        synchronized (lock) {
            this.urlSet.remove(url);
        }
    }

    static class ViewHolder {
        RelativeLayout articlelistrelativelayout;
        TextView nickNameTV;
        ImageView avatarIV;
        WebView contentTV;
        TextView floorTV;
        TextView postTimeTV;
        TextView levelTV;
        TextView aurvrcTV;
        TextView postnumTV;
        int position = -1;
        ImageButton viewBtn;
        ImageButton clientBtn;

    }

    /** 头像处理结束 **/
}
