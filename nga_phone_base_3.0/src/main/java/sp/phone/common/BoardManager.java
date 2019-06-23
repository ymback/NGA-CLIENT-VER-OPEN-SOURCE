package sp.phone.common;

import java.util.List;

import sp.phone.bean.Board;
import sp.phone.bean.BoardCategory;

public interface BoardManager {

    List<BoardCategory> getCategoryList();

    int getCategorySize();

    BoardCategory getCategory(int index);

    void addBookmark(String fid, String name);

    void removeBookmark(String fid);

    void removeAllBookmarks();

    String getBoardName(String fid);

    @Deprecated
    boolean isBookmarkBoard(String fid);

    @Deprecated
    boolean isBookmarkBoard(int stid);

    boolean isBookmarkBoard(Board board);

    void swapBookmark(int from, int to);

    void removeBookmark(int index);

}
