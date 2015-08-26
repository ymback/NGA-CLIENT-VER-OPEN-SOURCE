package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import gov.anzong.androidnga.R;

public class BoardActivity extends FragmentActivity {

    @SuppressWarnings("unused")
    private boolean dualScreen;

    @Override
    protected void onCreate(Bundle arg0) {
        setContentView(R.layout.toplist_activity_two_panel);
        super.onCreate(arg0);
        if (null == findViewById(R.id.item_detail_container))
            dualScreen = false;
    }

}
