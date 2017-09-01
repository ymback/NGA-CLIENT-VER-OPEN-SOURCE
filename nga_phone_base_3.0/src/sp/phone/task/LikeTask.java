package sp.phone.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Locale;

import gov.anzong.androidnga.Utils;
import sp.phone.common.PhoneConfiguration;
import sp.phone.forumoperation.HttpPostClient;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.StringUtils;

/**
 * 赞或者踩
 * Created by elrond on 2017/9/1.
 */

public class LikeTask extends AsyncTask<String, Integer, String> {

    private static final String URL = "__lib=topic_recommend&__act=add&tid=%1$d&pid=%2$d&value=%3$d&raw=3";
    private Context context;
    private String mBody;
    private String mUrl;

    public LikeTask(Context context, int tid, int pid, int value) {
        this.context = context;
        mBody = String.format(Locale.SIMPLIFIED_CHINESE, URL, tid, pid, value);
        mUrl = Utils.getNGAHost() + "nuke.php?" + mBody;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpPostClient c = new HttpPostClient(mUrl);
        String cookie = PhoneConfiguration.getInstance().getCookie();
        c.setCookie(cookie);

        String ret = null;
        try {
            InputStream input = null;
            HttpURLConnection conn = c.post_body(mBody);
            if (conn != null)
                input = conn.getInputStream();

            if (input != null) {
                ret = IOUtils.toString(input, "gbk");
            }

        } catch (IOException ignore) {
        }
        return ret;
    }

    @Override
    protected void onPreExecute() {
        ActivityUtils.getInstance().noticeSaying(context);
    }

    @Override
    protected void onPostExecute(String result) {
        ActivityUtils.getInstance().dismiss();
        if (StringUtils.isEmpty(result))
            return;

        String msg = StringUtils.getStringBetween(result, 0, "{\"0\":\"", "\",\"1\":1").result;
        if (!StringUtils.isEmpty(msg)) {
            Toast.makeText(context, msg.trim(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled(String result) {
        this.onCancelled();
    }

    @Override
    protected void onCancelled() {
        ActivityUtils.getInstance().dismiss();
    }
}
