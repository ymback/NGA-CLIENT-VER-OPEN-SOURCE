package sp.phone.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import gov.anzong.androidnga.activity.BaseActivity;

/**
 * Created by liuboyu on 16/6/28.
 */
public class BaseFragment extends Fragment {

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
            mToast = Toast.makeText(getActivity(), res, Toast.LENGTH_SHORT);
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
