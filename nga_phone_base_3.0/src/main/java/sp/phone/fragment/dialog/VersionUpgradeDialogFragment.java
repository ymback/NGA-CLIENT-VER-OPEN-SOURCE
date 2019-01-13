package sp.phone.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.lang.reflect.Field;

import gov.anzong.androidnga.BuildConfig;
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

    private String getUpgradeTip() {
        try {
            Class clz = VersionUpgradeTips.class;
            Field field = clz.getDeclaredField("TIPS_" + BuildConfig.VERSION_CODE);
            return (String) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
