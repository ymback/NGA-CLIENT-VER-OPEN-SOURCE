package sp.phone.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sp.phone.forumoperation.LoginAction;
import sp.phone.interfaces.OnAuthCodeLoadFinishedListener;
import sp.phone.presenter.contract.LoginContract;
import sp.phone.task.AccountAuthCodeImageReloadTask;
import sp.phone.task.LoginTask;

/**
 * Created by Yang Yihang on 2017/6/16.
 */

public class LoginModel implements LoginContract.Model {

    private AccountAuthCodeImageReloadTask mAuthCodeImageTask;

    private LoginContract.Presenter mPresenter;

    private static final String POST_URL = "http://account.178.com/q_account.php?_act=login&print=login";

    public interface OnLoginListener {

        void onLoginSuccess();

        void onLoginFailure(String errorMsg);
    }


    public LoginModel(LoginContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void login(LoginAction loginAction,OnLoginListener listener) {
        try {
            String bodyBuilder = "email="
                    + URLEncoder.encode(loginAction.getUserName(), "utf-8")
                    + "&password="
                    + URLEncoder.encode(loginAction.getPassword(), "utf-8")
                    + "&vcode="
                    + URLEncoder.encode(loginAction.getAuthCode(), "utf-8");
            new LoginTask(loginAction,listener).execute(POST_URL, bodyBuilder);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void loadAuthCode(OnAuthCodeLoadFinishedListener listener) {
        if (mAuthCodeImageTask != null && !mAuthCodeImageTask.isCancelled()) {
            mAuthCodeImageTask.cancel(true);
        }
        mAuthCodeImageTask = new AccountAuthCodeImageReloadTask(mPresenter.getContext(),listener);
        mAuthCodeImageTask.execute();
    }
}
