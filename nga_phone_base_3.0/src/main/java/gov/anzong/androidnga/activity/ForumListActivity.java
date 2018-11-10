package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private EditText mFilterText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_list);

        mListView = (RecyclerView) findViewById(R.id.list);
        mListView.setLayoutManager(new GridLayoutManager(this, BoardCategoryFragment.COLUMN_NUMBER));
        mListView.setBackgroundResource(ThemeManager.getInstance().getBackgroundColor());
        mAdapter = new ForumListAdapter(this, mDataList);
        mListView.setAdapter(mAdapter);

        mFilterText = (EditText) findViewById(R.id.filer_text);
        mFilterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String filter = s.toString();
                Pattern p = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);
                List<ForumsListModel.Forum> filtered = new ArrayList<ForumsListModel.Forum>();
                for (ForumsListModel.Forum forum: mDataList) {
                    Matcher matcher = p.matcher(forum.getName());
                    if(matcher.find()) {
                        filtered.add(forum);
                    }
                }
                mAdapter = new ForumListAdapter(ForumListActivity.this, filtered);
                mListView.setAdapter(mAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
