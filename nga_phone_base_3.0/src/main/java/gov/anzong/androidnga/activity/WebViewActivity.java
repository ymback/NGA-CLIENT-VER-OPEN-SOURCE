package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import gov.anzong.androidnga.activity.fragment.WebViewFragment;
import gov.anzong.androidnga.ui.fragment.BaseFragment;

/**
 * @author yangyihang
 */
public class WebViewActivity extends BaseActivity {

    private BaseFragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        initTitle();
        mFragment = createFragment();
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, mFragment).commit();
    }

    private void initTitle() {
        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }
    }

    private BaseFragment createFragment() {
        BaseFragment fragment = new WebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", getIntent().getStringExtra("url"));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onBackPressed() {
        if (!mFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
