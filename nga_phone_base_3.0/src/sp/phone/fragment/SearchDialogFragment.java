package sp.phone.fragment;

import gov.anzong.androidnga.R;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class SearchDialogFragment extends DialogFragment {
	View view = null;
	Spinner databasechooseSpinner;
	RadioGroup searchradio ;
	RadioButton search_topic_button;
	RadioButton search_alltopic_button;
	RadioButton search_user_topic_button;
	RadioButton search_user_apply_button;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//this.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());          
		LayoutInflater inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.search_dialog, null);
        alert.setView(view);  
		alert.setTitle(R.string.search_hint);
		alert.setPositiveButton("����", new PositiveOnClickListener());
		search_topic_button=(RadioButton) view.findViewById(R.id.search_topic);
		search_topic_button.setChecked(true);
		search_alltopic_button=(RadioButton) view.findViewById(R.id.search_alltopic);
		search_user_topic_button=(RadioButton) view.findViewById(R.id.search_user_topic);
		search_user_apply_button=(RadioButton) view.findViewById(R.id.search_user_apply);
		searchradio = (RadioGroup) view.findViewById(R.id.radioGroup);
		searchradio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			EditText input = (EditText) view.findViewById(R.id.search_data);
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
					case R.id.search_topic://��������
						input.setHint(R.string.search_dialog_hint);
				break;
					case R.id.search_alltopic://��������
						input.setHint(R.string.search_dialog_hint);
				break ;
					case R.id.search_user_topic://��������
						input.setHint(R.string.search_dialog_hint_topic_reply_byself);
				break ;
					case R.id.search_user_apply://�����ظ�
						input.setHint(R.string.search_dialog_hint_reply_byself);
				}
			}
			
		});
		alert.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int whichButton) {  
                dialog.dismiss();
            }
		});
		return alert.show();
	}
	
	class PositiveOnClickListener implements DialogInterface.OnClickListener {

		EditText input = (EditText) view.findViewById(R.id.search_data);
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			final String inputString = input.getText().toString();
    		Intent intent_search = new Intent(getActivity(), PhoneConfiguration.getInstance().topicActivityClass);
    		if (PhoneConfiguration.getInstance().fullscreen) {
    			intent_search.putExtra("isFullScreen", true);
    		}
			if(searchradio.getCheckedRadioButtonId() == search_user_topic_button.getId()){//�û�����
				if(!StringUtil.isEmpty(inputString))
		    	{
		    		intent_search.putExtra("fid",getArguments().getInt("id",-7));
		    		intent_search.putExtra("author", inputString);
		    		intent_search.putExtra("authorid", getArguments().getInt("authorid",0));
					if(PhoneConfiguration.getInstance().showAnimation)
						getActivity().overridePendingTransition(R.anim.zoom_enter,
								R.anim.zoom_exit);
		    		startActivity(intent_search);
		    	}else{
		    		String userName = PhoneConfiguration.getInstance().userName;
		    		if (userName != ""){
			    		intent_search.putExtra("fid",getArguments().getInt("id",-7));
			    		intent_search.putExtra("author", userName);
			    		intent_search.putExtra("authorid", getArguments().getInt("authorid",0));
						if(PhoneConfiguration.getInstance().showAnimation)
							getActivity().overridePendingTransition(R.anim.zoom_enter,
									R.anim.zoom_exit);
			    		startActivity(intent_search);
		    		}
		    	}
	        	
			} else if(searchradio.getCheckedRadioButtonId() == search_user_apply_button.getId()){//�û��ظ�
				if(!StringUtil.isEmpty(inputString))
		    	{
		    		intent_search.putExtra("fid",getArguments().getInt("id",-7));
		    		intent_search.putExtra("author", inputString+"&searchpost=1");
		    		intent_search.putExtra("authorid", getArguments().getInt("authorid",0));
					if(PhoneConfiguration.getInstance().showAnimation)
						getActivity().overridePendingTransition(R.anim.zoom_enter,
								R.anim.zoom_exit);
		    		startActivity(intent_search);
		    	}else{
		    		String userName = PhoneConfiguration.getInstance().userName;
		    		if (userName != ""){
			    		intent_search.putExtra("fid",getArguments().getInt("id",-7));
			    		intent_search.putExtra("author", userName+"&searchpost=1");
			    		intent_search.putExtra("authorid", getArguments().getInt("authorid",0));
						if(PhoneConfiguration.getInstance().showAnimation)
							getActivity().overridePendingTransition(R.anim.zoom_enter,
									R.anim.zoom_exit);
			    		startActivity(intent_search);
		    		}
		    	}
		    }else if(searchradio.getCheckedRadioButtonId() == search_topic_button.getId()) {
		    	if(!StringUtil.isEmpty(inputString))
		    	{
		    		intent_search.putExtra("fid",getArguments().getInt("id",-7));
		    		intent_search.putExtra("key", inputString);
		    		intent_search.putExtra("table", view.getContext().getString(
							R.string.largesttablenum));
		    		intent_search.putExtra("authorid", getArguments().getInt("authorid",0));
					if(PhoneConfiguration.getInstance().showAnimation)
						getActivity().overridePendingTransition(R.anim.zoom_enter,
								R.anim.zoom_exit);
		    		startActivity(intent_search);
		    	}
		    }else{
		    	if(!StringUtil.isEmpty(inputString))
		    	{
		    		intent_search.putExtra("key", inputString);
		    		intent_search.putExtra("fidgroup", "user");
		    		intent_search.putExtra("table", view.getContext().getString(
							R.string.largesttablenum));
					if(PhoneConfiguration.getInstance().showAnimation)
						getActivity().overridePendingTransition(R.anim.zoom_enter,
								R.anim.zoom_exit);
		    		startActivity(intent_search);
		    	}
		    }
		}
		
	}

}
