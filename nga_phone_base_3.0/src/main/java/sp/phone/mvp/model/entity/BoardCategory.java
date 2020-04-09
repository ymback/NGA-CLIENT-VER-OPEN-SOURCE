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

    private List<BoardCategory> mSubCategoryList;

    public BoardCategory(String name) {
        mBoardList = new ArrayList<>();
        mBoardMap = new HashMap<>();
        mCategoryName = name;
    }

    public void addSubCategory(BoardCategory category) {
        if (mSubCategoryList == null) {
            mSubCategoryList = new ArrayList<>();
        }
        mSubCategoryList.add(category);
    }

    public List<BoardCategory> getSubCategoryList() {
        return mSubCategoryList;
    }

    public BoardCategory getSubCategory(int index) {
        return mSubCategoryList.get(index);
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
        mSubCategoryList = in.createTypedArrayList(BoardCategory.CREATOR);

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mBoardList);
        dest.writeString(mCategoryName);
        dest.writeByte((byte) (mIsBookmarkCategory ? 1 : 0));
        dest.writeTypedList(mSubCategoryList);
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

    public boolean removeBoard(Board.BoardKey boardKey) {
        Board board = mBoardMap.remove(boardKey);
        if (board != null) {
            return mBoardList.remove(board);
        } else {
            return false;
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
