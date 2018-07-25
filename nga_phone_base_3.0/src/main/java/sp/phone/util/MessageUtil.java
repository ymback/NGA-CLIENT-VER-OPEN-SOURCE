package sp.phone.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import gov.anzong.androidnga.R;
import sp.phone.bean.MessageArticlePageInfo;
import sp.phone.bean.MessageDetailInfo;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.common.PhoneConfiguration;
import sp.phone.theme.ThemeManager;
import sp.phone.bean.MessageArticlePageInfo;
import sp.phone.bean.MessageDetailInfo;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.common.PhoneConfiguration;
import sp.phone.theme.ThemeManager;

/**
 * 解析页面内容类
 */
public class MessageUtil {
    private final static String TAG = MessageUtil.class.getSimpleName();
    private static Context context;

    @SuppressWarnings("static-access")
    public MessageUtil(Context context) {
        super();
        this.context = context;
    }

    public static boolean isInWifi() {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        return wifi == State.CONNECTED;
    }

    public static int showImageQuality() {
        return 0;
//        if (isInWifi()) {
//            return 0;
//        } else {
//            return PhoneConfiguration.getInstance().imageQuality;
//        }
    }

    private boolean isShowImage() {
        return PhoneConfiguration.getInstance().isDownImgNoWifi() || isInWifi();
    }

    /**
     * 解析页面内容
     *
     * @param js
     * @param page
     * @return
     */
    public MessageDetailInfo parseJsonThreadPage(String js, int page) {
        js = js.replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
        js = js.replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",");

        js = js.replaceAll("\"content\":(0\\d+),", "\"content\":\"$1\",");
        js = js.replaceAll("\"subject\":(0\\d+),", "\"subject\":\"$1\",");
        js = js.replaceAll("\"author\":(0\\d+),", "\"author\":\"$1\",");
        final String start = "\"__P\":{\"aid\":";
        final String end = "\"this_visit_rows\":";
        if (js.indexOf(start) != -1 && js.indexOf(end) != -1) {
            NLog.w(TAG, "here comes an invalid response");
            String validJs = js.substring(0, js.indexOf(start));
            validJs += js.substring(js.indexOf(end));
            js = validJs;

        }
        JSONObject o = null;
        try {
            o = (JSONObject) JSON.parseObject(js).get("data");
        } catch (Exception e) {
            NLog.e(TAG, "can not parse :\n" + js);
        }
        if (o == null)
            return null;

        MessageDetailInfo data = new MessageDetailInfo();

        JSONObject o1;
        o1 = (JSONObject) o.get("0");
        if (o1 == null)
            return null;

        JSONObject userInfoMap = (JSONObject) o1.get("userInfo");

        List<MessageArticlePageInfo> messageEntryList = convertJSobjToList(o1, userInfoMap, page);
        if (messageEntryList == null)
            return null;
        data.setMessageEntryList(messageEntryList);
        data.set__currentPage(o1.getIntValue("currentPage"));
        data.set__nextPage(o1.getIntValue("nextPage"));
        String alluser = o1.getString("allUsers"), allusertmp = "";
        alluser = alluser.replaceAll("	", " ");
        String alluserarray[] = alluser.split(" ");
        for (int i = 1; i < alluserarray.length; i += 2) {
            allusertmp += alluserarray[i] + ",";
        }
        if (allusertmp.length() > 0)
            allusertmp = allusertmp.substring(0, allusertmp.length() - 1);
        data.set_Alluser(allusertmp);
        if (data.getMessageEntryList().get(0) != null) {
            String title = data.getMessageEntryList().get(0).getSubject();
            if (!StringUtils.isEmpty(title)) {
                data.set_Title(title);
            } else {
                data.set_Title("");
            }
        }
        return data;

    }

    private List<MessageArticlePageInfo> convertJSobjToList(JSONObject rowMap, JSONObject userInfoMap, int page) {
        List<MessageArticlePageInfo> __R = new ArrayList<MessageArticlePageInfo>();
        if (rowMap == null)
            return null;
        JSONObject rowObj = (JSONObject) rowMap.get("0");
        for (int i = 1; rowObj != null; i++) {
            MessageArticlePageInfo row = new MessageArticlePageInfo();

            row.setContent(rowObj.getString("content"));
            row.setLou(20 * (page - 1) + i);
            row.setSubject(rowObj.getString("subject"));
            int time = rowObj.getIntValue("time");
            if (time > 0) {
                row.setTime(StringUtils.timeStamp2Date1(String.valueOf(time)));
            } else {
                row.setTime("");
            }
            row.setFrom(rowObj.getString("from"));
            fillUserInfo(row, userInfoMap);
            fillFormated_html_data(row, i);
            __R.add(row);
            rowObj = (JSONObject) rowMap.get(String.valueOf(i));
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
    private List<MessageArticlePageInfo> convertJSobjToList(JSONObject rowMap, JSONObject userInfoMap) {
        return convertJSobjToList(rowMap, userInfoMap, 1);
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

        String formated_html_data = convertToHtmlText(row,
                isShowImage(), showImageQuality(), fgColorStr, bgcolorStr);

        row.setFormated_html_data(formated_html_data);
    }

    /**
     * 转换函数
     *
     * @param row
     * @param showImage
     * @param imageQuality
     * @param fgColorStr
     * @param bgcolorStr
     * @return
     */
    public static String convertToHtmlText(final MessageArticlePageInfo row,
                                           boolean showImage, int imageQuality, final String fgColorStr,
                                           final String bgcolorStr) {
        String ngaHtml = StringUtils.decodeForumTag(row.getContent(), showImage, imageQuality, null);
        if (StringUtils.isEmpty(ngaHtml)) {
            ngaHtml = "<font color='red'>[" + ApplicationContextHolder.getString(R.string.hide) + "]</font>";
        }
        ngaHtml = "<HTML> <HEAD><META   http-equiv=Content-Type   content= \"text/html;   charset=utf-8 \">"
                + buildHeader(row, fgColorStr)
                + "<body bgcolor= '#"
                + bgcolorStr
                + "'>"
                + "<font color='#"
                + fgColorStr
                + "' size='2'>"
                + ngaHtml
                + "</font></body>";

        return ngaHtml;
    }

    private static String buildHeader(MessageArticlePageInfo row, String fgColorStr) {
        if (row == null || StringUtils.isEmpty(row.getSubject()))
            return "";
        StringBuilder sb = new StringBuilder();
        sb.append("<h4 style='color:").append(fgColorStr).append("' >")
                .append(row.getSubject()).append("</h4>");
        return sb.toString();
    }

}
