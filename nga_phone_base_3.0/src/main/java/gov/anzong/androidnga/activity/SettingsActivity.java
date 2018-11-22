package gov.anzong.androidnga.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;

import gov.anzong.androidnga.R;
import sp.phone.fragment.SettingsFragment;
import sp.phone.fragment.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    private PreferenceFragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        hideActionBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupFragment();
        setupActionBar((Toolbar) findViewById(R.id.toolbar));
    }


    private void setupFragment(){
        FragmentManager fm = getFragmentManager();
        mSettingsFragment = (PreferenceFragment) fm.findFragmentByTag(SettingsFragment.class.getSimpleName());
        if (mSettingsFragment == null){
            mSettingsFragment = new SettingsFragment();
            fm.beginTransaction().replace(R.id.container,mSettingsFragment,SettingsFragment.class.getSimpleName()).commit();
        }
    }

}
