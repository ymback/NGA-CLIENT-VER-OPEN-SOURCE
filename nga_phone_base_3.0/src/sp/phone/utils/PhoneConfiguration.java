package sp.phone.utils;

import gov.anzong.androidnga.activity.ArticleListActivity;
import gov.anzong.androidnga.activity.FlexibleMessageListActivity;
import gov.anzong.androidnga.activity.FlexibleNonameTopicListActivity;
import gov.anzong.androidnga.activity.FlexibleProfileActivity;
import gov.anzong.androidnga.activity.FlexibleTopicListActivity;
import gov.anzong.androidnga.activity.HaArticleListActivity;
import gov.anzong.androidnga.activity.HaFlexibleMessageListActivity;
import gov.anzong.androidnga.activity.HaFlexibleNonameTopicListActivity;
import gov.anzong.androidnga.activity.HaFlexibleProfileActivity;
import gov.anzong.androidnga.activity.HaFlexibleSignActivity;
import gov.anzong.androidnga.activity.HaFlexibleTopicListActivity;
import gov.anzong.androidnga.activity.HaMessageDetialActivity;
import gov.anzong.androidnga.activity.HaMessagePostActivity;
import gov.anzong.androidnga.activity.HaNonameArticleListActivity;
import gov.anzong.androidnga.activity.HaNonamePostActivity;
import gov.anzong.androidnga.activity.HaPostActivity;
import gov.anzong.androidnga.activity.HaSignPostActivity;
import gov.anzong.androidnga.activity.HaSplitArticleListActivity;
import gov.anzong.androidnga.activity.HaSplitFlexibleMessageListActivity;
import gov.anzong.androidnga.activity.HaSplitFlexibleNonameTopicListActivity;
import gov.anzong.androidnga.activity.HaSplitFlexibleProfileActivity;
import gov.anzong.androidnga.activity.HaSplitFlexibleSignActivity;
import gov.anzong.androidnga.activity.HaSplitFlexibleTopicListActivity;
import gov.anzong.androidnga.activity.HaSplitMessageDetialActivity;
import gov.anzong.androidnga.activity.HaSplitMessagePostActivity;
import gov.anzong.androidnga.activity.HaSplitNonameArticleListActivity;
import gov.anzong.androidnga.activity.HaSplitNonamePostActivity;
import gov.anzong.androidnga.activity.HaSplitPostActivity;
import gov.anzong.androidnga.activity.HaSplitSignPostActivity;
import gov.anzong.androidnga.activity.LoginActivity;
import gov.anzong.androidnga.activity.MeiziMainActivity;
import gov.anzong.androidnga.activity.MeiziTopicActivity;
import gov.anzong.androidnga.activity.MessageDetialActivity;
import gov.anzong.androidnga.activity.MessagePostActivity;
import gov.anzong.androidnga.activity.NonameArticleListActivity;
import gov.anzong.androidnga.activity.NonamePostActivity;
import gov.anzong.androidnga.activity.PostActivity;
import gov.anzong.androidnga.activity.RecentReplyListActivity;
import gov.anzong.androidnga.activity.SignPostActivity;
import gov.anzong.androidnga.activity.SplitArticleListActivity;
import gov.anzong.androidnga.activity.SplitFlexibleMessageListActivity;
import gov.anzong.androidnga.activity.SplitFlexibleNonameTopicListActivity;
import gov.anzong.androidnga.activity.SplitFlexibleProfileActivity;
import gov.anzong.androidnga.activity.SplitFlexibleSignActivity;
import gov.anzong.androidnga.activity.SplitFlexibleTopicListActivity;
import gov.anzong.androidnga.activity.SplitMeiziMainActivity;
import gov.anzong.androidnga.activity.SplitMeiziTopicActivity;
import gov.anzong.androidnga.activity.SplitMessageDetialActivity;
import gov.anzong.androidnga.activity.SplitMessagePostActivity;
import gov.anzong.androidnga.activity.SplitNonameArticleListActivity;
import gov.anzong.androidnga.activity.SplitNonamePostActivity;
import gov.anzong.androidnga.activity.SplitPostActivity;
import gov.anzong.androidnga.activity.FlexibleSignActivity;
import gov.anzong.androidnga.activity.SplitRecentReplyListActivity;
import gov.anzong.androidnga.activity.SplitSignPostActivity;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.location.Location;
import sp.phone.bean.Bookmark;
import sp.phone.bean.PerferenceConstant;

