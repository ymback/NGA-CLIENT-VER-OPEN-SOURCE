package sp.phone.task;

import android.content.Context;
import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import gov.anzong.androidnga.R;
import sp.phone.http.bean.ThreadData;
import sp.phone.util.ArticleUtil;
import sp.phone.common.PhoneConfiguration;
import sp.phone.util.ActivityUtils;
import sp.phone.util.HttpUtil;
import sp.phone.util.NLog;

public class JsonThreadLoadTask extends AsyncTask<String, Integer, ThreadData> {

    public interface OnThreadPageLoadFinishedListener {
        void finishLoad(ThreadData data);

    }

    static final String TAG = JsonThreadLoadTask.class.getSimpleName();
    final private Context context;
    final private OnThreadPageLoadFinishedListener notifier;
    private String errorStr;

    public JsonThreadLoadTask(Context context, OnThreadPageLoadFinishedListener notifier) {
        super();
        this.context = context;
        this.notifier = notifier;
    }

    @Override
    protected ThreadData doInBackground(String... params) {
        if (params.length == 0)
            return null;

        final String url = params[0];
        NLog.d(TAG, "start to load:" + url);

        ThreadData result = this.loadAndParseJsonPage(url);
        int originalTid = 0;
        if (null != result && null != result.getThreadInfo()) {
          //  originalTid = result.getThreadInfo().getQuote_from();
        }
        if (null != result && originalTid != 0) {
            String origUrl = url.replaceAll("tid=(\\d+)", "tid=" + originalTid);
            NLog.i(TAG, "quoted page,load from orignal article,tid=" + originalTid);
            result = loadAndParseJsonPage(origUrl);
        }
        return result;
    }

    private ThreadData loadAndParseJsonPage(String uri) {
        String js = HttpUtil.getHtml(uri, PhoneConfiguration.getInstance().getCookie());
        if (null == js) {
            errorStr = context.getString(R.string.network_error);
            return null;
        }
        if (js.indexOf("/*error fill content") > 0)
            js = js.substring(0, js.indexOf("/*error fill content"));
        js = js.replaceAll("/\\*\\$js\\$\\*/", "");
        ThreadData result = new ArticleUtil(context).parseJsonThreadPage(js);

        if (null == result) {
            errorStr = context.getResources().getString(
                    R.string.thread_load_error);
            do {
                try {
                    JSONObject o = JSON.parseObject(js);
                    if (o == null)
                        break;
                    o = (JSONObject) o.get("data");
                    if (o == null)
                        break;
                    String message = null;
                    Object tmp = o.get("__MESSAGE");
                    if (tmp instanceof String) {
                        message = (String) o.get("__MESSAGE");
                    } else if (tmp instanceof JSONObject) {
                        o = (JSONObject) tmp;
                        message = (String) o.get("1");
                        if (message == null) {
                            message = (String) o.get("0");
                        }
                    } else {
                        break;
                    }
                    if (message == null)
                        break;

                    int pos = message.indexOf("<a href=");
                    if (pos > 0) {
                        message = message.substring(0, pos);
                    }
                    pos = message.indexOf("<br/>");
                    if (pos > 0)
                        errorStr = message.replace("<br/>", "");
                    else
                        errorStr = message;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (false);
        }
        return result;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ThreadData result) {
        if (result == null) {
            ActivityUtils.getInstance().dismiss();
            ActivityUtils.getInstance().noticeError(errorStr, context);
        }
        notifier.finishLoad(result);

        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        ActivityUtils.getInstance().dismiss();
        super.onCancelled();
    }
}
