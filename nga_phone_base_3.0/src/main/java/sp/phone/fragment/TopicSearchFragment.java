package sp.phone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.BaseActivity;
import gov.anzong.androidnga.arouter.ARouterConstants;
import sp.phone.adapter.BaseAppendableAdapter;
import sp.phone.adapter.ReplyListAdapter;
import sp.phone.adapter.TopicListAdapter;
import sp.phone.common.ApiConstants;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.TopicHistoryManager;
import sp.phone.forumoperation.ArticleListParam;
import sp.phone.forumoperation.ParamKey;
import sp.phone.forumoperation.TopicListParam;
import sp.phone.mvp.contract.TopicListContract;
import sp.phone.mvp.model.entity.ThreadPageInfo;
import sp.phone.mvp.model.entity.TopicListInfo;
import sp.phone.mvp.presenter.TopicListPresenter;
import sp.phone.util.ActivityUtils;
import sp.phone.util.StringUtils;
import sp.phone.view.RecyclerViewEx;
import sp.phone.util.NLog;

public class TopicSearchFragment extends BaseMvpFragment<TopicListPresenter> implements TopicListContract.View, View.OnClickListener {

    private static final String TAG = TopicSearchFragment.class.getSimpleName();

    protected TopicListParam mRequestParam;

    protected BaseAppendableAdapter mAdapter;

    protected TopicListInfo mTopicListInfo;

    @BindView(R.id.swipe_refresh)
    public SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.list)
    public RecyclerViewEx mListView;

    @BindView(R.id.loading_view)
    public View mLoadingView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestParam = getArguments().getParcelable(ParamKey.KEY_PARAM);
        setTitle();
    }

    @Override
    protected TopicListPresenter onCreatePresenter() {
        return new TopicListPresenter();
    }

    protected void setTitle() {
        if (!StringUtils.isEmpty(mRequestParam.key)) {
            if (mRequestParam.content == 1) {
                if (!StringUtils.isEmpty(mRequestParam.fidGroup)) {
                    setTitle("搜索全站(包含正文):" + mRequestParam.key);
                } else {
                    setTitle("搜索(包含正文):" + mRequestParam.key);
                }
            } else {
                if (!StringUtils.isEmpty(mRequestParam.fidGroup)) {
                    setTitle("搜索全站:" + mRequestParam.key);
                } else {
                    setTitle("搜索:" + mRequestParam.key);
                }
            }
        } else if (!StringUtils.isEmpty(mRequestParam.author)) {
            if (mRequestParam.searchPost > 0) {
                final String title = "搜索" + mRequestParam.author + "的回复";
                setTitle(title);
            } else {
                final String title = "搜索" + mRequestParam.author + "的主题";
                setTitle(title);
            }
        } else if (mRequestParam.recommend == 1) {
            setTitle(mRequestParam.title + " - 精华区");
        } else if (mRequestParam.twentyfour == 1){
            setTitle(mRequestParam.title + " - 24小时热帖");
        } else if (!TextUtils.isEmpty(mRequestParam.title)) {
            setTitle(mRequestParam.title);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_topic_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        ((BaseActivity) getActivity()).setupToolbar();

        if (mRequestParam.searchPost > 0) {
            mAdapter = new ReplyListAdapter(getContext());
            mListView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        } else {

            mAdapter = new TopicListAdapter(getContext());
        }

        mAdapter.setOnClickListener(this);

        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.setOnNextPageLoadListener(new RecyclerViewEx.OnNextPageLoadListener() {
            @Override
            public void loadNextPage() {
                if (!isRefreshing()) {
                    mPresenter.loadNextPage(mAdapter.getNextPage(), mRequestParam);
                }
            }
        });
        mListView.setEmptyView(view.findViewById(R.id.empty_view));
        mListView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setVisibility(View.GONE);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadPage(1, mRequestParam);
            }
        });

        TextView sayingView = (TextView) mLoadingView.findViewById(R.id.saying);
        sayingView.setText(ActivityUtils.getSaying());

        mPresenter.loadPage(1, mRequestParam);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void scrollTo(int position) {
        mListView.scrollToPosition(position);
    }

    @Override
    public void setNextPageEnabled(boolean enabled) {
        mAdapter.setNextPageEnabled(enabled);
    }

    @Override
    public void removeTopic(int position) {

    }

    @Override
    public void removeTopic(ThreadPageInfo pageInfo) {

    }

    @Override
    public void hideLoadingView() {
        mLoadingView.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
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
    public void setData(TopicListInfo result) {
        mTopicListInfo = result;
        mAdapter.setData(result.getThreadPageList());
    }

    @Override
    public void clearData() {
        mAdapter.setData(null);
    }

    @Override
    public void onClick(View view) {
        ThreadPageInfo info = (ThreadPageInfo) view.getTag();

        if ((info.getType() & ApiConstants.MASK_TYPE_ASSEMBLE) == ApiConstants.MASK_TYPE_ASSEMBLE) {
            TopicListParam param = new TopicListParam();
            param.title = info.getSubject();
            param.stid = info.getTid();
            ARouter.getInstance().build(ARouterConstants.ACTIVITY_TOPIC_LIST)
                    .withParcelable(ParamKey.KEY_PARAM, param)
                    .navigation();

        } else {

            ArticleListParam param = new ArticleListParam();
            param.tid = info.getTid();
            param.page = info.getPage();
            param.title = info.getSubject();
            if (mRequestParam.searchPost != 0) {
                param.pid = info.getPid();
                param.authorId = info.getAuthorId();
                param.searchPost = mRequestParam.searchPost;
            }

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putParcelable(ParamKey.KEY_PARAM, param);
            intent.putExtras(bundle);
            intent.setClass(getContext(), PhoneConfiguration.getInstance().articleActivityClass);
            startActivity(intent);
            TopicHistoryManager.getInstance().addTopicHistory(info);
        }
    }

}
