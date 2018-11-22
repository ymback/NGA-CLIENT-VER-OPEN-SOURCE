package sp.phone.task;

import android.content.Context;
import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import gov.anzong.androidnga.Utils;
import sp.phone.common.PhoneConfiguration;
import sp.phone.forumoperation.HttpPostClient;
import sp.phone.util.ActivityUtils;
import sp.phone.util.NLog;

/**
 * Created by Justwen on 2017/5/28.
 */

public class MessagePostTask extends AsyncTask<String, Integer, String> {

    private CallBack mCallBack;

    private Context mContext;

    private boolean mSuccess = true;

    private  String mReplyUrl = Utils.getNGAHost() + "nuke.php?";

    private static final String LOG_TAG = MessagePostTask.class.getSimpleName();


    public interface CallBack{

        void onMessagePostFinished(boolean isSuccess,String result);
    }


    public MessagePostTask(Context context,CallBack callBack) {
        mCallBack = callBack;
        mContext = context;
    }


    @Override
    protected void onPreExecute() {
        ActivityUtils.getInstance().noticeSaying(mContext);
        super.onPreExecute();
    }

    @Override
    protected void onCancelled() {
        mCallBack.onMessagePostFinished(false,null);
        super.onCancelled();
    }

    @Override
    protected void onCancelled(String result) {
        mCallBack.onMessagePostFinished(false,null);
        super.onCancelled();
    }

    @Override
    protected String doInBackground(String... params) {
        if (params.length == 0) {
            return "parameter error";
        }
        String ret = "网络错误";
        String body = params[0];

        HttpPostClient c = new HttpPostClient(mReplyUrl);
        String cookie = PhoneConfiguration.getInstance().getCookie();
        c.setCookie(cookie);
        try {
            InputStream input = null;
            HttpURLConnection conn = c.post_body(body);
            if (conn != null) {
                if (conn.getResponseCode() >= 500) {
                    input = null;
                    mSuccess = false;
                    ret = "二哥在用服务器下毛片";
                } else {
                    if (conn.getResponseCode() >= 400) {
                        input = conn.getErrorStream();
                        mSuccess = false;
                    } else
                        input = conn.getInputStream();
                }
            } else
                mSuccess = false;

            if (input != null) {
                String html = IOUtils.toString(input, "gbk");
                ret = getReplyResult(html);
            } else
                mSuccess = false;
        } catch (IOException e) {
            mSuccess = false;
            NLog.e(LOG_TAG, NLog.getStackTraceString(e));

        }
        return ret;
    }

    private String getReplyResult(String js) {
        if (null == js) {
            return "发送失败";
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
            NLog.e("TAG", "can not parse :\n" + js);
        }
        if (o == null) {
            try {
                o = (JSONObject) JSON.parseObject(js).get("error");
            } catch (Exception e) {
                NLog.e("TAG", "can not parse :\n" + js);
            }
            if (o == null) {
                return "发送失败";
            }
            return o.getString("0");
        }
        return o.getString("0");
    }

    @Override
    protected void onPostExecute(String result) {
        String success_results[] = {"发送完毕 ...", " @提醒每24小时不能超过50个", "操作成功"};
        if (mSuccess) {
            boolean success = false;
            for (int i = 0; i < success_results.length; ++i) {
                if (result.contains(success_results[i])) {
                    success = true;
                    break;
                }
            }
            if (!success)
                mSuccess = false;
        }

        mCallBack.onMessagePostFinished(mSuccess,result);

        super.onPostExecute(result);
    }
}
