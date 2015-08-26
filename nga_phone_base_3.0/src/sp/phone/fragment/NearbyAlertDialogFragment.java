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

public class NearbyAlertDialogFragment extends DialogFragment {
    DialogInterface.OnClickListener okLintener = null;
    DialogInterface.OnClickListener cancleLintener = null;

    public static NearbyAlertDialogFragment create(String title, String text) {
        NearbyAlertDialogFragment f = new NearbyAlertDialogFragment();
        Bundle args = new Bundle();
        if (title != null) {
            args.putString("title", title);
        }
        args.putString("text", text);
        f.setArguments(args);
        return f;
    }

    public static NearbyAlertDialogFragment create(String text) {
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
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final String title = this.getArguments().getString("title");
        final View view = layoutInflater.inflate(R.layout.default_dialog, null);
        alert.setView(view);
        if (title != null)
            alert.setTitle(title);
        else
            alert.setTitle(R.string.warnnearby);
        final String text = this.getArguments().getString("text");
        TextView v = new TextView(this.getActivity());
        v.setText(text);
        v.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        alert.setView(v);

        alert.setPositiveButton(R.string.nearbyinprofile, okLintener);
        alert.setNegativeButton(R.string.nearbyingooglemaps, cancleLintener);


        return alert.create();

    }

    public void setOkListener(DialogInterface.OnClickListener okLintener) {
        this.okLintener = okLintener;
    }

    public void setCancleListener(DialogInterface.OnClickListener cancleLintener) {
        this.cancleLintener = cancleLintener;
    }

}
