package sp.phone.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.Locale;

public class Board implements Parcelable {

    private int mFid;

    private String mName;

    private int mCategory;

    private int mStd;

    private BoardKey mBoardKey;

    public static class BoardKey implements Parcelable {

        int fid;

        int stid;

        protected BoardKey(Parcel in) {
            fid = in.readInt();
            stid = in.readInt();
        }

        public static final Creator<BoardKey> CREATOR = new Creator<BoardKey>() {
            @Override
            public BoardKey createFromParcel(Parcel in) {
                return new BoardKey(in);
            }

            @Override
            public BoardKey[] newArray(int size) {
                return new BoardKey[size];
            }
        };

        @Override
        public boolean equals(@Nullable Object obj) {
            return obj instanceof BoardKey
                    && (fid != 0 && fid == ((BoardKey) obj).fid || stid != 0 && stid == ((BoardKey) obj).stid);
        }

        @Override
        public int hashCode() {
            return String.format(Locale.getDefault(), "&fid=%d&stid=%d", fid, stid).hashCode();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(fid);
            dest.writeInt(stid);
        }
    }

    public Board(int fid, String name) {
        mName = name;
        mFid = fid;
    }

    public Board(int fid, int stid, String name) {
        mFid = fid;
        mName = name;
        mStd = stid;
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

    public String compueUrl() {
        if (mBoardKey.stid != 0) {
            return String.format(Locale.getDefault(), "stid=%d", mBoardKey.stid);
        } else {
            return String.format(Locale.getDefault(), "fid=%d", mBoardKey.fid);
        }
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

    public int getStid() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Board
                && (mFid != 0 && mFid == ((Board) obj).mFid || getStid() != 0 && getStid() == ((Board) obj).getStid());
    }

    @Override
    public int hashCode() {
        return mFid != 0 ? mFid : getStid();
    }
}
