package sp.phone.forumoperation;

public class SignPostParam {

    private String mSign;

    private String mUid;

    public String getSign() {
        return mSign;
    }

    public void setSign(String sign) {
        mSign = sign;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    @Override
    public String toString() {
        return "SignPostParam{" +
                "mSign='" + mSign + '\'' +
                ", mUid='" + mUid + '\'' +
                '}';
    }
}

