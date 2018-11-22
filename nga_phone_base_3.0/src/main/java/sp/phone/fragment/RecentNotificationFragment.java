package sp.phone.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.R;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.model.entity.RecentReplyInfo;
import sp.phone.adapter.RecentNotificationAdapter;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.PreferenceKey;
import sp.phone.forumoperation.ParamKey;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.model.entity.RecentReplyInfo;
import sp.phone.task.ForumNotificationTask;
import sp.phone.view.EmptyLayout;
import sp.phone.view.LoadingLayout;
import sp.phone.view.RecyclerViewEx;

public class RecentNotificationFragment extends BaseRxFragment implements OnHttpCallBack<List<RecentReplyInfo>>, View.OnClickListener {

    private RecentNotificationAdapter mNotificationAdapter;

    private ForumNotificationTask mNotificationTask;

    private SwipeRefreshLayout mRefreshLayout;

    private LoadingLayout mLoadingLayout;

    private EmptyLayout mEmptyLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mNotificationTask = new ForumNotificationTask(getLifecycleProvider());
        mNotificationAdapter = new RecentNotificationAdapter(getContext());
        mNotificationAdapter.setClickListener(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            ArrayList<RecentReplyInfo> unreadRecentReplyList = bundle.getParcelableArrayList("unread");
            mNotificationAdapter.setUnreadRecentReplyList(unreadRecentReplyList);
        }

        PreferenceManager.getDefaultSharedPreferences(getContext())
                .edit().putInt(PreferenceKey.KEY_REPLY_COUNT, 0).apply();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recent_reply, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRefreshLayout = view.findViewById(R.id.swipe_refresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mNotificationTask.queryRecentReply(RecentNotificationFragment.this);
            }
        });
        mRefreshLayout.setVisibility(View.GONE);

        mLoadingLayout = view.findViewById(R.id.loading_view);
        mEmptyLayout = view.findViewById(R.id.empty_view);

        RecyclerViewEx listView = view.findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(mNotificationAdapter);
        listView.setEmptyView(mEmptyLayout);
        listView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        mNotificationTask.queryRecentReply(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(this.getString(R.string.delete_recentreply_confirm_text))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mNotificationTask.clearAllNotification();
                                mNotificationAdapter.setRecentReplyList(null);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null);
                builder.create().show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.recent_reply_menu, menu);
    }

    @Override
    public void onError(String text) {
        setRefreshing(false);
        mEmptyLayout.setEmptyText(text);
        showToast(text);
    }

    private void setRefreshing(boolean refreshing) {
        if (!refreshing) {
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRefreshLayout.setRefreshing(false);
            mLoadingLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSuccess(List<RecentReplyInfo> data) {
        if (data.isEmpty()) {
            showToast("没有最近被喷内容");
            mEmptyLayout.setEmptyText("没有最近被喷内容");
            setRefreshing(false);
        } else {
            if (data.get(0).isUnread()) {
                mNotificationAdapter.setUnreadRecentReplyList(data);
                mNotificationTask.queryRecentReply(this);
            } else {
                mNotificationAdapter.setRecentReplyList(data);
                mRefreshLayout.setRefreshing(false);
                mLoadingLayout.setVisibility(View.GONE);
                mRefreshLayout.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onClick(View v) {
        RecentReplyInfo info = (RecentReplyInfo) v.getTag();

        Intent intent = new Intent(getContext(), PhoneConfiguration.getInstance().articleActivityClass);
        intent.putExtra(ParamKey.KEY_TID, Integer.parseInt(info.getTidStr()));
        intent.putExtra(ParamKey.KEY_PID, Integer.parseInt(info.getPidStr()));
        intent.putExtra(ParamKey.KEY_TITLE, info.getTitle());
        intent.putExtra(ParamKey.KEY_SEARCH_POST, 1);
        startActivity(intent);
    }
}
