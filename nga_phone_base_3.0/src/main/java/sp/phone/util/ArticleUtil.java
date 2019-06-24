package sp.phone.util;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sp.phone.http.bean.ThreadData;
import sp.phone.http.bean.ThreadRowInfo;
import sp.phone.common.UserManagerImpl;
import sp.phone.mvp.model.entity.ThreadPageInfo;

public class ArticleUtil {
    private final static String TAG = ArticleUtil.class.getSimpleName();
    private Context context;

    @SuppressWarnings("static-access")
    public ArticleUtil(Context context) {
        super();
        this.context = context;
    }

    public static int showImageQuality() {
        return 0;
//        if (NetUtil.getInstance().isInWifi()) {
//            return 0;
//        } else {
//            return PhoneConfiguration.getInstance().imageQuality;
//        }
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
            NLog.e(TAG, "can not parse :\n" + js);
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
            NLog.e(TAG, o1.toJSONString());
            return null;
        }

        int rows = (Integer) o.get("__R__ROWS");

        int all_rows = (Integer) o.get("__ROWS");
        o1 = (JSONObject) o.get("__R");

        if (o1 == null)
            return null;
        JSONObject userInfoMap = (JSONObject) o.get("__U");

        List<ThreadRowInfo> __R = convertJsObjToList(o1, rows, userInfoMap);
        data.setRowList(__R);
        data.setRowNum(__R.size());
        data.set__ROWS(all_rows);

        return data;
    }

    private List<ThreadRowInfo> convertJsObjToList(JSONObject rowMap, int count, JSONObject userInfoMap) {
        if (rowMap == null)
            return null;
        List<ThreadRowInfo> __R = new ArrayList<ThreadRowInfo>();
        NLog.d("ArticleUtil", "convertJsObjToList");
        for (int i = 0; i < count; i++) {
            Object obj = rowMap.get(String.valueOf(i));
            JSONObject rowObj = null;
            if (obj instanceof JSONObject) {
                rowObj = (JSONObject) obj;
            } else {
                continue;
            }
            ThreadRowInfo row = JSONObject.toJavaObject(rowObj, ThreadRowInfo.class);
            //解析贴条
            JSONObject commObj = (JSONObject) rowObj.get("comment");
            if (commObj != null) {
                row.setComments(convertJsObjToList(commObj, commObj.size(), userInfoMap));
            }
            //热门回复
            String hotObj = rowObj.getString("17");
            if (hotObj != null) {
                row.hotReplies = new ArrayList<>();
                String[] hots = hotObj.split(",");
                for (String hot : hots) {
                    if (!TextUtils.isEmpty(hot)) {
                        row.hotReplies.add(hot);
                    }
                }
            }

            String from_client = rowObj.getString("from_client");
            if (!StringUtils.isEmpty(from_client)) {
                row.setFromClient(from_client);
                if (!from_client.trim().equals("")) {
                    String clientAppCode = "";
                    if (from_client.indexOf(" ") > 0) {
                        clientAppCode = from_client.substring(0, from_client.indexOf(" "));
                    } else {
                        clientAppCode = from_client;
                    }
                    if (clientAppCode.equals("1") || clientAppCode.equals("7") || clientAppCode.equals("101")) {
                        row.setFromClientModel("ios");
                    } else if (clientAppCode.equals("103") || clientAppCode.equals("9")) {
                        row.setFromClientModel("wp");
                    } else if (!clientAppCode.equals("8") && !clientAppCode.equals("100")) {
                        row.setFromClientModel("unknown");
                    } else {
                        row.setFromClientModel("android");
                    }
                }
            }
            String vote = rowObj.getString("vote");
            if (!StringUtils.isEmpty(vote)) {
                row.setVote(vote);
            }
            fillUserInfo(row, userInfoMap);
            FunctionUtils.fillFormatedHtmlData(row, i, context);
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
        row.set_IsInBlackList(UserManagerImpl.getInstance().checkBlackList(String.valueOf(uid)));
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
        row.setMuteTime(userInfo.getString("mute_time"));
        try {
            row.setAurvrc(Integer.valueOf(userInfo.getString("rvrc")));
        } catch (Exception e) {
            row.setAurvrc(0);
        }
        row.setSignature(userInfo.getString("signature"));
    }
}
