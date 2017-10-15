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
import sp.phone.adapter.material.AppendableMessageDetailAdapter;
import sp.phone.bean.MessageDetailInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.presenter.MessageDetailPresenter;
import sp.phone.presenter.contract.tmp.MessageDetailContract;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.StringUtils;
import sp.phone.view.RecyclerViewEx;

public class MessageDetailFragment extends BaseMvpFragment implements MessageDetailContract.IMessageView {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.progress_panel)
    ViewGroup mProgressPanel;

    private int mMid;

    private String mTitle;

    private String mRecipient;

    private MessageDetailContract.IMessagePresenter mPresenter;

    private AppendableMessageDetailAdapter mAdapter;

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

        mAdapter = new AppendableMessageDetailAdapter(getContext(),mPresenter,mMid);

        RecyclerViewEx listView = (RecyclerViewEx) view.findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setEmptyView(view.findViewById(R.id.empty_view));
        listView.setAdapter(mAdapter);
        listView.setItemViewCacheSize(20);

        TextView sayView = (TextView) view.findViewById(R.id.saying);
        sayView.setText(ActivityUtils.getSaying());

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadPage(1,mMid);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startArticleReply();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mPresenter.loadPage(1,mMid);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void hideProgressBar() {
        mProgressPanel.setVisibility(View.GONE);
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
    public void clearData() {
        mAdapter.clear();
    }

    private void startArticleReply(){
        Intent intent = new Intent();
        intent.putExtra("mid", mMid);
        intent.putExtra("title", mTitle);
        intent.putExtra("to", mRecipient);
        intent.putExtra("action", "reply");
        intent.putExtra("messagemode", "yes");
        if (!StringUtils.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
            intent.setClass(
                            getActivity(),
                            PhoneConfiguration.getInstance().messagePostActivityClass);
        } else {
            intent.setClass(getActivity(),
                    PhoneConfiguration.getInstance().loginActivityClass);
        }
        startActivity(intent);
    }

}
