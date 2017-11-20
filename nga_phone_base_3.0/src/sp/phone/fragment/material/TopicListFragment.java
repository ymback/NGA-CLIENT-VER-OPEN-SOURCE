package sp.phone.fragment.material;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

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
        mAdapter.setOnItemClickListener(new EnterJsonArticle());

        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.setOnNextPageLoadListener(new RecyclerViewEx.OnNextPageLoadListener() {
            @Override
            public void loadNextPage() {
                if (!isRefreshing()) {
                    mPresenter.loadPage(mAdapter.getNextPage(), mRequestParam);
                }
            }
        });
        mListView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadPage(1, mRequestParam);
            }
        });

        mPresenter.loadPage(1, mRequestParam);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View getTopicListView() {
        return mListView;
    }

    @Override
    public void scrollTo(int position) {
        mListView.scrollToPosition(position);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        if (mSwipeRefreshLayout.isShown()) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    @Override
    public boolean isRefreshing() {
        return mSwipeRefreshLayout.isRefreshing();
    }

    @Override
    public void setData(TopicListInfo result) {
        mAdapter.setData(result);
    }

    @Override
    public void clearData() {
        mAdapter.clear();
    }

    private class EnterJsonArticle implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            String guide = (String) view.getTag(R.id.title);
            if (StringUtils.isEmpty(guide)) {
                return;
            }

            guide = guide.trim();

            int pid = StringUtils.getUrlParameter(guide, "pid");
            int tid = StringUtils.getUrlParameter(guide, "tid");
            int authorId = StringUtils.getUrlParameter(guide, "authorid");

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
