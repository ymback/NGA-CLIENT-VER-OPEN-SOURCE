package sp.phone.mvp.contract;

import sp.phone.forumoperation.LoginParam;
import sp.phone.listener.OnHttpCallBack;

/**
 * Created by Justwen on 2017/6/16.
 */

public interface LoginContract {

    interface Presenter {

        void loadAuthCode();

        void login(String userName, String password, String authCode);

        void parseCookie(String cookie);

    }

    interface View {

        void setAuthCodeImg(String dataUrl);

        void setResult(boolean isChanged);
    }

    interface Model {

        void loadAuthCode(OnHttpCallBack<LoginParam> callBack);

        void login(LoginParam loginParam, OnHttpCallBack<String> callBack);

    }
}
