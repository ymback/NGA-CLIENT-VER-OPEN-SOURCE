package sp.phone.common;

import android.content.Context;

import java.util.List;

import sp.phone.bean.BoardCategory;

/**
 * Created by Yang Yihang on 2017/7/25.
 */

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
