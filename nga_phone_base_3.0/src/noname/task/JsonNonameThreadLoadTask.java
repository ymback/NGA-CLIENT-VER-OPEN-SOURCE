package noname.task;

import android.content.Context;
import android.os.AsyncTask;

import gov.anzong.androidnga.R;
import noname.gson.parse.NonameParseJson;
import noname.gson.parse.NonameReadResponse;
import sp.phone.common.PhoneConfiguration;
import noname.interfaces.OnNonameThreadPageLoadFinishedListener;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.NLog;
import sp.phone.utils.StringUtils;

public class JsonNonameThreadLoadTask extends AsyncTask<String, Integer, NonameReadResponse> {
    static final String TAG = JsonNonameThreadLoadTask.class.getSimpleName();
    final private Context context;
    final private OnNonameThreadPageLoadFinishedListener notifier;
    private String errorStr;

    public JsonNonameThreadLoadTask(Context context,
                                    OnNonameThreadPageLoadFinishedListener notifier) {
        super();
        this.context = context;
        this.notifier = notifier;
    }

    @Override
    protected NonameReadResponse doInBackground(String... params) {
        if (params.length == 0)
            return null;

        final String url = params[0];
        NLog.d(TAG, "start to load:" + url);
        NonameReadResponse result = this.loadAndParseJsonPage(url);

        return result;
    }

    private NonameReadResponse loadAndParseJsonPage(String uri) {
        // NLog.d(TAG, "start to load:" + uri);
        String js = HttpUtil.getHtml(uri, PhoneConfiguration.getInstance()
                .getCookie());
        if (StringUtils.isEmpty(js)) {
            if (context != null)
                errorStr = context.getResources().getString(R.string.network_error);
            return null;
        }
        if (!js.startsWith("{")) {
            if (context != null)
                errorStr = context.getResources().getString(R.string.datafromserver_error);
            return null;
        }
        NonameReadResponse result = NonameParseJson.parseRead(js);
        if (result.error) {
            errorStr = result.errorinfo;
            return null;
        }
        return result;

    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(NonameReadResponse result) {
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
