package gov.anzong.androidnga.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;

import gov.anzong.androidnga.R;
import sp.phone.fragment.material.SettingsFragment;

public class SettingsActivity extends SwipeBackAppCompatActivity {

    private PreferenceFragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        } else if (fm.getBackStackEntryCount() > 0){
            fm.beginTransaction().hide(mSettingsFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            fm.beginTransaction().show(mSettingsFragment).commit();
        } else{
            super.onBackPressed();
        }
    }

}
