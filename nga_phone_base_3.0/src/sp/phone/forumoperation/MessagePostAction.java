package sp.phone.forumoperation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sp.phone.utils.StringUtil;

public class MessagePostAction {
    private String action_;
    private int mid_;
    private String attachments_;
    private String attachments_check_;
    private String post_subject_;
    private String post_content_;
    private String __ngaClientChecksum;
    private String to_;

    public MessagePostAction(int mid, String subject, String content) {
        action_ = "new";
        mid_ = mid;
        attachments_ = "";
        attachments_check_ = "";
        post_subject_ = subject;
        post_content_ = content;


    }

    public void setTo_(String to) {
        to = to.replaceAll("，", ",");
        to = StringUtil.encodeUrl(to, "GBK");
        this.to_ = to;
    }

    public int getMid_() {
        return mid_;
    }

    public void setMid_(int mid_) {
        this.mid_ = mid_;
    }

    public String getPost_subject_() {
        return post_subject_;
    }

    public void setPost_subject_(String postSubject) {
        post_subject_ = postSubject;
    }

    public String getPost_content_() {
        return post_content_;
    }

    public void setPost_content_(String postContent) {
        post_content_ = postContent;
    }

    public String getAction_() {
        return action_;
    }

    public void setAction_(String action) {
        action_ = action;
    }

    public void set__ngaClientChecksum(String getngaClientChecksum) {
        // TODO Auto-generated method stub
        __ngaClientChecksum = getngaClientChecksum;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("__lib=message&__act=message&lite=js");
        sb.append("&act=");
        sb.append(action_);

        if (!action_.equals("new")) {
            sb.append("&mid=");
            sb.append(String.valueOf(mid_));
        }
        sb.append("&to=");
        sb.append(to_);
        sb.append("&subject=");
        sb.append(StringUtil.encodeUrl(post_subject_, "GBK"));
        sb.append("&content=");
        sb.append(StringUtil.encodeUrl(post_content_, "GBK"));
        sb.append("&__ngaClientChecksum=");
        sb.append(__ngaClientChecksum);
        return sb.toString();
    }

    public void appendAttachments_(String attachments_) {
        if (StringUtil.isEmpty(this.attachments_))
            this.attachments_ = attachments_;
        else {
            try {
                this.attachments_ = this.attachments_ + URLEncoder.encode("\t", "GBK") + attachments_;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public void appendAttachments_check_(String attachments_check_) {
        if (StringUtil.isEmpty(this.attachments_check_))
            this.attachments_check_ = attachments_check_;
        else {
            try {
                this.attachments_check_ = this.attachments_check_ + URLEncoder.encode("\t", "GBK") + attachments_check_;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


}

