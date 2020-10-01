package sp.phone.ui.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.util.ToastUtils;
import gov.anzong.androidnga.http.OnHttpCallBack;
import sp.phone.task.ReportTask;

/**
 * @author Justwen
 */
public class ReportDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_report, null, false);
        builder.setView(contentView);
        TextInputEditText editText = contentView.findViewById(android.R.id.edit);
        builder.setPositiveButton(android.R.string.ok, (arg0, arg1) -> report(editText.getText())
        ).setNegativeButton(android.R.string.cancel, null);

        return builder.create();
    }

    private void report(CharSequence value) {
        Bundle arguments = getArguments();
        if (arguments == null) {
            return;
        }
        int tid = arguments.getInt("tid", 0);
        int pid = arguments.getInt("pid", 0);
        Map<String, String> query = new HashMap<>();
        Map<String, String> field = new HashMap<>();

        query.put("__lib", "log_post");
        query.put("__act", "report");
        query.put("__output", "8");

        field.put("__output", "8");
        field.put("__lib", "log_post");
        field.put("__act", "report");
        field.put("pid", String.valueOf(pid));
        field.put("tid", String.valueOf(tid));
        field.put("info", String.valueOf(value));
        ReportTask task = new ReportTask();
        task.pos(query, field, new OnHttpCallBack<String>() {
            @Override
            public void onError(String text) {
                ToastUtils.error(text);
            }

            @Override
            public void onSuccess(String data) {
                ToastUtils.success(data);
            }
        });
    }

}
