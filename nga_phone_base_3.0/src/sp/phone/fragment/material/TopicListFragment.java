package sp.phone.fragment.material;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import gov.anzong.androidnga.R;
import sp.phone.adapter.material.AppendableTopicAdapter;
import sp.phone.bean.TopicListInfo;
import sp.phone.bean.TopicListRequestInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.interfaces.NextJsonTopicListLoader;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.presenter.contract.TopicListContract;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.StringUtils;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;


public class TopicListFragment extends MaterialCompatFragment implements TopicListContract.View, AdapterView.OnItemLongClickListener {

    private static final String TAG = TopicListFragment.class.getSimpleName();

    private TopicListRequestInfo mRequestInfo;

    private AppendableTopicAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mListView;

    private TopicListInfo mTopicListInfo;

    private TopicListContract.Presenter mPresenter;

    private boolean mFromReplayActivity;

    private boolean mIsVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestInfo = getArguments().getParcelable("requestInfo");
        if (mRequestInfo.authorId > 0 || mRequestInfo.searchPost > 0 || mRequestInfo.favor > 0
                || !StringUtils.isEmpty(mRequestInfo.key) || !StringUtils.isEmpty(mRequestInfo.author)
                || !StringUtils.isEmpty(mRequestInfo.fidGroup)) {//!StringUtils.isEmpty(table) ||
            mFromReplayActivity = true;
        }
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getParentFragment() == null) {
            return super.onCreateView(inflater, container, savedInstanceState);
        } else {
            return inflater.inflate(R.layout.fragment_topic_list, container, false);
        }
    }

    @Override
    public View onCreateContainerView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_topic_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mListView = (RecyclerView) view.findViewById(R.id.list);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new AppendableTopicAdapter(getContext(), null, new NextJsonTopicListLoader() {
            @Override
            public void loadNextPage(OnTopListLoadFinishedListener callback) {
                mPresenter.loadNextPage(callback);
            }
        });
        mAdapter.setOnItemClickListener(new EnterJsonArticle());
        if (mRequestInfo.favor != 0) {
            Toast.makeText(getActivity(), "长按可删除收藏的帖子", Toast.LENGTH_SHORT).show();
            mAdapter.setOnItemLongClickListener(this);
        }
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
    public TopicListRequestInfo getTopicListRequestInfo() {
        return mRequestInfo;
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

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, final View view, int position, long id) {
        final int finalPosition = position;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        mPresenter.removeBookmark(mAdapter.getTidArray(finalPosition), finalPosition);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // Do nothing
                        break;
                }
            }
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(this.getString(R.string.delete_favo_confirm_text))
                .setPositiveButton(R.string.confirm, dialogClickListener)
                .setNegativeButton(R.string.cancle, dialogClickListener);
        final AlertDialog dialog = builder.create();
        dialog.show();
        return true;
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
            intent.setClass(getContext(), PhoneConfiguration.getInstance().articleActivityClass);
            startActivity(intent);
        }

    }


}
