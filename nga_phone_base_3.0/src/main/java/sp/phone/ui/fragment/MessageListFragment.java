package sp.phone.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.util.ToastUtils;
import gov.anzong.androidnga.mvvm.viewmodel.MessageListViewModel;
import sp.phone.ui.adapter.MessageListAdapter;
import sp.phone.util.ActivityUtils;
import sp.phone.view.RecyclerViewEx;

public class MessageListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ViewGroup mLoadView;

    private View.OnClickListener mClickListener;

    private MessageListAdapter mAdapter;

    private MessageListViewModel mMessageViewModel;

    private RecyclerViewEx.OnNextPageLoadListener mNextPageLoadListener = new RecyclerViewEx.OnNextPageLoadListener() {
        @Override
        public void loadNextPage() {
            if (!isRefreshing()) {
                setRefreshing(true);
                mMessageViewModel.getMessageList(mAdapter.getNextPage());
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mMessageViewModel = getActivityViewModelProvider().get(MessageListViewModel.class);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);
        mLoadView = view.findViewById(R.id.loading_view);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

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
        mMessageViewModel.observeMessageList(this, messageListInfo -> {
            setRefreshing(false);
            hideLoadingView();
            if (messageListInfo != null) {
                mAdapter.setData(messageListInfo);
            }
        });
        mMessageViewModel.observeErrorInfo(this, s -> {
            setRefreshing(false);
            hideLoadingView();
            if (TextUtils.isEmpty(s)) {
                ToastUtils.error(R.string.error_network);
            } else {
                ToastUtils.error(s);
            }
        });
        mMessageViewModel.getMessageList(1);
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
    public void onRefresh() {
        mMessageViewModel.getMessageList(1);
    }

    public void hideLoadingView() {
        mLoadView.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    public boolean isRefreshing() {
        return mSwipeRefreshLayout.isRefreshing();
    }

    public void setRefreshing(boolean refreshing) {
        if (mSwipeRefreshLayout.isShown()) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }
}
