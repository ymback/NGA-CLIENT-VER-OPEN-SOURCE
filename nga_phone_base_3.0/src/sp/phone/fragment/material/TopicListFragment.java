package sp.phone.fragment.material;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.BaseActivity;
import sp.phone.adapter.material.TopicListAdapter;
import sp.phone.bean.TopicListInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.forumoperation.TopicListParam;
import sp.phone.presenter.TopicListPresenter;
import sp.phone.presenter.contract.tmp.TopicListContract;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.StringUtils;
import sp.phone.view.RecyclerViewEx;


public class TopicListFragment extends BaseMvpFragment implements TopicListContract.View {

    private static final String TAG = TopicListFragment.class.getSimpleName();

    protected TopicListParam mRequestParam;

    protected TopicListAdapter mAdapter;

    @BindView(R.id.swipe_refresh)
    public SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.list)
    public RecyclerViewEx mListView;

    @BindView(R.id.loading_view)
    public View mLoadingView;

    protected TopicListContract.Presenter mPresenter;

    private boolean mFromReplayActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mPresenter = new TopicListPresenter();
        setPresenter(mPresenter);
        super.onCreate(savedInstanceState);
        mRequestParam = getArguments().getParcelable("requestParam");
        if (mRequestParam.authorId > 0 || mRequestParam.searchPost > 0 || mRequestParam.favor > 0
                || !StringUtils.isEmpty(mRequestParam.key) || !StringUtils.isEmpty(mRequestParam.author)
                || !StringUtils.isEmpty(mRequestParam.fidGroup)) {//!StringUtils.isEmpty(table) ||
            mFromReplayActivity = true;
        }
        setTitle();
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
        } else if (mRequestParam.category == 1) {
            setTitle(mRequestParam.boardName + " - 精华区");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_topic_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        ((BaseActivity) getActivity()).setupActionBar();

        mAdapter = new TopicListAdapter(getContext());
        mAdapter.setOnClickListener(new EnterJsonArticle());

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
        mAdapter.setData(result);
    }

    @Override
    public void clearData() {
        mAdapter.clear();
    }

    private class EnterJsonArticle implements View.OnClickListener {

        public void onClick(View view) {
            String url = (String) view.getTag(R.id.title);
            if (StringUtils.isEmpty(url)) {
                return;
            }

            url = url.trim();

            int pid = StringUtils.getUrlParameter(url, "pid");
            int tid = StringUtils.getUrlParameter(url, "tid");
            int authorId = StringUtils.getUrlParameter(url, "authorid");

            Intent intent = new Intent();
            intent.putExtra("tab", "1");
            intent.putExtra("tid", tid);
            intent.putExtra("pid", pid);
            intent.putExtra("authorid", authorId);
            if (mFromReplayActivity) {
                intent.putExtra("fromreplyactivity", 1);
            }
            intent.setClass(getContext(), PhoneConfiguration.getInstance().articleActivityClass);
            startActivity(intent);
        }
    }


}
