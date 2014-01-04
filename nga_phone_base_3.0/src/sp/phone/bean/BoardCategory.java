package sp.phone.bean;

import java.util.ArrayList;
import java.util.List;

public class BoardCategory{
	private List<Board> boardList;
	
	public BoardCategory(){
		boardList = new ArrayList<Board>();
	}
	
	

	/**
	 * @return the boardList
	 */
	public List<Board> getBoardList() {
		return boardList;
	}



	/**
	 * @param boardList the boardList to set
	 */
	public void setBoardList(List<Board> boardList) {
		this.boardList = boardList;
	}



	public Board get(int index) {
		// TODO Auto-generated method stub
		return (Board) boardList.get(index);
	}

	public int size() {
		return boardList.size();
	}

	public void remove(String fid) {
		for( Board b : ((List<Board>)boardList)){
			if(b.getUrl().equals(fid)){
				boardList.remove(b);
				break;
			}
		}
		
	}

	public void add(Board board) {
		boardList.add(board);
		
		
	}
	
	public void addFront(Board board) {
		boardList.add(0,board);
		
		
	}
	

}
