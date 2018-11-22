package gov.anzong.androidnga.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import sp.phone.fragment.BaseFragment;

public class LauncherSubActivity extends BaseActivity {

    private BaseFragment mBaseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.getBooleanExtra("hideActionBar", false)) {
            hideActionBar();
        }
        super.onCreate(savedInstanceState);
        String fragmentStr = intent.getStringExtra("fragment");
        if (fragmentStr != null) {
            commitFragment(fragmentStr);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void commitFragment(String fragmentStr) {
        try {
            Object fragment = Class.forName(fragmentStr).newInstance();
            if (fragment instanceof BaseFragment) {
                mBaseFragment = (BaseFragment) fragment;
                Bundle bundle = getIntent().getExtras();
                mBaseFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(android.R.id.content, mBaseFragment).commit();
            } else {
                Bundle bundle = getIntent().getExtras();
                ((Fragment) fragment).setArguments(bundle);
                getFragmentManager().beginTransaction().replace(android.R.id.content, (Fragment) fragment).commit();
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (mBaseFragment == null || !mBaseFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
