package gov.anzong.androidnga.activity;

import gov.anzong.androidnga.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BoardActivity extends FragmentActivity {

	private boolean dualScreen;

	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.toplist_activity_two_panel);
		super.onCreate(arg0);
		if(null == findViewById(R.id.item_detail_container))
			dualScreen = false;
	}

}
