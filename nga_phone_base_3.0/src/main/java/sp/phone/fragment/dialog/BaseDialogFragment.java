package sp.phone.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Justwen on 2018/2/16.
 */

public abstract class BaseDialogFragment extends DialogFragment {

    protected View.OnClickListener mPositiveClickListener;

    protected View.OnClickListener mNegativeClickListener;

    public void setPositiveClickListener(View.OnClickListener positiveClickListener) {
        mPositiveClickListener = positiveClickListener;
    }

    public void setNegativeClickListener(View.OnClickListener negativeClickListener) {
        mNegativeClickListener = negativeClickListener;
    }


    public void showToast(String toast) {
        if (getContext() != null) {
            Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        if (dialog instanceof AlertDialog) {
            if (mPositiveClickListener != null) {
                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(mPositiveClickListener);
            }

            if (mNegativeClickListener != null) {
                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(mNegativeClickListener);
            }
        }
        super.onViewCreated(view, savedInstanceState);
    }

    public void show(FragmentManager fm) {
        show(fm, getClass().getSimpleName());
    }

    public static void show(FragmentManager fm, Bundle args, Class<?> target) {
        try {
            DialogFragment df = (DialogFragment) target.newInstance();
            df.setArguments(args);
            df.show(fm, target.getSimpleName());
        } catch (IllegalAccessException | java.lang.InstantiationException e) {
            e.printStackTrace();
        }
    }

}
