package sp.phone.bean;

import java.util.HashSet;
import java.util.Set;

public class User {

	private String userId;
	private String nickName;
	private String cid;
	private String replyString;
	private int replytotalnum;
	private String blacklist;
	public String getBlackList() {
		return blacklist;
	}
	public void setBlackList(String blacklist) {
		this.blacklist = blacklist;
	}
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
