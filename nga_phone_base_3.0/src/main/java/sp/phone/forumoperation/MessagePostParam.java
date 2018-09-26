package sp.phone.forumoperation;

import android.os.Parcel;
import android.os.Parcelable;

import sp.phone.util.StringUtils;

public class MessagePostParam implements Parcelable {

    private String mAction;

    private int mMid;

    private String mPostSubject;

    private String mPostContent;

    private String mRecipient;

    public MessagePostParam() {
        mAction = "new";
    }

    public MessagePostParam(String action, int mid, String postSubject) {
        mAction = action;
        mMid = mid;
        mPostSubject = postSubject;
    }

    protected MessagePostParam(Parcel in) {
        mAction = in.readString();
        mMid = in.readInt();
        mPostSubject = in.readString();
        mPostContent = in.readString();
        mRecipient = in.readString();
    }

    public String getRecipient() {
        return mRecipient;
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

    public int getMid() {
        return mMid;
    }

    public String getAction() {
        return mAction;
    }

    public static final Creator<MessagePostParam> CREATOR = new Creator<MessagePostParam>() {
        @Override
        public MessagePostParam createFromParcel(Parcel in) {
            return new MessagePostParam(in);
        }

        @Override
        public MessagePostParam[] newArray(int size) {
            return new MessagePostParam[size];
        }
    };

    public void setRecipient(String recipient) {
        mRecipient = recipient;
    }

    @Override
    public String toString() {
        String recipient = mRecipient.replaceAll("，", ",");
        recipient = StringUtils.encodeUrl(recipient, "GBK");
        StringBuilder builder = new StringBuilder();
        builder.append("__lib=message&__act=message&lite=js&act=")
                .append(mAction)
                .append("&to=")
                .append(recipient)
                .append("&mid=")
                .append(mMid)
                .append("&subject=")
                .append(StringUtils.encodeUrl(mPostSubject, "GBK"))
                .append("&content=")
                .append(StringUtils.encodeUrl(mPostContent, "GBK"));
        return builder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAction);
        dest.writeInt(mMid);
        dest.writeString(mPostSubject);
        dest.writeString(mPostContent);
        dest.writeString(mRecipient);
    }
}

