package sp.phone.task;

import android.content.Context;
import android.os.AsyncTask;

import gov.anzong.androidnga.Utils;
import sp.phone.bean.PerferenceConstant;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;

public class JsonCleanRecentNotifierLoadTask extends AsyncTask<String, Integer, String> implements PerferenceConstant {
    static final String TAG = JsonCleanRecentNotifierLoadTask.class.getSimpleName();
    final String url = Utils.getNGAHost() + "nuke.php?__lib=noti&raw=3&__act=del";
    final private Context context;

    public JsonCleanRecentNotifierLoadTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        final String cookie = params[0];
        HttpUtil.getHtml(url, cookie, null, 3000);
        return null;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {
        ActivityUtil.getInstance().dismiss();
        super.onCancelled();
    }

}
