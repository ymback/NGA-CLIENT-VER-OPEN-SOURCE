package sp.phone.task;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.activity.MyApp;
import gov.anzong.androidnga.activity.ReplyListActivity;
import sp.phone.bean.MsgNotificationObject;
import sp.phone.bean.NotificationObject;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.User;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;

public class CheckReplyNotificationTask extends
        AsyncTask<String, Integer, String> implements PerferenceConstant {
    final String url = Utils.getNGAHost() + "nuke.php?__lib=noti&raw=3&__act=get_all";
    final Context context;
    final String TAG = getClass().getSimpleName();
    List<NotificationObject> notificationList = new ArrayList<NotificationObject>();
    List<MsgNotificationObject> msgnotificationlist = new ArrayList<MsgNotificationObject>();

    public CheckReplyNotificationTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        final String cookie = params[0];
        String result = "";

        result = HttpUtil.getHtml(url, cookie, null, 3000);

        PhoneConfiguration.getInstance().lastMessageCheck = System
                .currentTimeMillis();
        Log.i(this.getClass().getSimpleName(), "get message:" + result);
        // return
        // "[truncated]window.script_muti_get_var_store={0:[{\"0\":1,\"1\":20174851,\"2\":\"\326\361\276\256\324\212\277\227\300\357\",\"3\":20174851,\"4\":\"\326\361\276\256\324\212\277\227\300\357\",\"5\":\"\277\264\262\273\265\275\316\322\",\"9\":1403393562,\"6\":71387683,\"7\":133146646,\"10\":1}] }";
//		return "get message:<html><head><meta http-equiv='Content-Type' content='text/html; charset=GBK'></head><body><script>window.script_muti_get_var_store={\"data\":{\"0\":{\"0\":[{\"9\":1418355735 ,\"0\":1,\"1\":20174851,\"2\":\"竹井詩織里\",\"3\":20174851,\"4\":\"竹井詩織里\",\"5\":\"我喜欢测试\",\"6\":7638703,\"7\":143928028,\"10\":1}],\"unread\":0}},\"time\":1418357357}</script></body></html>";
        return result;
    }

    @Override
    protected void onPostExecute(String totalresult) {

        if (StringUtil.isEmpty(totalresult)) {
            return;
        }
        totalresult = StringUtil.getStringBetween(totalresult, 0,
                "window.script_muti_get_var_store=", "</script>").result;
        String notiresult = "";
        String msgresult = "";
        JSONObject ojson = new JSONObject();
        JSONArray ojsonnoti = new JSONArray();
        JSONArray ojsonmsg = new JSONArray();
        int unread = 0;
        try {
            ojson = (JSONObject) JSON.parseObject(totalresult);
            ojson = (JSONObject) ojson.get("data");
            ojson = (JSONObject) ojson.get("0");
            unread = ojson.getIntValue("unread");
        } catch (Exception e) {
        }
        if (ojson == null) {
            return;
        }
        try {
            ojsonnoti = (JSONArray) ojson.get("0");
            ojsonmsg = (JSONArray) ojson.get("1");
        } catch (Exception e) {
            Log.i(TAG, "JSON DATA ERROR");
        }
        if (unread < 1) {//都读了，顺便刷新一次数据
            if (ojsonnoti != null) {
                if (ojsonnoti.size() > 0) {
                    for (int i = 0; i < ojsonnoti.size(); i++) {
                        try {
                            JSONObject ojsonnotidata = (JSONObject) ojsonnoti
                                    .get(i);
                            String authorId = ojsonnotidata.getString("1");
                            String nickName = ojsonnotidata.getString("2");
                            String tid = ojsonnotidata.getString("6");
                            String pid = ojsonnotidata.getString("7");
                            String title = ojsonnotidata.getString("5");
                            if (!StringUtil.isEmpty(authorId)
                                    && !StringUtil.isEmpty(nickName)
                                    && !StringUtil.isEmpty(tid)
                                    && !StringUtil.isEmpty(pid)
                                    && !StringUtil.isEmpty(title)) {
                                title = StringUtil.unEscapeHtml(title);
                                addNotification(authorId, nickName, tid, pid, title);
                            }
                        } catch (Exception e) {

                        }
                    }
                }
            }
            if (notificationList.size() > 0) {
                SharedPreferences share = context.getSharedPreferences(PERFERENCE,
                        Context.MODE_PRIVATE);
                String strold = share.getString(PENDING_REPLYS, "");

                List<NotificationObject> list = new ArrayList<NotificationObject>();
                list = notificationList;
                String recentstr = JSON.toJSONString(list);
                PhoneConfiguration.getInstance().setReplyString(recentstr);
                PhoneConfiguration.getInstance().setReplyTotalNum(list.size());
                String userListString = share.getString(USER_LIST, "");
                List<User> userList = null;
                if (!StringUtil.isEmpty(userListString)) {
                    userList = JSON.parseArray(userListString, User.class);
                    for (User u : userList) {
                        if (u.getUserId().equals(
                                PhoneConfiguration.getInstance().uid)) {
                            MyApp app = (MyApp) ((Activity) context)
                                    .getApplication();
                            app.addToUserList(u.getUserId(), u.getCid(),
                                    u.getNickName(), recentstr, list.size(),
                                    u.getBlackList());
                            break;
                        }
                    }
                } else {
                    PhoneConfiguration.getInstance().setReplyString(recentstr);
                    PhoneConfiguration.getInstance().setReplyTotalNum(list.size());
                    Editor editor = share.edit();
                    editor.putString(PENDING_REPLYS, recentstr);
                    editor.putString(REPLYTOTALNUM, String.valueOf(list.size()));
                    editor.apply();
                }
            }

            return;
        }

        if (ojsonnoti != null) {
            if (ojsonnoti.size() > 0) {
                for (int i = 0; i < ojsonnoti.size(); i++) {
                    try {
                        JSONObject ojsonnotidata = (JSONObject) ojsonnoti
                                .get(i);
                        String authorId = ojsonnotidata.getString("1");
                        String nickName = ojsonnotidata.getString("2");
                        String tid = ojsonnotidata.getString("6");
                        String pid = ojsonnotidata.getString("7");
                        String title = ojsonnotidata.getString("5");
                        if (!StringUtil.isEmpty(authorId)
                                && !StringUtil.isEmpty(nickName)
                                && !StringUtil.isEmpty(tid)
                                && !StringUtil.isEmpty(pid)
                                && !StringUtil.isEmpty(title)) {
                            title = StringUtil.unEscapeHtml(title);
                            addNotification(authorId, nickName, tid, pid, title);
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }
        if (ojsonmsg != null) {
            if (ojsonmsg.size() > 0) {
                for (int i = 0; i < ojsonmsg.size(); i++) {
                    JSONObject ojsonmsgdata = (JSONObject) ojsonmsg.get(i);
                    String authorId = ojsonmsgdata.getString("1");
                    String mid = ojsonmsgdata.getString("6");
                    String title = ojsonmsgdata.getString("2");
                    if (!StringUtil.isEmpty(authorId)
                            && !StringUtil.isEmpty(mid)
                            && !StringUtil.isEmpty(title)) {
                        title = StringUtil.unEscapeHtml(title);
                        addMsgNotification(authorId, mid, title);
                    }
                }
            }
        }
        if (msgnotificationlist.size() == 1) {
            MsgNotificationObject o = msgnotificationlist.get(0);
            showMsgNotification(o.getAuthorId(), o.getMid(), o.getTitle());
        } else if (msgnotificationlist.size() > 1) {
            showStackedMsgNotification();
        }

        if (notificationList.size() == 1) {
            NotificationObject o = notificationList.get(0);
            showNotification(o.getNickName(), String.valueOf(o.getTid()),
                    String.valueOf(o.getPid()), o.getTitle());
        } else if (notificationList.size() > 1) {
            showStackedNotification();
        }
    }

    void addNotification(String authorid, String nickName, String tid,
                         String pid, String title) {
        if (StringUtil.isEmpty(tid)) {
            return;
        }
        int pidNum = 0;
        try {
            pidNum = Integer.parseInt(pid);
        } catch (Exception e) {
            pidNum = 0;
        }

        if (notificationList.size() > 0) {
            NotificationObject last = notificationList.get(notificationList
                    .size() - 1);
            if (last.getTid() == Integer.parseInt(tid)
                    && last.getPid() == pidNum) {
                return;
            }
        }

        NotificationObject o = new NotificationObject();
        o.setAuthorId(Integer.parseInt(authorid));
        o.setNickName(nickName);
        o.setTid(Integer.parseInt(tid));

        o.setPid(pidNum);

        o.setTitle(title);
        notificationList.add(o);

    }

    void addMsgNotification(String authorid, String mid, String title) {

        if (StringUtil.isEmpty(mid)) {
            return;
        }
        int midNum = 0;
        try {
            midNum = Integer.parseInt(mid);
        } catch (Exception e) {
            return;
        }

        MsgNotificationObject o = new MsgNotificationObject();
        o.setAuthorId(Integer.parseInt(authorid));
        o.setMid(midNum);

        o.setTitle(title);
        msgnotificationlist.add(o);

    }

    @SuppressWarnings("deprecation")
    void showStackedNotification() {
        String str = JSON.toJSONString(notificationList);
        SharedPreferences share = context.getSharedPreferences(PERFERENCE,
                Context.MODE_PRIVATE);
        Editor editor = share.edit();
        editor.putString(PENDING_REPLYS_FOR_SHOW, str);
        editor.apply();


        Intent intent = new Intent();
        intent.setClass(context, ReplyListActivity.class);
        PendingIntent pending = PendingIntent
                .getActivity(context, 0, intent, 0);

        NotificationObject o = notificationList.get(0);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setTicker(String.format(
                context.getString(R.string.multi_reply_format),
                notificationList.size()));
        builder.setNumber(notificationList.size());
        builder.setLights(Color.parseColor("#fff0cd"), 2333, 0);
        builder.setSmallIcon(R.drawable.nga_bg); //设置图标
        builder.setContentTitle(o.getNickName()); //设置标题
        builder.setContentText(o.getTitle()); //消息内容
        builder.setWhen(System.currentTimeMillis()); //发送时间
        builder.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
        builder.setAutoCancel(true);//打开程序后图标消失
        builder.setContentIntent(pending);
        int defaults = Notification.DEFAULT_LIGHTS;
        AudioManager audioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);

        if (PhoneConfiguration.getInstance().notificationSound
                && audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            switch (PhoneConfiguration.getInstance().blackgunsound) {
                case 0:
                    defaults |= Notification.DEFAULT_SOUND;
                    break;
                case 1:
                    builder.setSound(Uri.parse("android.resource://"
                            + context.getPackageName() + "/" + R.raw.taijun));
                    break;
                case 2:
                    builder.setSound(Uri.parse("android.resource://"
                            + context.getPackageName() + "/"
                            + R.raw.balckgunoftaijun));
                    break;
                case 3:
                    builder.setSound(Uri.parse("android.resource://"
                            + context.getPackageName() + "/" + R.raw.balckgunofyou));
                    break;
                default:
                    defaults |= Notification.DEFAULT_SOUND;
                    break;
            }
        }
        builder.setDefaults(defaults);

        Notification notification1 = builder.build();
        notification1.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(R.layout.topiclist_activity, notification1); // 通过通知管理器发送通知
    }

    @SuppressWarnings("deprecation")
    void showNotification(String nickName, String tid, String pid, String title) {

        if (StringUtil.isEmpty(tid)) {
            return;
        }

        Log.i(this.getClass().getSimpleName(), "showNotification: pid=" + pid
                + ",tid=" + tid);

        Intent intent = new Intent(context,
                PhoneConfiguration.getInstance().articleActivityClass);

        int pidValue = 0;
        try {
            if (!StringUtil.isEmpty(pid))
                pidValue = Integer.valueOf(pid);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "invalid pid: " + pid);
        }
        Resources res = context.getResources();
        String url = res.getString(R.string.myscheme) + "://"
                + res.getString(R.string.myhost) + "/read.php?tid=" + tid;
        if (pidValue != 0) {
            url = url + "&pid=" + pid;
        }
        intent.setData(Uri.parse(url));
        intent.putExtra("fromreplyactivity", 1);

        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK
        // );
        intent.addFlags(Intent.FILL_IN_DATA);

        PendingIntent pending = PendingIntent
                .getActivity(context, 0, intent, 0);

        int id = Integer.valueOf(tid);
        if (pidValue != 0) {
            id = pidValue;
        }


        Notification.Builder builder = new Notification.Builder(context);
        builder.setTicker(nickName + context.getString(R.string.reply_to_you));
        builder.setLights(Color.parseColor("#fff0cd"), 2333, 0);
        builder.setSmallIcon(R.drawable.nga_bg); //设置图标
        builder.setContentTitle(nickName); //设置标题
        builder.setContentText(title); //消息内容
        builder.setWhen(System.currentTimeMillis()); //发送时间
        builder.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
        builder.setAutoCancel(true);//打开程序后图标消失
        builder.setContentIntent(pending);
        int defaults = Notification.DEFAULT_LIGHTS;
        AudioManager audioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);

        if (PhoneConfiguration.getInstance().notificationSound
                && audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            switch (PhoneConfiguration.getInstance().blackgunsound) {
                case 0:
                    defaults |= Notification.DEFAULT_SOUND;
                    break;
                case 1:
                    builder.setSound(Uri.parse("android.resource://"
                            + context.getPackageName() + "/" + R.raw.taijun));
                    break;
                case 2:
                    builder.setSound(Uri.parse("android.resource://"
                            + context.getPackageName() + "/"
                            + R.raw.balckgunoftaijun));
                    break;
                case 3:
                    builder.setSound(Uri.parse("android.resource://"
                            + context.getPackageName() + "/" + R.raw.balckgunofyou));
                    break;
                default:
                    defaults |= Notification.DEFAULT_SOUND;
                    break;
            }
        }
        builder.setDefaults(defaults);

        Notification notification1 = builder.build();
        notification1.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(id, notification1); // 通过通知管理器发送通知
    }

    @SuppressWarnings("deprecation")
    void showMsgNotification(int authId, int mid, String title) {

        if (mid == 0) {
            return;
        }
        Intent intent = new Intent(context,
                PhoneConfiguration.getInstance().messageDetialActivity);

        Resources res = context.getResources();// .getString(R.string.myscheme)
        String url = res.getString(R.string.myscheme) + "://"
                + res.getString(R.string.myhost)
                + "/nuke.php?func=message&mid=" + mid;
        intent.setData(Uri.parse(url));

        intent.addFlags(Intent.FILL_IN_DATA);

        PendingIntent pending = PendingIntent
                .getActivity(context, 0, intent, 0);

        String tickerText = String.format(
                context.getString(R.string.message_to_you), title);


        Notification.Builder builder = new Notification.Builder(context);
        builder.setTicker(tickerText);
        builder.setLights(Color.parseColor("#fff0cd"), 2333, 0);
        builder.setSmallIcon(R.drawable.nga_bg); //设置图标
        builder.setContentTitle(title); //设置标题
        builder.setContentText(title + "(" + String.valueOf(authId) + ")向你发送了短消息"); //消息内容
        builder.setWhen(System.currentTimeMillis()); //发送时间
        builder.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
        builder.setAutoCancel(true);//打开程序后图标消失
        builder.setContentIntent(pending);
        int defaults = Notification.DEFAULT_LIGHTS;
        AudioManager audioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);

        if (PhoneConfiguration.getInstance().notificationSound
                && audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            switch (PhoneConfiguration.getInstance().blackgunsound) {
                case 0:
                    defaults |= Notification.DEFAULT_SOUND;
                    break;
                case 1:
                    builder.setSound(Uri.parse("android.resource://"
                            + context.getPackageName() + "/" + R.raw.taijun));
                    break;
                case 2:
                    builder.setSound(Uri.parse("android.resource://"
                            + context.getPackageName() + "/"
                            + R.raw.balckgunoftaijun));
                    break;
                case 3:
                    builder.setSound(Uri.parse("android.resource://"
                            + context.getPackageName() + "/" + R.raw.balckgunofyou));
                    break;
                default:
                    defaults |= Notification.DEFAULT_SOUND;
                    break;
            }
        }
        builder.setDefaults(defaults);

        Notification notification1 = builder.build();
        notification1.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(mid, notification1); // 通过通知管理器发送通知
    }

    @SuppressWarnings("deprecation")
    void showStackedMsgNotification() {
        Intent intent = new Intent();
        // intent.setFlags(Intent.flag)
        intent.setClass(context,
                PhoneConfiguration.getInstance().messageActivityClass);
        PendingIntent pending = PendingIntent
                .getActivity(context, 0, intent, 0);

        MsgNotificationObject o = msgnotificationlist.get(0);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setTicker(String.format(
                context.getString(R.string.multi_message_format),
                msgnotificationlist.size()));
        builder.setLights(Color.parseColor("#fff0cd"), 2333, 0);
        builder.setSmallIcon(R.drawable.nga_bg); //设置图标
        builder.setNumber(msgnotificationlist.size());
        builder.setContentTitle( o.getTitle()); //设置标题
        builder.setContentText(o.getTitle() + "("
                + String.valueOf(o.getAuthorId()) + ")等向你发送了短消息"); //消息内容
        builder.setWhen(System.currentTimeMillis()); //发送时间
        builder.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
        builder.setAutoCancel(true);//打开程序后图标消失
        builder.setContentIntent(pending);
        int defaults = Notification.DEFAULT_LIGHTS;
        AudioManager audioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);

        if (PhoneConfiguration.getInstance().notificationSound
                && audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            switch (PhoneConfiguration.getInstance().blackgunsound) {
                case 0:
                    defaults |= Notification.DEFAULT_SOUND;
                    break;
                case 1:
                    builder.setSound(Uri.parse("android.resource://"
                            + context.getPackageName() + "/" + R.raw.taijun));
                    break;
                case 2:
                    builder.setSound(Uri.parse("android.resource://"
                            + context.getPackageName() + "/"
                            + R.raw.balckgunoftaijun));
                    break;
                case 3:
                    builder.setSound(Uri.parse("android.resource://"
                            + context.getPackageName() + "/" + R.raw.balckgunofyou));
                    break;
                default:
                    defaults |= Notification.DEFAULT_SOUND;
                    break;
            }
        }
        builder.setDefaults(defaults);

        Notification notification1 = builder.build();
        notification1.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(R.layout.messagelist_activity, notification1); // 通过通知管理器发送通知
    }
}
