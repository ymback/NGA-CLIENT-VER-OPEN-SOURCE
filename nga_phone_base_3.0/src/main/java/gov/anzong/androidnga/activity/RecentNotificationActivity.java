package gov.anzong.androidnga.activity;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;

import com.alibaba.android.arouter.facade.annotation.Route;

import gov.anzong.androidnga.arouter.ARouterConstants;
import sp.phone.ui.fragment.RecentNotificationFragment;

@Route(path = ARouterConstants.ACTIVITY_NOTIFICATION)
public class RecentNotificationActivity extends BaseActivity {

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
