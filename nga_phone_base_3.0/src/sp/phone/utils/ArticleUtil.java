package sp.phone.utils;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gov.anzong.androidnga.util.NetUtil;
import sp.phone.bean.ArticlePage;
import sp.phone.bean.ThreadData;
import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.ThreadRowInfo;

public class ArticleUtil {
    private final static String TAG = ArticleUtil.class.getSimpleName();
    private Context context;

    @SuppressWarnings("static-access")
    public ArticleUtil(Context context) {
        super();
        this.context = context;
    }

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

    public static int showImageQuality() {
        if (NetUtil.getInstance().isInWifi()) {
            return 0;
        } else {
            return PhoneConfiguration.getInstance().imageQuality;
        }
    }

    private boolean isShowImage() {
        return PhoneConfiguration.getInstance().isDownImgNoWifi() || NetUtil.getInstance().isInWifi();
    }

    private boolean isShowAvatar() {
        return PhoneConfiguration.getInstance().isDownAvatarNoWifi() || NetUtil.getInstance().isInWifi();
    }

    public ThreadData parseJsonThreadPage(String js) {
        js = js.replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
        js = js.replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",");

        js = js.replaceAll("\"content\":(0\\d+),", "\"content\":\"$1\",");
        js = js.replaceAll("\"subject\":(0\\d+),", "\"subject\":\"$1\",");
        js = js.replaceAll("\"author\":(0\\d+),", "\"author\":\"$1\",");
        js = js.replaceAll("\"alterinfo\":\"\\[(\\w|\\s)+\\]\\s+\",", ""); //部分页面打不开的问题

        JSONObject o = null;
        try {
            o = (JSONObject) JSON.parseObject(js).get("data");
        } catch (Exception e) {
            Log.e(TAG, "can not parse :\n" + js);
        }
        if (o == null)
            return null;

        ThreadData data = new ThreadData();

        JSONObject o1;
        o1 = (JSONObject) o.get("__T");
        if (o1 == null)
            return null;
        ThreadPageInfo pageInfo;
        try {
            pageInfo = JSONObject.toJavaObject(o1, ThreadPageInfo.class);
            data.setThreadInfo(pageInfo);
        } catch (RuntimeException e) {
            Log.e(TAG, o1.toJSONString());
            return null;
        }

        int rows = (Integer) o.get("__R__ROWS");

        int all_rows = (Integer) o.get("__ROWS");
        o1 = (JSONObject) o.get("__R");

        if (o1 == null)
            return null;
        JSONObject userInfoMap = (JSONObject) o.get("__U");

        List<ThreadRowInfo> __R = convertJSobjToList(o1, rows, userInfoMap);
        data.setRowList(__R);
        data.setRowNum(__R.size());
        data.set__ROWS(all_rows);

