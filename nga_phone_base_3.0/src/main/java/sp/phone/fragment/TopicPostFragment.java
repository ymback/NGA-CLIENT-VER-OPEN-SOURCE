package sp.phone.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.EditText;

import gov.anzong.androidnga.R;
import sp.phone.forumoperation.ParamKey;
import sp.phone.mvp.contract.TopicPostContract;
import sp.phone.mvp.presenter.TopicPostPresenter;
import sp.phone.rxjava.RxEvent;
import sp.phone.util.PermissionUtils;
import sp.phone.util.StringUtils;
import sp.phone.view.toolbar.ToolbarContainer;

public class TopicPostFragment extends BaseMvpFragment<TopicPostPresenter> implements TopicPostContract.View {

    private static final int REQUEST_CODE_SELECT_PIC = 1;

    private CheckBox mAnonyCheckBox;

    private EditText mBodyEditText;

    private ProgressDialog mProgressDialog;

    private EditText mTitleEditText;

    private ToolbarContainer mToolbarContainer;

    private Uri mUploadFilePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.setPostParam(getArguments().getParcelable("param"));
        registerRxBus();
    }

    @Override
    protected TopicPostPresenter onCreatePresenter() {
        return new TopicPostPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_topic_post, container, false);
        mTitleEditText = rootView.findViewById(R.id.reply_titile_edittext);
        mBodyEditText = rootView.findViewById(R.id.reply_body_edittext);
        mAnonyCheckBox = rootView.findViewById(R.id.anony);
        return rootView;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mAnonyCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showToast("匿名发帖/回复每次将扣除一百铜币,慎重");
            }
        });
        mToolbarContainer = view.findViewById(R.id.control_panel);
        mToolbarContainer.setPresenter(mPresenter);
        mBodyEditText.setOnFocusChangeListener(mToolbarContainer);
        mTitleEditText.setOnFocusChangeListener(mToolbarContainer);
        mBodyEditText.setOnTouchListener(mToolbarContainer);
        mTitleEditText.setOnTouchListener(mToolbarContainer);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.REQUEST_CODE_WRITE_EXTERNAL_STORAGE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mPresenter.showFilePicker();
        }
    }

    @Override
    public boolean onBackPressed() {
        return mToolbarContainer.onBackPressed();
    }

    @Override
    public void insertBodyText(CharSequence text) {
        insertBodyText(text, 0);
    }

    @Override
    public void insertBodyText(CharSequence text, int position) {
        mBodyEditText.requestFocus();
        int index = mBodyEditText.getSelectionStart();
        if (mBodyEditText.getText().toString().replaceAll("\\n", "").trim().isEmpty()
                || index <= 0
                || index >= mBodyEditText.length()) {
            mBodyEditText.append(text);
        } else {
            mBodyEditText.getText().insert(index, text);
        }
        if (position > 0) {
            mBodyEditText.setSelection(index + position);
        }
    }

    @Override
    public void insertTitleText(CharSequence text) {
        mTitleEditText.getText().insert(0, text);
    }

    @Override
    public void insertFile(String path, CharSequence file) {
        int index = mBodyEditText.getSelectionStart();
        String content = mBodyEditText.getText().toString();
        if (StringUtils.isEmpty(path)) {
            if (content.replaceAll("\\n", "").trim().isEmpty()) {
                mBodyEditText.append("[img]./" + file + "[/img]\n");
            } else if (index > 0 && index < mBodyEditText.length()) {
                mBodyEditText.getText().insert(index, "[img]./" + file + "[/img]");
            } else if (mBodyEditText.getText().toString().endsWith("\n")) {
                mBodyEditText.append("[img]./" + file + "[/img]\n");
            } else {
                mBodyEditText.append("\n[img]./" + file + "[/img]\n");
            }
        } else if (content.replaceAll("\\n", "").trim().isEmpty()) {
            mBodyEditText.append(file);
            mBodyEditText.append("\n");
        } else if (index > 0 && index < mBodyEditText.length()) {
            mBodyEditText.getText().insert(index, file);
        } else if (content.endsWith("\n")) {
            mBodyEditText.append(file);
            mBodyEditText.append("\n");
        } else {
            mBodyEditText.append("\n");
            mBodyEditText.append(file);
            mBodyEditText.append("\n");
        }
    }

    @Override
    public void showFilePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(intent, REQUEST_CODE_SELECT_PIC);
    }

    @Override
    public void showUploadFileProgressBar() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("上传文件中......");
            mProgressDialog.setProgressStyle(0);
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    @Override
    public void hideUploadFileProgressBar() {
        mProgressDialog.dismiss();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.topic_post_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.send) {
            mPresenter.post(mTitleEditText.getText().toString(), mBodyEditText.getText().toString(), mAnonyCheckBox.isChecked());
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void accept(RxEvent rxEvent) {
        if (rxEvent.what == RxEvent.EVENT_INSERT_EMOTICON) {
            mPresenter.setEmoticon((String) rxEvent.obj);
        }
    }

    @Override
    public void onResume() {
        if (mUploadFilePath != null) {
            mPresenter.startUploadTask(mUploadFilePath);
            mUploadFilePath = null;
        }
        if (!"new".equals(getArguments().getString(ParamKey.KEY_ACTION))) {
            mTitleEditText.setHint(R.string.titlecannull);
            mBodyEditText.requestFocus();
        }
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SELECT_PIC
                && resultCode == Activity.RESULT_OK
                && data != null) {
            mUploadFilePath = data.getData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}