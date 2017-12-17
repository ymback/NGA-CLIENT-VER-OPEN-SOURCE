package sp.phone.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.BaseActivity;

/**
 * Created by liuboyu on 16/6/28.
 */
public abstract class BaseFragment extends Fragment {

    protected AppCompatActivity mActivity;

    protected Toast mToast;

    public void showToast(int res) {
        String str = getString(res);
        showToast(str);
    }

    public void showToast(String res) {
        if (mActivity == null) {
            return;
        }
        if (mToast != null) {
            mToast.setText(res);
            mToast.setDuration(Toast.LENGTH_SHORT);
        } else {
            mToast = Toast.makeText(mActivity, res, Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    protected void changeNightMode(final MenuItem menu) {
        getBaseActivity().changeNightMode(menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_material, container, false);
        FrameLayout realContainer = rootView.findViewById(R.id.container);
        setSupportActionBar((Toolbar) rootView.findViewById(R.id.toolbar));
        View view = onCreateView(inflater, realContainer, savedInstanceState);
        if (view != null) {
            realContainer.addView(view);
        }
        return rootView;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable FrameLayout container, @Nullable Bundle savedInstanceState) {
        return null;
    }

    public void finish() {
        if (mActivity != null) {
            mActivity.finish();
        }
    }

    protected void setSupportActionBar(Toolbar toolbar) {
        if (mActivity != null) {
            mActivity.setSupportActionBar(toolbar);
            mActivity.getSupportActionBar().setHomeButtonEnabled(true);
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected ActionBar getSupportActionBar() {
        return mActivity != null ? mActivity.getSupportActionBar() : null;
    }

    protected void setTitle(String title) {
        if (mActivity != null) {
            mActivity.setTitle(title);
        }
    }

    protected void setTitle(int resId) {
        setTitle(getString(resId));
    }

    protected FragmentManager getSupportFragmentManager() {
        return getChildFragmentManager();
    }

    @Override
    public void onAttach(Context context) {
        mActivity = (AppCompatActivity) context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }
}
