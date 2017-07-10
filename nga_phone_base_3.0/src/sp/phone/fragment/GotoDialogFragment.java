package sp.phone.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.NumberPicker;

import gov.anzong.androidnga.R;
import sp.phone.interfaces.PagerOwner;

public class GotoDialogFragment extends NoframeDialogFragment {

    private final static String TAG = "GotoDialogFragment";

    private View mView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        mView = getDialogView();
        builder.setTitle(R.string.goto_floor_description)
                .setView(mView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                PagerOwner father;

                if (getParentFragment() instanceof PagerOwner) {
                    father = (PagerOwner) getParentFragment();
                } else if (getActivity() instanceof PagerOwner) {
                    father = (PagerOwner) getActivity();
                } else {
                    return;
                }

                father.setCurrentItem(getPageValue() - 1);

            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        return builder.create();

    }

    private View getNumberPicker() {
        final NumberPicker picker = new NumberPicker(getActivity());
        picker.setMinValue(1);
        int count = getArguments().getInt("count", 1);
        picker.setMaxValue(count);
        picker.setValue(count);

        return picker;
    }

    private View getDialogView() {
        return getNumberPicker();
    }

    private int getPageValue() {
        return getNumberPickerValue();
    }


    private int getNumberPickerValue() {
        final NumberPicker picker = (NumberPicker) mView;
        picker.clearFocus();
        return picker.getValue();
    }

}
