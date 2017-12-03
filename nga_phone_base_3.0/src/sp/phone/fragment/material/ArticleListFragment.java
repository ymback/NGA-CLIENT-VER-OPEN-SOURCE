package sp.phone.fragment.material;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.BaseActivity;
import io.reactivex.annotations.NonNull;
import sp.phone.adapter.ArticleListAdapter;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.forumoperation.ArticleListParam;
import sp.phone.forumoperation.ParamKey;
import sp.phone.fragment.dialog.PostCommentDialogFragment;
import sp.phone.mvp.contract.ArticleListContract;
import sp.phone.mvp.presenter.ArticleListPresenter;
import sp.phone.rxjava.RxBus;
import sp.phone.rxjava.RxEvent;
import sp.phone.task.LikeTask;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.FunctionUtils;
import sp.phone.utils.NLog;
import sp.phone.utils.StringUtils;
import sp.phone.view.RecyclerViewEx;

/*
 * MD 帖子详情每一页
 */
public class ArticleListFragment extends BaseMvpFragment<ArticleListPresenter> implements ArticleListContract.View, ActionMode.Callback {

    private final static String TAG = ArticleListFragment.class.getSimpleName();

    @BindView(R.id.list)
    public RecyclerViewEx mListView;

    @BindView(R.id.loading_view)
    public View mLoadingView;

    @BindView(R.id.swipe_refresh)
    public SwipeRefreshLayout mSwipeRefreshLayout;

    private ArticleListAdapter mArticleAdapter;

    private ActionMode mActionMode;

    protected ArticleListParam mRequestParam;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        NLog.d(TAG, "onCreate");
        mRequestParam = getArguments().getParcelable(ParamKey.KEY_PARAM);
        registerRxBus();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void accept(@NonNull RxEvent rxEvent) {
        if (rxEvent.what == RxEvent.EVENT_ARTICLE_UPDATE && rxEvent.arg + 1 == mRequestParam.page) {
            loadPage();
        }
    }

