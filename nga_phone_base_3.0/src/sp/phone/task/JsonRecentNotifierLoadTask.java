package sp.phone.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.Utils;
import sp.phone.bean.NotificationObject;
import sp.phone.common.PreferenceKey;
import sp.phone.common.UserManagerImpl;
import sp.phone.interfaces.OnRecentNotifierFinishedListener;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.NLog;
import sp.phone.utils.StringUtils;

public class JsonRecentNotifierLoadTask extends AsyncTask<String, Integer, String> implements PreferenceKey {
    static final String TAG = JsonRecentNotifierLoadTask.class.getSimpleName();
    final String url = Utils.getNGAHost() + "nuke.php?__lib=noti&raw=3&__act=get_all";
    final private Context context;
    final private OnRecentNotifierFinishedListener notifier;
    List<NotificationObject> notificationList = new ArrayList<NotificationObject>();
    private String errorStr;

    public JsonRecentNotifierLoadTask(Context context,
                                      OnRecentNotifierFinishedListener notifier) {
        super();
        this.context = context;
        this.notifier = notifier;
    }

    @Override
    protected String doInBackground(String... params) {
        final String cookie = params[0];
        String result = "";

        result = HttpUtil.getHtml(url, cookie, null, 3000);
        return result;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        if (StringUtils.isEmpty(result)) {
            UserManagerImpl.getInstance().setReplyString(0,"");
            notifier.jsonfinishLoad();
            return;
        }

        String totalresult = StringUtils.getStringBetween(result, 0,
                "window.script_muti_get_var_store=", "</script>").result;
        JSONObject ojson = new JSONObject();
        JSONArray ojsonnoti = new JSONArray();
        try {
            ojson = (JSONObject) JSON.parseObject(totalresult);
            ojson = (JSONObject) ojson.get("data");
            ojson = (JSONObject) ojson.get("0");
        } catch (Exception e) {
        }
        if (ojson == null) {
            return;
        }

        try {
            ojsonnoti = (JSONArray) ojson.get("0");
        } catch (Exception e) {
            NLog.i(TAG, "JSON DATA ERROR");
        }
        if (ojsonnoti != null) {
            if (ojsonnoti.size() > 0) {
                for (int i = 0; i < ojsonnoti.size(); i++) {
                    try {
                        JSONObject ojsonnotidata = (JSONObject) ojsonnoti
                                .get(i);
                        String authorId = ojsonnotidata.getString("1");
                        String nickName = ojsonnotidata.getString("2");
                        String tid = ojsonnotidata.getString("6");
                        String pid = ojsonnotidata.getString("7");
                        String title = ojsonnotidata.getString("5");
                        if (!StringUtils.isEmpty(authorId)
                                && !StringUtils.isEmpty(nickName)
                                && !StringUtils.isEmpty(tid)
                                && !StringUtils.isEmpty(pid)
                                && !StringUtils.isEmpty(title)) {
                            title = StringUtils.unEscapeHtml(title);
                            addNotification(authorId, nickName, tid, pid, title);
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }
        if (notificationList.size() > 0) {
            SharedPreferences share = context.getSharedPreferences(PERFERENCE,
                    Context.MODE_PRIVATE);
            String strold = share.getString(PENDING_REPLYS, "");

            List<NotificationObject> list = new ArrayList<NotificationObject>();
            list = notificationList;
            String recentstr = JSON.toJSONString(list);

            UserManagerImpl.getInstance().setReplyString(list.size(),recentstr);

        }
        notifier.jsonfinishLoad();
        super.onPostExecute(result);
    }

    void addNotification(String authorid, String nickName, String tid,
                         String pid, String title) {
        if (StringUtils.isEmpty(tid)) {
            return;
        }
        int pidNum = 0;
        try {
            pidNum = Integer.parseInt(pid);
        } catch (Exception e) {
            pidNum = 0;
        }

        if (notificationList.size() > 0) {
            NotificationObject last = notificationList.get(notificationList
                    .size() - 1);
            if (last.getTid() == Integer.parseInt(tid)
                    && last.getPid() == pidNum) {
                return;
            }
        }

        NotificationObject o = new NotificationObject();
        o.setAuthorId(Integer.parseInt(authorid));
        o.setNickName(nickName);
        o.setTid(Integer.parseInt(tid));

        o.setPid(pidNum);

        o.setTitle(title);
        notificationList.add(o);

    }

    @Override
    protected void onCancelled() {
        ActivityUtils.getInstance().dismiss();
        super.onCancelled();
    }

}
