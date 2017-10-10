package sp.phone.fragment.material;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import sp.phone.adapter.material.AppendableMessageListAdapter;
import sp.phone.bean.MessageListInfo;
import sp.phone.presenter.MessageListPresenter;
import sp.phone.presenter.contract.tmp.MessageListContract;
import sp.phone.utils.ActivityUtils;
import sp.phone.view.RecyclerViewEx;

public class MessageListFragment extends BaseMvpFragment implements SwipeRefreshLayout.OnRefreshListener,MessageListContract.IMessageView{

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.progress_panel)
    ViewGroup mProgressPanel;

    private View.OnClickListener mClickListener;

    private MessageListContract.IMessagePresenter mPresenter;

    private AppendableMessageListAdapter mAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = new MessageListPresenter();
        setPresenter(mPresenter);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list,container,false);
        ButterKnife.bind(this,view);

        mAdapter = new AppendableMessageListAdapter(getContext(),mPresenter);
        mAdapter.setOnClickListener(mClickListener);

        RecyclerViewEx listView = (RecyclerViewEx) view.findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setEmptyView(view.findViewById(R.id.empty_view));
        listView.setAdapter(mAdapter);

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
    public void onRefresh() {
        mPresenter.loadPage(1);
    }

    @Override
    public void hideProgressBar() {
        mProgressPanel.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void setData(MessageListInfo listInfo) {
        mAdapter.setData(listInfo);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }
}
