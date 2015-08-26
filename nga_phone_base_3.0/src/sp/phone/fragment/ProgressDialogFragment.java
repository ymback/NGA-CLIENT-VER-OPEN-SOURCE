package sp.phone.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import sp.phone.utils.ActivityUtil;
import sp.phone.utils.StringUtil;


public class ProgressDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        //
        Bundle b = getArguments();
        if (b != null) {
            String title = b.getString("title");
            String content = b.getString("content");
            dialog.setTitle(title);
            if (StringUtil.isEmpty(content))
                content = ActivityUtil.getSaying();
            dialog.setMessage(content);
        }


        //dialog.setCanceledOnTouchOutside(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        //dialog.setCancelable(true);


        // etc...
        this.setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);
        return dialog;
    }

}
