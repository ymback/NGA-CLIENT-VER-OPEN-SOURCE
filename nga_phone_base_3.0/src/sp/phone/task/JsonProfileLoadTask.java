package sp.phone.task;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import sp.phone.bean.MissionDetialData;
import sp.phone.bean.ProfileData;
import sp.phone.bean.ReputationData;
import sp.phone.bean.SignData;
import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.adminForumsData;
import sp.phone.interfaces.OnProfileLoadFinishedListener;
import sp.phone.interfaces.OnSignPageLoadFinishedListener;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.MD5Util;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import gov.anzong.androidnga2.R;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class JsonProfileLoadTask extends
		AsyncTask<String, Integer, ProfileData> {
	static final String TAG = JsonProfileLoadTask.class.getSimpleName();
	final private Context context;
	final private OnProfileLoadFinishedListener notifier;
	private String error;
	private Toast toast;

	public JsonProfileLoadTask(Context context,
			OnProfileLoadFinishedListener notifier) {
		super();
		this.context = context;
		this.notifier = notifier;
	}

	private String url;

	@Override
	protected ProfileData doInBackground(String... params) {
		if (params.length == 0)
			return null;
		url = "http://nga.178.com/nuke.php?__lib=ucp&__act=get&lite=js&noprefix&"
				+ params[0];

		Log.d(TAG, "start to load:" + url);

		ProfileData result = this.loadAndParseJsonPage(url);
		return result;
	}

	private ProfileData loadAndParseJsonPage(String uri) {
		String js;
		js = HttpUtil
				.getHtml(uri, PhoneConfiguration.getInstance().getCookie());
		// Log.i(TAG,js);
		List<ReputationData> EntryList = new ArrayList<ReputationData>();
		List<adminForumsData> EntryList2 = new ArrayList<adminForumsData>();

		if (null == js) {
			error = context.getString(R.string.network_error);
			return null;
		}
		js = js.replaceAll("window.script_muti_get_var_store=", "");
		if (js.indexOf("/*error fill content") > 0)
			js = js.substring(0, js.indexOf("/*error fill content"));// 二哥不会那么无聊了,不过还是留着吧
		js = js.replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
		js = js.replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",");
		js = js.replaceAll("/\\*\\$js\\$\\*/", "");
		JSONObject o = null, oerror = null, o0 = null, oreputation = null, oadminForums = null;
		try {
			o = (JSONObject) JSON.parseObject(js).get("data");
			oerror = (JSONObject) JSON.parseObject(js).get("error");
		} catch (Exception e) {
			Log.e(TAG, "can not parse :\n" + js);
		}
		ProfileData ret = new ProfileData();
		if (o == null) {
			if (null == oerror) {
				error = "二哥玩坏了或者你需要重新登录";
				return null;
			} else {
				error = oerror.getString("0");
				return null;
			}
		}
		try {
			o0 = (JSONObject) o.get("0");
			oreputation = (JSONObject) o0.get("reputation");
			oadminForums = (JSONObject) o0.get("adminForums");
		} catch (Exception e) {
			Log.e(TAG, "can not parse :\n" + js);
		}
		if (null == o0) {
			error = "请重新登录";
			return null;
		}

		if (!StringUtil.isEmpty(o0.getString("uid"))) {
			ret.set_uid(o0.getString("uid"));
		} else {
			ret.set_uid("未知");
		}
		if (!StringUtil.isEmpty(o0.getString("username"))) {
			ret.set_username(o0.getString("username"));
		} else {
			ret.set_uid("未知");
		}
		if (!StringUtil.isEmpty(o0.getString("email"))) {
			ret.set_hasemail(true, o0.getString("email"));
		}
		if (!StringUtil.isEmpty(o0.getString("fame"))) {
			ret.set_fame(o0.getString("fame"));
		}else{
			ret.set_fame("0");
		}
		if (!StringUtil.isEmpty(o0.getString("phone"))) {
			ret.set_hastel(true, o0.getString("phone"));
		}
		String group = null;
		if (o0.getString("group") != null) {
			group = o0.getString("group");
		}
		if (o0.getString("groupid") != null) {
			group = group + " (" + o0.getString("groupid") + ")";
		}
		if (!StringUtil.isEmpty(group)) {
			ret.set_group(group);
		} else {
			ret.set_group("未知");
		}
		if (!StringUtil.isEmpty(o0.getString("posts"))) {
			ret.set_posts(o0.getString("posts"));
		} else {
			ret.set_posts("0");
		}
		if (!StringUtil.isEmpty(o0.getString("money"))) {
			ret.set_money(o0.getString("money"));
		} else {
			ret.set_money("0");
		}
		if (!StringUtil.isEmpty(o0.getString("title"))) {
			String title = o0.getString("title");
			title = title.substring(title.lastIndexOf(" ") + 1, title.length());
			ret.set_title(title);
		} else {
			ret.set_title("无");
		}
		if (!StringUtil.isEmpty(o0.getString("verified"))) {
			ret.set_verified(o0.getString("verified"));
		} else {
			ret.set_verified("1");
		}
		if (!StringUtil.isEmpty(o0.getString("muteTime"))) {
			if(o0.getString("muteTime").equals("0")){
				ret.set_muteTime("-1");
			}else{
				ret.set_muteTime("禁言至: " + TimeStamp2Date(o0.getString("muteTime")));
			}
		}else{
			ret.set_muteTime("-1");
		}
		if (!StringUtil.isEmpty(o0.getString("regdate"))) {
			ret.set_regdate(TimeStamp2Date(o0.getString("regdate")));
		} else {
			ret.set_regdate("未知");
		}
		if (!StringUtil.isEmpty(o0.getString("lastpost"))) {
			ret.set_lastpost(TimeStamp2Date(o0.getString("lastpost")));
		} else {
			ret.set_lastpost("未知");
		}
		if (!StringUtil.isEmpty(o0.getString("sign"))) {
			ret.set_sign(o0.getString("sign"));
		}else{
			ret.set_sign("无签名");
		}
		if (!StringUtil.isEmpty(o0.getString("avatar"))) {
			ret.set_avatar(o0.getString("avatar"));
		}
		int i = 0;
		if (oreputation != null) {
			JSONObject oreputationdata = null;
			if (oreputation.get(String.valueOf(i)) != null) {
				oreputationdata = (JSONObject) oreputation.get(String
						.valueOf(i));
			}
			for (i = 1; oreputationdata != null; i++) {
				try {
					ReputationData entry = new ReputationData();
					if(!StringUtil.isEmpty(oreputationdata.getString("0"))){
						entry.set_name(oreputationdata.getString("0"));
					}else{
						entry.set_name("未知");
					}
					if(!StringUtil.isEmpty(oreputationdata.getString("1"))){
						entry.set_data(oreputationdata.getString("1"));
					}else{
						entry.set_data("0");
					}
					if(!StringUtil.isEmpty(oreputationdata.getString("2"))){
						entry.set_detail(oreputationdata.getString("2"));
					}else{
						entry.set_detail("未知");
					}
					EntryList.add(entry);
					if (oreputation.get(String.valueOf(i)) != null) {
						oreputationdata = (JSONObject) oreputation.get(String
								.valueOf(i));
					} else {
						oreputationdata = null;
					}
				} catch (Exception e) {
				}
			}
			ret.set_ReputationEntryListrows(i - 1);
			ret.set_ReputationEntryList(EntryList);
		} else {
			ret.set_ReputationEntryListrows(0);
		}
		if (oadminForums != null) {
			String adminforums = oadminForums.toString();
			adminforums = adminforums.substring(1, adminforums.length() - 1);
			String sarray[] = adminforums.split(",");
			String ss[] = new String[sarray.length];
			for (i = 0; i < sarray.length; i++) {
				ss[i] = StringUtil.getStringBetween(sarray[i], 0, "\"", "\"").result;
			}
			for (i = 0; i < sarray.length; i++) {
				try {
					adminForumsData entry = new adminForumsData();
					entry.Set_Data(ss[i], oadminForums.getString(ss[i]));
					EntryList2.add(entry);
				} catch (Exception e) {
				}
			}
			ret.set_adminForumsEntryList(EntryList2);
			ret.set_adminForumsEntryListrows(sarray.length);
		} else {
			ret.set_adminForumsEntryListrows(0);
		}

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
	protected void onPostExecute(ProfileData result) {
		ActivityUtil.getInstance().dismiss();
		if (result == null) {
			ActivityUtil.getInstance().noticeError(error, context);
		} else {
		}
		notifier.jsonfinishLoad(result);

		super.onPostExecute(result);
	}

	@Override
	protected void onCancelled() {
		ActivityUtil.getInstance().dismiss();
		super.onCancelled();
	}

}
