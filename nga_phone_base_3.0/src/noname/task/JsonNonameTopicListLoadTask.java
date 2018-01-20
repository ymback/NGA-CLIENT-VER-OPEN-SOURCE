package noname.task;

import android.content.Context;
import android.os.AsyncTask;

import gov.anzong.androidnga.R;
import noname.gson.parse.NonameParseJson;
import noname.gson.parse.NonameThreadResponse;
import sp.phone.common.PhoneConfiguration;
import noname.interfaces.OnNonameTopListLoadFinishedListener;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.NLog;
import sp.phone.utils.StringUtils;

public class JsonNonameTopicListLoadTask extends AsyncTask<String, Integer, NonameThreadResponse> {
    private final static String TAG = JsonNonameTopicListLoadTask.class.getSimpleName();
    private final Context context;
    final private OnNonameTopListLoadFinishedListener notifier;
    private String error;


    public JsonNonameTopicListLoadTask(Context context,
                                       OnNonameTopListLoadFinishedListener notifier) {
        super();
        this.context = context;
        this.notifier = notifier;
    }

    @Override
    protected NonameThreadResponse doInBackground(String... params) {
        if (params.length == 0)
            return null;
        NLog.d(TAG, "start to load " + params[0]);
        String uri = params[0];
        String js = HttpUtil.getHtml(uri, PhoneConfiguration.getInstance().getCookie());
        if (StringUtils.isEmpty(js)) {
            if (context != null)
                error = context.getResources().getString(R.string.network_error);
            return null;
        }
        if (!js.startsWith("{")) {
            if (context != null)
                error = context.getResources().getString(R.string.datafromserver_error);
            return null;
        }
        NonameThreadResponse ret = NonameParseJson.parseThreadRead(js);

        return ret;
    }

    @Override
    protected void onPostExecute(NonameThreadResponse result) {
        ActivityUtils.getInstance().dismiss();
        if (result == null) {
            ActivityUtils.getInstance().noticeError
                    (error, context);
            return;
        }
        if (result.error) {
            ActivityUtils.getInstance().noticeError
                    (result.errorinfo, context);
            return;
        }
        if (null != notifier)
            notifier.jsonfinishLoad(result);
        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        ActivityUtils.getInstance().dismiss();
        super.onCancelled();
    }


}
