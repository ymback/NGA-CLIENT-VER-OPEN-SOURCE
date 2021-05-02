package sp.phone.ui.fragment;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.BaseActivity;
import gov.anzong.androidnga.arouter.ARouterConstants;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.base.util.DeviceUtils;
import gov.anzong.androidnga.base.widget.DividerItemDecorationEx;
import sp.phone.common.ApiConstants;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.TopicHistoryManager;
import sp.phone.mvp.contract.TopicListContract;
import sp.phone.mvp.model.entity.ThreadPageInfo;
import sp.phone.mvp.model.entity.TopicListInfo;
import sp.phone.mvp.presenter.TopicListPresenter;
import sp.phone.param.ArticleListParam;
import sp.phone.param.ParamKey;
import sp.phone.param.TopicListParam;
import sp.phone.ui.adapter.BaseAppendableAdapter;
import sp.phone.ui.adapter.ReplyListAdapter;
import sp.phone.ui.adapter.TopicListAdapter;
import sp.phone.util.ARouterUtils;
import sp.phone.util.ActivityUtils;
import sp.phone.util.StringUtils;
import sp.phone.view.RecyclerViewEx;

public class TopicSearchFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = TopicSearchFragment.class.getSimpleName();

    public static final int REQUEST_IMPORT_CACHE = 0;

    protected TopicListParam mRequestParam;

    protected BaseAppendableAdapter mAdapter;

    protected TopicListInfo mTopicListInfo;

    @BindView(R.id.swipe_refresh)
    public SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.list)
    public RecyclerViewEx mListView;

    @BindView(R.id.loading_view)
    public View mLoadingView;

    protected TopicListPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mRequestParam = getArguments().getParcelable(ParamKey.KEY_PARAM);
        super.onCreate(savedInstanceState);
        setTitle();
        mPresenter = onCreatePresenter();
        getLifecycle().addObserver(mPresenter);
    }

    protected TopicListPresenter onCreatePresenter() {
        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        TopicListPresenter topicListPresenter = viewModelProvider.get(TopicListPresenter.class);
        topicListPresenter.setRequestParam(mRequestParam);
        return topicListPresenter;
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
        } else if (mRequestParam.twentyfour == 1) {
            setTitle(mRequestParam.title + " - 24小时热帖");
        } else if (!TextUtils.isEmpty(mRequestParam.title)) {
            setTitle(mRequestParam.title);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutId = R.layout.fragment_topic_list;
        return inflater.inflate(layoutId, container, false);
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
        if (PhoneConfiguration.getInstance().useSolidColorBackground()) {
            int padding = PhoneConfiguration.getInstance().useSolidColorBackground() ? ContextUtils.getDimension(R.dimen.topic_list_item_padding) : 0;
            mListView.addItemDecoration(new DividerItemDecorationEx(view.getContext(), padding, DividerItemDecoration.VERTICAL));
        }

        mSwipeRefreshLayout.setVisibility(View.GONE);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadPage(1, mRequestParam);
            }
        });

        TextView sayingView = (TextView) mLoadingView.findViewById(R.id.saying);
        sayingView.setText(ActivityUtils.getSaying());

        super.onViewCreated(view, savedInstanceState);

        mPresenter.getFirstTopicList().observe(this, topicListInfo -> {
            scrollTo(0);
            clearData();
            if (topicListInfo != null) {
                setData(topicListInfo);
            }
        });

        mPresenter.getNextTopicList().observe(this, this::setData);

        mPresenter.getErrorMsg().observe(this, res -> {
            showToast(res);
            setNextPageEnabled(false);
        });

        mPresenter.isRefreshing().observe(this, aBoolean -> {
            setRefreshing(aBoolean);
            if (!aBoolean) {
                hideLoadingView();
            }
        });
        mPresenter.loadPage(1,mRequestParam);
    }



    public void scrollTo(int position) {
        mListView.scrollToPosition(position);
    }

    public void setNextPageEnabled(boolean enabled) {
        mAdapter.setNextPageEnabled(enabled);
    }

    public void removeTopic(int position) {

    }

    public void removeTopic(ThreadPageInfo pageInfo) {

    }

    public void hideLoadingView() {
        if (mLoadingView.getVisibility() == View.VISIBLE) {
            mLoadingView.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setRefreshing(boolean refreshing) {
        if (mSwipeRefreshLayout.getVisibility() == View.VISIBLE) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    public boolean isRefreshing() {
        return mSwipeRefreshLayout.isShown() ? mSwipeRefreshLayout.isRefreshing() : mLoadingView.isShown();
    }

    public void setData(TopicListInfo result) {
        mTopicListInfo = result;
        mAdapter.setData(result.getThreadPageList());
    }

    public void clearData() {
        mAdapter.setData(null);
    }

    @Override
    public void onClick(View view) {
        ThreadPageInfo info = (ThreadPageInfo) view.getTag();

        if (info.isMirrorBoard()) {
            ARouterUtils.build(ARouterConstants.ACTIVITY_TOPIC_LIST)
                    .withInt(ParamKey.KEY_FID, info.getFid())
                    .withString(ParamKey.KEY_TITLE, info.getSubject())
                    .navigation(view.getContext());
        } else if ((info.getType() & ApiConstants.MASK_TYPE_ASSEMBLE) == ApiConstants.MASK_TYPE_ASSEMBLE) {
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
            param.title = StringUtils.unEscapeHtml(info.getSubject());
            if (mRequestParam.searchPost != 0) {
                param.pid = info.getPid();
                param.authorId = info.getAuthorId();
                param.searchPost = mRequestParam.searchPost;
            }
            param.topicInfo = JSON.toJSONString(info);

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
