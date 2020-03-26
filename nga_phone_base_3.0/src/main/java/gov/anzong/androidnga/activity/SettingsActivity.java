package gov.anzong.androidnga.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;

import gov.anzong.androidnga.base.util.ThemeUtils;
import sp.phone.ui.fragment.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    private static final String KEY_RECREATE = "recreate";

    public static boolean sRecreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFragment();
        setupActionBar();
        if (sRecreated) {
            sRecreated = false;
            ThemeUtils.init(this);
            setResult(Activity.RESULT_OK);
            findViewById(android.R.id.content).post(() -> startAnimation(findViewById(android.R.id.content)));
        }
    }
    private void setupFragment() {
        FragmentManager fm = getFragmentManager();
        Fragment settingsFragment = fm.findFragmentByTag(SettingsFragment.class.getSimpleName());
        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
            fm.beginTransaction().replace(android.R.id.content, settingsFragment, SettingsFragment.class.getSimpleName()).commit();
        }
    }

    private void startAnimation(View contentView) {
        int cx = contentView.getWidth() / 2;
        int cy = contentView.getHeight() / 2;
        float finalRadius = (float) Math.hypot(cx, cy);
        ViewAnimationUtils.createCircularReveal(contentView, cx, cy, 0f, finalRadius).start();
    }

}
