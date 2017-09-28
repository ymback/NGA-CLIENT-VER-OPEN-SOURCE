package sp.phone.task;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.AsyncTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.NgaClientApp;
import sp.phone.common.PhoneConfiguration;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.NLog;
import sp.phone.utils.StringUtils;

public class AppUpdateCheckTask extends AsyncTask<String, Integer, String> {

    static final String TAG = AppUpdateCheckTask.class.getSimpleName();
    static final String url = "http://code.google.com/feeds/p/ngacnphone/downloads/basic";
    static final String ipurl = "http://74.125.71.102/feeds/p/ngacnphone/downloads/basic";
    static final String host = "code.google.com";
    static final String entryStartTag = "<entry>";
    static final String updateStartTag = "<updated>";
    static final String updateEndtTag = "</updated>";
    static final String idStartTag = "<id>";
    static final String idEndtTag = "</id>";
    static final String contentStartTag = "&lt;pre&gt;";
    //static final String contentStartTag = "<content type=\"html\">";
    static final String contentEndtTag = "&lt;a";
    final private Context context;
    private String content = null;
    public AppUpdateCheckTask(Context context) {
        super();
        this.context = context;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected String doInBackground(String... params) {
        NLog.d(TAG, "start to check new app version");
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if (wifi != State.CONNECTED) {
            NLog.d(TAG, "not in wifi,return");
            return null;
        }
        NLog.d(TAG, "start to get html data");
        String rssString = HttpUtil.getHtml(url, "", null, 1000);
        if (StringUtils.isEmpty(rssString)) {
            NLog.w(TAG, "seems gfwed, try ip");
            rssString = HttpUtil.getHtml(ipurl, "", host, 1000);
        }
        String apkUrl = null;
        String apkId = null;
        do {
            NLog.d(TAG, "start to check");
            if (StringUtils.isEmpty(rssString))
                break;
            int start = 0;
            int end = 0;

            start = rssString.indexOf(entryStartTag);
            if (start == -1)
                break;

            start = rssString.indexOf(updateStartTag, start);
            if (start == -1)
                break;
            start += updateStartTag.length();
            end = rssString.indexOf(updateEndtTag, start);
            if (end == -1)
                break;
            String date = rssString.substring(start, end);//2012-05-29T17:55:08Z
            date = date.replace('T', ' ');
            date = date.replace("Z", "");
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = sdf.parse(date);
                Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                c.setTime(d);
                //long gap = System.currentTimeMillis() - c.getTimeInMillis();//utc
                //long hour = gap/(1000*3600);

                //if(hour < (8+2))
                //	break;

            } catch (ParseException e) {
                NLog.e(TAG, "invalid date:" + date);
                break;
            }


            start = rssString.indexOf(idStartTag, end);
            if (start == -1)
                break;
            start += idStartTag.length();
            end = rssString.indexOf(idEndtTag, start);
            if (end == -1)
                break;

            apkUrl = rssString.substring(start, end);
            //id -->http://code.google.com/feeds/p/ngacnphone/downloads/basic/nga_phone200.apk
            //url http://ngacnphone.googlecode.com/files/nga_phone200.apk
            apkId = apkUrl.replace("http://code.google.com/feeds/p/ngacnphone/downloads/basic/nga_phone",
                    "");
            apkId = apkId.replace(".apk", "");

            start = rssString.indexOf(contentStartTag, end);
            if (start == -1)
                break;
            start += contentStartTag.length();
            end = rssString.indexOf(contentEndtTag, start);
            if (end == -1)
                break;
            this.content = rssString.substring(start, end).trim();
            this.content = StringUtils.unEscapeHtml(content);
        } while (false);

        return apkId;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPostExecute(String result) {
        if (result == null)
            return;
        int id = 0;
        id = Integer.parseInt(result);

        if (id <= NgaClientApp.version) {
            NLog.i(TAG, "application alread up to date");
            return;
        }
        NLog.i(TAG, "new version found:" + id);
        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String url = "http://ngacnphone.googlecode.com/files/nga_phone" +
                id + ".apk";
        intent.setData(Uri.parse(url));

        intent.addFlags(Intent.FILL_IN_DATA);

        PendingIntent pending =
                PendingIntent.getActivity(context, 0, intent, 0);

        String tickerText = "有新版本";


        Notification notification = new Notification();
        notification.icon = R.drawable.p7;
        // notification.largeIcon = avatar;
        // notification.number = 5;

        notification.defaults = Notification.DEFAULT_LIGHTS;
        if (PhoneConfiguration.getInstance().notificationSound)
            notification.defaults |= Notification.DEFAULT_SOUND;
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        // Notification notification = new Notification(sp.phone.activity.R.drawable.defult_img,tickerText,
        //        System.currentTimeMillis());
        notification.tickerText = tickerText;
        notification.when = System.currentTimeMillis();
        //反正本功能也不用，先放着吧
//        notification.setLatestEventInfo(context, "更新内容", content, pending);
//        nm.notify(gov.anzong.androidnga.R.layout.pagerview_article_list, notification);
        super.onPostExecute(result);
    }


}
