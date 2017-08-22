package sp.phone.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.MyApp;
import gov.anzong.androidnga.activity.PostActivity;
import sp.phone.adapter.ActionBarUserListAdapter;
import sp.phone.adapter.SpinnerUserListAdapter;
import sp.phone.bean.User;
import sp.phone.presenter.contract.TopicPostContract;
import sp.phone.utils.FunctionUtil;
import sp.phone.common.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtils;
import sp.phone.common.ThemeManager;

/**
 * Created by Yang Yihang on 2017/6/6.
 */

public class TopicPostContainer extends BaseFragment implements TopicPostContract.View {

    private TopicPostContract.Presenter mPresenter;

    private EditText mTitleEditText;

    private EditText mBodyEditText;

    private CheckBox mAnonyCheckBox;

    private String mAction;

    private Uri mUploadFilePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAction = getArguments().getString("action");
        mPresenter.prepare();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.reply,container,false);
        mTitleEditText = (EditText) rootView.findViewById(R.id.reply_titile_edittext);
        mBodyEditText = (EditText) rootView.findViewById(R.id.reply_body_edittext);
        mAnonyCheckBox = (CheckBox) rootView.findViewById(R.id.anony);
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
        initUserSpinner(view);

        updateThemeUi();
        super.onViewCreated(view, savedInstanceState);
    }

    private void initUserSpinner(View rootView){

        Spinner userList = (Spinner) rootView.findViewById(R.id.user_list);
        if (userList != null) {
            SpinnerUserListAdapter adapter = new SpinnerUserListAdapter(getContext());
            userList.setAdapter(adapter);
            userList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    User u = (User) parent.getItemAtPosition(position);
                    MyApp app = (MyApp) getActivity().getApplication();
                    app.addToUserList(u.getUserId(), u.getCid(),
                            u.getNickName(), u.getReplyString(),
                            u.getReplyTotalNum(), u.getBlackList());
                    PhoneConfiguration.getInstance().setUid(u.getUserId());
                    PhoneConfiguration.getInstance().setCid(u.getCid());
                    PhoneConfiguration.getInstance().setReplyString(
                            u.getReplyString());
                    PhoneConfiguration.getInstance().setReplyTotalNum(
                            u.getReplyTotalNum());
                    PhoneConfiguration.getInstance().blacklist = StringUtils
                            .blackListStringToHashset(u.getBlackList());

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            });
        } else {
            setNavigation();
        }
    }


    @TargetApi(11)
    private void setNavigation() {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        final SpinnerUserListAdapter categoryAdapter = new ActionBarUserListAdapter(
                getContext());
        ActionBar.OnNavigationListener callback = new ActionBar.OnNavigationListener() {

            @Override
            public boolean onNavigationItemSelected(int itemPosition,
                                                    long itemId) {
                User u = (User) categoryAdapter.getItem(itemPosition);
                MyApp app = (MyApp) getActivity().getApplication();
                app.addToUserList(u.getUserId(), u.getCid(), u.getNickName(),
                        u.getReplyString(), u.getReplyTotalNum(),
                        u.getBlackList());
                PhoneConfiguration.getInstance().setUid(u.getUserId());
                PhoneConfiguration.getInstance().setCid(u.getCid());
                PhoneConfiguration.getInstance().setReplyString(
                        u.getReplyString());
                PhoneConfiguration.getInstance().setReplyTotalNum(
                        u.getReplyTotalNum());
                PhoneConfiguration.getInstance().blacklist = StringUtils
                        .blackListStringToHashset(u.getBlackList());
                mPresenter.getTopicPostAction().set__ngaClientChecksum(FunctionUtil.getngaClientChecksum(getContext()));
                return true;
            }

        };
        actionBar.setListNavigationCallbacks(categoryAdapter, callback);
    }

    @Override
    public void setPresenter(TopicPostContract.Presenter presenter) {
        mPresenter = presenter;
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
        getActivity().setResult(result);
    }


    private void updateThemeUi(){
        ThemeManager tm = ThemeManager.getInstance();
        if (tm.getMode() == ThemeManager.MODE_NIGHT) {
            mBodyEditText.setBackgroundResource(tm.getBackgroundColor());
            mTitleEditText.setBackgroundResource(tm.getBackgroundColor());
            int textColor = getResources().getColor(tm.getForegroundColor());
            mBodyEditText.setTextColor(textColor);
            mTitleEditText.setTextColor(textColor);
        }
        getView().setBackgroundColor(getResources().getColor(tm.getBackgroundColor()));
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (PhoneConfiguration.getInstance().HandSide == 1) {// lefthand
            int flag = PhoneConfiguration.getInstance().getUiFlag();
            if (flag >= 4) {// 大于等于4肯定有
                inflater.inflate(R.menu.post_menu_left, menu);
            } else {
                inflater.inflate(R.menu.post_menu, menu);
            }
        } else {
            inflater.inflate(R.menu.post_menu, menu);
        }
        final int flags = 7;
        ReflectionUtil.actionBar_setDisplayOption(getBaseActivity(), flags);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.supertext:
                FunctionUtil.handleSupertext(mBodyEditText, getContext(), getView());
                break;
            case R.id.send:
                mPresenter.post(mTitleEditText.getText().toString(),mBodyEditText.getText().toString(),mAnonyCheckBox.isChecked());
                break;
            case R.id.upload:
                mPresenter.prepareUploadFile();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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
