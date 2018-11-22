package sp.phone.forumoperation;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sp.phone.util.StringUtils;

public class PostParam implements Parcelable {

    private boolean mAnonymous;

    private StringBuilder mAttachments;

    private StringBuilder mAttachmentsCheck;

    private String mAuthCode;

    private String mPostAction;

    private String mPostContent;

    private int mPostFid;

    private String mPostPid;

    private String mPostSubject;

    private String mPostTid;

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

    protected PostParam(Parcel in) {
        mPostPid = in.readString();
        mPostAction = in.readString();
        mPostFid = in.readInt();
        mPostTid = in.readString();
        mPostSubject = in.readString();
        mPostContent = in.readString();
        mAnonymous = in.readByte() != (byte) 0;
        mAuthCode = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPostPid);
        dest.writeString(mPostAction);
        dest.writeInt(mPostFid);
        dest.writeString(mPostTid);
        dest.writeString(mPostSubject);
        dest.writeString(mPostContent);
        dest.writeByte((byte) (mAnonymous ? 1 : 0));
        dest.writeString(mAuthCode);
    }

    public int describeContents() {
        return 0;
    }

    public String getPostAction() {
        return mPostAction;
    }

    public synchronized String getAuthCode() {
        return mAuthCode;
    }

    public synchronized void setAuthCode(String authCode) {
        mAuthCode = authCode;
    }

    public PostParam(String tid, String subject, String content) {
        mPostTid = tid;
        mPostSubject = subject;
        mPostContent = content;
    }

    public void setPostAction(String postAction) {
        mPostAction = postAction;
    }

    public void setAnonymous(boolean anonymous) {
        mAnonymous = anonymous;
    }

    public String getPostSubject() {
        return mPostSubject;
    }

    public void setPostSubject(String postSubject) {
        mPostSubject = postSubject;
    }

    public String getPostContent() {
        return mPostContent;
    }

    public void setPostContent(String postContent) {
        mPostContent = postContent;
    }

    public String getPostPid() {
        return mPostPid;
    }

    public void setPostPid(String postPid) {
        mPostPid = postPid;
    }

    public int getPostFid() {
        return mPostFid;
    }

    public void setPostFid(int postFid) {
        mPostFid = postFid;
    }

    public String getPostTid() {
        return mPostTid;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("step=2");
        builder.append("&post_content=").append(StringUtils.encodeUrl(mPostContent, "GBK"));
        if (mPostPid != null) {
            builder.append("&pid=").append(mPostPid);
        }
        if (mPostTid != null) {
            builder.append("&tid=").append(mPostTid);
        }
        if (mPostAction != null) {
            builder.append("&action=").append(mPostAction);
        }
        if (mPostSubject != null) {
            builder.append("&post_subject=").append(StringUtils.encodeUrl(mPostSubject, "GBK"));
        }
        if (mPostFid != 0) {
            builder.append("&fid=").append(mPostFid);
        }
        if (mAnonymous) {
            builder.append("&anony=1");
        }
        if (mAttachments != null) {
            builder.append("&attachments=").append(mAttachments).append("&attachments_check=").append(mAttachmentsCheck);
        }
        return builder.toString();
    }

    public void appendAttachment(String attachment, String attachmentCheck) {
        try {
            if (mAttachments == null) {
                mAttachments = new StringBuilder();
                mAttachmentsCheck = new StringBuilder();
            }
            mAttachments.append(URLEncoder.encode("\t", "GBK")).append(attachment);
            mAttachmentsCheck.append(URLEncoder.encode("\t", "GBK")).append(attachmentCheck);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}