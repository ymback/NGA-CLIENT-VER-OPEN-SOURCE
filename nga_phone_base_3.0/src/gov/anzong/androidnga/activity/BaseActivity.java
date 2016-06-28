package gov.anzong.androidnga.activity;

import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

/**
 * Created by liuboyu on 16/6/28.
 */
public class BaseActivity extends ActionBarActivity {

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
            toast = Toast.makeText(this, res, Toast.LENGTH_SHORT);
        }
        toast.show();
    }

}
