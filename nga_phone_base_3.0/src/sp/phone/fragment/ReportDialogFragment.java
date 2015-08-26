package sp.phone.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import sp.phone.task.JsonThreadLoadTask;
import sp.phone.task.ReportTask;
import sp.phone.utils.ActivityUtil;

public class ReportDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final TextView tv = new TextView(this.getActivity());
        alert.setTitle(R.string.report_confirm);
        tv.setText(R.string.reportdialog_description);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        alert.setView(tv);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        int tid = getArguments().getInt("tid", 0);
                        int pid = getArguments().getInt("pid", 0);
                        String url = Utils.getNGAHost() + "nuke.php?func=logpost&tid="
                                + tid + "&pid=" + pid
                                + "&log";
                        ReportTask task = new ReportTask(getActivity());
                        if (ActivityUtil.isGreaterThan_2_3_3())
                            RunParallen(task, url);
                        else
                            task.execute(url);
                    }

                    @TargetApi(11)
                    private void RunParallen(ReportTask task, String url) {
                        task.executeOnExecutor(JsonThreadLoadTask.THREAD_POOL_EXECUTOR, url);

                    }

                }
        );

        alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //dialog.dismiss();
            }
        });

        return alert.create();
    }

}
