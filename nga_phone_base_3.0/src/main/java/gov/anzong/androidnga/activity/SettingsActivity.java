package gov.anzong.androidnga.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import sp.phone.fragment.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFragment();
        setupActionBar();
    }

    private void setupFragment() {
        FragmentManager fm = getFragmentManager();
        Fragment settingsFragment = fm.findFragmentByTag(SettingsFragment.class.getSimpleName());
        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
            fm.beginTransaction().replace(android.R.id.content, settingsFragment, SettingsFragment.class.getSimpleName()).commit();
        }
    }

}
