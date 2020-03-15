package sp.phone.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import sp.phone.http.bean.MessageListInfo;
import sp.phone.mvp.contract.MessageListContract;
import sp.phone.mvp.presenter.MessageListPresenter;
import sp.phone.ui.adapter.MessageListAdapter;
import sp.phone.util.ActivityUtils;
import sp.phone.view.RecyclerViewEx;

public class MessageListFragment extends BaseMvpFragment<MessageListPresenter> implements SwipeRefreshLayout.OnRefreshListener, MessageListContract.IMessageView {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.loading_view)
    ViewGroup mLoadView;

    private View.OnClickListener mClickListener;

    private MessageListAdapter mAdapter;

    private RecyclerViewEx.OnNextPageLoadListener mNextPageLoadListener = new RecyclerViewEx.OnNextPageLoadListener() {
        @Override
        public void loadNextPage() {
            if (!isRefreshing()) {
                mPresenter.loadPage(mAdapter.getNextPage());
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);
        ButterKnife.bind(this, view);

        mAdapter = new MessageListAdapter(getContext());
        mAdapter.setOnClickListener(mClickListener);

        RecyclerViewEx listView = (RecyclerViewEx) view.findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setEmptyView(view.findViewById(R.id.empty_view));
        listView.setAdapter(mAdapter);
        listView.setOnNextPageLoadListener(mNextPageLoadListener);

        mSwipeRefreshLayout.setOnRefreshListener(this);

        TextView sayView = (TextView) view.findViewById(R.id.saying);
        sayView.setText(ActivityUtils.getSaying());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mPresenter.loadPage(1);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof View.OnClickListener) {
            mClickListener = (View.OnClickListener) context;
        }
        super.onAttach(context);
    }

    @Override
    protected MessageListPresenter onCreatePresenter() {
        return new MessageListPresenter();
    }

    @Override
    public void onRefresh() {
        mPresenter.loadPage(1);
    }

    @Override
    public void hideLoadingView() {
        mLoadView.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean isRefreshing() {
        return mSwipeRefreshLayout.isRefreshing();
    }

    @Override
    public void setData(MessageListInfo listInfo) {
        mAdapter.setData(listInfo);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        if (mSwipeRefreshLayout.isShown()) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }
}
