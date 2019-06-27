package sp.phone.mvp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardCategory implements Parcelable {

    private List<Board> mBoardList;

    private Map<Board.BoardKey, Board> mBoardMap;

    private String mCategoryName;

    private boolean mIsBookmarkCategory;

    public BoardCategory(String name) {
        mBoardList = new ArrayList<>();
        mBoardMap = new HashMap<>();
        mCategoryName = name;
    }

    protected BoardCategory(Parcel in) {
        mBoardList = in.createTypedArrayList(Board.CREATOR);
        mCategoryName = in.readString();
        mIsBookmarkCategory = in.readByte() != 0;

        if (mBoardList == null) {
            mBoardList = new ArrayList<>();
        }

        mBoardMap = new HashMap<>();

        for (Board board : mBoardList) {
            mBoardMap.put(board.getBoardKey(), board);
        }
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
        return mBoardList;
    }

    @Deprecated
    public Board get(int index) {
        return getBoard(index);
    }

    public int size() {
        return mBoardList.size();
    }

    public void addBoards(List<Board> data) {
        mBoardList.addAll(data);
        for (Board board : data) {
            mBoardMap.put(board.getBoardKey(), board);
        }
    }

    public void addBoard(Board board) {
        mBoardList.add(board);
        mBoardMap.put(board.getBoardKey(), board);
    }


    public void removeAllBoards() {
        mBoardList.clear();
        mBoardMap.clear();
    }


    public void removeBoard(Board board) {
        mBoardList.remove(board);
        mBoardMap.remove(board.getBoardKey());
    }

    public void removeBoard(Board.BoardKey boardKey) {
        Board board = mBoardMap.get(boardKey);
        if (board != null) {
            mBoardList.remove(board);
            mBoardMap.remove(boardKey);
        }
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

    public Board getBoard(Board.BoardKey boardKey) {
        return mBoardMap.get(boardKey);
    }

    public boolean contains(Board board) {
        return mBoardList.contains(board);
    }

    public boolean contains(Board.BoardKey boardKey) {
        return mBoardMap.containsKey(boardKey);
    }

}
