package sp.phone.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import gov.anzong.androidnga.R;
import sp.phone.utils.StringUtils;


public class VersionUpgradeDialogFragment extends BaseDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton(android.R.string.ok,null)
                .setTitle(R.string.prompt)
        .setMessage(StringUtils.getTips());
        return builder.create();
    }
}
