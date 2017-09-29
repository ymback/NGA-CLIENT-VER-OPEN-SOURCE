package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import gov.anzong.androidnga.R;
import sp.phone.task.GetAllForumsTask;

/**
 * 在线获取版面列表
 * Created by elrond on 2017/9/28.
 */

public class ForumListActivity extends SwipeBackAppCompatActivity {

    private RecyclerView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_list);
        mList = (RecyclerView) findViewById(R.id.list);
        GetAllForumsTask task = new GetAllForumsTask(this);
        task.execute();
    }
}
