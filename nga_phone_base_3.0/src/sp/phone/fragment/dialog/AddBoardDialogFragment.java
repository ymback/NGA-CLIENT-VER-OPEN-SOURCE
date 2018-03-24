package sp.phone.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import gov.anzong.androidnga.R;

/**
 * Created by Justwen on 2018/2/17.
 */

public class AddBoardDialogFragment extends BaseDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_board, null);
        final EditText addFidNameView = view.findViewById(R.id.addfid_name);
        final EditText addFidIdView = view.findViewById(R.id.addfid_id);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view)
                .setTitle(R.string.addfid_title_hint)
                .setPositiveButton("添加", (dialog, which) -> {
                    String name = addFidNameView.getText().toString();
                    String fid = addFidIdView.getText().toString();
                }).setNegativeButton("取消", null);
        return builder.create();
    }
}
