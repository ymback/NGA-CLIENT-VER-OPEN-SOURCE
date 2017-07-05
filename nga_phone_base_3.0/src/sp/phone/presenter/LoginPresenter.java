package sp.phone.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.MyApp;
import sp.phone.bean.PreferenceConstant;
import sp.phone.forumoperation.LoginAction;
import sp.phone.interfaces.OnAuthCodeLoadFinishedListener;
import sp.phone.model.LoginModel;
import sp.phone.presenter.contract.LoginContract;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;

/**
 * Created by Yang Yihang on 2017/6/16.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View mView;

    private LoginContract.Model mModel;

    private LoginAction mLoginAction;

    private boolean mLoading;

    private static final Object COMMIT_LOCK = new Object();

    private static final String TAG_UID = "ngaPassportUid";

    private static final String TAG_CID = "ngaPassportCid";

    private static final String TAG_USER_NAME = "ngaPassportUrlencodedUname";


    public LoginPresenter(LoginContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mModel = new LoginModel(this);
    }

    @Override
    public Context getContext() {
        return mView.getContext();
    }

    @Override
    public void loadAuthCode() {
        mLoginAction.setAuthCodeCookie("");
        mView.setAuthCode("");
        mView.setAuthCodeImg(R.drawable.q_vcode);
        mModel.loadAuthCode(new OnAuthCodeLoadFinishedListener() {
            @Override
            public void authCodeFinishLoad(Bitmap authImg, String authCode) {
                mLoginAction.setAuthCodeCookie(authCode);
                mView.setAuthCodeImg(authImg);
            }

            @Override
            public void authCodeFinishLoadError() {
                mView.showToast("载入验证码失败，请点击刷新重新加载");
            }
        });
    }

    @Override
    public void login(String userName, String password, String authCode) {
        synchronized (COMMIT_LOCK) {
            if (mLoading) {
                mView.showToast(R.string.avoidWindfury);
            } else if (StringUtil.isEmpty(mLoginAction.getAuthCodeCookie())) {
                mView.showToast("验证码信息错误，请重试");
                loadAuthCode();
            } else if (StringUtil.isEmpty(userName) ||
                    StringUtil.isEmpty(password) ||
                    StringUtil.isEmpty(authCode)){
                mView.showToast("内容缺少，请检查后再试");
                loadAuthCode();
            } else {
                mLoginAction.setUserName(userName);
                mLoginAction.setPassword(password);
                mLoginAction.setAuthCode(authCode);
                mModel.login(mLoginAction, new LoginModel.OnLoginListener() {
                    @Override
                    public void onLoginSuccess() {
                        String uid = mLoginAction.getUid();
                        String cid = mLoginAction.getCid();
                        String userName = mLoginAction.getUserName();
                        saveCookie(uid,cid,userName);
                    }

                    @Override
                    public void onLoginFailure(String errorMsg) {
                        loadAuthCode();
                        if (StringUtil.isEmpty(errorMsg)){
                            mView.showToast(R.string.login_failed);
                        } else {
                            mView.showToast(errorMsg);
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
        if (!StringUtil.isEmpty(mLoginAction.getAction())) {
            mView.showToast("你需要登录才能进行下一步操作");
        }
    }

    @Override
    public void parseCookie(String cookies) {
        if (!cookies.contains(TAG_UID) || mLoginAction.getUid() != null) {
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

        if (!StringUtil.isEmpty(cid)
                && !StringUtil.isEmpty(uid)
                && !StringUtil.isEmpty(userName)) {
            mLoginAction.setUid(uid);
            saveCookie(uid,cid,userName);
        }

    }

    private void saveCookie(String uid,String cid,String userName) {
        mView.showToast(R.string.login_successfully);
        SharedPreferences sp = getContext().getSharedPreferences(PreferenceConstant.PERFERENCE, Context.MODE_PRIVATE);
        sp.edit().putString(PreferenceConstant.UID, uid)
                .putString(PreferenceConstant.CID, cid).putString(PreferenceConstant.PENDING_REPLYS, "")
                .putString(PreferenceConstant.REPLYTOTALNUM, "0")
                .putString(PreferenceConstant.USER_NAME, uid)
                .putString(PreferenceConstant.BLACK_LIST, "")
                .apply();
        MyApp app = (MyApp) ((Activity)mView.getContext()).getApplication();
        app.addToUserList(uid, cid, userName, "", 0, "");

        PhoneConfiguration config = PhoneConfiguration.getInstance();

        config.setUid(uid);
        config.setCid(cid);
        config.userName = userName;
        config.setReplyTotalNum(0);
        config.setReplyString("");
        config.blacklist = StringUtil.blackliststringtolisttohashset("");
        mView.setResult(true);
        mView.finish();
    }

}
