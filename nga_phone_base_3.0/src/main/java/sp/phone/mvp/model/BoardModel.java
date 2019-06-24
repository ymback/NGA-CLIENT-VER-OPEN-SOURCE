package sp.phone.mvp.model;

import java.util.ArrayList;
import java.util.List;

import sp.phone.mvp.model.entity.BoardCategory;
import sp.phone.mvp.contract.BoardContract;

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
        return null;
    }

    @Override
    public void addBookmark(int fid, int stid, String boardName) {

    }

    @Override
    public void removeBookmark(int fid, int stid) {

    }

    @Override
    public void removeAllBookmarks() {

    }

    @Override
    public boolean isBookmark(int fid, int stid) {
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
