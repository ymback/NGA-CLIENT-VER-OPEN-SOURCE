package sp.phone.bean;

import android.util.SparseArray;


public class BoardHolder {
    public static SparseArray<String> boardNameMap = new SparseArray<>();

    private SparseArray<BoardCategory> mBoardInfo;

    private SparseArray<String> mCategoryName;

    public BoardHolder() {
        mBoardInfo = new SparseArray<>();
        mCategoryName = new SparseArray<>();
    }

    /**
     * @return the boardInfo
     */
    public SparseArray<BoardCategory> getBoardInfo() {
        return mBoardInfo;
    }

    /**
     * @param boardInfo the boardInfo to set
     */
    public void setBoardInfo(SparseArray<BoardCategory> boardInfo) {
        mBoardInfo = boardInfo;
    }

    public void addCategoryName(int index, String name) {
        mCategoryName.put(index, name);
        if (mBoardInfo.get(index) == null) {
            mBoardInfo.put(index, new BoardCategory());
        }
    }

    public String getCategoryName(int index) {
        return mCategoryName.get(index);
    }

	/*public void convertChildren(){
		Map<Integer,BoardCategory> newInfo = new HashMap<Integer,BoardCategory>();
		for( Object key : boardInfo.keySet()){
			Object v = boardInfo.get(key);
			if( v instanceof  JSONObject )
			{
				BoardCategory b = JSON.toJavaObject((JSONObject)v, BoardCategory.class);
				//boardInfo.remove(key);
				b.convert();
				Integer ki = Integer.parseInt((String)key);
				newInfo.put(ki, b);
			}else{
				newInfo.put((Integer)key, boardInfo.get(key));
			}
		}
		
		this.boardInfo = newInfo;
		
	}*/

    public int getCategoryCount() {
        return mCategoryName.size();
    }

    public int size(int categoryId) {
        BoardCategory boardCategory = mBoardInfo.get(categoryId);
        if (boardCategory == null)
            return 0;
        return boardCategory.size();
    }

    public BoardCategory getCategory(int index) {

        return mBoardInfo.get(index);
    }

    public Board get(int category, int index) {
        BoardCategory categoryList = mBoardInfo.get(category);
        return categoryList == null ? null : categoryList.get(index);
    }

    public void add(Board board) {
        add(board.getCategory(), board);


    }

    public void add(int category, Board board) {
        if (mBoardInfo.get(category) == null) {
            mBoardInfo.put(category, new BoardCategory());
        }

//        mBoardInfo.get(category).add(board);
//        try {
//            int fid = Integer.parseInt(board.getUrl());
//            boardNameMap.put(fid, board.getName());
//        } catch (Exception e) {
//
//        }

    }

	/*public void remove(String fid) {
		remove(0,fid);
		
	}*/

    public void remove(int category, String fid) {
        BoardCategory categoryList =  mBoardInfo.get(category);
        if (categoryList != null) {
            categoryList.remove(fid);
        }

    }


}
