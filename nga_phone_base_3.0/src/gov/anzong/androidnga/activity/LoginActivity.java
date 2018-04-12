package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.alibaba.android.arouter.facade.annotation.Route;

import sp.phone.fragment.LoginWebFragment;
import sp.phone.util.ActivityUtils;

@Route(path = ActivityUtils.PATH_LOGIN)
public class LoginActivity extends SwipeBackAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, getLoginFragment()).commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private Fragment getLoginFragment() {
        return new LoginWebFragment();
    }

}
