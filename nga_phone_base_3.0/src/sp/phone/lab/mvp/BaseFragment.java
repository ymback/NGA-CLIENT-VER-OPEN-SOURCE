package sp.phone.lab.mvp;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxFragment;

import gov.anzong.androidnga.activity.BaseActivity;

/**
 * Created by liuboyu on 16/6/28.
 */
public abstract class BaseFragment extends RxFragment {

    protected AppCompatActivity mActivity;

    protected Toast mToast;

    public void showToast(int res) {
        String str = getString(res);
        showToast(str);
    }

    public void showToast(String res) {
        if (mActivity == null){
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


    public void finish(){
        if (mActivity != null) {
            mActivity.finish();
        }
    }

    protected void setSupportActionBar(Toolbar toolbar) {
        if (mActivity != null) {
            mActivity.setSupportActionBar(toolbar);
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

    protected LayoutInflater getLayoutInflater() {
        return mActivity != null ? mActivity.getLayoutInflater() : null;
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
