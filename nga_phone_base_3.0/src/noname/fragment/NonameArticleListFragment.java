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
import android.widget.Toast;

import gov.anzong.androidnga.R;
import noname.gson.parse.NonameReadBody;
import noname.gson.parse.NonameReadResponse;
import noname.adapter.NonameArticleListAdapter;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.PreferenceKey;
import sp.phone.theme.ThemeManager;
import noname.interfaces.OnNonameThreadPageLoadFinishedListener;
import noname.interfaces.PagerOwner;
import noname.task.JsonNonameThreadLoadTask;
import sp.phone.task.ReportTask;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.FunctionUtils;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.NLog;
import sp.phone.utils.StringUtils;

public class NonameArticleListFragment extends Fragment implements
        OnNonameThreadPageLoadFinishedListener, PreferenceKey {
    final static private String TAG = NonameArticleListFragment.class
            .getSimpleName();
    @SuppressWarnings("unused")
    private static Context activity;
    NonameReadResponse result;
    /*
     * static final int QUOTE_ORDER = 0; static final int REPLY_ORDER = 1;
     * static final int COPY_CLIPBOARD_ORDER = 2; static final int
     * SHOW_THISONLY_ORDER = 3; static final int SHOW_MODIFY_ORDER = 4; static
     * final int SHOW_ALL = 5; static final int POST_COMMENT = 6; static final
     * int SEARCH_POST = 7; static final int SEARCH_SUBJECT = 8;
     */
    private ListView listview = null;
    private NonameArticleListAdapter articleAdpater;
    // private JsonThreadLoadTask task;
    private int page = 0;
    private int tid;
    @SuppressWarnings("unused")
    private String title;
    private int pid;
    private boolean needLoad = true;
    private Object mActionModeCallback = null;
    private int mListPosition;
    private int mListFirstTop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        page = getArguments().getInt("page") + 1;
        tid = getArguments().getInt("id");
        articleAdpater = new NonameArticleListAdapter(this.getActivity());
        super.onCreate(savedInstanceState);
        String fatheractivityclassname = getActivity().getClass()
                .getSimpleName();
        if (!StringUtils.isEmpty(fatheractivityclassname)) {
            if (fatheractivityclassname.indexOf("TopicListActivity") < 0)
                setRetainInstance(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        listview = new ListView(this.getActivity());

        listview.setBackgroundResource(ThemeManager.getInstance()
                .getBackgroundColor());
        listview.setDivider(null);

        activeActionMode();
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setOnItemLongClickListener(new OnItemLongClickListener() {

            @TargetApi(11)
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                ListView lv = (ListView) parent;
                lv.setItemChecked(position, true);
                if (mActionModeCallback != null) {
                    ((AppCompatActivity) getActivity())
                            .startSupportActionMode((Callback) mActionModeCallback);
                    return true;
                }
                return false;
            }

        });

        listview.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);

        return listview;
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
                inflater.inflate(R.menu.nonamearticlelist_context_menu, menu);

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
        NLog.d(TAG, "onResume pid=" + pid + "&page=" + page);
        // setHasOptionsMenu(true);
        loadPage();
        super.onResume();
        if (result != null) {
            ((OnNonameThreadPageLoadFinishedListener) getActivity())
                    .finishLoad(result);
        }
        super.onResume();
        listview.setSelectionFromTop(mListPosition, mListFirstTop);
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

    @Override
    public void onPause() {
        super.onPause();
        if (listview.getChildCount() >= 1) {
            mListPosition = listview.getFirstVisiblePosition();
            mListFirstTop = listview.getChildAt(0).getTop();
        }
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.nonamearticlelist_context_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        NLog.d(TAG, "onContextItemSelected,tid=" + tid + ",page=" + page);
        PagerOwner father = null;
        try {
            father = (PagerOwner) getActivity();
        } catch (ClassCastException e) {
            NLog.e(TAG, "father activity does not implements interface "
                    + PagerOwner.class.getName());
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
            Toast.makeText(getActivity(), R.string.floor_error,
                    Toast.LENGTH_LONG).show();
            position = 0;
        }
        StringBuffer postPrefix = new StringBuffer();
        String tidStr = String.valueOf(this.tid);

        NonameReadBody row = (NonameReadBody) listview
                .getItemAtPosition(position);
        if (row == null) {
            Toast.makeText(getActivity(), R.string.unknow_error,
                    Toast.LENGTH_LONG).show();
            return true;
        }
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
                    postTime = StringUtils.TimeStamp2Date(String
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
                FunctionUtils.CopyDialog(content, getActivity(), listview);
                break;

        }
        return true;
    }

    public void modechange() {
        listview.setBackgroundResource(ThemeManager.getInstance()
                .getBackgroundColor());
        if (result != null) {
            for (int i = 0; i < result.data.posts.length; i++) {
                FunctionUtils.fillFormated_html_data(result.data.posts[i], i,
                        getActivity());
            }
            finishLoad(result);
        }
    }

    @Override
    public void finishLoad(NonameReadResponse data) {
        NLog.d(TAG, "finishLoad");
        if (null != data) {
            result = data;
            articleAdpater.setData(data);
            articleAdpater.notifyDataSetChanged();
            tid = data.data.tid;
            title = data.data.title;
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

}
