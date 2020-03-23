package sp.phone.ui.fragment;


import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import sp.phone.ui.fragment.dialog.LoginDialogFragment;
import sp.phone.mvp.contract.LoginContract;
import sp.phone.mvp.presenter.LoginPresenter;
import sp.phone.rxjava.RxBus;
import sp.phone.rxjava.RxEvent;
import sp.phone.rxjava.RxUtils;

public class LoginFragment extends BaseMvpFragment<LoginPresenter> implements View.OnClickListener, LoginContract.View, LoginDialogFragment.OnAuthCodeLoadCallback {

    private EditText mPasswordView;

    private EditText mUserNameView;

    private String mAuthCodeDataUrl;

    @Override
    protected LoginPresenter onCreatePresenter() {
        return new LoginPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        mPasswordView = rootView.findViewById(R.id.login_password_edittext);
        mUserNameView = rootView.findViewById(R.id.login_user_edittext);
        RxUtils.clicks(rootView.findViewById(R.id.login_button), this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mPresenter.loadAuthCode();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                LoginDialogFragment fragment = new LoginDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("data_url", mAuthCodeDataUrl);
                fragment.setArguments(bundle);
                fragment.setAuthCodeLoadCallback(this);
                fragment.show(getActivity().getSupportFragmentManager());
                break;
            case R.id.user_name:
                mUserNameView.setText(((TextView) v).getText());
                mUserNameView.selectAll();
                break;
            default:
                break;
        }
    }

    @Override
    public void setAuthCodeImg(String dataUrl) {
        mAuthCodeDataUrl = dataUrl;
        RxBus.getInstance().post(new RxEvent(RxEvent.EVENT_LOGIN_AUTH_CODE_UPDATE, mAuthCodeDataUrl));
    }

    @Override
    public void setResult(boolean isChanged) {
        if (isChanged) {
            getActivity().setResult(Activity.RESULT_OK);
        }
    }

    @Override
    public void loadAuthCodeImage(WebView webView) {
        mPresenter.loadAuthCode();
    }

    @Override
    public void login(String authCode) {
        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        mPresenter.login(userName, password, authCode);
    }
}
