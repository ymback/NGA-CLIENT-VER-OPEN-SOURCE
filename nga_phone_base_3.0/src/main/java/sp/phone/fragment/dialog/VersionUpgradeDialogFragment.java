package sp.phone.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

import gov.anzong.androidnga.R;
import sp.phone.common.VersionUpgradeTips;


public class VersionUpgradeDialogFragment extends BaseDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton(android.R.string.ok,null)
                .setTitle(R.string.prompt)
        .setMessage(VersionUpgradeTips.TIPS_2089);
        return builder.create();
    }
}
