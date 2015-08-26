package sp.phone.forumoperation;

import android.util.Log;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import gov.anzong.androidnga.activity.MyApp;

public class HttpPostClient {
    private static final String LOG_TAG = HttpPostClient.class
            .getSimpleName();
    private String urlString;
    private String cookie;
    public HttpPostClient(String urlString) {
        this.urlString = urlString;
        cookie = null;
    }
    public HttpPostClient(String urlString, String cookie) {
        this.urlString = urlString;
        this.cookie = cookie;
    }

    /**
     * @return the cookie
     */
    public String getCookie() {
        return cookie;
    }

    /**
     * @param cookie the cookie to set
     */
    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public HttpURLConnection post_body(String body) {
        HttpURLConnection conn;
        String machine = "";
        String MODEL = android.os.Build.MODEL.toUpperCase(Locale.US);
        String MANUFACTURER = android.os.Build.MANUFACTURER.toUpperCase(Locale.US);
        if (MODEL.indexOf(MANUFACTURER) >= 0) {
            machine = android.os.Build.MODEL;
        } else {
            machine = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
        }
        if (machine.length() < 19) {
            machine = "[" + machine + "]";
        }
        final String USER_AGENT = new StringBuilder().append("Nga_Official/").append(MyApp.version).append("(").append(machine).append(";Android").append(android.os.Build.VERSION.RELEASE).append(")").toString();

        try {


            URL url = new URL(this.urlString);
            conn = (HttpURLConnection) url.openConnection();
            if (cookie != null)
                conn.setRequestProperty("Cookie", cookie);
            conn.setInstanceFollowRedirects(false);

            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(body.length()));
            conn.setRequestProperty("Accept-Charset", "GBK");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            conn.connect();

            OutputStreamWriter out = new OutputStreamWriter(conn
                    .getOutputStream());
            out.write(body);
            out.flush();
            out.close();


            Log.i(LOG_TAG, conn.getResponseMessage());

        } catch (Exception e) {
            //sb.append(e.toString());
            conn = null;
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        }
        return conn;
    }

}
