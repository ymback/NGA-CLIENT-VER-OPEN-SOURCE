package sp.phone.task;

import android.os.AsyncTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sp.phone.interfaces.OnNearbyLoadComplete;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.StringUtil;

public class NearbyUserTask extends AsyncTask<String, Integer, String> {
    private static final String ips[] = {"74.125.129.141",
            "74.125.129.142",
            "74.125.129.143",
            "74.125.129.144",
            "74.125.129.145"
    };
    private final double latitude;
    private final double longitude;
    private final String name;
    private final String uid;
    private final OnNearbyLoadComplete notifier;
    public NearbyUserTask(double latitude, double longitude, String name,
                          String uid, OnNearbyLoadComplete notifier) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.uid = uid;
        this.notifier = notifier;
    }

    @Override
    protected String doInBackground(String... params) {

        String host = "ngalocation.appspot.com";
        String ret = null;
        for (int i = 0; i < ips.length; ++i) {
            StringBuilder sb = new StringBuilder("https://");
            try {
                sb.append(ips[i]).append("/test?nick_name=")
                        .append(URLEncoder.encode(name, "utf-8"))
                        .append("&user_id=").append(uid)
                        .append("&longitude=").append(longitude)
                        .append("&latitude=").append(latitude);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                return null;
            }
            ret = HttpUtil.getHtml(sb.toString(), "", host, 8000);
            if (!StringUtil.isEmpty(ret))
                break;
            else {
                this.publishProgress(i + 1, ips.length);
            }
        }

        return ret;
    }

    @Override
    protected void onPostExecute(String result) {
        notifier.OnComplete(result);
    }

    @Override
    protected void onCancelled(String result) {
        notifier.OnComplete(null);
    }

    @Override
    protected void onCancelled() {
        notifier.OnComplete(null);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        notifier.onProgresUpdate(values[0], values[1]);
    }


}
