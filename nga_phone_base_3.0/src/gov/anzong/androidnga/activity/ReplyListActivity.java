package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import gov.anzong.androidnga.R;
import sp.phone.fragment.ReplyListFragment;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.ThemeManager;

public class ReplyListActivity extends SwipeBackAppCompatActivity {
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        getSupportActionBar().setTitle("我的被喷");
        this.setContentView(R.layout.topiclist_activity);
        FragmentManager fm = this.getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.item_list);
        if (f == null) {
            f = new ReplyListFragment();
            fm.beginTransaction().add(R.id.item_list, f).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final int flags = ThemeManager.ACTION_BAR_FLAG;
        ReflectionUtil.actionBar_setDisplayOption(this, flags);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                finish();
        }
        return true;
    }

}
