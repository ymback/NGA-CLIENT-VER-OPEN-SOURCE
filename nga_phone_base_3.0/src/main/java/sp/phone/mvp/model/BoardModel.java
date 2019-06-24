package sp.phone.mvp.model;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.base.util.PreferenceUtils;
import sp.phone.common.PreferenceKey;
import sp.phone.mvp.contract.BoardContract;
import sp.phone.mvp.model.entity.Board;
import sp.phone.mvp.model.entity.BoardCategory;

/**
 * Created by Justwen on 2019/6/23.
 */
public class BoardModel extends BaseModel implements BoardContract.Model {

    private List<BoardCategory> mBoardCategoryList = new ArrayList<>();

    private BoardCategory mBookmarkCategory;

    private BoardModel() {
        mBookmarkCategory = loadBookmarkBoards();
        mBoardCategoryList.add(mBookmarkCategory);
        mBoardCategoryList.add(loadPreloadBoards());
    }

    private BoardCategory loadPreloadBoards() {
        return null;

    }

    private BoardCategory loadBookmarkBoards() {
        BoardCategory category = new BoardCategory("我的收藏");
        List<Board> bookmarkBoards = PreferenceUtils.getData(PreferenceKey.BOOKMARK_BOARD, Board.class);
        category.addBoards(bookmarkBoards);
        return category;
    }

    @Override
    public void addBookmark(int fid, int stid, String boardName) {

    }

    @Override
    public void removeBookmark(int fid, int stid) {

    }

    @Override
    public void removeAllBookmarks() {
        mBookmarkCategory.removeAllBoards();
    }

    @Override
    public boolean isBookmark(int fid, int stid) {
        for (Board board : mBookmarkCategory.getBoardList()) {
            if (board.getFid() != 0 && board.getFid() == fid || board.getStid() != 0 && board.getStid() == stid) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void swapBookmark(int from, int to) {

    }

    @Override
    public int getCategorySize() {
        return mBoardCategoryList.size();
    }

    @Override
    public BoardCategory getBoardCategory(int index) {
        return mBoardCategoryList.get(index);
    }


    private static class SingletonHolder {
        private static BoardModel sInstance = new BoardModel();
    }

    public static BoardModel getInstance() {
        return SingletonHolder.sInstance;
    }

}
