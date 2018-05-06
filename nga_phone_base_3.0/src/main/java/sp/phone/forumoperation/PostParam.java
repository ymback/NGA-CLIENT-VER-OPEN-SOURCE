package sp.phone.forumoperation;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sp.phone.util.StringUtils;

public class PostParam implements Parcelable {
    private int step_;
    private String pid_;
    private String mAction;
    private int mFid;
    private String tid_;
    private String _ff_;
    private String attachments_;
    private String attachments_check_;
    private String force_topic_key_;
    private int filter_key_;
    private String post_subject_;
    private String post_content_;
    private String checkkey_;
    private String mention_;
    private String __ngaClientChecksum;
    private boolean __isanony;//&anony=1

    private String auth = "";

    protected PostParam(Parcel in) {
        step_ = in.readInt();
        pid_ = in.readString();
        mAction = in.readString();
        mFid = in.readInt();
        tid_ = in.readString();
        _ff_ = in.readString();
        attachments_ = in.readString();
        attachments_check_ = in.readString();
        force_topic_key_ = in.readString();
        filter_key_ = in.readInt();
        post_subject_ = in.readString();
        post_content_ = in.readString();
        checkkey_ = in.readString();
        mention_ = in.readString();
        __ngaClientChecksum = in.readString();
        __isanony = in.readByte() != 0;
        auth = in.readString();
    }

    public static final Creator<PostParam> CREATOR = new Creator<PostParam>() {
        @Override
        public PostParam createFromParcel(Parcel in) {
            return new PostParam(in);
        }

        @Override
        public PostParam[] newArray(int size) {
            return new PostParam[size];
        }
    };

    public String getAuth() {
        return auth;
    }

    public String getAction(){
        return mAction;
    }

    public synchronized void setAuth(String auth) {
        this.auth = auth;
    }

    public String getTid(){
        return tid_;
    }

    public String getPid(){
        return pid_;
    }

    public PostParam(String tid, String subject, String content) {
        step_ = 2;
        pid_ = "";
        mAction = "new";
        mFid = 0;
        tid_ = tid;
        _ff_ = "";
        attachments_ = "";
        attachments_check_ = "";
        force_topic_key_ = "";
        filter_key_ = 1;
        post_subject_ = subject;
        post_content_ = content;
        checkkey_ = "";
        mention_ = "";
        __isanony = false;


    }

    public int getFid() {
        return mFid;
    }

    public void setFid(int fid) {
        mFid = fid;
    }

    public void set__isanony(boolean isanony) {
        __isanony = isanony;
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

    public void setPid_(String pid) {
        pid_ = pid;
    }

    public void setTid_(String tid) {
        tid_ = tid;
    }


    public void setAction(String action) {
        mAction = action;
    }

    public void set__ngaClientChecksum(String getngaClientChecksum) {
        // TODO Auto-generated method stub
        __ngaClientChecksum = getngaClientChecksum;
    }

    public void setMention_(String mention_) {
        this.mention_ = mention_;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("step=");
        sb.append(step_);
        sb.append("&pid=");
        sb.append(pid_);
        sb.append("&action=");
        sb.append(mAction);

        if (!mAction.equals("modify")) {
            sb.append("&fid=");
            sb.append(mFid);
        } else {
            sb.append("&fid=");
        }
        sb.append("&tid=");
        sb.append(tid_);
        if (__isanony) {
            sb.append("&anony=1");
        }
        sb.append("&_ff=");
        sb.append(_ff_);
        sb.append("&attachments=");
        sb.append(attachments_);
        sb.append("&attachments_check=");
        sb.append(attachments_check_);
        sb.append("&force_topic_key=");
        sb.append(force_topic_key_);
        sb.append("&filter_key=");
        sb.append(filter_key_);
        sb.append("&post_subject=");
        sb.append(StringUtils.encodeUrl(post_subject_, "GBK"));
        sb.append("&post_content=");
        sb.append(StringUtils.encodeUrl(post_content_, "GBK"));
        if (mention_.length() != 0) {
            sb.append("&mention=");
            sb.append(StringUtils.encodeUrl(mention_, "GBK"));

        } else {
            sb.append("&mention=");
            sb.append(StringUtils.encodeUrl("", "GBK"));
        }
        sb.append("&checkkey=");
        sb.append(checkkey_);
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
        dest.writeInt(step_);
        dest.writeString(pid_);
        dest.writeString(mAction);
        dest.writeInt(mFid);
        dest.writeString(tid_);
        dest.writeString(_ff_);
        dest.writeString(attachments_);
        dest.writeString(attachments_check_);
        dest.writeString(force_topic_key_);
        dest.writeInt(filter_key_);
        dest.writeString(post_subject_);
        dest.writeString(post_content_);
        dest.writeString(checkkey_);
        dest.writeString(mention_);
        dest.writeString(__ngaClientChecksum);
        dest.writeByte((byte) (__isanony ? 1 : 0));
        dest.writeString(auth);
    }
}

