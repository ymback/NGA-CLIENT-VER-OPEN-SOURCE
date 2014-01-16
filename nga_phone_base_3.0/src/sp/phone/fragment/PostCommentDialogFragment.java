package sp.phone.fragment;

import sp.phone.interfaces.OnPostCommentFinishedListener;
import sp.phone.task.PostCommentTask;
import gov.anzong.androidnga.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.Toast;

public class PostCommentDialogFragment extends DialogFragment
implements OnPostCommentFinishedListener{
	EditText input=null;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());  
        input = new EditText(getActivity());
        alert.setView(input);  
		alert.setTitle(R.string.post_comment);
		alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int whichButton) { 
            	
            	int tid = getArguments().getInt("tid", 0);
            	int pid = getArguments().getInt("pid", 0);
            	new PostCommentTask(pid,tid, getActivity()).execute(input.getText().toString());
            }
		});
		
		alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int whichButton) {  
                dialog.dismiss();
            }
		});
		return alert.create();
	}
	@Override
	public void OnPostCommentFinished(String result) {
		Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
		
	}
	

}
