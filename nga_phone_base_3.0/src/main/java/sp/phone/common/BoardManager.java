package sp.phone.common;

import android.content.Context;

import java.util.List;

import sp.phone.bean.BoardCategory;
import sp.phone.bean.BoardCategory;

public interface BoardManager {

    void initialize(Context context);

    List<BoardCategory> getCategoryList();

    int getCategorySize();

    BoardCategory getCategory(int index);

    void addBookmark(String fid, String name);

    void removeBookmark(String fid);

    void removeAllBookmarks();

    String getBoardName(String fid);

    boolean isBookmarkBoard(String fid);

    void swapBookmark(int from, int to);

    void removeBookmark(int index);

}
