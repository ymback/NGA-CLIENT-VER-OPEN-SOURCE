package sp.phone.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class BoardCategory implements Parcelable{

    private List<Board> mBoardList;

    public BoardCategory() {
        mBoardList = new ArrayList<>();
    }


    protected BoardCategory(Parcel in) {
        mBoardList = in.createTypedArrayList(Board.CREATOR);
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

    /**
     * @return the boardList
     */
    public List<Board> getBoardList() {
        return mBoardList;
    }


    /**
     * @param boardList the boardList to set
     */
    public void setBoardList(List<Board> boardList) {
        mBoardList = boardList;
    }


    public Board get(int index) {
        // TODO Auto-generated method stub
        return mBoardList.get(index);
    }

    public int size() {
        return mBoardList.size();
    }

    public void remove(String fid) {
        for (Board b : mBoardList) {
            if (b.getUrl().equals(fid)) {
                mBoardList.remove(b);
                break;
            }
        }

    }

    public void add(Board board) {
        mBoardList.add(board);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mBoardList);
    }
}
