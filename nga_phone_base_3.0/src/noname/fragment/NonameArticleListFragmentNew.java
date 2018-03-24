package noname.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import noname.gson.parse.NonameReadBody;
import noname.gson.parse.NonameReadResponse;
import noname.interfaces.OnNonameThreadPageLoadFinishedListener;
import noname.listener.MyListenerForNonameReply;
import noname.task.JsonNonameThreadLoadTask;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.PreferenceKey;
import sp.phone.task.ReportTask;
import sp.phone.theme.ThemeManager;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.ArticleListWebClient;
import sp.phone.utils.FunctionUtils;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.NLog;
import sp.phone.utils.StringUtils;

public class NonameArticleListFragmentNew extends Fragment implements
        OnNonameThreadPageLoadFinishedListener, PreferenceKey {
    final static private String TAG = NonameArticleListFragmentNew.class
            .getSimpleName();
    @SuppressWarnings("unused")
    private static Context activity;
    NonameReadResponse mData;
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
    private int page = 0;
    private int tid;
    @SuppressWarnings("unused")
    private String title;
    private int pid;
    private boolean needLoad = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        page = getArguments().getInt("page") + 1;
        tid = getArguments().getInt("id");
        super.onCreate(savedInstanceState);
        client = new ArticleListWebClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        scrollview = new ScrollView(this.getActivity());

        linear = (LinearLayout) LayoutInflater.from(getActivity()).inflate(
                R.layout.noname_article_scrollview, null, false);
        scrollview.addView(linear);
        scrollview.setBackgroundResource(ThemeManager.getInstance()
                .getBackgroundColor());

        scrollview
                .setDescendantFocusability(ScrollView.FOCUS_AFTER_DESCENDANTS);

        return scrollview;
    }

    private Object activeActionMode(final NonameReadResponse data,
                                    final int position) {
        Object mActionModeCallback = new ActionMode.Callback() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.nonamearticlelist_context_menu, menu);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                onContextItemSelected(item, data.data.posts[position], position);
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }

        };
        return mActionModeCallback;
    }

    public boolean onContextItemSelected(MenuItem item, NonameReadBody row,
                                         int position) {

        StringBuffer postPrefix = new StringBuffer();
        String tidStr = String.valueOf(this.tid);
        String content = row.content;
        final String name = row.hip;
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
                final long longposttime = row.ptime;
                String postTime = "";
                if (longposttime != 0) {
                    postTime = StringUtils.timeStamp2Date1(String
                            .valueOf(longposttime));
                }

                content = FunctionUtils.checkContent(content);
                content = StringUtils.unEscapeHtml(content);
                mention = name;
                postPrefix.append("[quote]");
                postPrefix.append("[b]Post by [hip]");
                postPrefix.append(name);
                postPrefix.append("[/hip] (");
                postPrefix.append(postTime);
                postPrefix.append("):[/b]\n");
                postPrefix.append(content);
                postPrefix.append("[/quote]\n");

                // case R.id.r:

                if (!StringUtils.isEmpty(mention))
                    intent.putExtra("mention", mention);
                intent.putExtra("prefix",
                        StringUtils.removeBrTag(postPrefix.toString()));
                intent.putExtra("tid", tidStr);
                intent.putExtra("action", "reply");
                intent.setClass(getActivity(),
                        PhoneConfiguration.getInstance().nonamePostActivityClass);
                startActivity(intent);
                break;
            case R.id.copy_to_clipboard:
                FunctionUtils.CopyDialog(content, getActivity(), scrollview);
                break;

        }
        return true;
    }

    @Override
    public void onResume() {
        NLog.d(TAG, "onResume pid=" + pid + "&page=" + page);
        // setHasOptionsMenu(true);

        this.loadPage();
        if (mData != null) {
            ((OnNonameThreadPageLoadFinishedListener) getActivity())
                    .finishLoad(mData);
        }
        super.onResume();
    }

    @TargetApi(11)
    private void RunParallen(JsonNonameThreadLoadTask task, String url) {
        task.executeOnExecutor(JsonNonameThreadLoadTask.THREAD_POOL_EXECUTOR,
                url);
    }

    @TargetApi(11)
    private void RunParallen(ReportTask task, String url) {
        task.executeOnExecutor(JsonNonameThreadLoadTask.THREAD_POOL_EXECUTOR,
                url);
    }

    private void loadPage() {
        if (needLoad) {

            Activity activity = getActivity();
            JsonNonameThreadLoadTask task = new JsonNonameThreadLoadTask(
                    activity, this);
            String url = HttpUtil.NonameServer + "/read.php?" + "&page=" + page
                    + "&lite=js&noprefix&v2";
            if (tid != 0)
                url = url + "&tid=" + tid;
            RunParallen(task, url);
        } else {
            ActivityUtils.getInstance().dismiss();
        }

    }

    public void modechange() {
        scrollview.setBackgroundResource(ThemeManager.getInstance()
                .getBackgroundColor());
        if (mData != null) {
            for (int i = 0; i < mData.data.posts.length; i++) {
                FunctionUtils.fillFormated_html_data(mData.data.posts[i], i,
                        getActivity());
            }
            linear.removeAllViewsInLayout();
            finishLoad(mData);
        }
    }

    @Override
    public void finishLoad(NonameReadResponse data) {
        NLog.d(TAG, "finishLoad");
        // ArticleListActivity father = (ArticleListActivity)
        // this.getActivity();
        if (null != data) {
            mData = data;
            tid = data.data.tid;
            title = data.data.title;
            create_pageview(data);
            OnNonameThreadPageLoadFinishedListener father = null;
            try {
                father = (OnNonameThreadPageLoadFinishedListener) getActivity();
                if (father != null)
                    father.finishLoad(data);
            } catch (ClassCastException e) {
                NLog.e(TAG,
                        "father activity should implements OnThreadPageLoadFinishedListener");
            }

        }
        ActivityUtils.getInstance().dismiss();
        this.needLoad = false;

    }

    private ViewHolder initHolder(final View view) {
        final ViewHolder holder = new ViewHolder();
        holder.nickNameTV = (TextView) view.findViewById(R.id.nickName);

        holder.floorTV = (TextView) view.findViewById(R.id.floor);
        holder.postTimeTV = (TextView) view.findViewById(R.id.postTime);
        holder.contentTV = (WebView) view.findViewById(R.id.content);
        holder.contentTV.setHorizontalScrollBarEnabled(false);
        holder.viewBtn = (ImageButton) view.findViewById(R.id.listviewreplybtn);
        holder.articlelistrelativelayout = (RelativeLayout) view
                .findViewById(R.id.articlelistrelativelayout);
        return holder;
    }

    public void create_pageview(NonameReadResponse data) {
        if (getActivity() != null) {
            for (int i = 0; i < data.data.posts.length; i++) {
                View view = LayoutInflater.from(getActivity()).inflate(
                        R.layout.noname_relative_nonamearitclelist, null, false);
                linear.addView(childview(view, data, i), i);
            }
        }
    }

    public View childview(View view, final NonameReadResponse data,
                          final int position) {
        ViewHolder holder = initHolder(view);
        final NonameReadBody row = data.data.posts[position];
        int lou = -1;
        if (row != null)
            lou = row.floor;
        MyListenerForNonameReply myListenerForReply = new MyListenerForNonameReply(
                position, getActivity(), mData);
        holder.viewBtn.setOnClickListener(myListenerForReply);
        ThemeManager theme = ThemeManager.getInstance();
        int colorId = theme.getBackgroundColor(position);
        view.setBackgroundResource(colorId);
        if (row == null) {
            return view;
        }
        int fgColorId = ThemeManager.getInstance().getForegroundColor();
        final int fgColor = getActivity().getResources().getColor(fgColorId);

        FunctionUtils.handleNickName(row, fgColor, holder.nickNameTV);
        final String floor = String.valueOf(lou);
        TextView floorTV = holder.floorTV;
        floorTV.setText("[" + floor + " ¥]");
        floorTV.setTextColor(fgColor);
        TextView postTimeTV = holder.postTimeTV;
        final long longposttime = row.ptime;
        String postTime = "";
        if (longposttime != 0) {
            postTime = StringUtils.timeStamp2Date1(String.valueOf(longposttime));
        }
        postTimeTV.setText(postTime);
        postTimeTV.setTextColor(fgColor);
        final int bgColor = getActivity().getResources().getColor(colorId);
        final WebView contentTV = holder.contentTV;
        final Callback mActionModeCallback = (Callback) activeActionMode(data,
                position);
        FunctionUtils.handleContentTV(contentTV, row, bgColor, fgColor,
                getActivity(), mActionModeCallback, client);
        holder.articlelistrelativelayout
                .setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        // TODO Auto-generated method stub
                        ((AppCompatActivity) getActivity())
                                .startSupportActionMode(mActionModeCallback);
                        return false;
                    }

                });
        return view;
    }

    static class ViewHolder {
        TextView nickNameTV;
        WebView contentTV;
        TextView floorTV;
        TextView postTimeTV;
        int position = -1;
        ImageButton viewBtn;
        RelativeLayout articlelistrelativelayout;
    }
}