public class PhoneConfiguration implements PerferenceConstant{
	private static PhoneConfiguration instance;
	private boolean refreshAfterPost;
	List<Bookmark> bookmarks;// url<-->tilte
	private float textSize;
	private int webSize;
	public String userName;
	public int nikeWidth = 100;
	public boolean downAvatarNoWifi;
	public boolean downImgNoWifi;
	public boolean iconmode;
	public int imageQuality = 0; 	//0 = original, 1 = small, 2= medium, 3 = large
	public int playMode = 0; 	//0 = all, 1 = notacfun, 2=notbili, 3=notacbili, 4=none
	public int HandSide = 0; 	//0 = right, 1 = left
	public int blackgunsound = 0; 	//0 = right, 1 = left
	public boolean notification;
	public boolean notificationSound;
	public long lastMessageCheck = 0;
	public String cid;
	public String uid;
	public boolean showAnimation=false;
	public boolean showSignature = true;
	public boolean useViewCache;
	public Location location = null;
	public boolean uploadLocation = false;
    public boolean showStatic = false;
    public boolean showReplyButton = true;
    public boolean showColortxt = false;
    public boolean showNewweiba  = false;
    public boolean showLajibankuai  = true;
	private int uiFlag = 0;
    public boolean fullscreen  = false;
    public boolean kitwebview =false;
    public int replytotalnum = 0;
    

	public String db_cookie;
	

	public String getDb_Cookie() {
		return db_cookie;
	}
	public void setDb_Cookie(String db_cookie) {
		this.db_cookie = db_cookie;
	}
	
	

