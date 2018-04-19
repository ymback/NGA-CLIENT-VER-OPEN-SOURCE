package sp.phone.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Justwen on 2018/3/10.
 */

public class RecentReplyInfo extends NotificationInfo implements Parcelable {

    private String mTitle;

    private String mPidStr;

    private String mTidStr;

    public RecentReplyInfo(int type, String userName, String title, String pidStr, String tidStr) {
        super(type,userName);
        mTitle = title;
        mPidStr = pidStr;
        mTidStr = tidStr;
    }

    protected RecentReplyInfo(Parcel in) {
        super(in);
        mTitle = in.readString();
        mPidStr = in.readString();
        mTidStr = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mTitle);
        dest.writeString(mPidStr);
        dest.writeString(mTidStr);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecentReplyInfo> CREATOR = new Creator<RecentReplyInfo>() {
        @Override
        public RecentReplyInfo createFromParcel(Parcel in) {
            return new RecentReplyInfo(in);
        }

        @Override
        public RecentReplyInfo[] newArray(int size) {
            return new RecentReplyInfo[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public String getPidStr() {
        return mPidStr;
    }

    public String getTidStr() {
        return mTidStr;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RecentReplyInfo) {
            return mPidStr.equals(((RecentReplyInfo) obj).getPidStr());
        } else {
            return false;
        }
    }


}
