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

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import sp.phone.mvp.contract.MessagePostContract;
import sp.phone.mvp.presenter.MessagePostPresenter;
import sp.phone.util.StringUtils;

/**
 * Created by Justwen on 2017/5/28.
 */

public class MessagePostFragment extends BaseMvpFragment<MessagePostPresenter> implements MessagePostContract.View {

    @BindView(R.id.et_title)
    public EditText mTitleEditor;

    @BindView(R.id.et_recipient)
    public EditText mRecipientEditor;

    @BindView(R.id.et_body)
    public EditText mBodyEditor;

    @BindView(R.id.panel_recipient)
    public ViewGroup mRecipientPanel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.setPostParam(getArguments().getParcelable("param"));
    }

    @Override
    protected MessagePostPresenter onCreatePresenter() {
        return new MessagePostPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message_post, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        mBodyEditor.requestFocus();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.message_post_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send:
                String title = mTitleEditor.getText().toString();
                String recipient = mRecipientEditor.getText().toString();
                String body = mBodyEditor.getText().toString();
                if (StringUtils.isEmpty(recipient) && mRecipientPanel.isShown()) {
                    mRecipientEditor.setError("请输入收件人");
                    mRecipientEditor.requestFocus();
                } else if (StringUtils.isEmpty(body) && body.length() < 6) {
                    mBodyEditor.setError("请输入内容或者内容字数少于6");
                    mBodyEditor.requestFocus();
                } else {
                    mPresenter.commit(title, recipient, body);
                }
                break;
        }
        return true;
    }

    @Override
    public void setRecipient(String recipient) {
        mRecipientEditor.setText(recipient);
        if (recipient != null) {
            mRecipientEditor.setSelection(recipient.length());
        }
    }

    @Override
    public void finish(int resultCode) {
        getActivity().setResult(resultCode);
        getActivity().finish();
    }

    @Override
    public void hideRecipientEditor() {
        mRecipientPanel.setVisibility(View.GONE);
    }
}