        return data;
    }

    private List<ThreadRowInfo> convertJSobjToList(JSONObject rowMap, int count, JSONObject userInfoMap) {
        if (rowMap == null)
            return null;
        List<ThreadRowInfo> __R = new ArrayList<ThreadRowInfo>();
        Log.d("ArticleUtil", "convertJSobjToList");
        for (int i = 0; i < count; i++) {
            Object obj = rowMap.get(String.valueOf(i));
            JSONObject rowObj = null;
            if (obj instanceof JSONObject) {
                rowObj = (JSONObject) obj;
            } else {
                continue;
            }
            ThreadRowInfo row = JSONObject.toJavaObject(rowObj, ThreadRowInfo.class);
            JSONObject commObj = (JSONObject) rowObj.get("comment");

            if (commObj != null) {
                row.setComments(convertJSobjToList(commObj, commObj.size(), userInfoMap));
            }
            String from_client = rowObj.getString("from_client");
            if (!StringUtil.isEmpty(from_client)) {
                row.setFromClient(from_client);
                if (!from_client.trim().equals("")) {
                    String clientappcode = "";
                    if (from_client.indexOf(" ") > 0) {
                        clientappcode = from_client.substring(0, from_client.indexOf(" "));
                    } else {
                        clientappcode = from_client;
                    }
                    if (clientappcode.equals("1") || clientappcode.equals("7") || clientappcode.equals("101")) {
                        row.setFromClientModel("ios");
                    } else if (clientappcode.equals("103") || clientappcode.equals("9")) {
                        row.setFromClientModel("wp");
                    } else if (!clientappcode.equals("8") && !clientappcode.equals("100")) {
                        row.setFromClientModel("unknown");
                    } else {
                        row.setFromClientModel("android");
                    }
                }
            }
            String vote = rowObj.getString("vote");
            if (!StringUtil.isEmpty(vote)) {
                row.setVote(vote);
            }
            fillUserInfo(row, userInfoMap);
            FunctionUtil.fillFormated_html_data(row, i, context);
            __R.add(row);
        }
        return __R;
    }

    private void fillUserInfo(ThreadRowInfo row, JSONObject userInfoMap) {
        if (row.getAuthorid() == 0) {
            return;
        }
        JSONObject userInfo = (JSONObject) userInfoMap.get(String.valueOf(row
                .getAuthorid()));
        if (userInfo == null) {
            return;
        }
        int uid = row.getAuthorid();
        Set<Integer> blacklist = PhoneConfiguration.getInstance().blacklist;
        if (blacklist != null)
            if (blacklist.contains(uid)) {
                row.set_IsInBlackList(true);
            }
        String t1 = "甲乙丙丁戊己庚辛壬癸子丑寅卯辰巳午未申酉戌亥";
        String t2 = "王李张刘陈杨黄吴赵周徐孙马朱胡林郭何高罗郑梁谢宋唐许邓冯韩曹曾彭萧蔡潘田董袁于余叶蒋杜苏魏程吕丁沈任姚卢傅钟姜崔谭廖范汪陆金石戴贾韦夏邱方侯邹熊孟秦白江阎薛尹段雷黎史龙陶贺顾毛郝龚邵万钱严赖覃洪武莫孔汤向常温康施文牛樊葛邢安齐易乔伍庞颜倪庄聂章鲁岳翟殷詹申欧耿关兰焦俞左柳甘祝包宁尚符舒阮柯纪梅童凌毕单季裴霍涂成苗谷盛曲翁冉骆蓝路游辛靳管柴蒙鲍华喻祁蒲房滕屈饶解牟艾尤阳时穆农司卓古吉缪简车项连芦麦褚娄窦戚岑景党宫费卜冷晏席卫米柏宗瞿桂全佟应臧闵苟邬边卞姬师和仇栾隋商刁沙荣巫寇桑郎甄丛仲虞敖巩明佘池查麻苑迟邝 ";
        if (userInfo.getString("username").length() == 39
                && userInfo.getString("username").startsWith("#anony_")) {
            StringBuilder sb = new StringBuilder();
            String aname = userInfo.getString("username");
            int i = 6;
            for (int j = 0; j < 6; j++) {
                int pos = 0;
                if (j == 0 || j == 3) {
                    pos = Integer.valueOf(aname.substring(i + 1, i + 2), 16);
                    sb.append(t1.charAt(pos));
                } else if (j < 6) {
                    pos = Integer.valueOf(aname.substring(i, i + 2), 16);
                    sb.append(t2.charAt(pos));
                }
                i += 2;
            }
            row.setAuthor(sb.toString());
            row.setISANONYMOUS(true);
        } else {
            row.setAuthor(userInfo.getString("username"));
        }
        row.setJs_escap_avatar(userInfo.getString("avatar"));
        row.setYz(userInfo.getString("yz"));
        row.setMute_time(userInfo.getString("mute_time"));
        try {
            row.setAurvrc(Integer.valueOf(userInfo.getString("rvrc")));
        } catch (Exception e) {
            row.setAurvrc(0);
        }
        row.setSignature(userInfo.getString("signature"));
    }
}
