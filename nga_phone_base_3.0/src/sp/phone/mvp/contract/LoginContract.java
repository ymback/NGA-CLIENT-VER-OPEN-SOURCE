package sp.phone.mvp.contract;

import android.graphics.Bitmap;

import sp.phone.forumoperation.LoginAction;
import sp.phone.interfaces.OnAuthCodeLoadFinishedListener;
import sp.phone.mvp.model.LoginModel;
import sp.phone.presenter.contract.*;
import sp.phone.presenter.contract.BaseContract;

/**
 * Created by Yang Yihang on 2017/6/16.
 */

public interface LoginContract {

    interface Presenter extends sp.phone.presenter.contract.BaseContract.Presenter<View> {

        void loadAuthCode();

        void login(String userName,String password,String authCode);

        void setLoginAction(LoginAction loginAction);

        void start();

        void parseCookie(String cookie);

    }

    interface View extends sp.phone.presenter.contract.BaseContract.View<Presenter> {

        void setAuthCodeImg(Bitmap bitmap);

        void setAuthCodeImg(int resId);

        void setAuthCode(String text);

        void setResult(boolean isChanged);
    }

    interface Model extends BaseContract.Model {

        void login(LoginAction loginAction, LoginModel.OnLoginListener listener);

        void loadAuthCode(OnAuthCodeLoadFinishedListener listener);

    }
}
