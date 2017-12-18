package sp.phone.mvp.presenter;

import android.graphics.Bitmap;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import gov.anzong.androidnga.R;
import sp.phone.common.UserManagerImpl;
import sp.phone.forumoperation.LoginAction;
import sp.phone.fragment.material.LoginWebFragment;
import sp.phone.interfaces.OnAuthCodeLoadFinishedListener;
import sp.phone.mvp.contract.LoginContract;
import sp.phone.mvp.model.LoginModel;
import sp.phone.utils.StringUtils;

/**
 * Created by Justwen on 2017/6/16.
 */

public class LoginPresenter extends BasePresenter<LoginWebFragment, LoginModel> implements LoginContract.Presenter {

    private LoginAction mLoginAction;

    private boolean mLoading;

    private static final Object COMMIT_LOCK = new Object();

    private static final String TAG_UID = "ngaPassportUid";

    private static final String TAG_CID = "ngaPassportCid";

    private static final String TAG_USER_NAME = "ngaPassportUrlencodedUname";

    @Override
    protected LoginModel onCreateModel() {
        return null;
    }

    @Override
    public void loadAuthCode() {
        mLoginAction.setAuthCodeCookie("");
        mBaseView.setAuthCode("");
        mBaseView.setAuthCodeImg(R.drawable.q_vcode);
        mBaseModel.loadAuthCode(new OnAuthCodeLoadFinishedListener() {
            @Override
            public void authCodeFinishLoad(Bitmap authImg, String authCode) {
                mLoginAction.setAuthCodeCookie(authCode);
                mBaseView.setAuthCodeImg(authImg);
            }

            @Override
            public void authCodeFinishLoadError() {
                mBaseView.showToast("载入验证码失败，请点击刷新重新加载");
            }
        });
    }

    @Override
    public void login(String userName, String password, String authCode) {
        synchronized (COMMIT_LOCK) {
            if (mLoading) {
                mBaseView.showToast(R.string.avoidWindfury);
            } else if (StringUtils.isEmpty(mLoginAction.getAuthCodeCookie())) {
                mBaseView.showToast("验证码信息错误，请重试");
                loadAuthCode();
            } else if (StringUtils.isEmpty(userName) ||
                    StringUtils.isEmpty(password) ||
                    StringUtils.isEmpty(authCode)) {
                mBaseView.showToast("内容缺少，请检查后再试");
                loadAuthCode();
            } else {
                mLoginAction.setUserName(userName);
                mLoginAction.setPassword(password);
                mLoginAction.setAuthCode(authCode);
                mBaseModel.login(mLoginAction, new LoginModel.OnLoginListener() {
                    @Override
                    public void onLoginSuccess() {
                        String uid = mLoginAction.getUid();
                        String cid = mLoginAction.getCid();
                        String userName = mLoginAction.getUserName();
                        saveCookie(uid, cid, userName);
                    }

                    @Override
                    public void onLoginFailure(String errorMsg) {
                        loadAuthCode();
                        if (StringUtils.isEmpty(errorMsg)) {
                            mBaseView.showToast(R.string.login_failed);
                        } else {
                            mBaseView.showToast(errorMsg);
                        }
                    }
                });
                mLoading = true;
            }
        }
    }

    @Override
    public void setLoginAction(LoginAction loginAction) {
        mLoginAction = loginAction;
    }

    @Override
    public void start() {
        if (!StringUtils.isEmpty(mLoginAction.getAction())) {
            mBaseView.showToast("你需要登录才能进行下一步操作");
        }
    }

    @Override
    public void parseCookie(String cookies) {
        if (!cookies.contains(TAG_UID)) {
            return;
        }
        String uid = null;
        String cid = null;
        String userName = null;

        for (String cookie : cookies.split(";")) {
            cookie = cookie.trim();
            if (cookie.contains(TAG_UID)) {
                uid = cookie.substring(TAG_UID.length() + 1);
            } else if (cookie.contains(TAG_CID)) {
                cid = cookie.substring(TAG_CID.length() + 1);
            } else if (cookie.contains(TAG_USER_NAME)) {
                userName = cookie.substring(TAG_USER_NAME.length() + 1);
                try {
                    userName = URLDecoder.decode(userName, "gbk");
                    userName = URLDecoder.decode(userName, "gbk");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!StringUtils.isEmpty(cid)
                && !StringUtils.isEmpty(uid)
                && !StringUtils.isEmpty(userName)) {
            saveCookie(uid, cid, userName);
        }

    }

    private void saveCookie(String uid, String cid, String userName) {
        mBaseView.showToast(R.string.login_successfully);
        UserManagerImpl.getInstance().addUser(uid, cid, userName, "", 0, "");
        mBaseView.setResult(true);
    }

}
