package sp.phone.ui.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;

import java.lang.reflect.Field;

import gov.anzong.androidnga.BuildConfig;
import gov.anzong.androidnga.NgaClientApp;
import gov.anzong.androidnga.R;
import sp.phone.common.VersionUpgradeTips;


public class VersionUpgradeDialogFragment extends BaseDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String tip = getUpgradeTip();
        if (tip == null) {
            dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton(android.R.string.ok, null)
                .setTitle(R.string.prompt)
                .setMessage(tip);
        return builder.create();
    }

    @Override
    public void onDestroy() {
        NgaClientApp.setNewVersion(false);
        super.onDestroy();
    }

    private String getUpgradeTip() {
        try {
            int id  = getContext().getResources().getIdentifier("tip_" + BuildConfig.VERSION_CODE, "string", getContext().getPackageName());
            if (id > 0) {
                return getString(id);
            } else {
                return null;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
