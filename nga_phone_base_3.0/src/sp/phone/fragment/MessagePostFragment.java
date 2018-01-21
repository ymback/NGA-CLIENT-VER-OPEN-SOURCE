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
import sp.phone.mvp.contract.MessagePostContract;
import sp.phone.utils.FunctionUtils;
import sp.phone.utils.StringUtils;

/**
 * Created by Justwen on 2017/5/28.
 */

public class MessagePostFragment extends MaterialCompatFragment implements MessagePostContract.View {


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
    public View onCreateContainerView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message_post,container,false);
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

        return rootView;
    }



    @Override
    public void setPresenter(MessagePostContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.message_post_menu,menu);
    }

    @Override
    public void onResume() {
        if (mAction.equals("new")) {
            if (StringUtils.isEmpty(mToEditText.getText().toString())) {
                mToEditText.requestFocus();
            } else {
                mTitleEditText.requestFocus();
            }
        } else {
            mBodyEditText.requestFocus();
        }
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.supertext:
                FunctionUtils.handleSupertext(mBodyEditText, getContext(), getView());
                break;
            case R.id.send:
                String title = mTitleEditText.getText().toString();
                String to = mToEditText.getText().toString();
                String body = mBodyEditText.getText().toString();
                if (StringUtils.isEmpty(to)) {
                    mToEditText.setError("请输入收件人");
                    mToEditText.requestFocus();
                } else if (StringUtils.isEmpty(title)) {
                    mTitleEditText.setError("请输入标题");
                    mTitleEditText.requestFocus();
                } else if (StringUtils.isEmpty(body)) {
                    mBodyEditText.setError("请输入内容");
                    mBodyEditText.requestFocus();
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
