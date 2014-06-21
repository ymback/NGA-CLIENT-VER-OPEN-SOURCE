package gov.anzong.androidnga.activity;

import sp.phone.fragment.RecentReplyListFragment;
import sp.phone.fragment.ReplyListFragment;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import gov.anzong.androidnga.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

public class ReplyListActivity extends SwipeBackAppCompatActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getSupportActionBar().setTitle("�ҵı���");
		this.setContentView(R.layout.topiclist_activity);
		FragmentManager fm  = this.getSupportFragmentManager();
		Fragment f = fm.findFragmentById(R.id.item_list);
		if( f == null)
		{
				f = new ReplyListFragment();
			fm.beginTransaction().add(R.id.item_list,f ).commit();
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
		switch(item.getItemId()){
		default:
			finish();
		}
		return true;
	}

}
