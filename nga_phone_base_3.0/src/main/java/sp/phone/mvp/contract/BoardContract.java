package sp.phone.mvp.contract;

import java.util.List;

import sp.phone.common.User;
import sp.phone.mvp.model.entity.Board;
import sp.phone.mvp.model.entity.BoardCategory;

/**
 * Created by Justwen on 2017/6/29.
 */

public interface BoardContract {

    interface Presenter {

        void loadBoardInfo();

        boolean addBoard(String fid, String name, String stid);

        void toggleUser(List<User> userList);

        void toTopicListPage(int position, String fidString);

        void notifyDataSetChanged();

        void clearRecentBoards();

        void startUserProfile(String userName);

        void startLogin();



        BoardCategory getBookmarkCategory();

        List<BoardCategory> getBoardCategories();

        void clearAllBookmarkBoards();

        void swapBookmarkBoard(int from, int to);

        void addBookmarkBoard(int fid, int stid, String name);

        void showTopicList(Board board);

        void showTopicList(int fid, int stid, String boardName);

        void showTopicList(String url);

        void showTopicContent(String url);

    }

    interface View {

        int switchToNextUser();

        void jumpToLogin();

        void updateHeaderView();

        void notifyDataSetChanged();

        int getCurrentItem();
    }

    interface Model {

        void addBookmark(int fid, int stid, String boardName);

        void addBookmark(Board.BoardKey boardKey, String boardName);

        void removeBookmark(int fid, int stid);

        void removeAllBookmarks();

        boolean isBookmark(int fid, int stid);

        void swapBookmark(int from, int to);

        int getCategorySize();

        BoardCategory getBoardCategory(int index);

        List<BoardCategory> getBoardCategories();

        String getBoardName(Board.BoardKey boardKey);

        String getBoardName(int fid, int stid);

        BoardCategory getBookmarkCategory();
    }
}
