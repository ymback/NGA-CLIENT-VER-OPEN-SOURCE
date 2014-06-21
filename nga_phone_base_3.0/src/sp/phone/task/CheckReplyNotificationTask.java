package sp.phone.task;

import gov.anzong.androidnga.activity.MyApp;
import gov.anzong.androidnga.activity.ReplyListActivity;
import gov.anzong.androidnga.R;

import java.util.ArrayList;
import java.util.List;

import sp.phone.bean.MsgNotificationObject;
import sp.phone.bean.NotificationObject;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.StringFindResult;
import sp.phone.bean.User;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.alibaba.fastjson.JSON;

public class CheckReplyNotificationTask extends
		AsyncTask<String, Integer, String> implements PerferenceConstant {
	final String url = "http://nga.178.com/nuke.php?func=noti&__notpl&__nodb&__nolib";
	final Context context;

	public CheckReplyNotificationTask(Context context) {
		this.context = context;
	}

	@Override
	protected String doInBackground(String... params) {

		final String cookie = params[0];
		// final String emptyMessage = "window.script_muti_get_var_store=null";
		String result = null;// emptyMessage;

		result = HttpUtil.getHtml(url, cookie, null, 3000);

		PhoneConfiguration.getInstance().lastMessageCheck = System
				.currentTimeMillis();
		Log.i(this.getClass().getSimpleName(), "get message:" + result);
//		return "{0:[{0:2,1:20174851,2:\"��Ԋ����\",3:20174851,4:\"��Ԋ����\",5:\"���������ⷢ�������ⷢ�������ⷢ��������\",9:1399199275,8:130348654,6:7031525,7:130397834,10:2},{0:2,1:20174851,2:\"��Ԋ����\",3:20174851,4:\"��Ԋ����\",5:\"���������ⷢ�������ⷢ�������ⷢ��������\",9:1399199294,8:130397834,6:7031525,7:130397842,10:3}],1:[{0:10,1:20174851,2:\"\326\361\276\256\324\212\277\227\300\357\",9:1403009958,6:121212},{0:10,1:20174851,2:\"\326\361\276\256\324\212\277\227\300\357\",9:1403009958,6:212121}]}";
		 return result;
	}

	@Override
	protected void onPostExecute(String totalresult) {

		if (StringUtil.isEmpty(totalresult)) {
			return;
		}
		String notiresult = "";
		String msgresult = "";

		if (totalresult.indexOf("1:[") > 0) {
			if (totalresult.indexOf("0:[") < 0) {
				msgresult = totalresult;
			} else {
				msgresult = "{"
						+ totalresult.substring(totalresult.indexOf("1:["));
			}
		}// msg��Ϣ
		if (totalresult.indexOf("0:[") > 0) {
			if (totalresult.indexOf("1:[") < 0) {
				notiresult = totalresult;
			} else {
				notiresult = totalresult.substring(0,
						totalresult.indexOf("1:[") - 1)
						+ "}";
			}
		}// �ظ���Ϣ

		if (StringUtil.isEmpty(msgresult) && StringUtil.isEmpty(notiresult)) {
			return;
		} else {
			if (!StringUtil.isEmpty(notiresult)) {
				int start = 0;

				while (true) {
					/*
					 * start = result.indexOf(",2:\"", start)+4; int end =
					 * result.indexOf("\",3:",start); String nickName =
					 * result.substring(start, end); start = end;
					 */
					StringFindResult ret = StringUtil.getStringBetween(
							notiresult, start, ",1:", ",2");
					start = ret.position;
					if (StringUtil.isEmpty(ret.result) || ret.position == -1)
						break;
					String authorId = ret.result;

					ret = StringUtil.getStringBetween(notiresult, start, ":\"",
							"\",3:");
					if (StringUtil.isEmpty(ret.result) || ret.position == -1)
						break;
					String nickName = ret.result;
					start = ret.position;

					/*
					 * start = result.indexOf(",5:\"", start)+4; end =
					 * result.indexOf("\",9:",start); String title =
					 * result.substring(start, end); start = end;
					 */

					ret = StringUtil.getStringBetween(notiresult, start,
							",5:\"", "\",9:");
					if (StringUtil.isEmpty(ret.result) || ret.position == -1)
						break;

					String title = ret.result;
					start = ret.position;

					/*
					 * start = result.indexOf(",6:", start)+3; end =
					 * result.indexOf(",7:",start); String tid =
					 * result.substring(start, end); start = end;
					 */
					ret = StringUtil.getStringBetween(notiresult, start, ",6:",
							",7");
					if (StringUtil.isEmpty(ret.result) || ret.position == -1)
						break;

					String tid = ret.result;
					if (tid.indexOf('}') > 0) {
						tid = tid.substring(0, tid.indexOf('}'));
					}

					start = ret.position;

					/*
					 * start = result.indexOf(",7:", start)+3; end =
					 * result.indexOf("}",start); String pid =
					 * result.substring(start, end); start = end;
					 */

					ret = StringUtil.getStringBetween(notiresult, start, ":",
							",10:");
					String pid = ret.result;
					if (!StringUtil.isEmpty(ret.result))
						start = ret.position;
					else
						pid = "0";

					title = StringUtil.unEscapeHtml(title);
					addNotification(authorId, nickName, tid, pid, title);
				}

				if (notificationList.size() == 1) {
					NotificationObject o = notificationList.get(0);
					showNotification(o.getNickName(),
							String.valueOf(o.getTid()),
							String.valueOf(o.getPid()), o.getTitle());
				} else if (notificationList.size() > 1) {
					showStackedNotification();
				}
				if (notificationList.size() > 0) {
					SharedPreferences share = context.getSharedPreferences(
							PERFERENCE, Context.MODE_PRIVATE);
					String strold = share.getString(PENDING_REPLYS, "");

					List<NotificationObject> list = new ArrayList<NotificationObject>();
					if (!StringUtil.isEmpty(strold)) {
						list = JSON
								.parseArray(strold, NotificationObject.class);
						list.addAll(notificationList);
					} else {
						list = notificationList;
					}
					String recentstr = JSON.toJSONString(list);
					PhoneConfiguration.getInstance().setReplyString(recentstr);
					PhoneConfiguration.getInstance().setReplyTotalNum(
							list.size());
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
										u.getNickName(), recentstr, list.size());
								break;
							}
						}
					} else {
						PhoneConfiguration.getInstance().setReplyString(
								recentstr);
						PhoneConfiguration.getInstance().setReplyTotalNum(
								list.size());
						Editor editor = share.edit();
						editor.putString(PENDING_REPLYS, recentstr);
						editor.putString(REPLYTOTALNUM,
								String.valueOf(list.size()));
						editor.commit();
					}
				}
			}//��������
			
			if(!StringUtil.isEmpty(msgresult)){
				int starta = 0;

				while (true) {
					/*
					 * start = result.indexOf(",2:\"", start)+4; int end =
					 * result.indexOf("\",3:",start); String nickName =
					 * result.substring(start, end); start = end;
					 */
					StringFindResult ret = StringUtil.getStringBetween(msgresult, starta,
							",1:", ",2");
					starta = ret.position-2;
					if (StringUtil.isEmpty(ret.result) || ret.position == -1){
						break;
					}
					String authorId = ret.result;

					ret = StringUtil.getStringBetween(msgresult, starta, ",2:\"", "\",");
					if (StringUtil.isEmpty(ret.result) || ret.position == -1){
						break;
					}
					String title = ret.result;
					starta = ret.position;

					ret = StringUtil.getStringBetween(msgresult, starta, ",6:", "}");
					if (StringUtil.isEmpty(ret.result) || ret.position == -1){
						break;
					}

					String mid = ret.result;
					starta = ret.position;

					title = StringUtil.unEscapeHtml(title);
					addMsgNotification(authorId, mid, title);
				}

				if (msgnotificationlist.size() == 1) {
					MsgNotificationObject o = msgnotificationlist.get(0);
					showMsgNotification(o.getAuthorId(),o.getMid(), o.getTitle());
				} else if (msgnotificationlist.size() > 1) {
					showStackedMsgNotification();
				}
			}//�������Ϣ
		}

	}

	List<NotificationObject> notificationList = new ArrayList<NotificationObject>();
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
	List<MsgNotificationObject> msgnotificationlist = new ArrayList<MsgNotificationObject>();
	void addMsgNotification(String authorid,
			String mid, String title) {


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


	void showStackedNotification() {
		String str = JSON.toJSONString(notificationList);
		SharedPreferences share = context.getSharedPreferences(PERFERENCE,
				Context.MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString(PENDING_REPLYS_FOR_SHOW, str);
		editor.commit();
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent();
		// intent.setFlags(Intent.flag)
		intent.setClass(context, ReplyListActivity.class);
		PendingIntent pending = PendingIntent
				.getActivity(context, 0, intent, 0);
		Notification notification = new Notification();
		notification.icon = R.drawable.nga_bg;
		notification.defaults = Notification.DEFAULT_LIGHTS;
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);

		if (PhoneConfiguration.getInstance().notificationSound
				&& audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
			switch (PhoneConfiguration.getInstance().blackgunsound) {
			case 0:
				notification.defaults |= Notification.DEFAULT_SOUND;
				break;
			case 1:
				notification.sound = Uri.parse("android.resource://"
						+ context.getPackageName() + "/" + R.raw.taijun);
				break;
			case 2:
				notification.sound = Uri.parse("android.resource://"
						+ context.getPackageName() + "/"
						+ R.raw.balckgunoftaijun);
				break;
			case 3:
				notification.sound = Uri.parse("android.resource://"
						+ context.getPackageName() + "/" + R.raw.balckgunofyou);
				break;
			default:
				notification.defaults |= Notification.DEFAULT_SOUND;
				break;
			}
		}
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		notification.tickerText = String.format(
				context.getString(R.string.multi_reply_format),
				notificationList.size());
		notification.when = System.currentTimeMillis();

		notification.number = notificationList.size();

		NotificationObject o = notificationList.get(0);
		notification.setLatestEventInfo(context, o.getNickName(), o.getTitle(),
				pending);
		nm.notify(R.layout.topiclist_activity, notification);
	}

	void showNotification(String nickName, String tid, String pid, String title) {

		if (StringUtil.isEmpty(tid)) {
			return;
		}

		Log.i(this.getClass().getSimpleName(), "showNotification: pid=" + pid
				+ ",tid=" + tid);

		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent = new Intent(context,
				PhoneConfiguration.getInstance().articleActivityClass);
		// intent.putExtra("tid", Integer.valueOf(tid).intValue());
		// Intent intent = new Intent(Intent.ACTION_VIEW);

		int pidValue = 0;
		try {
			if (!StringUtil.isEmpty(pid))
				pidValue = Integer.valueOf(pid).intValue();
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), "invalid pid: " + pid);
		}
		// intent.putExtra("pid", pidValue);

		Resources res = context.getResources();// .getString(R.string.myscheme)
		String url = res.getString(R.string.myscheme) + "://"
				+ res.getString(R.string.myhost) + "/read.php?tid=" + tid;
		if (pidValue != 0) {
			url = url + "&pid=" + pid;
		}
		intent.setData(Uri.parse(url));

		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK
		// );
		intent.addFlags(Intent.FILL_IN_DATA);

		PendingIntent pending = PendingIntent
				.getActivity(context, 0, intent, 0);

		String tickerText = nickName + context.getString(R.string.reply_to_you);

		Notification notification = new Notification();
		notification.icon = R.drawable.nga_bg;
		// notification.largeIcon = avatar;
		// notification.number = 5;

		notification.defaults = Notification.DEFAULT_LIGHTS;
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);

		if (PhoneConfiguration.getInstance().notificationSound
				&& audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
			switch (PhoneConfiguration.getInstance().blackgunsound) {
			case 0:
				notification.defaults |= Notification.DEFAULT_SOUND;
				break;
			case 1:
				notification.sound = Uri.parse("android.resource://"
						+ context.getPackageName() + "/" + R.raw.taijun);
				break;
			case 2:
				notification.sound = Uri.parse("android.resource://"
						+ context.getPackageName() + "/"
						+ R.raw.balckgunoftaijun);
				break;
			case 3:
				notification.sound = Uri.parse("android.resource://"
						+ context.getPackageName() + "/" + R.raw.balckgunofyou);
				break;
			default:
				notification.defaults |= Notification.DEFAULT_SOUND;
				break;
			}
		}
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		// Notification notification = new
		// Notification(sp.phone.activity.R.drawable.defult_img,tickerText,
		// System.currentTimeMillis());
		notification.tickerText = tickerText;
		notification.when = System.currentTimeMillis();
		int id = Integer.valueOf(tid).intValue();
		if (pidValue != 0)
			id = pidValue;

		notification.setLatestEventInfo(context, nickName, title, pending);
		nm.notify(id, notification);
	}
	
	

	void showMsgNotification(int authId, int mid, String title) {

		if (mid==0) {
			return;
		}

		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent = new Intent(context,
				PhoneConfiguration.getInstance().messageDetialActivity);
		

		Resources res = context.getResources();// .getString(R.string.myscheme)
		String url = res.getString(R.string.myscheme) + "://"
				+ res.getString(R.string.myhost) + "/nuke.php?func=message&mid=" + mid;
		intent.setData(Uri.parse(url));

		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK
		// );
		intent.addFlags(Intent.FILL_IN_DATA);

		PendingIntent pending = PendingIntent
				.getActivity(context, 0, intent, 0);

		String tickerText = String.format(
				context.getString(R.string.message_to_you),
				authId);

		Notification notification = new Notification();
		notification.icon = R.drawable.nga_bg;
		// notification.largeIcon = avatar;
		// notification.number = 5;

		notification.defaults = Notification.DEFAULT_LIGHTS;
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);

		if (PhoneConfiguration.getInstance().notificationSound
				&& audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
			switch (PhoneConfiguration.getInstance().blackgunsound) {
			case 0:
				notification.defaults |= Notification.DEFAULT_SOUND;
				break;
			case 1:
				notification.sound = Uri.parse("android.resource://"
						+ context.getPackageName() + "/" + R.raw.taijun);
				break;
			case 2:
				notification.sound = Uri.parse("android.resource://"
						+ context.getPackageName() + "/"
						+ R.raw.balckgunoftaijun);
				break;
			case 3:
				notification.sound = Uri.parse("android.resource://"
						+ context.getPackageName() + "/" + R.raw.balckgunofyou);
				break;
			default:
				notification.defaults |= Notification.DEFAULT_SOUND;
				break;
			}
		}
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		// Notification notification = new
		// Notification(sp.phone.activity.R.drawable.defult_img,tickerText,
		// System.currentTimeMillis());
		notification.tickerText = tickerText;
		notification.when = System.currentTimeMillis();
		notification.setLatestEventInfo(context, title, title+"("+String.valueOf(authId)+")���㷢���˶���Ϣ", pending);
		nm.notify(mid, notification);
	}

	void showStackedMsgNotification() {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent();
		// intent.setFlags(Intent.flag)
		intent.setClass(context, 
				PhoneConfiguration.getInstance().messageActivityClass);
		PendingIntent pending = PendingIntent
				.getActivity(context, 0, intent, 0);
		Notification notification = new Notification();
		notification.icon = R.drawable.nga_bg;
		notification.defaults = Notification.DEFAULT_LIGHTS;
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);

		if (PhoneConfiguration.getInstance().notificationSound
				&& audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
			switch (PhoneConfiguration.getInstance().blackgunsound) {
			case 0:
				notification.defaults |= Notification.DEFAULT_SOUND;
				break;
			case 1:
				notification.sound = Uri.parse("android.resource://"
						+ context.getPackageName() + "/" + R.raw.taijun);
				break;
			case 2:
				notification.sound = Uri.parse("android.resource://"
						+ context.getPackageName() + "/"
						+ R.raw.balckgunoftaijun);
				break;
			case 3:
				notification.sound = Uri.parse("android.resource://"
						+ context.getPackageName() + "/" + R.raw.balckgunofyou);
				break;
			default:
				notification.defaults |= Notification.DEFAULT_SOUND;
				break;
			}
		}
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		notification.tickerText = String.format(
				context.getString(R.string.multi_message_format),
				msgnotificationlist.size());
		notification.when = System.currentTimeMillis();

		notification.number = msgnotificationlist.size();

		MsgNotificationObject o = msgnotificationlist.get(0);
		notification.setLatestEventInfo(context, o.getTitle(), o.getTitle()+"("+String.valueOf(o.getAuthorId())+")�����㷢���˶���Ϣ",
				pending);
		nm.notify(R.layout.messagelist_activity, notification);
	}
}
