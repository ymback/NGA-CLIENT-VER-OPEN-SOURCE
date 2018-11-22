package sp.phone.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.List;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.TopicListActivity;
import gov.anzong.androidnga.arouter.ARouterConstants;
import sp.phone.adapter.BoardSubListAdapter;
import sp.phone.bean.SubBoard;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.adapter.BoardSubListAdapter;
import sp.phone.bean.SubBoard;
import sp.phone.forumoperation.ParamKey;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.task.SubscribeSubBoardTask;
import sp.phone.view.RecyclerViewEx;

/**
 * Created by Justwen on 2018/1/27.
 */

public class BoardSubListFragment extends BaseRxFragment implements View.OnClickListener {

    private RecyclerViewEx mListView;

    private BoardSubListAdapter mListAdapter;

    private List<SubBoard> mBoardList;

    private SubscribeSubBoardTask mSubscribeTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        setTitle(String.format("%s - 子版快", bundle.getString(ParamKey.KEY_TITLE)));
        mBoardList = bundle.getParcelableArrayList("subBoard");
        mSubscribeTask = new SubscribeSubBoardTask(getLifecycleProvider());
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mListView = new RecyclerViewEx(inflater.getContext());
        mListView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        mListAdapter = new BoardSubListAdapter(inflater.getContext(), mBoardList);
        mListAdapter.setOnClickListener(this);
        mListView.setAdapter(mListAdapter);
        return mListView;
    }

    @Override
    public void onClick(final View v) {
        final SubBoard board = (SubBoard) v.getTag();
        if (v.getId() == R.id.check) {
            OnHttpCallBack<String> callBack = new OnHttpCallBack<String>() {
                @Override
                public void onError(String text) {
                    showToast(text);
                    ((Checkable) v).setChecked(board.isChecked());
                }

                @Override
                public void onSuccess(String data) {
                    showToast(data);
                    board.setChecked(v.isClickable());
                    setResult(Activity.RESULT_OK);
                }
            };
            if (board.isChecked()) {
                mSubscribeTask.unsubscribe(board, callBack);
            } else {
                mSubscribeTask.subscribe(board, callBack);
            }
        } else {
            ARouter.getInstance()
                    .build(ARouterConstants.ACTIVITY_TOPIC_LIST)
                    .withString(ParamKey.KEY_TITLE, board.getName())
                    .withInt(ParamKey.KEY_FID, board.getFid())
                    .withInt(ParamKey.KEY_STID, board.getStid())
                    .navigation(getContext());
        }
    }
}