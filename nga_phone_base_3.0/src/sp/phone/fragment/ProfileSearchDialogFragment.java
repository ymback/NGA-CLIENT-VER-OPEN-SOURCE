package sp.phone.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import gov.anzong.androidnga.R;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;

public class ProfileSearchDialogFragment extends DialogFragment {
    View view = null;
    RadioGroup searchradio;
    RadioButton profilesearch_name;
    RadioButton profilesearch_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //this.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.profilesearch_dialog, null);
        alert.setView(view);
        alert.setTitle(R.string.profile_search_hint);
        alert.setPositiveButton("查看", new PositiveOnClickListener());
        profilesearch_name = (RadioButton) view.findViewById(R.id.profilesearch_name);
        profilesearch_id = (RadioButton) view.findViewById(R.id.profilesearch_id);
        searchradio = (RadioGroup) view.findViewById(R.id.radioGroup);
        alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
            String inputStringtp = input.getText().toString().trim();
            final String inputString = inputStringtp.replaceAll("\\n", "");
            Intent intent_search = new Intent(getActivity(), PhoneConfiguration.getInstance().topicActivityClass);
            if (searchradio.getCheckedRadioButtonId() == profilesearch_name.getId()) {//用户名
                if (!StringUtil.isEmpty(inputString)) {
                    intent_search.putExtra("mode", "username");
                    intent_search.putExtra("username", inputString);
                    intent_search.setClass(getActivity(), PhoneConfiguration.getInstance().profileActivityClass);
                    if (PhoneConfiguration.getInstance().showAnimation)
                        getActivity().overridePendingTransition(R.anim.zoom_enter,
                                R.anim.zoom_exit);
                    startActivity(intent_search);
                } else {
                    String userName = PhoneConfiguration.getInstance().userName;
                    if (userName != "") {
                        intent_search.putExtra("mode", "username");
                        intent_search.putExtra("username", userName);
                        intent_search.setClass(getActivity(), PhoneConfiguration.getInstance().profileActivityClass);
                        if (PhoneConfiguration.getInstance().showAnimation)
                            getActivity().overridePendingTransition(R.anim.zoom_enter,
                                    R.anim.zoom_exit);
                        startActivity(intent_search);
                    }
                }

            } else {
                if (!StringUtil.isEmpty(inputString)) {
                    intent_search.putExtra("mode", "uid");
                    intent_search.putExtra("uid", inputString);
                    intent_search.setClass(getActivity(), PhoneConfiguration.getInstance().profileActivityClass);
                    if (PhoneConfiguration.getInstance().showAnimation)
                        getActivity().overridePendingTransition(R.anim.zoom_enter,
                                R.anim.zoom_exit);
                    startActivity(intent_search);
                } else {
                    String userName = PhoneConfiguration.getInstance().userName;
                    if (userName != "") {
                        intent_search.putExtra("mode", "username");
                        intent_search.putExtra("username", userName);
                        intent_search.setClass(getActivity(), PhoneConfiguration.getInstance().profileActivityClass);
                        if (PhoneConfiguration.getInstance().showAnimation)
                            getActivity().overridePendingTransition(R.anim.zoom_enter,
                                    R.anim.zoom_exit);
                        startActivity(intent_search);
                    }
                }
            }
        }

    }

}
