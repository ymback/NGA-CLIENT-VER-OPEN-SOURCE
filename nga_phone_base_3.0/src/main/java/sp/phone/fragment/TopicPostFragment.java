package sp.phone.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.PostActivity;
import sp.phone.fragment.dialog.BaseDialogFragment;
import sp.phone.fragment.dialog.EmotionCategorySelectFragment;
import sp.phone.mvp.contract.TopicPostContract;
import sp.phone.mvp.presenter.TopicPostPresenter;
import sp.phone.util.FunctionUtils;
import sp.phone.util.PermissionUtils;
import sp.phone.util.StringUtils;

/**
 * Created by Justwen on 2017/6/6.
 */

public class TopicPostFragment extends  BaseMvpFragment<TopicPostPresenter> implements TopicPostContract.View {

    private EditText mTitleEditText;

    private EditText mBodyEditText;

    private CheckBox mAnonyCheckBox;

    private String mAction;

    private Uri mUploadFilePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAction = getArguments().getString("action");
        mPresenter.setTopicPostAction(getArguments().getParcelable("param"));
        mPresenter.getPostInfo();
    }

    @Override
    protected TopicPostPresenter onCreatePresenter() {
        return new TopicPostPresenter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_topic_post,container,false);
        mTitleEditText = rootView.findViewById(R.id.reply_titile_edittext);
        mBodyEditText = rootView.findViewById(R.id.reply_body_edittext);
        mAnonyCheckBox = rootView.findViewById(R.id.anony);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        String prefix = bundle.getString("prefix");
        if (prefix != null){
            mBodyEditText.setText(prefix);
            mBodyEditText.setSelection(prefix.length());
        }

        String title = bundle.getString("title");
        if (title != null){
            mTitleEditText.setText(title);
        }
        mTitleEditText.setSelected(true);
        if (!mAction.equals("new")) {
            mTitleEditText.setHint(R.string.titlecannull);
        }

        mAnonyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    showToast("匿名发帖/回复每次将扣除一百铜币,慎重");
                }
            }

        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPresenter.prepareUploadFile();
            }
        }
    }

    @Override
    public void insertBodyText(CharSequence text) {
        int index = mBodyEditText.getSelectionStart();
        if (mBodyEditText.getText().toString().replaceAll("\\n", "").trim()
                .equals("") || index <= 0 || index >= mBodyEditText.length()) {
            mBodyEditText.append(text);
        } else {
            mBodyEditText.getText().insert(index, text);
        }
    }

    @Override
    public void finish() {
        if (getActivity() != null)
            getActivity().finish();
    }

    @Override
    public void insertFile(String path, CharSequence file) {
        int index = mBodyEditText.getSelectionStart();
        if (!StringUtils.isEmpty(path)){
            if (mBodyEditText.getText().toString().replaceAll("\\n", "").trim()
                    .equals("")) {// NO INPUT DATA
                mBodyEditText.append(file);
                mBodyEditText.append("\n");
            } else {
                if (index <= 0 || index >= mBodyEditText.length()) {// pos @ begin /
                    // end
                    if (mBodyEditText.getText().toString().endsWith("\n")) {
                        mBodyEditText.append(file);
                        mBodyEditText.append("\n");
                    } else {
                        mBodyEditText.append("\n");
                        mBodyEditText.append(file);
                        mBodyEditText.append("\n");
                    }
                } else {
                    mBodyEditText.getText().insert(index, file);
                }
            }
        } else {
            if (mBodyEditText.getText().toString().replaceAll("\\n", "").trim()
                    .equals("")) {// NO INPUT DATA
                mBodyEditText.append("[img]./" + file + "[/img]\n");
            } else {
                if (index <= 0 || index >= mBodyEditText.length()) {// pos @ begin /
                    // end
                    if (mBodyEditText.getText().toString().endsWith("\n")) {
                        mBodyEditText.append("[img]./" + file + "[/img]\n");
                    } else {
                        mBodyEditText.append("\n[img]./" + file + "[/img]\n");
                    }
                } else {
                    mBodyEditText.getText().insert(index,
                            "[img]./" + file + "[/img]");
                }
            }
        }
    }

    @Override
    public void showFilePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PostActivity.REQUEST_CODE_SELECT_PIC);
    }

    @Override
    public void setResult(int result) {
        if (getActivity() != null) {
            getActivity().setResult(result);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.topic_post_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.supertext:
                FunctionUtils.handleSupertext(mBodyEditText, getContext(), getView());
                break;
            case R.id.send:
                mPresenter.post(mTitleEditText.getText().toString(),mBodyEditText.getText().toString(),mAnonyCheckBox.isChecked());
                break;
            case R.id.upload:
                mPresenter.prepareUploadFile();
                break;
            case R.id.emotion:
                BaseDialogFragment newFragment = new EmotionCategorySelectFragment();
                newFragment.show(getActivity().getSupportFragmentManager());
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    public void setEmoticon(String emotion) {
        mPresenter.setEmoticon(emotion);
    }

    @Override
    public void onResume() {
        if (mAction.equals("new")) {
            mTitleEditText.requestFocus();
        } else {
            mBodyEditText.requestFocus();
        }
        if (mUploadFilePath != null) {
            mPresenter.startUploadTask(mUploadFilePath);
            mUploadFilePath = null;
        }
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED || data == null)
            return;
        switch (requestCode) {
            case PostActivity.REQUEST_CODE_SELECT_PIC:
                mUploadFilePath = data.getData();
                break;
            default: break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
