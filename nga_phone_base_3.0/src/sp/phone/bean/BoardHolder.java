package sp.phone.bean;

import android.util.SparseArray;


public class BoardHolder {
	public static SparseArray<String> boardNameMap = new SparseArray<String>();
	private SparseArray<BoardCategory> boardInfo;
	private SparseArray<String> categoryName;
	
	/**
	 * @return the boardInfo
	 */
	public SparseArray<BoardCategory> getBoardInfo() {
		return boardInfo;
	}

	/**
	 * @param boardInfo the boardInfo to set
	 */
	public void setBoardInfo(SparseArray<BoardCategory> boardInfo) {
		this.boardInfo = boardInfo;
	}

	public BoardHolder(){
		boardInfo = new SparseArray<BoardCategory>();
		categoryName = new SparseArray<String>();
	}
	
	public void addCategoryName(int index, String name){
		categoryName.put(index, name);
	}
	
	public String getCategoryName(int index){
		return categoryName.get(index);
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
	
	public int getCategoryCount(){
		return categoryName.size();
	}
	
	public int size(int categoryid){
		BoardCategory boardCategory = (BoardCategory) boardInfo.get(categoryid);
		if(boardCategory == null)
			return 0;
		return boardCategory.size();
	}
	
	public BoardCategory getCategory(int index){
		
		return boardInfo.get(index);
	}
	
	public Board get(int category, int index){
		BoardCategory categoryList = (BoardCategory) boardInfo.get(category); 
		return categoryList == null? null:categoryList.get(index);
	}

	public void add(Board board) {
		add(board.getCategory(), board); 
		
		
	}
	
	public void add(int category, Board board) {
		if(boardInfo.get(category) == null)
			boardInfo.put(category, new  BoardCategory());
		
		((BoardCategory) boardInfo.get(category)).add(board);
		try{
			int fid = Integer.parseInt(board.getUrl());
			boardNameMap.put(fid, board.getName());
		}catch(Exception e){
			
		}

	}

	/*public void remove(String fid) {
		remove(0,fid);
		
	}*/

	public void remove(int category, String fid) {
		BoardCategory categoryList = (BoardCategory) boardInfo.get(category); 
		if(categoryList != null){
			
			categoryList.remove(fid);
			
		}
		
	}
	
	

}
