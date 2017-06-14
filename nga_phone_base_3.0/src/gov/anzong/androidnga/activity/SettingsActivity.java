package gov.anzong.androidnga.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import gov.anzong.androidnga.R;
import sp.phone.fragment.material.SettingsFragment;
import sp.phone.utils.ThemeManager;

public class SettingsActivity extends SwipeBackAppCompatActivity {

    private PreferenceFragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        updateThemeUi();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupFragment();
        setupActionBar();
    }

    @Override
    protected void updateThemeUi(){
        if (ThemeManager.getInstance().isNightMode()){
            setTheme(R.style.MaterialThemeDarkNoActionBar);
        } else {
            setTheme(R.style.MaterialThemeNoActionBar);
        }
    }

    private void setupActionBar(){
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void setupFragment(){
        FragmentManager fm = getFragmentManager();
        mSettingsFragment = (PreferenceFragment) fm.findFragmentByTag(SettingsFragment.class.getSimpleName());
        if (mSettingsFragment == null){
            mSettingsFragment = new SettingsFragment();
            getFragmentManager().beginTransaction().replace(R.id.container,mSettingsFragment,SettingsFragment.class.getSimpleName()).commit();
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
