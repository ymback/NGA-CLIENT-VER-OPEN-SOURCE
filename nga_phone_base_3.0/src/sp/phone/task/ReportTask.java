package sp.phone.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import gov.anzong.androidnga.R;
import sp.phone.forumoperation.HttpPostClient;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;

public class ReportTask extends AsyncTask<String, Integer, String> {
    final private Context context;

    public ReportTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected String doInBackground(String... arg0) {
        if (arg0.length == 0)
            return null;
        final String uri = arg0[0];
        if (StringUtil.isEmpty(uri))
            return null;


        HttpPostClient c = new HttpPostClient(uri);
        String cookie = PhoneConfiguration.getInstance().getCookie();
        c.setCookie(cookie);
        final String body = this.buildBody();


        try {
            InputStream input = null;
            HttpURLConnection conn = c.post_body(body);
            if (conn == null)
                return null;

            input = conn.getInputStream();
            if (input != null) {
                String html = IOUtils.toString(input, "gbk");
                if (html.contains(context.getString(R.string.report_success)))
                    return "ok";

            }

            if (conn.getResponseCode() == 200) {
                return "ok";
            }


        } catch (IOException e) {

        }
        return null;

    }

    private String buildBody() {
        return "Content-Type: application/x-www-form-urlencoded\r\n"
                + "Content-Length: 0";
    }

    @Override
    protected void onPostExecute(String result) {
        if (!StringUtil.isEmpty(result)) {
            Toast.makeText(context, R.string.report_success, Toast.LENGTH_SHORT).show();
        }
    }


}
