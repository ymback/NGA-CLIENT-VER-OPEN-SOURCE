package sp.phone.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Justwen on 2018/2/16.
 */

public abstract class BaseDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {

    public void showToast(String toast) {
        if (getContext() != null) {
            Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
        }
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

    protected boolean onPositiveClick() {
        return false;
    }

    @Override
    public void onResume() {
        Dialog dialog = getDialog();
        if (dialog instanceof AlertDialog) {
            Button btn = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
            if (btn != null) {
                btn.setOnClickListener(this);
            }
        }
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (onPositiveClick()) {
            dismiss();
        }
    }
}
