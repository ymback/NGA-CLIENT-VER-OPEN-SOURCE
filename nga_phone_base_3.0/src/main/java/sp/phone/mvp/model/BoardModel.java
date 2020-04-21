package sp.phone.mvp.model;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gov.anzong.androidnga.NgaClientApp;
import gov.anzong.androidnga.base.util.PreferenceUtils;
import gov.anzong.androidnga.common.PreferenceKey;
import sp.phone.http.bean.CategoryBean;
import sp.phone.mvp.contract.BoardContract;
import sp.phone.mvp.model.entity.Board;
import sp.phone.mvp.model.entity.BoardCategory;
import sp.phone.util.StringUtils;

/**
 * Created by Justwen on 2019/6/23.
 */
public class BoardModel extends BaseModel implements BoardContract.Model {

    private List<BoardCategory> mBoardCategoryList = new ArrayList<>();

    private BoardCategory mBookmarkCategory;

    private BoardModel() {
        mBookmarkCategory = loadBookmarkBoards();
        mBoardCategoryList.add(mBookmarkCategory);
        mBoardCategoryList.addAll(loadPreloadBoards());
    }

    private List<BoardCategory> loadPreloadBoards() {
        String categoryJson = StringUtils.getStringFromAssets("json/category.json");
        List<CategoryBean> beans = JSON.parseArray(categoryJson, CategoryBean.class);
        List<BoardCategory> categories = new ArrayList<>();

        for (CategoryBean categoryBean : beans) {
            BoardCategory category = new BoardCategory(categoryBean.getName());
            for (CategoryBean.SubBean subBean : categoryBean.getSub()) {
                BoardCategory subCategory = new BoardCategory(subBean.getName());
                for (CategoryBean.SubBean.ContentBean contentBean : subBean.getContent()) {
                    String boardName;
                    if (TextUtils.isEmpty(contentBean.getNameS())) {
                        boardName = contentBean.getName();
                    } else {
                        boardName = contentBean.getNameS();
                    }

                    Board board = new Board(contentBean.getFid(), contentBean.getStid(), boardName);
                    board.setBoardHead(contentBean.getHead());
                    subCategory.addBoard(board);

                }
                category.addSubCategory(subCategory);
            }
            categories.add(category);
        }
        upgradeBookmarkBoard(categories);
        return categories;
    }

    private void upgradeBookmarkBoard(List<BoardCategory> preloadCategory) {
        if (!NgaClientApp.isNewVersion()) {
            return;
        }
        int previousVersionCode = PreferenceUtils.getData(PreferenceKey.PREVIOUS_VERSION_CODE, Integer.MAX_VALUE);

        if (previousVersionCode < 3033) {
            List<Board> allPreloadBoards = new ArrayList<>();
            for (BoardCategory category : preloadCategory) {
                if (category.getSubCategoryList() != null && !category.getSubCategoryList().isEmpty()) {
                    for (BoardCategory subCategory : category.getSubCategoryList()) {
                        allPreloadBoards.addAll(subCategory.getBoardList());
                    }
                } else {
                    allPreloadBoards.addAll(category.getBoardList());
                }
            }
            for (Board board : allPreloadBoards) {
                for (Board bookmark : mBookmarkCategory.getBoardList()) {
                    if (bookmark.getFid() != 0 && bookmark.getFid() == board.getFid()) {
                        bookmark.setBoardHead(board.getBoardHead());
                    }
                    if (bookmark.getStid() != 0 && bookmark.getStid() == board.getStid()) {
                        bookmark.setFid(board.getFid());
                    }
                }
            }
            saveBookmark();
        }
    }


    private BoardCategory loadBookmarkBoards() {
        BoardCategory category = new BoardCategory("我的收藏");
        List<Board> bookmarkBoards = PreferenceUtils.getData(PreferenceKey.BOOKMARK_BOARD, Board.class);
        for (Board board : bookmarkBoards) {
            board.fixBoardKey();
        }
        category.addBoards(bookmarkBoards);
        category.setBookmarkCategory(true);
        return category;
    }

    @Override
    public void addBookmark(Board board) {
        if (!mBookmarkCategory.contains(board)) {
            mBookmarkCategory.addBoard(board);
            saveBookmark();
        }
    }

    @Override
    public void addBookmark(int fid, int stid, String boardName) {
        addBookmark(new Board.BoardKey(fid, stid), boardName);
    }

    @Override
    public void addBookmark(Board.BoardKey boardKey, String boardName) {
        if (!mBookmarkCategory.contains(boardKey)) {
            mBookmarkCategory.addBoard(new Board(boardKey, boardName));
            saveBookmark();
        }
    }

    @Override
    public void removeBookmark(int fid, int stid) {
        if (mBookmarkCategory.removeBoard(new Board.BoardKey(fid, stid))) {
            saveBookmark();
        }
    }

    @Override
    public void removeAllBookmarks() {
        mBookmarkCategory.removeAllBoards();
        saveBookmark();
    }

    @Override
    public boolean isBookmark(int fid, int stid) {
        return mBookmarkCategory.contains(new Board.BoardKey(fid, stid));
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
    public String getBoardName(Board.BoardKey boardKey) {
        for (BoardCategory boardCategory : mBoardCategoryList) {
            Board board = boardCategory.getBoard(boardKey);
            if (board != null) {
                return board.getName();
            }
        }
        return null;
    }

    @Override
    public String getBoardName(int fid, int stid) {
        return getBoardName(new Board.BoardKey(fid, stid));
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
