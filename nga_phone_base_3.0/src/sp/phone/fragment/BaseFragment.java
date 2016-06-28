package sp.phone.fragment;

import android.support.v4.app.Fragment;
import android.widget.Toast;

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
}
