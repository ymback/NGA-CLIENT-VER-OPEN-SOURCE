package sp.phone.forumoperation;

import sp.phone.utils.StringUtil;


public class AvatarPostAction {
    private String func_;
    private String icon_;
    private String __ngaClientChecksum;

    public AvatarPostAction() {
        func_ = "avatar";
    }

    public void seticon_(String icon) {
        icon_ = icon;
    }

    public String geticon_() {
        return icon_;
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
        sb.append("&icon=");
        sb.append(StringUtil.encodeUrl(icon_, "GBK"));
        sb.append("&__ngaClientChecksum=");
        sb.append(__ngaClientChecksum);
        return sb.toString();
    }
}

