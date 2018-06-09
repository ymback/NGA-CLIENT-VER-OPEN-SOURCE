package sp.phone.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.PostActivity;
import sp.phone.adapter.EmoticonParentAdapter;
import sp.phone.mvp.contract.TopicPostContract;
import sp.phone.mvp.presenter.TopicPostPresenter;
import sp.phone.rxjava.RxEvent;
import sp.phone.util.FunctionUtils;
import sp.phone.util.PermissionUtils;
import sp.phone.util.StringUtils;
import sp.phone.view.KeyboardLayout;

/**
 * Created by Justwen on 2017/6/6.
 */

public class TopicPostFragment extends BaseMvpFragment<TopicPostPresenter> implements TopicPostContract.View, KeyboardLayout.KeyboardLayoutListener {

    private EditText mTitleEditText;

    private EditText mBodyEditText;

    private CheckBox mAnonyCheckBox;

    private String mAction;

    private Uri mUploadFilePath;

    private ProgressDialog mProgressDialog;

    private int mKeyboardHeight;

    private ViewGroup mEmoticonPanel;

    private View.OnTouchListener mEditorTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (mEmoticonPanel != null && event.getAction() == MotionEvent.ACTION_UP) {
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() { // 输入法弹出之后，重新调整
                        if (getActivity() != null) {
                            mEmoticonPanel.setVisibility(View.GONE);
                            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                        }
                    }
                }, 750); // 延迟一段时间，等待输入法完全弹出
            }

            return false;
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAction = getArguments().getString("action");
        mPresenter.setTopicPostAction(getArguments().getParcelable("param"));
        mPresenter.getPostInfo();
        registerRxBus();
        mKeyboardHeight = getResources().getDimensionPixelSize(R.dimen.bottom_emoticon_min_height);
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();

        String prefix = bundle.getString("prefix");
        if (prefix != null) {
            mBodyEditText.setText(prefix);
            mBodyEditText.setSelection(prefix.length());
        }

        String title = bundle.getString("title");
        if (title != null) {
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

        KeyboardLayout keyboardLayout = view.findViewById(R.id.keyboard_layout);
        keyboardLayout.setListener(this);

        mBodyEditText.setOnTouchListener(mEditorTouchListener);
        mTitleEditText.setOnTouchListener(mEditorTouchListener);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPresenter.showFilePicker();
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
        if (!StringUtils.isEmpty(path)) {
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
    public void showUploadFileProgressBar() {

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("上传文件中......");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    @Override
    public void hideUploadFileProgressBar() {
        mProgressDialog.dismiss();
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
                mPresenter.post(mTitleEditText.getText().toString(), mBodyEditText.getText().toString(), mAnonyCheckBox.isChecked());
                break;
            case R.id.upload:
                mPresenter.showFilePicker();
                break;
            case R.id.emotion:
                toggleEmoticonView();
//                BaseDialogFragment newFragment = new EmotionCategorySelectFragment();
//                newFragment.show(getActivity().getSupportFragmentManager());
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void toggleEmoticonView() {
        if (mEmoticonPanel == null) {
            ViewStub viewStub = getView().findViewById(R.id.bottom_emoticon_stub);
            viewStub.inflate();

            mEmoticonPanel = getView().findViewById(R.id.bottom_emoticon_panel);
            ViewPager emoticonViewPager = getView().findViewById(R.id.bottom_emoticon);
            int height = mKeyboardHeight - getResources().getDimensionPixelSize(R.dimen.bottom_emoticon_tab_height);
            emoticonViewPager.getLayoutParams().height = height;
            emoticonViewPager.setAdapter(new EmoticonParentAdapter(getContext(), height));

            TabLayout tabLayout = getView().findViewById(R.id.bottom_emoticon_tab);
            tabLayout.setupWithViewPager(emoticonViewPager);
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        }

        if (mEmoticonPanel.isShown()) {
            mEmoticonPanel.setVisibility(View.GONE);
        } else {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            mEmoticonPanel.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mBodyEditText.getWindowToken(), 0);
        }
    }

    @Override
    protected void accept(RxEvent rxEvent) {
        switch (rxEvent.what) {
            case RxEvent.EVENT_INSERT_EMOTICON:
                mPresenter.setEmoticon((String) rxEvent.obj);
                break;
            default:
                break;
        }
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
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onKeyboardStateChanged(boolean isActive, int keyboardHeight) {

        if (mKeyboardHeight < keyboardHeight) {
            mKeyboardHeight = keyboardHeight;
        }
    }
}