	public Class<?> topicActivityClass = FlexibleTopicListActivity.class;
	public Class<?> articleActivityClass = ArticleListActivity.class;
	public Class<?> nonameArticleActivityClass = NonameArticleListActivity.class;
	public Class<?> postActivityClass = PostActivity.class;
	public Class<?> nonamePostActivityClass = NonamePostActivity.class;
	public Class<?> messagePostActivityClass = MessagePostActivity.class;
	public Class<?> signPostActivityClass = SignPostActivity.class;
	public Class<?> signActivityClass = FlexibleSignActivity.class;
	public Class<?> profileActivityClass = FlexibleProfileActivity.class;
	public Class<?> loginActivityClass = LoginActivity.class;
	public Class<?> recentReplyListActivityClass = RecentReplyListActivity.class;
	public Class<?> MeiziMainActivityClass = MeiziMainActivity.class;
	public Class<?> MeiziTopicActivityClass = MeiziTopicActivity.class;
	public Class<?> messageActivityClass = FlexibleMessageListActivity.class;
	public Class<?> nonameActivityClass = FlexibleNonameTopicListActivity.class;
	public Class<?> messageDetialActivity = MessageDetialActivity.class;
	public String replyString;
	
	
	public int getNikeWidth() {
		return nikeWidth;
	}
	public void setNikeWidth(int nikeWidth) {
		this.nikeWidth = nikeWidth;
	}
	public boolean isDownAvatarNoWifi() {
		return downAvatarNoWifi;
	}
	public void setDownAvatarNoWifi(boolean downAvatarNoWifi) {
		this.downAvatarNoWifi = downAvatarNoWifi;
	}
	public boolean isDownImgNoWifi() {
		return downImgNoWifi;
	}
	public void setDownImgNoWifi(boolean downImgNoWifi) {
		this.downImgNoWifi = downImgNoWifi;
	}
	public boolean isNotification() {
		return notification;
	}
	public void setNotification(boolean notification) {
		this.notification = notification;
	}
	public boolean isNotificationSound() {
		return notificationSound;
	}
	public void setNotificationSound(boolean notificationSound) {
		this.notificationSound = notificationSound;
	}
	public long getLastMessageCheck() {
		return lastMessageCheck;
	}
	public void setLastMessageCheck(long lastMessageCheck) {
		this.lastMessageCheck = lastMessageCheck;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public void setNickname(String userName) {
		this.userName = userName;
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
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public float getTextSize() {
		return textSize;
	}
	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}
	public int getWebSize() {
		return webSize;
	}
	public void setWebSize(int webSize) {
		this.webSize = webSize;
	}
	private PhoneConfiguration(){

		bookmarks= new ArrayList<Bookmark>();

		
	}
	public boolean isRefreshAfterPost() {
		return refreshAfterPost;
	}
	public void setRefreshAfterPost(boolean refreshAfterPost) {
		this.refreshAfterPost = refreshAfterPost;
	}
	public static PhoneConfiguration getInstance(){
		if(instance ==null){
			instance = new PhoneConfiguration();
		}
		return instance;
	}
	public List<Bookmark> getBookmarks() {
		return bookmarks;
	}
	
	public void setBookmarks(List<Bookmark> bookmarks) {
		this.bookmarks = bookmarks;
	}
	
	public String getCookie(){
		if( !StringUtil.isEmpty(uid) && !StringUtil.isEmpty(cid)){
			return "ngaPassportUid="+ uid+
					"; ngaPassportCid=" + cid;
		}
		return "";
	}
	
	public boolean addBookmark(String url,String title){
		boolean ret = true;
		for(Bookmark b:bookmarks){
			if(b.getUrl().equals(url))
				return false;
			
		}
		Bookmark newBookmark = new Bookmark();
		newBookmark.setTitle(title);
		newBookmark.setUrl(url);
		bookmarks.add(newBookmark);
		return ret;
	}
	
	public boolean removeBookmark(String url){

		for(Bookmark b:bookmarks){
			if(b.getUrl().equals(url)){
				bookmarks.remove(b);
				return true;
			}
		}
		return false;
		
	}
	

	public int getUiFlag() {
		return uiFlag;
	}

	public void setUiFlag(int uiFlag) {
		this.uiFlag = uiFlag;
		switch (uiFlag) {
		
		case UI_FLAG_SPLIT:/*�������˵����·��������º�������split����������ͨ��*/
			topicActivityClass = SplitFlexibleTopicListActivity.class;
			messageActivityClass = SplitFlexibleMessageListActivity.class;
			nonameActivityClass = SplitFlexibleNonameTopicListActivity.class;
			articleActivityClass = SplitArticleListActivity.class;
			nonameArticleActivityClass = SplitNonameArticleListActivity.class;
			messageDetialActivity = SplitMessageDetialActivity.class;
			postActivityClass = PostActivity.class;
			signPostActivityClass = SignPostActivity.class;
			nonamePostActivityClass = NonamePostActivity.class;
			messagePostActivityClass = MessagePostActivity.class;
			signActivityClass = SplitFlexibleSignActivity.class;//
			profileActivityClass = SplitFlexibleProfileActivity.class;
			recentReplyListActivityClass =SplitRecentReplyListActivity.class;
			MeiziMainActivityClass = SplitMeiziMainActivity.class;
			MeiziTopicActivityClass = SplitMeiziTopicActivity.class;
			break;
		case UI_FLAG_HA:/*������Ӳ�����٣�ȫ����Ҫ���٣����Է���ҲҪӲ�����ٵ�*/
			topicActivityClass = HaFlexibleTopicListActivity.class;
			articleActivityClass = HaArticleListActivity.class;
			nonameArticleActivityClass = HaNonameArticleListActivity.class;
			messageDetialActivity = HaMessageDetialActivity.class;
			messageActivityClass = HaFlexibleMessageListActivity.class;
			nonameActivityClass = HaFlexibleNonameTopicListActivity.class;
			postActivityClass = HaPostActivity.class;
			signPostActivityClass = HaSignPostActivity.class;
			nonamePostActivityClass = HaNonamePostActivity.class;
			messagePostActivityClass = HaMessagePostActivity.class;
			signActivityClass = HaFlexibleSignActivity.class;//OK
			profileActivityClass = HaFlexibleProfileActivity.class;
			recentReplyListActivityClass = RecentReplyListActivity.class;
			MeiziMainActivityClass = MeiziMainActivity.class;
			MeiziTopicActivityClass = MeiziTopicActivity.class;
			break;
		case UI_FLAG_REPLYSPLIT:/*�������������棬��˵���Ӳ�����ٶ�����ͨ�ģ�����Split�汾��*/
			topicActivityClass = FlexibleTopicListActivity.class;
			messageActivityClass = FlexibleMessageListActivity.class;
			nonameActivityClass = FlexibleNonameTopicListActivity.class;
			articleActivityClass = ArticleListActivity.class;
			nonameArticleActivityClass = NonameArticleListActivity.class;
			messageDetialActivity = MessageDetialActivity.class;
			postActivityClass = SplitPostActivity.class;
			signPostActivityClass = SplitSignPostActivity.class;
			nonamePostActivityClass = SplitNonamePostActivity.class;
			messagePostActivityClass = SplitMessagePostActivity.class;
			signActivityClass = FlexibleSignActivity.class;//
			profileActivityClass = FlexibleProfileActivity.class;
			recentReplyListActivityClass = RecentReplyListActivity.class;
			MeiziMainActivityClass = MeiziMainActivity.class;
			MeiziTopicActivityClass = MeiziTopicActivity.class;
			break;
		case (UI_FLAG_SPLIT + UI_FLAG_HA):/*����Ӳ�����ٺ���ͨ�˵������棬����ͨ��Ҫ����ͼ��٣�������Ҫ��ͨ�ͼ��٣��˵�ֻҪ����*/
			topicActivityClass = HaSplitFlexibleTopicListActivity.class;
			messageActivityClass = HaSplitFlexibleMessageListActivity.class;
			nonameActivityClass = HaSplitFlexibleNonameTopicListActivity.class;
			articleActivityClass = HaSplitArticleListActivity.class;
			nonameArticleActivityClass = HaSplitNonameArticleListActivity.class;
			messageDetialActivity = HaSplitMessageDetialActivity.class;
			postActivityClass = HaPostActivity.class;
			signPostActivityClass = HaSignPostActivity.class;
			nonamePostActivityClass = HaNonamePostActivity.class;
			messagePostActivityClass = HaMessagePostActivity.class;
			signActivityClass = HaSplitFlexibleSignActivity.class;//
			profileActivityClass = HaSplitFlexibleProfileActivity.class;
			recentReplyListActivityClass =SplitRecentReplyListActivity.class;
			MeiziMainActivityClass = SplitMeiziMainActivity.class;
			MeiziTopicActivityClass = SplitMeiziTopicActivity.class;
			break;
		case (UI_FLAG_SPLIT + UI_FLAG_REPLYSPLIT):/*����2�����棬���������split����ha*/
			topicActivityClass = SplitFlexibleTopicListActivity.class;
			messageActivityClass = SplitFlexibleMessageListActivity.class;
			nonameActivityClass = SplitFlexibleNonameTopicListActivity.class;
			articleActivityClass = SplitArticleListActivity.class;
			nonameArticleActivityClass = SplitNonameArticleListActivity.class;
			messageDetialActivity = SplitMessageDetialActivity.class;
			postActivityClass = SplitPostActivity.class;
			signPostActivityClass = SplitSignPostActivity.class;
			nonamePostActivityClass = SplitNonamePostActivity.class;
			messagePostActivityClass = SplitMessagePostActivity.class;
			signActivityClass = SplitFlexibleSignActivity.class;//
			profileActivityClass = SplitFlexibleProfileActivity.class;
			recentReplyListActivityClass =SplitRecentReplyListActivity.class;
			MeiziMainActivityClass = SplitMeiziMainActivity.class;
			MeiziTopicActivityClass = SplitMeiziTopicActivity.class;
			break;
		case (UI_FLAG_HA + UI_FLAG_REPLYSPLIT):/*�������ٺ����棬����ֻͨҪ���٣�����ҲֻҪ���٣��˵���Ҫ���ٺ�split*/
			topicActivityClass = HaFlexibleTopicListActivity.class;
			messageActivityClass = HaFlexibleMessageListActivity.class;
			nonameActivityClass = HaFlexibleNonameTopicListActivity.class;
			articleActivityClass = HaArticleListActivity.class;
			nonameArticleActivityClass = HaNonameArticleListActivity.class;
			messageDetialActivity = HaMessageDetialActivity.class;
			postActivityClass = HaSplitPostActivity.class;
			signPostActivityClass = HaSplitSignPostActivity.class;
			nonamePostActivityClass = HaSplitNonamePostActivity.class;
			messagePostActivityClass = HaMessagePostActivity.class;
			signActivityClass = HaFlexibleSignActivity.class;//
			profileActivityClass = HaFlexibleProfileActivity.class;
			recentReplyListActivityClass = RecentReplyListActivity.class;
			MeiziMainActivityClass = MeiziMainActivity.class;
			MeiziTopicActivityClass = MeiziTopicActivity.class;
			break;
		case (UI_FLAG_SPLIT + UI_FLAG_HA + UI_FLAG_REPLYSPLIT):/*ȫ����ȫ��*/
			topicActivityClass = HaSplitFlexibleTopicListActivity.class;
			messageActivityClass = HaSplitFlexibleMessageListActivity.class;
			nonameActivityClass = HaSplitFlexibleNonameTopicListActivity.class;
			articleActivityClass = HaSplitArticleListActivity.class;
			nonameArticleActivityClass = HaSplitNonameArticleListActivity.class;
			messageDetialActivity = HaSplitMessageDetialActivity.class;
			postActivityClass = HaSplitPostActivity.class;
			signPostActivityClass = HaSplitSignPostActivity.class;
			nonamePostActivityClass = HaSplitNonamePostActivity.class;
			messagePostActivityClass = HaSplitMessagePostActivity.class;
			signActivityClass = HaSplitFlexibleSignActivity.class;//
			profileActivityClass = HaSplitFlexibleProfileActivity.class;
			recentReplyListActivityClass =SplitRecentReplyListActivity.class;
			MeiziMainActivityClass = SplitMeiziMainActivity.class;
			MeiziTopicActivityClass = SplitMeiziTopicActivity.class;
			break;
		case 0:
		default:
			topicActivityClass = FlexibleTopicListActivity.class;
			articleActivityClass = ArticleListActivity.class;
			nonameArticleActivityClass = NonameArticleListActivity.class;
			messageDetialActivity = MessageDetialActivity.class;
			postActivityClass = PostActivity.class;
			signPostActivityClass = SignPostActivity.class;
			messagePostActivityClass = MessagePostActivity.class;
			signActivityClass = FlexibleSignActivity.class;
			profileActivityClass = FlexibleProfileActivity.class;
			recentReplyListActivityClass = RecentReplyListActivity.class;
			MeiziMainActivityClass = MeiziMainActivity.class;
			MeiziTopicActivityClass = MeiziTopicActivity.class;
			messageActivityClass = FlexibleMessageListActivity.class;
			nonameActivityClass = FlexibleNonameTopicListActivity.class;
			nonamePostActivityClass = NonamePostActivity.class;
		}
	}

}

