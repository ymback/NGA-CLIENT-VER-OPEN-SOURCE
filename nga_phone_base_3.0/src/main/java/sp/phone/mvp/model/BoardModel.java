package sp.phone.mvp.model;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.anzong.androidnga.base.util.PreferenceUtils;
import sp.phone.common.PreferenceKey;
import sp.phone.http.bean.BoardBean;
import sp.phone.mvp.contract.BoardContract;
import sp.phone.mvp.model.entity.Board;
import sp.phone.mvp.model.entity.BoardCategory;
import sp.phone.util.StringUtils;

/**
 * Created by Justwen on 2019/6/23.
 */
public class BoardModel extends BaseModel implements BoardContract.Model {

    private List<BoardCategory> mBoardCategoryList = new ArrayList<>();

    private Map<Board.BoardKey, Board> mBoardMap = new HashMap<>();

    private BoardCategory mBookmarkCategory;

    private BoardModel() {
        mBookmarkCategory = loadBookmarkBoards();
        mBoardCategoryList.add(mBookmarkCategory);
        mBoardCategoryList.addAll(loadPreloadBoards());
    }

    private List<BoardCategory> loadPreloadBoards() {
        String categoryJson = StringUtils.getStringFromAssets("json/category_old.json");
        List<BoardBean> beans = JSON.parseArray(categoryJson, BoardBean.class);
        List<BoardCategory> categories = new ArrayList<>();
        for (BoardBean categoryBean : beans) {

            BoardCategory category = new BoardCategory(categoryBean.name);
            for (BoardBean.ContentBean contentBean : categoryBean.content) {
                if (TextUtils.isEmpty(contentBean.nameS)) {
                    category.addBoard(new Board(contentBean.fid, contentBean.stid, contentBean.name));
                } else {
                    category.addBoard(new Board(contentBean.fid, contentBean.stid, contentBean.nameS));
                }
            }
            categories.add(category);
        }
        return categories;
    }

    private BoardCategory loadBookmarkBoards() {
        BoardCategory category = new BoardCategory("我的收藏");
        List<Board> bookmarkBoards = PreferenceUtils.getData(PreferenceKey.BOOKMARK_BOARD, Board.class);
        category.addBoards(bookmarkBoards);
        category.setBookmarkCategory(true);
        return category;
    }

    @Override
    public void addBookmark(int fid, int stid, String boardName) {
        if (!isBookmark(fid, stid)) {
            mBookmarkCategory.addBoard(new Board(fid, stid, boardName));
        }
    }

    @Override
    public void removeBookmark(int fid, int stid) {
        for (Board board : mBookmarkCategory.getBoardList()) {
            if (board.getFid() != 0 && board.getFid() == fid || board.getStid() != 0 && board.getStid() == stid) {
                mBookmarkCategory.removeBoard(board);
                return;
            }
        }
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
        List<Board> boards = mBookmarkCategory.getBoardList();
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(boards, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(boards, i, i - 1);
            }
        }
        saveBookmark();
    }

    private void saveBookmark() {
        PreferenceUtils.putData(PreferenceKey.BOOKMARK_BOARD, JSON.toJSONString(mBookmarkCategory.getBoardList()));
    }

    @Override
    public int getCategorySize() {
        return mBoardCategoryList.size();
    }

    @Override
    public BoardCategory getBoardCategory(int index) {
        return mBoardCategoryList.get(index);
    }

    @Override
    public List<BoardCategory> getBoardCategories() {
        return mBoardCategoryList;
    }

    @Override
    public String getBoardName(int fid, int stid) {
        for (BoardCategory category : mBoardCategoryList) {
            for (Board board : category.getBoardList()) {
                if (board.getFid() != 0 && board.getFid() == fid || board.getStid() != 0 && board.getStid() == stid) {
                    return board.getName();
                }
            }
        }
        return null;
    }

    @Override
    public BoardCategory getBookmarkCategory() {
        return mBookmarkCategory;
    }

    private static class SingletonHolder {
        private static BoardModel sInstance = new BoardModel();
    }

    public static BoardModel getInstance() {
        return SingletonHolder.sInstance;
    }

}
