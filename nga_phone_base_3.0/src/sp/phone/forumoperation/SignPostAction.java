package sp.phone.forumoperation;

import sp.phone.utils.StringUtil;

public class SignPostAction {
    private String func_;
    private String sign_;
    private String __ngaClientChecksum;

    public SignPostAction() {
        func_ = "sign";
    }

    public void setsign_(String sign) {
        sign_ = sign;
    }

    public String getsign_() {
        return sign_;
    }

    public void set__ngaClientChecksum(String getngaClientChecksum) {
        // TODO Auto-generated method stub
        __ngaClientChecksum = getngaClientChecksum;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("lite=js&noprefix");
        sb.append("&func=");
        sb.append(func_);
        sb.append("&sign=");
        sb.append(StringUtil.encodeUrl(sign_, "GBK"));
        sb.append("&__ngaClientChecksum=");
        sb.append(__ngaClientChecksum);
        return sb.toString();
    }


}

