package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import gov.anzong.androidnga.R;

/**
 * 在线获取版面列表
 * Created by elrond on 2017/9/28.
 */

public class ForumListActivity extends SwipeBackAppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_list);
        setupActionBar((Toolbar) findViewById(R.id.toolbar));
    }
}
