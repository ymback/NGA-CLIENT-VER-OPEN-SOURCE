package sp.phone.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.arouter.ARouterConstants;
import sp.phone.adapter.MessageContentAdapter;
import sp.phone.bean.MessageDetailInfo;
import sp.phone.mvp.contract.MessageDetailContract;
import sp.phone.mvp.presenter.MessageDetailPresenter;
import sp.phone.view.RecyclerViewEx;

public class MessageDetailFragment extends BaseMvpFragment<MessageDetailPresenter> implements MessageDetailContract.IMessageView {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private int mMid;

    private String mTitle;

    private String mRecipient;

    private MessageContentAdapter mAdapter;

    private RecyclerViewEx.OnNextPageLoadListener mNextPageLoadListener = new RecyclerViewEx.OnNextPageLoadListener() {
        @Override
        public void loadNextPage() {
            if (!isRefreshing()) {
                mPresenter.loadPage(mAdapter.getNextPage(), mMid);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMid = getArguments().getInt("mid");
    }

    @Override
    protected MessageDetailPresenter onCreatePresenter() {
        return new MessageDetailPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_detail, container, false);
        ButterKnife.bind(this, view);

        mAdapter = new MessageContentAdapter(getContext());

        RecyclerViewEx listView = (RecyclerViewEx) view.findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(mAdapter);
        listView.setItemViewCacheSize(20);
        listView.setOnNextPageLoadListener(mNextPageLoadListener);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadPage(1, mMid);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMessagePost();
            }
        });

        mSwipeRefreshLayout.setEnabled(false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mPresenter.loadPage(1, mMid);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void hideLoadingView() {
        mAdapter.hideLoadingView();
        mSwipeRefreshLayout.setEnabled(true);
    }


    @Override
    public void setData(MessageDetailInfo listInfo) {
        mTitle = listInfo.get_Title();
        mRecipient = listInfo.get_Alluser();
        mAdapter.setData(listInfo);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        if (mSwipeRefreshLayout.isEnabled()) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    @Override
    public boolean isRefreshing() {
        return mSwipeRefreshLayout.isRefreshing();
    }

    private void startMessagePost() {
        ARouter.getInstance().build(ARouterConstants.ACTIVITY_MESSAGE_POST)
                .withInt("mid", mMid)
                .withString("action", "reply")
                .withString("to", mRecipient)
                .withString("title", mTitle)
                .navigation(getContext());
    }

}
