package sp.phone.fragment.material;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import sp.phone.adapter.material.MessageDetailAdapter;
import sp.phone.bean.MessageDetailInfo;
import sp.phone.presenter.MessageDetailPresenter;
import sp.phone.presenter.contract.tmp.MessageDetailContract;
import sp.phone.utils.ActivityUtils;
import sp.phone.view.RecyclerViewEx;

public class MessageDetailFragment extends BaseMvpFragment implements MessageDetailContract.IMessageView {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.loading_view)
    ViewGroup mLoadingView;

    private int mMid;

    private String mTitle;

    private String mRecipient;

    private MessageDetailContract.IMessagePresenter mPresenter;

    private MessageDetailAdapter mAdapter;

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
        mPresenter = new MessageDetailPresenter();
        setPresenter(mPresenter);
        super.onCreate(savedInstanceState);
        mMid = getArguments().getInt("mid");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_detail,container,false);
        ButterKnife.bind(this,view);

        mAdapter = new MessageDetailAdapter(getContext());

        RecyclerViewEx listView = (RecyclerViewEx) view.findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setEmptyView(view.findViewById(R.id.empty_view));
        listView.setAdapter(mAdapter);
        listView.setItemViewCacheSize(20);
        listView.setOnNextPageLoadListener(mNextPageLoadListener);

        TextView sayView = (TextView) mLoadingView.findViewById(R.id.saying);
        sayView.setText(ActivityUtils.getSaying());

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

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mPresenter.loadPage(1, mMid);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void hideLoadingView() {
        mLoadingView.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public void setData(MessageDetailInfo listInfo) {
        mTitle = listInfo.get_Title();
        mRecipient = listInfo.get_Alluser();
        mAdapter.setData(listInfo);
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

    private void startMessagePost(){
        Intent intent = new Intent();
        intent.putExtra("mid", mMid);
        intent.putExtra("title", mTitle);
        intent.putExtra("to", mRecipient);
        intent.putExtra("action", "reply");
        intent.putExtra("messagemode", "yes");
        ActivityUtils.startMessagePostActivity(getActivity(),intent);
    }

}
