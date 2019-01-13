package sp.phone.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class BoardCategory implements Parcelable{

    private List<Board> mBoardList;

    private String mCategoryName;

    private int mCategoryIndex;

    public BoardCategory() {
        mBoardList = new ArrayList<>();
    }

    public BoardCategory(String name) {
        mBoardList = new ArrayList<>();
        mCategoryName = name;
    }

    protected BoardCategory(Parcel in) {
        mBoardList = in.createTypedArrayList(Board.CREATOR);
        mCategoryName = in.readString();
        mCategoryIndex = in.readInt();
    }

    public String getName() {
        return mCategoryName;
    }

    public static final Creator<BoardCategory> CREATOR = new Creator<BoardCategory>() {
        @Override
        public BoardCategory createFromParcel(Parcel in) {
            return new BoardCategory(in);
        }

        @Override
        public BoardCategory[] newArray(int size) {
            return new BoardCategory[size];
        }
    };

    public void setCategoryIndex(int index) {
        mCategoryIndex = index;
    }

    public int getCategoryIndex() {
        return mCategoryIndex;
    }


    public List<Board> getBoardList() {
        if (mBoardList == null) {
            mBoardList = new ArrayList<>();
        }
        return mBoardList;
    }


    public Board get(int index) {
        return mBoardList.get(index);
    }

    public int size() {
        return mBoardList.size();
    }

    public void remove(String fid) {
        for (Board board : mBoardList) {
            if (board.getUrl().equals(fid)) {
                mBoardList.remove(board);
                break;
            }
        }

    }

    public void remove(int index) {
        mBoardList.remove(index);
    }

    public void removeAll() {
        mBoardList.clear();
    }

    public void add(Board board) {
        board.setCategory(mCategoryIndex);
        mBoardList.add(board);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mBoardList);
        dest.writeString(mCategoryName);
        dest.writeInt(mCategoryIndex);
    }
}
