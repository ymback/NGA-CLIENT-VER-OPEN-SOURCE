package sp.phone.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Justwen on 2018/3/10.
 */

public class NotificationInfo implements Parcelable {

    private int mType;

    private String mUserName;

    private boolean mUnread;

    private String mTimeStamp;

    private String mUserId;

    public NotificationInfo(int type, String userName) {
        mType = type;
        mUserName = userName;
    }

    protected NotificationInfo(Parcel in) {
        mType = in.readInt();
        mUserName = in.readString();
        mUnread = in.readByte() != 0;
        mTimeStamp = in.readString();
        mUserId = in.readString();
    }

    public static final Creator<NotificationInfo> CREATOR = new Creator<NotificationInfo>() {
        @Override
        public NotificationInfo createFromParcel(Parcel in) {
            return new NotificationInfo(in);
        }

        @Override
        public NotificationInfo[] newArray(int size) {
            return new NotificationInfo[size];
        }
    };

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        mTimeStamp = timeStamp;
    }

    public void setUnread(boolean unread) {
        mUnread = unread;
    }

    public int getType() {
        return mType;
    }

    public String getUserName() {
        return mUserName;
    }

    public boolean isUnread() {
        return mUnread;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mType);
        dest.writeString(mUserName);
        dest.writeByte((byte) (mUnread ? 1 : 0));
        dest.writeString(mTimeStamp);
        dest.writeString(mUserId);
    }
}
