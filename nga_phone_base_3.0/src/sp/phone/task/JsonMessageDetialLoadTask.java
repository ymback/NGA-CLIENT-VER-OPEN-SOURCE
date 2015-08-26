package sp.phone.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import gov.anzong.androidnga.R;
import sp.phone.bean.MessageDetialInfo;
import sp.phone.interfaces.OnMessageDetialLoadFinishedListener;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.MessageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;

public class JsonMessageDetialLoadTask extends AsyncTask<String, Integer, MessageDetialInfo> {
    private final static String TAG = JsonMessageDetialLoadTask.class.getSimpleName();
    private final Context context;
    final private OnMessageDetialLoadFinishedListener notifier;
    private String error;
    @SuppressWarnings("unused")
    private String table;


    public JsonMessageDetialLoadTask(Context context,
                                     OnMessageDetialLoadFinishedListener notifier) {
        super();
        this.context = context;
        this.notifier = notifier;
    }

    @Override
    protected MessageDetialInfo doInBackground(String... params) {


        if (params.length == 0)
            return null;
        Log.d(TAG, "start to load " + params[0]);

        String uri = params[0];
        String page = StringUtil.getStringBetween(uri, 0, "page=", "&").result;
        if (StringUtil.isEmpty(page)) {
            page = "1";
        }
        String js = HttpUtil.getHtml(uri, PhoneConfiguration.getInstance().getCookie());
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
        js = js.replaceAll("\\[img\\]./mon_", "[img]http://img6.nga.178.com/attachments/mon_");

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
        MessageDetialInfo ret = new MessageUtil(context).parseJsonThreadPage(js, Integer.parseInt(page));
        return ret;
    }

    @Override
    protected void onPostExecute(MessageDetialInfo result) {
        ActivityUtil.getInstance().dismiss();
        if (result == null) {
            if (!StringUtil.isEmpty(error))
                ActivityUtil.getInstance().noticeError
                        (error, context);
        }
        if (null != notifier) {
            notifier.finishLoad(result);
        }
        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        ActivityUtil.getInstance().dismiss();
        super.onCancelled();
    }


}
