package sp.phone.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.R;
import sp.phone.bean.MessageListInfo;
import sp.phone.bean.MessageThreadPageInfo;
import sp.phone.interfaces.OnMessageListLoadFinishedListener;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;

public class JsonMessageListLoadTask extends AsyncTask<String, Integer, MessageListInfo> {
    private final static String TAG = JsonMessageListLoadTask.class.getSimpleName();
    private final Context context;
    final private OnMessageListLoadFinishedListener notifier;
    private String error;


    public JsonMessageListLoadTask(Context context,
                                   OnMessageListLoadFinishedListener notifier) {
        super();
        this.context = context;
        this.notifier = notifier;
    }

    @Override
    protected MessageListInfo doInBackground(String... params) {


        if (params.length == 0)
            return null;
        Log.d(TAG, "start to load " + params[0]);
        String uri = params[0];
        String js = HttpUtil.getHtml(uri, PhoneConfiguration.getInstance().getCookie());
        String page = StringUtil.getStringBetween(uri, 0, "page=", "&").result;
        if (StringUtil.isEmpty(page)) {
            page = "1";
        }
        if (js == null) {
            if (context != null)
                error = context.getResources().getString(R.string.network_error);
            return null;
        }
        js = js.replaceAll("window.script_muti_get_var_store=", "");
        if (js.indexOf("/*error fill content") > 0)
            js = js.substring(0, js.indexOf("/*error fill content"));
        js = js.replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
        js = js.replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",");
        js = js.replaceAll("/\\*\\$js\\$\\*/", "");
        JSONObject o = null;
        try {
            o = (JSONObject) JSON.parseObject(js).get("data");
        } catch (Exception e) {
            Log.e(TAG, "can not parse :\n" + js);
        }
        if (o == null) {
            try {
                o = (JSONObject) JSON.parseObject(js).get("error");
            } catch (Exception e) {
                Log.e(TAG, "can not parse :\n" + js);
            }
            if (o == null) {
                error = "请重新登录";
            } else {
                error = o.getString("0");
                if (StringUtil.isEmpty(error))
                    error = "请重新登录";
            }
            return null;
        }

        MessageListInfo ret = new MessageListInfo();
        JSONObject o1 = (JSONObject) o.get("0");
        if (o1 == null) {
            error = "请重新登录";
            return null;
        }
        ret.set__nextPage(o1.getIntValue("nextPage"));
        ret.set__currentPage(o1.getIntValue("currentPage"));
        ret.set__rowsPerPage(o1.getIntValue("rowsPerPage"));


        List<MessageThreadPageInfo> messageEntryList = new ArrayList<MessageThreadPageInfo>();
        JSONObject rowObj = (JSONObject) o1.get("0");
        for (int i = 1; rowObj != null; i++) {
            try {
                MessageThreadPageInfo entry = new MessageThreadPageInfo();
                entry.setMid(rowObj.getInteger("mid"));
                entry.setPosts(rowObj.getInteger("posts"));
                entry.setSubject(rowObj.getString("subject"));
                entry.setFrom_username(rowObj.getString("from_username"));
                entry.setLast_from_username(rowObj.getString("last_from_username"));
                int time = rowObj.getInteger("time");
                if (time > 0) {
                    entry.setTime(StringUtil.TimeStamp2Date(String.valueOf(time)));
                } else {
                    entry.setTime("");
                }
                time = rowObj.getIntValue("last_modify");
                if (time > 0) {
                    entry.setLastTime(StringUtil.TimeStamp2Date(String.valueOf(time)));
                } else {
                    entry.setLastTime("");
                }
                messageEntryList.add(entry);
                rowObj = (JSONObject) o1.get(String.valueOf(i));
            } catch (Exception e) {
                /*ThreadPageInfo entry = new ThreadPageInfo();
				String error = rowObj.getString("error");
				entry.setSubject(error);
				entry.setAuthor("");
				entry.setLastposter("");
				articleEntryList.add(entry);*/
            }
        }
        ret.setMessageEntryList(messageEntryList);

        return ret;
    }

    @Override
    protected void onPostExecute(MessageListInfo result) {
        ActivityUtil.getInstance().dismiss();
        if (result == null) {
            if (!StringUtil.isEmpty(error))
                ActivityUtil.getInstance().noticeError
                        (error, context);
        }
        if (null != notifier)
            notifier.jsonfinishLoad(result);
        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        ActivityUtil.getInstance().dismiss();
        super.onCancelled();
    }


}
