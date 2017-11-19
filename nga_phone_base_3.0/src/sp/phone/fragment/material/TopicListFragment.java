package sp.phone.fragment.material;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.BaseActivity;
import sp.phone.adapter.material.AppendableTopicAdapter;
import sp.phone.bean.TopicListInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.forumoperation.TopicListParam;
import sp.phone.fragment.BaseFragment;
import sp.phone.interfaces.NextJsonTopicListLoader;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.presenter.TopicListPresenter;
import sp.phone.presenter.contract.TopicListContract;
import sp.phone.utils.StringUtils;


public class TopicListFragment extends BaseFragment implements TopicListContract.View {

    private static final String TAG = TopicListFragment.class.getSimpleName();

    protected TopicListParam mRequestParam;

    protected AppendableTopicAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mListView;

    private TopicListInfo mTopicListInfo;

    protected TopicListContract.Presenter mPresenter;

    private boolean mFromReplayActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mPresenter = new TopicListPresenter(this);
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

        mListView = (RecyclerView) view.findViewById(R.id.list);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new AppendableTopicAdapter(getContext(), new NextJsonTopicListLoader() {
            @Override
            public void loadNextPage(OnTopListLoadFinishedListener callback) {
                mPresenter.loadNextPage(callback);
            }
        });
        mAdapter.setOnItemClickListener(new EnterJsonArticle());
        mListView.setAdapter(mAdapter);
        if (mTopicListInfo != null) {
            mPresenter.jsonFinishLoad(mTopicListInfo);
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refresh();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        if (mTopicListInfo == null && getUserVisibleHint() && mPresenter != null) {
            mPresenter.refresh();
        }
        super.onResume();
    }

    @Override
    public int getNextPage() {
        return mAdapter.getNextPage();
    }

    @Override
    public TopicListParam getTopicListRequestInfo() {
        return mRequestParam;
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
    public void setPresenter(TopicListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void setData(TopicListInfo result) {
        mTopicListInfo = result;
        mAdapter.clear();
        mAdapter.jsonFinishLoad(result);
    }

    private class EnterJsonArticle implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            String guide = (String) mAdapter.getItem(position);
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
            mAdapter.setSelected(position);
            mAdapter.notifyDataSetChanged();
            intent.setClass(getContext(), PhoneConfiguration.getInstance().articleActivityClass);
            startActivity(intent);
        }

    }


}
