package sp.phone.ui.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * @author yangyihang
 */
public class AlertDialogFragment extends DialogFragment {

    private DialogInterface.OnClickListener mPositiveClickListener = null;

    private DialogInterface.OnClickListener mNegativeClickListener = null;

    public static AlertDialogFragment create(String title, String message) {
        AlertDialogFragment f = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        f.setArguments(args);
        return f;
    }

    public static AlertDialogFragment create(String text) {
        return create(null, text);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String text = getArguments().getString("message");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title)
                .setMessage(text)
                .setPositiveButton(android.R.string.ok, mPositiveClickListener)
                .setNegativeButton(android.R.string.cancel, mNegativeClickListener);
        return builder.create();
    }

    public void setPositiveClickListener(DialogInterface.OnClickListener positiveClickListener) {
        mPositiveClickListener = positiveClickListener;
    }

    public void setNegativeClickListener(DialogInterface.OnClickListener negativeClickListener) {
        mNegativeClickListener = negativeClickListener;
    }
}
