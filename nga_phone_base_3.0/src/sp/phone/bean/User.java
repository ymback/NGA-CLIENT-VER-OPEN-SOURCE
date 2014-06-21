package sp.phone.bean;

public class User {

	private String userId;
	private String nickName;
	private String cid;
	private String replyString;
	private int replytotalnum;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public void setReplyTotalNum(int replytotalnum) {
		this.replytotalnum = replytotalnum;
	}
	public int getReplyTotalNum() {
		return replytotalnum;
	}
	public void setReplyString(String replyString) {
		this.replyString = replyString;
	}
	public String getReplyString() {
		return replyString;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}


	

}
