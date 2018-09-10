package sp.phone.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Board implements Parcelable {

    private int mFid;

    private String mName;

    private int mCategory;

    public Board(int fid, String name) {
        mFid = fid;
        mName = name;
    }

    public Board() {

    }

    @Deprecated
    public Board(String url, String name, int icon, int iconOld) {
        this(Integer.parseInt(url), name);
    }

    @Deprecated
    public Board(String url, String name, int icon) {
        this(Integer.parseInt(url), name);
    }

    @Deprecated
    public Board(String url, String name) {
        this(Integer.parseInt(url), name);
    }


    protected Board(Parcel in) {
        mFid = in.readInt();
        mName = in.readString();
        mCategory = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mFid);
        dest.writeString(mName);
        dest.writeInt(mCategory);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Board> CREATOR = new Creator<Board>() {
        @Override
        public Board createFromParcel(Parcel in) {
            return new Board(in);
        }

        @Override
        public Board[] newArray(int size) {
            return new Board[size];
        }
    };

    public void setCategory(int category) {
        mCategory = category;
    }

    @Deprecated
    public String getUrl() {
        return String.valueOf(getFid());
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getFid() {
        return mFid;
    }

    public void setFid(int fid) {
        mFid = fid;
    }

}
