package sp.phone.forumoperation;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sp.phone.util.StringUtils;
import sp.phone.util.StringUtils;

public class MessagePostAction implements Parcelable {
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

    protected MessagePostAction(Parcel in) {
        action_ = in.readString();
        mid_ = in.readInt();
        attachments_ = in.readString();
        attachments_check_ = in.readString();
        post_subject_ = in.readString();
        post_content_ = in.readString();
        __ngaClientChecksum = in.readString();
        to_ = in.readString();
    }

    public static final Creator<MessagePostAction> CREATOR = new Creator<MessagePostAction>() {
        @Override
        public MessagePostAction createFromParcel(Parcel in) {
            return new MessagePostAction(in);
        }

        @Override
        public MessagePostAction[] newArray(int size) {
            return new MessagePostAction[size];
        }
    };

    public void setTo_(String to) {
        to = to.replaceAll("，", ",");
        to = StringUtils.encodeUrl(to, "GBK");
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
        sb.append(StringUtils.encodeUrl(post_subject_, "GBK"));
        sb.append("&content=");
        sb.append(StringUtils.encodeUrl(post_content_, "GBK"));
        sb.append("&__ngaClientChecksum=");
        sb.append(__ngaClientChecksum);
        return sb.toString();
    }

    public void appendAttachments_(String attachments_) {
        if (StringUtils.isEmpty(this.attachments_))
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
        if (StringUtils.isEmpty(this.attachments_check_))
            this.attachments_check_ = attachments_check_;
        else {
            try {
                this.attachments_check_ = this.attachments_check_ + URLEncoder.encode("\t", "GBK") + attachments_check_;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(action_);
        dest.writeInt(mid_);
        dest.writeString(attachments_);
        dest.writeString(attachments_check_);
        dest.writeString(post_subject_);
        dest.writeString(post_content_);
        dest.writeString(__ngaClientChecksum);
        dest.writeString(to_);
    }
}