    @Override
    protected ArticleListPresenter onCreatePresenter() {
        return new ArticleListPresenter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        ((BaseActivity) getActivity()).setupActionBar();
        mArticleAdapter = new ArticleListAdapter(getContext());
        mArticleAdapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mArticleAdapter.setSelectedItem(position);
                ((AppCompatActivity) getActivity()).startSupportActionMode(ArticleListFragment.this);
                return true;
            }
        });
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.setItemViewCacheSize(20);
        mListView.setAdapter(mArticleAdapter);
        mListView.setEmptyView(view.findViewById(R.id.empty_view));

        TextView sayingView = (TextView) mLoadingView.findViewById(R.id.saying);
        sayingView.setText(ActivityUtils.getSaying());

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPage();
            }
        });
        loadPage();
        super.onViewCreated(view, savedInstanceState);
    }

    public void loadPage() {
        mPresenter.loadPage(mRequestParam);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int page = mRequestParam.page;
        int tid = mRequestParam.tid;
        NLog.d(TAG, "onContextItemSelected,tid=" + tid + ",page=" + page);

        if (!getUserVisibleHint()) {
            return false;
        }

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = mArticleAdapter.getSelectedItem();
        if (info != null) {
            position = info.position;
        }
        if (position < 0 || position >= mArticleAdapter.getItemCount()) {
            showToast(R.string.floor_error);
            position = 0;
        }
        String tidStr = String.valueOf(tid);

        ThreadRowInfo row = (ThreadRowInfo) mArticleAdapter.getItem(position);
        if (row == null) {
            showToast(R.string.unknow_error);
            return true;
        }
        String content = row.getContent();
        boolean isAnonymous = row.getISANONYMOUS();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch (item.getItemId()) {
            case R.id.menu_quote_subject:
                mPresenter.quote(mRequestParam, row);
                break;

            case R.id.menu_signature:
                if (isAnonymous) {
                    FunctionUtils.errordialog(getActivity(), mListView);
                } else {
                    FunctionUtils.Create_Signature_Dialog(row, getActivity(),
                            mListView);
                }
                break;

            case R.id.menu_vote:
                FunctionUtils.createVoteDialog(row, getActivity(), mListView, mToast);
                break;

            case R.id.menu_ban_this_one:
                mPresenter.banThisSB(row);
                break;

            case R.id.menu_show_profile:
                if (isAnonymous) {
                    FunctionUtils.errordialog(getActivity(), mListView);
                } else {
                    intent.putExtra("mode", "username");
                    intent.putExtra("username", row.getAuthor());
                    intent.setClass(getActivity(), PhoneConfiguration.getInstance().profileActivityClass);
                    startActivity(intent);
                }
                break;

            case R.id.menu_avatar:
                if (isAnonymous) {
                    FunctionUtils.errordialog(getActivity(), mListView);
                } else {
                    FunctionUtils.Create_Avatar_Dialog(row, getActivity(), mListView);
                }
                break;

            case R.id.menu_edit:
                if (FunctionUtils.isComment(row)) {
                    showToast(R.string.cannot_eidt_comment);
                    break;
                }
                Intent intentModify = new Intent();
                intentModify.putExtra("prefix", StringUtils.unEscapeHtml(StringUtils.removeBrTag(content)));
                intentModify.putExtra("tid", tidStr);
                String pid = String.valueOf(row.getPid());// getPid(map.get("url"));
                intentModify.putExtra("pid", pid);
                intentModify.putExtra("title", StringUtils.unEscapeHtml(row.getSubject()));
                intentModify.putExtra("action", "modify");
                if (!StringUtils.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
                    intentModify.setClass(getActivity(), PhoneConfiguration.getInstance().postActivityClass);
                } else {
                    intentModify.setClass(getActivity(), PhoneConfiguration.getInstance().loginActivityClass);
                }
                startActivity(intentModify);
                break;

            case R.id.menu_copy:
                FunctionUtils.CopyDialog(row.getFormated_html_data(), getActivity(), mListView);
                break;

            case R.id.menu_show_this_person_only:
                Intent intentThis = new Intent();
                intentThis.putExtra("tab", "1");
                intentThis.putExtra("tid", tid);
                intentThis.putExtra("authorid", row.getAuthorid());
                intentThis.putExtra("fromreplyactivity", 1);
                intentThis.setClass(getActivity(), PhoneConfiguration.getInstance().articleActivityClass);
                startActivity(intentThis);
                break;

            case R.id.menu_send_message:
                if (isAnonymous) {
                    FunctionUtils.errordialog(getActivity(), mListView);
                } else {
                    FunctionUtils.start_send_message(getActivity(), row);
                }
                break;

            case R.id.menu_post_comment:
                mPresenter.postComment(mRequestParam, row);
                break;

            case R.id.menu_report:
                FunctionUtils.handleReport(row, tid, getFragmentManager());
                break;

            case R.id.menu_search_post:
                bundle.putInt("searchpost", 1);
            case R.id.menu_search_subject:
                bundle.putInt("authorid", row.getAuthorid());
                bundle.putString("author", row.getAuthor());
                intent.putExtras(bundle);
                intent.setClass(getActivity(), PhoneConfiguration.getInstance().topicActivityClass);
                startActivity(intent);
                break;
            case R.id.menu_like:
                doLike(tid, row.getPid(), 1);
                break;

            case R.id.menu_dislike:
                doLike(tid, row.getPid(), -1);
                break;

            default:
                break;
        }
        return true;
    }

    private void doLike(int tid, int pid, int value) {
        LikeTask lt = new LikeTask(getActivity(), tid, pid, value);
        lt.execute();
    }

    @Override
    public void setData(ThreadData data) {
        if (data != null) {
            RxBus.getInstance().post(new RxEvent(RxEvent.EVENT_ARTICLE_TAB_UPDATE, data.getThreadInfo().getReplies()));
        }
        mArticleAdapter.setData(data);
        mArticleAdapter.notifyDataSetChanged();

    }

    @Override
    public void startPostActivity(Intent intent) {
        if (!StringUtils.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
            intent.setClass(getActivity(), PhoneConfiguration.getInstance().postActivityClass);
        } else {
            intent.setClass(getActivity(), PhoneConfiguration.getInstance().loginActivityClass);
        }
        startActivityForResult(intent, ActivityUtils.REQUEST_CODE_TOPIC_POST);
    }

    @Override
    public void showPostCommentDialog(String prefix, Bundle bundle) {
        Intent intent = new Intent();
        final String tag = "post comment";
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        DialogFragment df = new PostCommentDialogFragment();
        intent.putExtra("prefix", prefix);
        df.setArguments(bundle);
        df.show(ft, tag);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && mActionMode != null) {
            mActionMode.finish();
        }
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        if (mSwipeRefreshLayout.isShown()) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    @Override
    public boolean isRefreshing() {
        return mSwipeRefreshLayout.isShown() ? mSwipeRefreshLayout.isRefreshing() : mLoadingView.isShown();
    }

    @Override
    public void hideLoadingView() {
        mLoadingView.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        if (mRequestParam.pid == 0) {
            inflater.inflate(R.menu.article_list_context_menu, menu);
        } else {
            inflater.inflate(R.menu.article_list_context_menu_with_tid, menu);
        }
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        mActionMode = mode;
        int position = mArticleAdapter.getSelectedItem();
        ThreadRowInfo row = new ThreadRowInfo();
        if (position < mArticleAdapter.getItemCount()) {
            row = (ThreadRowInfo) mArticleAdapter.getItem(position);
        }

        MenuItem mi = menu.findItem(R.id.menu_ban_this_one);
        if (mi != null && row != null) {
            if (row.get_isInBlackList()) {// 处于屏蔽列表，需要去掉
                mi.setTitle(R.string.cancel_ban_thisone);
            } else {
                mi.setTitle(R.string.ban_thisone);
            }
        }
        MenuItem voteMenu = menu.findItem(R.id.menu_vote);
        if (voteMenu != null && StringUtils.isEmpty(row.getVote())) {
            menu.removeItem(R.id.menu_vote);
        }
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
        mActionMode = null;
    }
}
