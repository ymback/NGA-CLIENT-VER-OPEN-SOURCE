package gov.anzong.androidnga.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;

import gov.anzong.androidnga.base.util.ThemeUtils;
import sp.phone.fragment.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    private static final String KEY_RECREATE = "recreate";

    private boolean mRecreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFragment();
        setupActionBar();
        if (savedInstanceState != null) {
            mRecreated = savedInstanceState.getBoolean(KEY_RECREATE);
        }
        if (mRecreated) {
            View contentView = findViewById(android.R.id.content);
            contentView.post(() -> startAnimation(contentView));
            mRecreated = false;
            ThemeUtils.init(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_RECREATE, mRecreated);
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

    @Override
    public void recreate() {
        mRecreated = true;
        super.recreate();
    }
}
