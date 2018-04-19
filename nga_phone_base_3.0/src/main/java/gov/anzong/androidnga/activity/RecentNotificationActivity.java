package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;

import sp.phone.fragment.RecentNotificationFragment;
import sp.phone.fragment.RecentNotificationFragment;

public class RecentNotificationActivity extends SwipeBackAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(android.R.id.content);
        if (fragment == null) {
            fragment = new RecentNotificationFragment();
            fragment.setArguments(getIntent().getExtras());
            fm.beginTransaction().add(android.R.id.content, fragment).commit();
        }
        setTitle("我的被喷");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

    }

}
