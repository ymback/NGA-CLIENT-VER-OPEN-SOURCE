package sp.phone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.BaseActivity;
import gov.anzong.androidnga.arouter.ARouterConstants;
import io.reactivex.annotations.NonNull;
import sp.phone.adapter.ArticleListAdapter;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.User;
import sp.phone.common.UserManagerImpl;
import sp.phone.forumoperation.ArticleListParam;
import sp.phone.forumoperation.ParamKey;
import sp.phone.fragment.dialog.BaseDialogFragment;
import sp.phone.fragment.dialog.PostCommentDialogFragment;
import sp.phone.listener.OnTopicMenuItemClickListener;
import sp.phone.mvp.contract.ArticleListContract;
import sp.phone.mvp.presenter.ArticleListPresenter;
import sp.phone.rxjava.RxBus;
import sp.phone.rxjava.RxEvent;
import sp.phone.theme.ThemeManager;
import sp.phone.util.ActivityUtils;
import sp.phone.util.FunctionUtils;
import sp.phone.util.NLog;
import sp.phone.util.StringUtils;
import sp.phone.view.RecyclerViewEx;

/*
 * MD 帖子详情每一页
 */
public class ArticleListFragment extends BaseMvpFragment<ArticleListPresenter> implements ArticleListContract.View {

    private static final String TAG = ArticleListFragment.class.getSimpleName();

    @BindView(R.id.list)
    public RecyclerViewEx mListView;

    @BindView(R.id.loading_view)
    public View mLoadingView;

    @BindView(R.id.swipe_refresh)
    public SwipeRefreshLayout mSwipeRefreshLayout;

    private ArticleListAdapter mArticleAdapter;

    protected ArticleListParam mRequestParam;

    private OnTopicMenuItemClickListener mMenuItemClickListener = new OnTopicMenuItemClickListener() {

        private ThreadRowInfo mThreadRowInfo;

        @Override
        public void setThreadRowInfo(ThreadRowInfo threadRowInfo) {
            mThreadRowInfo = threadRowInfo;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            ThreadRowInfo row = mThreadRowInfo;

            String pidStr = String.valueOf(row.getPid());
            String tidStr = String.valueOf(row.getTid());
            int tid = row.getTid();

            switch (item.getItemId()) {
                case R.id.menu_edit:
                    if (FunctionUtils.isComment(row)) {
                        showToast(R.string.cannot_eidt_comment);
                        break;
                    } else {
                        ARouter.getInstance()
                                .build(ARouterConstants.ACTIVITY_POST)
                                .withString(ParamKey.KEY_PID, pidStr)
                                .withString(ParamKey.KEY_TID, tidStr)
                                .withString("title", StringUtils.unEscapeHtml(row.getSubject()))
                                .withString("action", "modify")
                                .withString("prefix", StringUtils.unEscapeHtml(StringUtils.removeBrTag(row.getContent())))
                                .navigation(getActivity(), ActivityUtils.REQUEST_CODE_LOGIN);
                    }
                    break;
                case R.id.menu_post_comment:
                    mPresenter.postComment(mRequestParam, row);
                    break;
                case R.id.menu_report:
                    FunctionUtils.handleReport(row, mRequestParam.tid, getFragmentManager());
                    break;
                case R.id.menu_signature:
                    if (row.getISANONYMOUS()) {
                        ActivityUtils.showToast("这白痴匿名了,神马都看不到");
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
                case R.id.menu_show_this_person_only:
                    ARouter.getInstance()
                            .build(ARouterConstants.ACTIVITY_TOPIC_CONTENT)
                            .withString("tab", "1")
                            .withInt(ParamKey.KEY_TID, tid)
                            .withInt(ParamKey.KEY_AUTHOR_ID, row.getAuthorid())
                            .withInt("fromreplyactivity", 1)
                            .navigation();
                    break;
                case R.id.menu_support:
                    mPresenter.postSupportTask(tid, row.getPid());
                    break;
                case R.id.menu_oppose:
                    mPresenter.postOpposeTask(tid, row.getPid());
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    private View.OnClickListener mMenuTogglerListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            mMenuItemClickListener.setThreadRowInfo((ThreadRowInfo) view.getTag());
            int menuId;
            if (mRequestParam.pid == 0) {
                menuId = R.menu.article_list_context_menu;
            } else {
                menuId = R.menu.article_list_context_menu_with_tid;
            }
            PopupMenu popupMenu = new PopupMenu(getContext(), view);
            popupMenu.inflate(menuId);
            onPrepareOptionsMenu(popupMenu.getMenu(), (ThreadRowInfo) view.getTag());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(mMenuItemClickListener);
        }

        private void onPrepareOptionsMenu(Menu menu, ThreadRowInfo row) {
            MenuItem item = menu.findItem(R.id.menu_ban_this_one);
            if (item != null) {
                item.setTitle(row.get_isInBlackList() ? R.string.cancel_ban_thisone : R.string.ban_thisone);
            }

            item = menu.findItem(R.id.menu_vote);
            if (item != null && StringUtils.isEmpty(row.getVote())) {
                item.setVisible(false);
            }

            item = menu.findItem(R.id.menu_edit);
            if (item != null) {
                User user = UserManagerImpl.getInstance().getActiveUser();
                if (user == null || !user.getUserId().equals(String.valueOf(row.getAuthorid()))) {
                    item.setVisible(false);
                }
            }
        }

    };

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
        } else if (rxEvent.what == RxEvent.EVENT_ARTICLE_GO_FLOOR
                && rxEvent.arg + 1 == mRequestParam.page
                && rxEvent.obj != null) {
            mListView.scrollToPosition((Integer) rxEvent.obj);
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
        ((BaseActivity) getActivity()).setupToolbar();
        mArticleAdapter = new ArticleListAdapter(getContext(),getActivity().getSupportFragmentManager());
        mArticleAdapter.setMenuTogglerListener(mMenuTogglerListener);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.setItemViewCacheSize(20);
        mListView.setAdapter(mArticleAdapter);
        mListView.setEmptyView(view.findViewById(R.id.empty_view));
        if (ThemeManager.getInstance().isNightMode()) {
            mListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        }

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
    public void setData(ThreadData data) {
        if (data != null) {
            RxBus.getInstance().post(new RxEvent(RxEvent.EVENT_ARTICLE_TAB_UPDATE, data.get__ROWS()));
        }
        mArticleAdapter.setData(data);
        mArticleAdapter.notifyDataSetChanged();

    }

    @Override
    public void startPostActivity(Intent intent) {
        if (!StringUtils.isEmpty(UserManagerImpl.getInstance().getUserName())) {// 登入了才能发
            intent.setClass(getActivity(), PhoneConfiguration.getInstance().postActivityClass);
        } else {
            intent.setClass(getActivity(), PhoneConfiguration.getInstance().loginActivityClass);
        }
        startActivityForResult(intent, ActivityUtils.REQUEST_CODE_TOPIC_POST);
    }

    @Override
    public void showPostCommentDialog(String prefix, Bundle bundle) {
        BaseDialogFragment df = new PostCommentDialogFragment();
        df.setArguments(bundle);
        df.show(getActivity().getSupportFragmentManager());
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


}
