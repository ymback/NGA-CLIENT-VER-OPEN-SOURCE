package sp.phone.fragment;

import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.widget.Toast;

import gov.anzong.androidnga.activity.BaseActivity;

/**
 * Created by liuboyu on 16/6/28.
 */
public class BaseFragment extends Fragment {
    protected Toast toast;

    protected void showToast(int res) {
        String str = getString(res);
        showToast(str);
    }

    protected void showToast(String res) {
        if (toast != null) {
            toast.setText(res);
            toast.setDuration(Toast.LENGTH_SHORT);
        } else {
            toast = Toast.makeText(getActivity(), res, Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    protected void changeNightMode(final MenuItem menu) {
        getBaseActivity().changeNightMode(menu);
    }
}
