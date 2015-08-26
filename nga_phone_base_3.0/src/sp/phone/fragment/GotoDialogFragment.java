package sp.phone.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import gov.anzong.androidnga.R;
import sp.phone.interfaces.PagerOwnner;
import sp.phone.utils.ActivityUtil;

public class GotoDialogFragment extends NoframeDialogFragment {
    final static private String TAG = "GotoDialogFragment";
    View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(R.string.goto_floor_description);
        v = this.getDialogView();
        alert.setView(v);

        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                PagerOwnner father = null;
                try {
                    father = (PagerOwnner) getActivity();
                } catch (ClassCastException e) {
                    Log.e(TAG, "father activity does not implements interface "
                            + PagerOwnner.class.getName());
                    return;
                }
                if (father != null)
                    father.setCurrentItem(getPageValue() - 1);

            }
        });

        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        return alert.create();

    }

    @TargetApi(11)
    private View getNumberPicker() {
        final NumberPicker picker = new NumberPicker(getActivity());
        picker.setMinValue(1);
        int count = getArguments().getInt("count", 1);
        picker.setMaxValue(count);
        picker.setValue(count);

        return picker;
    }

    private View getEditText() {
        final EditText input = new EditText(getActivity());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        return input;
    }

    private View getDialogView() {
        if (ActivityUtil.isGreaterThan_2_3_3())
            return getNumberPicker();
        return getEditText();
    }

    private int getPageValue() {
        if (ActivityUtil.isGreaterThan_2_3_3())
            return getNumberPickerValue();
        return getEditTextValue();
    }

    private int getEditTextValue() {
        final EditText input = (EditText) v;
        String value = input.getText().toString().trim();
        int count = getArguments().getInt("count", 1);
        int floor = count;
        try {

            floor = Integer.valueOf(value);
            if (floor > count || floor < 1)
                floor = count;

        } catch (Exception e) {

        }
        return floor;
    }

    @TargetApi(11)
    private int getNumberPickerValue() {
        final NumberPicker picker = (NumberPicker) v;
        picker.clearFocus();
        return picker.getValue();
    }

}
