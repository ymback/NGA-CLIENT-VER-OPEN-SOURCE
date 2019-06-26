package sp.phone.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class BoardCategory implements Parcelable {

    private List<Board> mBoardList;

    private String mCategoryName;

    private boolean mIsBookmarkCategory;

    public BoardCategory(String name) {
        mBoardList = new ArrayList<>();
        mCategoryName = name;
    }

    protected BoardCategory(Parcel in) {
        mBoardList = in.createTypedArrayList(Board.CREATOR);
        mCategoryName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mBoardList);
        dest.writeString(mCategoryName);
        dest.writeByte((byte) (mIsBookmarkCategory ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getName() {
        return mCategoryName;
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

    @Deprecated
    public void remove(int index) {
        mBoardList.remove(index);
    }

    @Deprecated
    public void removeAll() {
        mBoardList.clear();
    }

    @Deprecated
    public void add(Board board) {
        mBoardList.add(board);
    }

    public void addBoards(List<Board> data) {
        mBoardList.addAll(data);
    }

    public void removeAllBoards() {
        mBoardList.clear();
    }

    public void addBoard(Board board) {
        mBoardList.add(board);
    }

    public void removeBoard(Board board) {
        mBoardList.remove(board);
    }

    public boolean isBookmarkCategory() {
        return mIsBookmarkCategory;
    }

    public void setBookmarkCategory(boolean bookmarkCategory) {
        mIsBookmarkCategory = bookmarkCategory;
    }

    public Board getBoard(int index) {
        return mBoardList.get(index);
    }

}
