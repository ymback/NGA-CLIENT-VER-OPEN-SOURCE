package com.noname.task;

import android.content.Context;
import android.os.AsyncTask;

import com.noname.R;
import com.noname.gson.parse.NonameParseJson;
import com.noname.gson.parse.NonameThreadResponse;
import com.noname.interfaces.OnNonameTopListLoadFinishedListener;
import com.noname.util.ActivityUtils;
import com.noname.util.HttpUtil;
import com.noname.util.NLog;
import com.noname.util.StringUtils;

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
        String js = HttpUtil.getHtml(uri, "");
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
