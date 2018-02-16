package sp.phone.fragment.dialog;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

/**
 * Created by Justwen on 2018/2/16.
 */

public abstract class BaseDialogFragment extends DialogFragment {

    protected DialogInterface.OnClickListener mPositiveClickListener;

    protected DialogInterface.OnClickListener mNegativeClickListener;

    public void setPositiveClickListener(DialogInterface.OnClickListener positiveClickListener) {
        mPositiveClickListener = positiveClickListener;
    }

    public void setNegativeClickListener(DialogInterface.OnClickListener negativeClickListener) {
        mNegativeClickListener = negativeClickListener;
    }


    public void showToast(String toast) {
        if (getContext() != null) {
            Toast.makeText(getContext(),toast,Toast.LENGTH_SHORT).show();
        }
    }

    public void show(FragmentManager fm) {
        show(fm,getClass().getSimpleName());
    }

}
