package sp.phone.task;

import android.os.AsyncTask;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;

import sp.phone.forumoperation.HttpPostClient;
import sp.phone.forumoperation.LoginAction;
import sp.phone.mvp.model.LoginModel;
import sp.phone.utils.NLog;
import sp.phone.utils.StringUtils;

/**
 * Created by Yang Yihang on 2017/6/16.
 */

public class LoginTask extends AsyncTask<String, Integer, Boolean> {

    private String errorstr = "";

    private final String LOG_TAG = LoginTask.class.getSimpleName();

    private LoginModel.OnLoginListener mLoginListener;

    private LoginAction mLoginAction;

    public LoginTask(LoginAction loginAction,LoginModel.OnLoginListener listener) {
        mLoginAction = loginAction;
        mLoginListener = listener;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String url = params[0];
        String body = params[1];
        String cookie = "reg_vcode=" + mLoginAction.getAuthCodeCookie();
        HttpURLConnection conn = new HttpPostClient(url, cookie).post_body(body);
        return validate(conn);

    }

    private boolean validate(HttpURLConnection conn) {
        if (conn == null)
            return false;

        String cookieVal;
        String key;

        String uid = "";
        String cid = "";
        String location = "";

        for (int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++) {
            NLog.d(LOG_TAG,
                    conn.getHeaderFieldKey(i) + ":"
                            + conn.getHeaderField(i));
            if (key.equalsIgnoreCase("location")) {
                String re301location = conn.getHeaderField(i);
                if (re301location.indexOf("login_failed") > 0) {
                    if (re301location.indexOf("error_vcode") > 0) {
                        errorstr = ("验证码错误");
                    }
                    if (re301location.indexOf("e_login") > 0) {
                        errorstr = ("用户名或密码错误");
                    }
                    errorstr = "未知错误";
                    return false;
                }
            }
            if (key.equalsIgnoreCase("set-cookie")) {
                cookieVal = conn.getHeaderField(i);
                cookieVal = cookieVal.substring(0,
                        cookieVal.indexOf(';'));
                if (cookieVal.indexOf("_sid=") == 0)
                    cid = cookieVal.substring(5);
                if (cookieVal.indexOf("_178c=") == 0) {
                    uid = cookieVal.substring(6, cookieVal.indexOf('%'));
                    if (StringUtils.isEmail(mLoginAction.getUserName())) {
                        try {
                            String nametmp = cookieVal
                                    .substring(cookieVal.indexOf("%23") + 3);
                            nametmp = URLDecoder.decode(nametmp,
                                    "utf-8");
                            String[] stemp = nametmp.split("#");
                            for (int ia = 0; ia < stemp.length; ia++) {
                                if (!StringUtils.isEmail(stemp[ia])) {
                                    mLoginAction.setUserName(stemp[ia]);
                                    ia = stemp.length;
                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            if (key.equalsIgnoreCase("Location")) {
                location = conn.getHeaderField(i);

            }
        }
        if (cid != "" && uid != ""
                && location.contains("login_success&error=0")) {
            mLoginAction.setUid(uid);
            mLoginAction.setCid(cid);
            NLog.i(LOG_TAG, "uid =" + uid + ",csid=" + cid);
            return true;
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {

        if (!StringUtils.isEmpty(errorstr)) {
            mLoginListener.onLoginFailure(errorstr);
            super.onPostExecute(result);
        } else if (result) {
            mLoginListener.onLoginSuccess();
        } else {
            mLoginListener.onLoginFailure(null);
        }
    }
}
