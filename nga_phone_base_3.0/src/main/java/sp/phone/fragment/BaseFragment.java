package sp.phone.fragment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.BaseActivity;
import sp.phone.debug.LeakCanaryWatcher;
import sp.phone.common.PhoneConfiguration;
import sp.phone.util.ActivityUtils;

/**
 * Created by liuboyu on 16/6/28.
 */
public abstract class BaseFragment extends Fragment {

    @Nullable
    protected BaseActivity mActivity;

    protected Toast mToast;

    protected PhoneConfiguration mConfig = PhoneConfiguration.getInstance();

    private int mTitleId;

    private CharSequence mTitleStr;

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

    public void finish() {
        if (mActivity != null) {
            mActivity.finish();
        }
    }

    protected void setupToolbar(Toolbar toolbar) {
        if (mActivity != null) {
            mActivity.setupToolbar(toolbar);
        }
    }

    protected ActionBar getSupportActionBar() {
        return mActivity != null ? mActivity.getSupportActionBar() : null;
    }

    public void setTitle(String title) {
        mTitleStr = title;
        mTitleId = 0;
    }

    public void setTitle(int resId) {
        mTitleId = resId;
        mTitleStr = null;
    }

    public void setResult(int resultCode) {
        if (mActivity != null) {
            mActivity.setResult(resultCode);
        }
    }

    @Override
    public void onResume() {
        if (mActivity != null) {
            if (mTitleStr != null) {
                mActivity.setTitle(mTitleStr);
            } else if (mTitleId > 0) {
                mActivity.setTitle(mTitleId);
            }
        }
        super.onResume();
    }

    protected FragmentManager getSupportFragmentManager() {
        return getChildFragmentManager();
    }

    @Override
    public void onAttach(Context context) {
        mActivity = (BaseActivity) context;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_history:
                ActivityUtils.startHistoryTopicActivity(getContext());
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LeakCanaryWatcher.watch(this);
    }
}
