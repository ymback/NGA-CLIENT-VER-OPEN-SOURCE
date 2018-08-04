package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.R;
import sp.phone.fragment.BoardCategoryFragment;
import sp.phone.mvp.model.ForumsListModel;
import sp.phone.task.GetAllForumsTask;
import sp.phone.adapter.ForumListAdapter;
import sp.phone.theme.ThemeManager;
import sp.phone.fragment.BoardCategoryFragment;
import sp.phone.mvp.model.ForumsListModel;
import sp.phone.task.GetAllForumsTask;

/**
 * 在线获取版面列表
 * Created by elrond on 2017/9/28.
 */

public class ForumListActivity extends BaseActivity {

    private List<ForumsListModel.Forum> mDataList = new ArrayList<>();
    private ForumListAdapter mAdapter;
    private RecyclerView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_list);

        mListView = (RecyclerView) findViewById(R.id.list);
        mListView.setLayoutManager(new GridLayoutManager(this, BoardCategoryFragment.COLUMN_NUMBER));
        mListView.setBackgroundResource(ThemeManager.getInstance().getBackgroundColor());
        mAdapter = new ForumListAdapter(this, mDataList);
        mListView.setAdapter(mAdapter);

        GetAllForumsTask task = new GetAllForumsTask(this);
        task.execute();
    }

    public void notifyResult(ForumsListModel model) {
        for (ForumsListModel.Result result : model.getResult()) {
            for (ForumsListModel.Group group : result.getGroups()) {
                for (ForumsListModel.Forum forum : group.getForums()) {
                    if (!mDataList.contains(forum))
                        mDataList.add(forum);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
