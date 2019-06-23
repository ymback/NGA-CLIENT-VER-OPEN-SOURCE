package sp.phone.param;

/**
 * Created by Justwen on 2017/6/16.
 */

public class LoginParam {

    private String userName;

    private String password;

    private String authCode;

    private String mAuthCodeCookie;

    private String uid;

    private String cid;

    private String action;

    private String mAuthCode;

    private String mDataUrl;

    public String getDataUrl() {
        return mDataUrl;
    }

    public void setDataUrl(String dataUrl) {
        mDataUrl = dataUrl;
    }

    public String getAuthCodeCookie() {
        return mAuthCodeCookie;
    }

    public void setAuthCodeCookie(String authCodeCookie) {
        mAuthCodeCookie = authCodeCookie;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
