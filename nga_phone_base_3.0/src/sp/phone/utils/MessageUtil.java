package sp.phone.utils;

import java.util.ArrayList;
import java.util.List;

import sp.phone.adapter.MessageDetialAdapter;
import sp.phone.bean.ArticlePage;
import sp.phone.bean.MessageArticlePageInfo;
import sp.phone.bean.MessageDetialInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class MessageUtil {
	private final static String TAG = MessageUtil.class.getSimpleName();

	public static ArticlePage parserArticleList(String html) {

		/*
		 * AndFilter andFilter = new AndFilter(new TagNameFilter("table"), new
		 * HasAttributeFilter("class", "forumbox postbox")); AndFilter
		 * andFilter2 = new AndFilter(new TagNameFilter("span"), new
		 * HasAttributeFilter("class", "page_nav"));
		 * 
		 * AndFilter andFilter3 = new AndFilter(new TagNameFilter("div"), new
		 * HasAttributeFilter("id", "m_nav"));
		 * 
		 * OrFilter orFilter = new OrFilter();
		 * 
		 * orFilter.setPredicates(new NodeFilter[] { andFilter, andFilter2,
		 * andFilter3 });
		 * 
		 * if(html ==null || html.equals("")) return null; Parser myParser; try{
		 * myParser = new Parser(html); }catch(Exception e){
		 * Log.e(ArticleUtil.class.getSimpleName(),"fail to parse page " +
		 * Log.getStackTraceString(e)); Log.e(ArticleUtil.class.getSimpleName(),
		 * html); return null; } NodeList nodeList = myParser.parse(orFilter);
		 * 
		 * ArticlePage articlePage = new ArticlePage();
		 * 
		 * List<Article> listArticle = new ArrayList<Article>();
		 * 
		 * for (Node node : nodeList.toNodeArray()) { if (node instanceof
		 * TableTag) { TableTag table = (TableTag) node; TableRow[] rows =
		 * table.getRows(); if (rows.length == 1) { Article article = new
		 * Article(); User user = new User(); TableRow tr0 = (TableRow) rows[0];
		 * TableColumn[] tds0 = tr0.getColumns(); TableColumn td0 = tds0[0]; Div
		 * div0 = null; LinkTag lt = null; if( td0.getChild(1) instanceof Div) {
		 * div0 = (Div)td0.getChild(1); lt = (LinkTag) div0.getChild(1);
		 * }else{//XXX's reply continue; //lt = (LinkTag)td0.getChild(1); }
		 * String url = div0.getStringText(); url =
		 * url.substring(url.indexOf("<a id='pid") + "<a id='pid".length()); url
		 * = url.substring(0,url.indexOf("Anchor'></a>")); if(! "0".equals(url))
		 * url = "nga.178.com/read.php?pid="+url;
		 * 
		 * article.setUrl(url); //String floor =
		 * td0.getChild(1).getChildren().toNodeArray()[0].getText();
		 * 
		 * //floor = lt.getStringText(); String floor= lt.getChild(0).getText();
		 * floor = floor.substring(1, floor.length() - 3);
		 * article.setFloor(Integer.parseInt(floor)); LinkTag l = (LinkTag)
		 * div0.getChild(3); String nickName = l.getChild(1).getText();
		 * user.setNickName(nickName);
		 * 
		 * String userId = l.getLink().split("uid=")[1]; //
		 * System.out.println(userId); user.setUserId(userId);
		 * 
		 * TableColumn td1 = tds0[1]; String lastPostTime = "";
		 * 
		 * String content ="" ; Node td1Children[] =
		 * td1.getChildrenAsNodeArray(); for (Node node3 : td1Children) { if
		 * (node3 instanceof Span) { Span sss = (Span) node3;
		 * 
		 * if(sss.getAttribute("id") != null &&
		 * sss.getAttribute("id").startsWith("postcontent") ) { content =
		 * content + sss.getStringText();//sss.getFirstChild().getText();
		 * //break; } }else if( node3 instanceof HeadingTag){ HeadingTag ht =
		 * (HeadingTag)node3; String titleID = ht.getAttribute("id"); if(titleID
		 * != null && titleID.startsWith("postsubject") && ht.getChildCount()
		 * !=0){ if(!titleID.equals("postsubject0")){ String title
		 * =ht.getChild(0).getText() ;
		 * article.setTitle(StringUtil.unEscapeHtml(title)); }else{ String title
		 * =ht.getChild(2).getText() ;
		 * article.setTitle(StringUtil.unEscapeHtml(title)); } }
		 * 
		 * }else if ( node3 instanceof ImageTag){ ImageTag imgtag =
		 * (ImageTag)node3; String avatarStr = imgtag.getAttribute("onerror");
		 * String avatarImage = ""; if (avatarStr != null
		 * &&avatarStr.startsWith("commonui.postDisp") &&
		 * avatarStr.indexOf("http://") != -1) { avatarStr =
		 * avatarStr.substring(avatarStr.indexOf("http://")); avatarImage =
		 * avatarStr.substring(0,avatarStr.indexOf("\""));
		 * user.setAvatarImage(avatarImage); } }else if( node3 instanceof Div){
		 * Div div3 = (Div)node3; String postDate = div3.getStringText(); int
		 * start = postDate.indexOf("'postdate"); if( start != -1){ start +=
		 * "'postdate".length() ; start = postDate.indexOf('>', start) + 1; int
		 * end = postDate.indexOf("</span>", start); if (end == -1) end =
		 * postDate.length(); postDate = postDate.substring(start,end);
		 * article.setLastTime(postDate); } } }
		 * 
		 * article.setContent(StringUtil.unEscapeHtml(content));
		 * 
		 * 
		 * article.setUser(user); listArticle.add(article); } } else if (node
		 * instanceof Div) { Div div = (Div) node; Node[] links =
		 * div.getChild(1).getChildren() .toNodeArray(); LinkTag linkTag = null;
		 * for(Node linknode:links){ if(linknode instanceof LinkTag){ LinkTag
		 * tmp = (LinkTag) linknode; if(!tmp.getLink().equals("")){ linkTag =
		 * tmp; } }
		 * 
		 * } HashMap<String, String> current = new HashMap<String, String>();
		 * current.put("link", linkTag.getLink());
		 * current.put("title",StringUtil.unEscapeHtml(linkTag.getLinkText()));
		 * articlePage.setNow(current); } else if (node instanceof Span) { Span
		 * span = (Span) node; ArrayList<HashMap<String, String>> list = new
		 * ArrayList<HashMap<String, String>>();
		 * 
		 * HashMap<String, String> page = new HashMap<String, String>(); for
		 * (Node node2 : span.getChildren().toNodeArray()) { if (node2
		 * instanceof LinkTag) { LinkTag linkTag = (LinkTag) node2;
		 * 
		 * if (StringUtil.isNumer(linkTag.getLinkText())) { if
		 * ("b current".equals(linkTag .getAttribute("class"))) {
		 * page.put("current", linkTag.getLink()); page.put("num",
		 * linkTag.getLinkText()); } HashMap<String, String> hashMap = new
		 * HashMap<String, String>(); hashMap.put("link", linkTag.getLink());
		 * hashMap.put("num", linkTag.getLinkText()); list.add(hashMap); } else
		 * { if ("&lt;&lt;".equals(linkTag.getLinkText())) { page.put("first",
		 * linkTag.getLink()); } else if ("&lt;".equals(linkTag.getLinkText()))
		 * { page.put("prev", linkTag.getLink()); } else if
		 * ("&gt;".equals(linkTag.getLinkText())) { page.put("next",
		 * linkTag.getLink()); } else if
		 * ("&gt;&gt;".equals(linkTag.getLinkText())) { page.put("last",
		 * linkTag.getLink()); } } } } articlePage.setPage(page);
		 * articlePage.setList(list);
		 * 
		 * } } if(listArticle.size() == 0) articlePage = null; else
		 * articlePage.setListArticle(listArticle); return articlePage;
		 */
		return null;
	}

	private static Context context;

	@SuppressWarnings("static-access")
	public MessageUtil(Context context) {
		super();
		this.context = context;
	}

	public static boolean isInWifi() {
		ConnectivityManager conMan = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		return wifi == State.CONNECTED;
	}

	private boolean isShowImage() {
		return PhoneConfiguration.getInstance().isDownImgNoWifi() || isInWifi();
	}

	
	public MessageDetialInfo parseJsonThreadPage(String js,int page) {
		js = js.replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
		js = js.replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",");

		js = js.replaceAll("\"content\":(0\\d+),", "\"content\":\"$1\",");
		js = js.replaceAll("\"subject\":(0\\d+),", "\"subject\":\"$1\",");
		js = js.replaceAll("\"author\":(0\\d+),", "\"author\":\"$1\",");
		final String start = "\"__P\":{\"aid\":";
		final String end = "\"this_visit_rows\":";
		if (js.indexOf(start) != -1 && js.indexOf(end) != -1) {
			Log.w(TAG, "here comes an invalid response");
			String validJs = js.substring(0, js.indexOf(start));
			validJs += js.substring(js.indexOf(end));
			js = validJs;

		}
		JSONObject o = null;
		try {
			o = (JSONObject) JSON.parseObject(js).get("data");
		} catch (Exception e) {
			Log.e(TAG, "can not parse :\n" + js);
		}
		if (o == null)
			return null;

		MessageDetialInfo data = new MessageDetialInfo();

		JSONObject o1;
		o1 = (JSONObject) o.get("0");
		if (o1 == null)
			return null;

		JSONObject userInfoMap = (JSONObject) o1.get("userInfo");

		List<MessageArticlePageInfo> messageEntryList = convertJSobjToList(o1,  userInfoMap, page);
		if(messageEntryList==null)
			return null;
		data.setMessageEntryList(messageEntryList);
		data.set__currentPage(o1.getIntValue("currentPage"));
		data.set__nextPage(o1.getIntValue("nextPage"));
		String alluser=o1.getString("allUsers"),allusertmp="";
		alluser=alluser.replaceAll("	", " ");
		String alluserarray[]=alluser.split(" ");
		for(int i=1;i<alluserarray.length;i+=2){
			allusertmp+=alluserarray[i]+",";
		}
		if(allusertmp.length()>0)
			allusertmp=allusertmp.substring(0, allusertmp.length()-1);
		data.set_Alluser(allusertmp);
		if(data.getMessageEntryList().get(0)!=null){
			String title=data.getMessageEntryList().get(0).getSubject();
			if(!StringUtil.isEmpty(title)){
				data.set_Title(title);
			}else{
				data.set_Title("");
			}
		}
		return data;

	}
	
	private List<MessageArticlePageInfo> convertJSobjToList(JSONObject rowMap,
			JSONObject userInfoMap,int page) {
		List<MessageArticlePageInfo> __R = new ArrayList<MessageArticlePageInfo>();
		if (rowMap == null)
			return null;
		JSONObject rowObj = (JSONObject) rowMap.get("0");
		for (int i = 1; rowObj!=null; i++) {
			MessageArticlePageInfo row = new MessageArticlePageInfo();
				
				row.setContent(rowObj.getString("content"));
				row.setLou(20*(page-1)+i);
				row.setSubject(rowObj.getString("subject"));
				int time = rowObj.getIntValue("time");
				if(time>0){
					row.setTime(StringUtil.TimeStamp2Date(String.valueOf(time)));
				}else{
					row.setTime("");
				}
				row.setFrom(rowObj.getString("from"));
				fillUserInfo(row, userInfoMap);
				fillFormated_html_data(row, i);
				__R.add(row);
				rowObj=(JSONObject) rowMap.get(String.valueOf(i));
		}
		return __R;
	}

	private void fillUserInfo(MessageArticlePageInfo row, JSONObject userInfoMap) {
		JSONObject userInfo = (JSONObject) userInfoMap.get(row.getFrom());
		if (userInfo == null) {
			return;
		}
		
		row.setAuthor(userInfo.getString("username"));
		row.setJs_escap_avatar(userInfo.getString("avatar"));
		row.setYz(userInfo.getString("yz"));
		row.setMute_time(userInfo.getString("mute_time"));
		row.setSignature(userInfo.getString("signature"));
	}

	@SuppressWarnings("unused")
	private List<MessageArticlePageInfo> convertJSobjToList(JSONObject rowMap,
			JSONObject userInfoMap) {

		return convertJSobjToList(rowMap,  userInfoMap,1);
	}

	private void fillFormated_html_data(MessageArticlePageInfo row, int i) {

		ThemeManager theme = ThemeManager.getInstance();
		if (row.getContent() == null) {
			row.setContent(row.getSubject());
			row.setSubject(null);
		}
		int bgColor = context.getResources().getColor(
				theme.getBackgroundColor(i));
		int fgColor = context.getResources().getColor(
				theme.getForegroundColor());
		bgColor = bgColor & 0xffffff;
		final String bgcolorStr = String.format("%06x", bgColor);

		int htmlfgColor = fgColor & 0xffffff;
		final String fgColorStr = String.format("%06x", htmlfgColor);

		String formated_html_data = MessageDetialAdapter.convertToHtmlText(row,
				isShowImage(), showImageQuality(), fgColorStr, bgcolorStr);

		row.setFormated_html_data(formated_html_data);
	}

	public static int showImageQuality() {
		if (isInWifi()) {
			return 0;
		} else {
			return PhoneConfiguration.getInstance().imageQuality;
		}
	}

}
