package gov.anzong.androidnga.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class SettingsSubActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String fragmentStr = intent.getStringExtra("fragment");
        if (fragmentStr != null) {
            commitFragment(fragmentStr);
        }
    }

    private void commitFragment(String fragmentStr) {
        try {
            Object fragment = Class.forName(fragmentStr).newInstance();
            if (fragment instanceof Fragment) {
                getSupportFragmentManager().beginTransaction().replace(android.R.id.content, (Fragment) fragment).commit();
            } else {
                getFragmentManager().beginTransaction().replace(android.R.id.content, (android.app.Fragment) fragment).commit();
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
