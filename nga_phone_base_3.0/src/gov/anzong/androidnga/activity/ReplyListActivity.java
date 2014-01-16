package gov.anzong.androidnga.activity;

import sp.phone.fragment.ReplyListFragment;
import gov.anzong.androidnga.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class ReplyListActivity extends SwipeBackAppCompatActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		this.setContentView(R.layout.topiclist_activity);
		FragmentManager fm  = this.getSupportFragmentManager();
		Fragment f = fm.findFragmentById(R.id.item_list);
		if( f == null)
		{
			f = new ReplyListFragment();
			fm.beginTransaction().add(R.id.item_list,f ).commit();
		}
		
	}

}
