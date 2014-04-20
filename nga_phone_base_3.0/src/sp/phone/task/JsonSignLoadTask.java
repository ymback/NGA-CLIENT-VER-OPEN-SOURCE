package sp.phone.task;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import sp.phone.bean.MissionDetialData;
import sp.phone.bean.SignData;
import sp.phone.bean.ThreadPageInfo;
import sp.phone.interfaces.OnSignPageLoadFinishedListener;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.MD5Util;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import gov.anzong.androidnga.R;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class JsonSignLoadTask extends AsyncTask<String, Integer, SignData> {
	static final String TAG = JsonSignLoadTask.class.getSimpleName();
	final private Context context;
	final private OnSignPageLoadFinishedListener notifier;
	private String error;
	private Toast toast;

	public JsonSignLoadTask(Context context,
			OnSignPageLoadFinishedListener notifier) {
		super();
		this.context = context;
		this.notifier = notifier;
	}

	private String url;

	@Override
	protected SignData doInBackground(String... params) {
		if (params.length == 0)
			return null;
		url = "http://nga.178.com/nuke.php?__lib=check_in&lite=js&noprefix&__act=check_in&action=add&__ngaClientChecksum="
				+ getngaClientChecksum();

		Log.d(TAG, "start to load:" + url);

		SignData result = this.loadAndParseJsonPage(url);
		return result;
	}

	private SignData loadAndParseJsonPage(String uri) {
		// Log.d(TAG, "start to load:" + uri);
		String js;
		 js = HttpUtil.getHtml(uri,PhoneConfiguration.getInstance().getCookie());
//		 Log.i(TAG,js);
		List<MissionDetialData> EntryList = new ArrayList<MissionDetialData>();
//		js = "{\"data\":{\"0\":\"签到成功\",\"1\":{\"uid\":58,\"continued\":3,\"sum\":38,\"last_time\":1397270302},\"2\":{\"success\":{\"1\":{\"id\":1,\"name\":\"测试任务1S\",\"info\":\"测试\n   任务信息\",\"detail\":\"任务必须满足以下条件:连续签到3天,...\",\"stat\":\"目前已经连续签到1天\n共计签到1\n任务于 2014-04-11 18:11:48 完成\"},\"2\":{\"id\":2,\"name\":\"测试任务2S\",\"info\":\"测试\n   任务信息\",\"detail\":\"任务必须满足以下条件:连续签到3天,...\",\"stat\":\"目前已经连续签到1天\n共计签到1\n任务于 2014-04-11 18:11:48 完成\"}},\"available\":{\"1\":{\"id\":1,\"name\":\"测试任务1A\",\"info\":\"测试\n   任务信息\",\"detail\":\"任务必须满足以下条件:连续签到3天,...\",\"stat\":\"目前已经连续签到1天\n共计签到1\n任务于 2014-04-11 18:11:48 完成\"},\"2\":{\"id\":2,\"name\":\"测试任务2A\",\"info\":\"测试\n   任务信息\",\"detail\":\"任务必须满足以下条件:连续签到3天,...\",\"stat\":\"目前已经连续签到1天\n共计签到1\n任务于 2014-04-11 18:11:48 完成\"}}}},\"time\":1397452410}";
//		 js="window.script_muti_get_var_store={\"error\":{\"0\":\"你今天已经签到了(以服务器时间计算)\"},\"time\":1397704424}";//错误模式
//		js="{\"data\":{\"0\":\"签到成功\",\"1\":{\"uid\":15776622,\"continued\":1,\"sum\":1,\"last_time\":0},\"2\":{\"available\":{\"1\":{\"id\":1,\"name\":\"测试任务\",\"info\":\"测试\n	任务信息\",\"detail\":\"任务必须满足以下条件:\n连续签到3天(全局), \n\n任务可以获得以下奖励:\n铜币:1, \n\n任务可以重复完成, 每2天一次\n\n\",\"stat\":\"目前已经连续签到1天\n共计签到1\n\n\"}}}},\"time\":1397949753}";
//		js="S)DB ERROR";
		if (null == js) {
			error = context.getString(R.string.network_error);
			return null;
		}
		if (js.indexOf("DB ERROR") >= 0) {
			error = "二哥压根没弄好,数据库错误";
			return null;
		}// 测试模式
		js = js.replaceAll("window.script_muti_get_var_store=", "");
		if (js.indexOf("/*error fill content") > 0)
			js = js.substring(0, js.indexOf("/*error fill content"));// 二哥不会那么无聊了,不过还是留着吧
		js = js.replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
		js = js.replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",");
		js = js.replaceAll("/\\*\\$js\\$\\*/", "");
		JSONObject o = null, oerror = null;
		try {
			o = (JSONObject) JSON.parseObject(js).get("data");
			oerror = (JSONObject) JSON.parseObject(js).get("error");
		} catch (Exception e) {
			Log.e(TAG, "can not parse :\n" + js);
		}
		SignData ret = new SignData();
		if (o == null) {
			if (oerror == null) {
				error = "请重新登录";
				return null;
			} else {
				Log.i(TAG, oerror.getString("0"));
				if (oerror.getString("0") != null) {
					error = oerror.getString("0");
					ret.set__is_json_error(true);
					ret.set__SignResult(error);
					ret.setEntryList(EntryList);
					ret.set__Successrows(0);
					ret.set__Availablerows(0);
					ret.set__Totalrows(0);
					return ret;
				}
			}
		}
		String SignResult = o.getString("0");
		ret.set__SignResult(SignResult);
		JSONObject o1 = (JSONObject) o.get("1");
		if (null == o1) {
			error = "二哥玩坏了或者你需要重新登录";
			return null;
		}
		ret.set__Uid(Integer.parseInt(o1.getString("uid")));
		ret.set__Continued(Integer.parseInt(o1.getString("continued")));
		ret.set__Sum(Integer.parseInt(o1.getString("sum")));
		ret.set__Uid(Integer.parseInt(o1.getString("uid")));
		if (!StringUtil.isEmpty(o1.getString("last_time"))) {
			if(o1.getString("last_time").equals("0")){
				ret.set__Last_time("从未");
			}else{
				ret.set__Last_time(TimeStamp2Date(o1.getString("last_time")));
			}
		}
		JSONObject o2 = null;
		if (o.get("2") instanceof JSONObject) {
			o2 = (JSONObject) o.get("2");
		} else {
			error = "二哥玩坏了或者你需要重新登录";
			return null;
		}
		JSONObject o2success = null;
		int i = 1;
		int total = 0;
		if (o2.get("success") instanceof JSONObject) {
			o2success = (JSONObject) o2.get("success");

			JSONObject o2successdetail = null;
			if (o2success.get(String.valueOf(i)) != null) {
				o2successdetail = (JSONObject) o2success.get(String.valueOf(i));
			}
			for (i = 2; o2successdetail != null; i++) {
				try {
					MissionDetialData entry = new MissionDetialData();
					entry.set__id(Integer.parseInt(o2successdetail.getString("id")));
					entry.set__info(o2successdetail.getString("info"));
					entry.set__name(o2successdetail.getString("name"));
					entry.set__detail(o2successdetail.getString("detail"));
					entry.set__stat(o2successdetail.getString("stat"));
					entry.set__issuccessed(true);
					EntryList.add(entry);
					if (o2success.get(String.valueOf(i)) != null) {
						o2successdetail = (JSONObject) o2success.get(String
								.valueOf(i));
					} else {
						o2successdetail = null;
					}
				} catch (Exception e) {
				}
			}
			ret.set__Successrows(i - 2);
			total += i - 2;
		}else{
			ret.set__Successrows(0);
		}
		
		i = 1;

		JSONObject o2available = null;
		if (o2.get("available") instanceof JSONObject) {
			o2available = (JSONObject) o2.get("available");
			JSONObject o2availabledetail = null;
			if (o2available.get(String.valueOf(i)) != null) {
				o2availabledetail = (JSONObject) o2available.get(String.valueOf(i));
			}
			for (i = 2; o2availabledetail != null; i++) {
				try {
					MissionDetialData entry = new MissionDetialData();
					entry.set__id(Integer.parseInt(o2availabledetail
							.getString("id")));
					entry.set__info(o2availabledetail.getString("info"));
					entry.set__name(o2availabledetail.getString("name"));
					entry.set__detail(o2availabledetail.getString("detail"));
					entry.set__stat(o2availabledetail.getString("stat"));
					entry.set__issuccessed(false);
					EntryList.add(entry);
					if (o2available.get(String.valueOf(i)) != null) {
						o2availabledetail = (JSONObject) o2available.get(String
								.valueOf(i));
					} else {
						o2availabledetail = null;
					}
				} catch (Exception e) {
				}
			}
			ret.set__Availablerows(i - 2);
			total += i - 2;
		}else{
			ret.set__Availablerows(0);
		}
		
		ret.setEntryList(EntryList);
		ret.set__Totalrows(total);

		return ret;

	}

	public static String TimeStamp2Date(String timestampString) {
		Long timestamp = Long.parseLong(timestampString) * 1000;
		String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new java.util.Date(timestamp));
		return date;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(SignData result) {
		ActivityUtil.getInstance().dismiss();
		if (result == null) {
			ActivityUtil.getInstance().noticeError(error, context);
		}else{
			Log.i(TAG,"ONPOSTEXECUTE");
			if (result.get__SignResult().equals("签到成功")) {
				if (toast != null) {
					toast.setText(result.get__SignResult());
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.show();
				} else {
					toast = Toast.makeText(context, result.get__SignResult(),
							Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		}
		notifier.jsonfinishLoad(result);

		super.onPostExecute(result);
	}

	private String getngaClientChecksum() {
		String str = null;
		String secret = context.getResources().getString(R.string.checksecret);
		try {
			str = MD5Util.MD5(new StringBuilder(String
					.valueOf(PhoneConfiguration.getInstance().getUid()))
					.append(secret).append(System.currentTimeMillis() / 1000L)
					.toString())
					+ System.currentTimeMillis() / 1000L;
			return str;
		} catch (Exception localException) {
			while (true)
				str = MD5Util.MD5(new StringBuilder(secret).append(
						System.currentTimeMillis() / 1000L).toString())
						+ System.currentTimeMillis() / 1000L;
		}
	}

	@Override
	protected void onCancelled() {
		ActivityUtil.getInstance().dismiss();
		super.onCancelled();
	}

}
