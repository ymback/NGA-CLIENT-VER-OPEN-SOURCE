package sp.phone.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Objects;

import gov.anzong.androidnga.R;
import sp.phone.common.PhoneConfiguration;
import sp.phone.utils.StringUtils;

public class SearchDialogFragment extends DialogFragment {

    private RadioGroup mSearchRadio;

    private RadioButton mTopicButton;

    private RadioButton mAllTopicButton;

    private RadioButton mUserTopicButton;

    private RadioButton mUserReplyButton;

    private CheckBox mContentCheckBox;

    private EditText mEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void getViews(View view) {
        mSearchRadio = (RadioGroup) view.findViewById(R.id.radioGroup);
        mTopicButton = (RadioButton) mSearchRadio.findViewById(R.id.search_topic);
        mAllTopicButton = (RadioButton) mSearchRadio.findViewById(R.id.search_alltopic);
        mUserTopicButton = (RadioButton) mSearchRadio.findViewById(R.id.search_user_topic);
        mUserReplyButton = (RadioButton) mSearchRadio.findViewById(R.id.search_user_apply);
        mEditText = (EditText) view.findViewById(R.id.search_data);
        mContentCheckBox = (CheckBox) view.findViewById(R.id.withcontent);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getActivity().getLayoutInflater().inflate(R.layout.search_dialog, null);
        getViews(view);
        mSearchRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.search_topic://搜索本版面主题
                    case R.id.search_alltopic://搜索全部主题
                        mEditText.setHint(R.string.search_dialog_hint);
                        mContentCheckBox.setVisibility(View.VISIBLE);
                        break;
                    case R.id.search_user_topic://搜索用户主题
                        mEditText.setHint(R.string.search_dialog_hint_topic_reply_byself);
                        mContentCheckBox.setVisibility(View.GONE);
                        break;
                    case R.id.search_user_apply://搜索用户回复
                        mEditText.setHint(R.string.search_dialog_hint_reply_byself);
                        mContentCheckBox.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }

        });
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    getDialog().dismiss();
                    handleSearch();
                    return true;
                }
                return false;
            }
        });
        builder.setView(view)
                .setTitle(R.string.search_hint)
                .setPositiveButton("搜索", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handleSearch();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    private void handleSearch() {
        final String inputString = mEditText.getText().toString();
        if (mSearchRadio.getCheckedRadioButtonId() == mUserTopicButton.getId()) {//用户主题
            searchUserTopic(inputString);
        } else if (mSearchRadio.getCheckedRadioButtonId() == mUserReplyButton.getId()) {//用户回复
            searchUserReply(inputString);
        } else if (mSearchRadio.getCheckedRadioButtonId() == mTopicButton.getId()) {
            searchTopic(inputString);
        } else {
            searchAllTopic(inputString);
        }
    }

    private void searchUserTopic(String inputString) {
        Intent intent = new Intent(getContext(), PhoneConfiguration.getInstance().topicActivityClass);
        if (!StringUtils.isEmpty(inputString)) {
            intent.putExtra("fid", getArguments().getInt("id", -7));
            intent.putExtra("author", inputString);
            intent.putExtra("authorid", getArguments().getInt("authorid", 0));
            startActivity(intent);
        } else {
            String userName = PhoneConfiguration.getInstance().userName;
            if (!Objects.equals(userName, "")) {
                intent.putExtra("fid", getArguments().getInt("id", -7));
                intent.putExtra("author", userName);
                intent.putExtra("authorid", getArguments().getInt("authorid", 0));
                startActivity(intent);
            }
        }
    }

    private void searchUserReply(String inputString) {
        Intent intent = new Intent(getContext(), PhoneConfiguration.getInstance().topicActivityClass);
        if (!StringUtils.isEmpty(inputString)) {
            intent.putExtra("fid", getArguments().getInt("id", -7));
            intent.putExtra("author", inputString + "&searchpost=1");
            intent.putExtra("authorid", getArguments().getInt("authorid", 0));
            startActivity(intent);
        } else {
            String userName = PhoneConfiguration.getInstance().userName;
            if (!Objects.equals(userName, "")) {
                intent.putExtra("fid", getArguments().getInt("id", -7));
                intent.putExtra("author", userName + "&searchpost=1");
                intent.putExtra("authorid", getArguments().getInt("authorid", 0));
                startActivity(intent);
            }
        }
    }

    private void searchTopic(String inputString) {
        Intent intent = new Intent(getContext(), PhoneConfiguration.getInstance().topicActivityClass);
        if (!StringUtils.isEmpty(inputString)) {
            if (mContentCheckBox.isChecked()) {
                intent.putExtra("content", 1);
            }
            intent.putExtra("fid", getArguments().getInt("id", -7));
            intent.putExtra("key", inputString);
            intent.putExtra("table", getString(R.string.largesttablenum));
            intent.putExtra("authorid", getArguments().getInt("authorid", 0));
            startActivity(intent);
        }
    }

    private void searchAllTopic(String inputString) {
        Intent intent = new Intent(getContext(), PhoneConfiguration.getInstance().topicActivityClass);
        if (!StringUtils.isEmpty(inputString)) {
            if (mContentCheckBox.isChecked()) {
                intent.putExtra("content", 1);
            }
            intent.putExtra("key", inputString);
            intent.putExtra("fidgroup", "user");
            intent.putExtra("table", getString(R.string.largesttablenum));
            startActivity(intent);
        }
    }

}
