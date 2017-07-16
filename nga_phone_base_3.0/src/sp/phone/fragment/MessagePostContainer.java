package sp.phone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import gov.anzong.androidnga.R;
import sp.phone.presenter.contract.MessagePostContract;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.FunctionUtil;
import sp.phone.common.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.common.ThemeManager;

/**
 * Created by Yang Yihang on 2017/5/28.
 */

public class MessagePostContainer extends BaseFragment implements MessagePostContract.View{


    private MessagePostContract.Presenter mPresenter;

    private EditText mTitleEditText;

    private EditText mToEditText;

    private EditText mBodyEditText;

    private String mAction;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAction = getArguments().getString("action");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.messagereply,container,false);
        mTitleEditText = (EditText) rootView.findViewById(R.id.reply_titile_edittext);
        mToEditText = (EditText) rootView.findViewById(R.id.reply_titile_edittext_to);
        mBodyEditText = (EditText) rootView.findViewById(R.id.reply_body_edittext);

        Bundle bundle = getArguments();

        String prefix = bundle.getString("prefix");
        if (prefix != null){
            mBodyEditText.setText(prefix);
            mBodyEditText.setSelection(prefix.length());
        }

        String to = bundle.getString("to");
        if (to != null){
            mToEditText.setText(to);
        }

        String title = bundle.getString("title");
        if (title != null){
            mTitleEditText.setText(title);
        }
        mTitleEditText.setSelected(true);

        updateThemeUi(rootView);
        return rootView;
    }

    private void updateThemeUi(View rootView){
        ThemeManager tm = ThemeManager.getInstance();
        if (tm.getMode() == ThemeManager.MODE_NIGHT) {
            mBodyEditText.setBackgroundResource(tm.getBackgroundColor());
            mToEditText.setBackgroundResource(tm.getBackgroundColor());
            mTitleEditText.setBackgroundResource(tm.getBackgroundColor());
            int textColor = getResources().getColor(tm.getForegroundColor());
            mBodyEditText.setTextColor(textColor);
            mTitleEditText.setTextColor(textColor);
            mToEditText.setTextColor(textColor);
        }
        rootView.setBackgroundColor(getResources().getColor(tm.getBackgroundColor()));
    }


    @Override
    public void setPresenter(MessagePostContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (PhoneConfiguration.getInstance().HandSide == 1) {// lefthand
            int flag = PhoneConfiguration.getInstance().getUiFlag();
            if (flag >= 4) {// 大于等于4肯定有
                inflater.inflate(R.menu.messagepost_menu_left, menu);
            } else {
                inflater.inflate(R.menu.messagepost_menu, menu);
            }
        } else {
            inflater.inflate(R.menu.messagepost_menu, menu);
        }
        final int flags = ThemeManager.ACTION_BAR_FLAG;
        /*
         * ActionBar.DISPLAY_SHOW_HOME;//2 flags |=
		 * ActionBar.DISPLAY_USE_LOGO;//1 flags |=
		 * ActionBar.DISPLAY_HOME_AS_UP;//4
		 */
        ReflectionUtil.actionBar_setDisplayOption(getBaseActivity(), flags);
    }

    @Override
    public void onResume() {
        if (mAction.equals("new")) {
            if (StringUtil.isEmpty(mToEditText.getText().toString())) {
                mToEditText.requestFocus();
            } else {
                mTitleEditText.requestFocus();
            }
        } else {
            mBodyEditText.requestFocus();
        }
        if (PhoneConfiguration.getInstance().fullscreen) {
            ActivityUtil.getInstance().setFullScreen(getView());
        }
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.supertext:
                FunctionUtil.handleSupertext(mBodyEditText, getContext(), getView());
                break;
            case R.id.send:
                String title = mTitleEditText.getText().toString();
                String to = mToEditText.getText().toString();
                String body = mBodyEditText.getText().toString();
                if (StringUtil.isEmpty(to)) {
                    showToast("请输入收件人");
                } else if (StringUtil.isEmpty(title)) {
                    showToast("请输入标题");
                } else if (StringUtil.isEmpty(body)) {
                    showToast("请输入内容");
                } else {
                    mPresenter.commit(title,to,body);
                }
                break;
        }
        return true;
    }

    @Override
    public void finish(int resultCode) {
        getActivity().setResult(resultCode);
        getActivity().finish();
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
}
