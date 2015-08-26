package sp.phone.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import gov.anzong.androidnga.R;

public class AlertDialogFragment extends DialogFragment {
    DialogInterface.OnClickListener okLintener = null;
    DialogInterface.OnClickListener cancleLintener = null;

    public static AlertDialogFragment create(String title, String text) {
        AlertDialogFragment f = new AlertDialogFragment();
        Bundle args = new Bundle();
        if (title != null) {
            args.putString("title", title);
        }
        args.putString("text", text);
        f.setArguments(args);
        return f;
    }

    public static AlertDialogFragment create(String text) {
        return create(null, text);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.setCancelable(true);
        //setStyle(DialogFragment.STYLE_NO_FRAME, 0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.default_dialog, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setView(view);
        final String title = this.getArguments().getString("title");
        if (title != null)
            alert.setTitle(title);
        else
            alert.setTitle(R.string.warn);
        final String text = this.getArguments().getString("text");
        TextView v = (TextView) view.findViewById(R.id.defaultdialog_name);
        v.setText(text);
        v.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        alert.setPositiveButton(android.R.string.ok, okLintener);
        alert.setNegativeButton(android.R.string.cancel, cancleLintener);


        final AlertDialog dialog = alert.create();
//		dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {
//
//			@Override
//			public void onDismiss(DialogInterface arg0) {
//				// TODO Auto-generated method stub
//				dialog.dismiss();
//				if (PhoneConfiguration.getInstance().fullscreen) {
//					ActivityUtil.getInstance().setFullScreen(view);
//				}
//			}
//
//		});
        return dialog;

    }

    public void setOkListener(DialogInterface.OnClickListener okLintener) {
        this.okLintener = okLintener;
    }

    public void setCancleListener(DialogInterface.OnClickListener cancleLintener) {
        this.cancleLintener = cancleLintener;
    }

}
