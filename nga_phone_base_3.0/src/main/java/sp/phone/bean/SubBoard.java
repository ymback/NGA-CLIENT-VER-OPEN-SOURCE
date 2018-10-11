package sp.phone.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Justwen on 2018/1/31.
 */

public class SubBoard extends Board implements Parcelable {

    private String mTidStr;

    private int mStid;

    private int mType;

    private boolean mChecked = true;

    private String mDescription;

    private String mParentFidStr;

    public SubBoard() {
    }

    protected SubBoard(Parcel in) {
        super(in);
        mTidStr = in.readString();
        mStid = in.readInt();
        mType = in.readInt();
        mChecked = in.readByte() != 0;
        mDescription = in.readString();
        mParentFidStr = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mTidStr);
        dest.writeInt(mStid);
        dest.writeInt(mType);
        dest.writeByte((byte) (mChecked ? 1 : 0));
        dest.writeString(mDescription);
        dest.writeString(mParentFidStr);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SubBoard> CREATOR = new Creator<SubBoard>() {
        @Override
        public SubBoard createFromParcel(Parcel in) {
            return new SubBoard(in);
        }

        @Override
        public SubBoard[] newArray(int size) {
            return new SubBoard[size];
        }
    };


    public int getStid() {
        return mStid;
    }

    public void setStid(int stid) {
        mStid = stid;
    }

    public String getTidStr() {
        return mTidStr;
    }

    public void setTidStr(String tidStr) {
        mTidStr = tidStr;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getParentFidStr() {
        return mParentFidStr;
    }

    public void setParentFidStr(String parentFidStr) {
        mParentFidStr = parentFidStr;
    }
}
