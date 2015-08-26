package sp.phone.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Field;

import gov.anzong.androidnga.R;
import sp.phone.interfaces.OnPostCommentFinishedListener;
import sp.phone.task.PostCommentTask;

public class PostCommentDialogFragment extends DialogFragment implements
        OnPostCommentFinishedListener {
    EditText input = null;
    CheckBox anony;
    AlertDialog OptionDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.postcomment_dialog, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setView(view);
        anony = (CheckBox) view.findViewById(R.id.anony);
        input = (EditText) view.findViewById(R.id.post_data);
        alert.setTitle(R.string.post_comment);
        alert.setPositiveButton("发送", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int length = input.getText().toString().length();
                if (length > 5 && length < 651) {
                    int tid = getArguments().getInt("tid", 0);
                    int pid = getArguments().getInt("pid", 0);
                    int fid = getArguments().getInt("fid", 0);
                    int anonymode = 0;
                    if (anony.isChecked()) {
                        anonymode = 1;
                    }
                    String prefix = getArguments().getString("prefix");
                    new PostCommentTask(fid, pid, tid, anonymode, prefix,
                            getActivity(), PostCommentDialogFragment.this)
                            .execute(input.getText().toString());
                } else {
                    Toast.makeText(getActivity(), "贴条内容长度必须在6~650字节范围内",
                            Toast.LENGTH_SHORT).show();
                }
                try {
                    Field field = dialog.getClass().getSuperclass()
                            .getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        alert.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        try {
                            Field field = dialog.getClass().getSuperclass()
                                    .getDeclaredField("mShowing");
                            field.setAccessible(true);
                            field.set(dialog, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        OptionDialog = alert.create();
        return OptionDialog;
    }

    @Override
    public void OnPostCommentFinished(String result, boolean success) {
        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
        if (success == true) {
            Log.i("TAG", "SUCCESS");
            try {
                Field field = OptionDialog.getClass().getSuperclass()
                        .getDeclaredField("mShowing");
                field.setAccessible(true);
                field.set(OptionDialog, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            OptionDialog.dismiss();
        }
    }

}
