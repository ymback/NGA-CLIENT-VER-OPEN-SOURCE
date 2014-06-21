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
	private int realstart = 0;

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
		List<MissionDetialData> EntryList = new ArrayList<MissionDetialData>();
		js = HttpUtil.getHtml(uri,PhoneConfiguration.getInstance().getCookie());
		Log.i(TAG,js);
//		 js = "{\"data\":{\"0\":\"ǩ���ɹ�\",\"1\":{\"uid\":15776622,\"continued\":2,\"sum\":7,\"last_time\":1402056459},\"2\":{\"available\":{\"2\":{\"id\":2,\"name\":\"ʹ��NGA�ֻ��ͻ��˹�ǽ\",\"info\":\"ʹ��NGA�ֻ��ͻ��˹�ǽ �������ҽ���\",\"detail\":\"�������������������:\n����ʹ����֤�Ŀͻ���, ����ǩ��5��, \n\n������Ի�����½���:\nͭ��:500, \n\n��������ظ����, ÿ0��һ��\n\n\",\"stat\":\"Ŀǰ�Ѿ�����ǩ��2��(�������)\n����ǩ��4��(�������)\n\n�����ڷ�����ʱ�� 2014-05-31 07:10:39 ���\n\n\",\"raw_detail\":{\"1\":2,\"9\":\"ʹ��NGA�ֻ��ͻ��˹�ǽ\",\"10\":\"ʹ��NGA�ֻ��ͻ��˹�ǽ �������ҽ���\",\"16\":{\"6\":1},\"2\":{\"3\":5},\"4\":3,\"5\":0,\"6\":1,\"7\":{\"2\":500},\"12\":0,\"15\":1},\"raw_stat\":{\"1\":2,\"2\":4,\"3\":1401491439}}},\"success\":{}}},\"time\":1402101293}";
		// js="{\"data\":{\"0\":\"ǩ���ɹ�(0)\"},\"time\":1399162876}";
		// js =
		// "{\"data\":{\"0\":\"ǩ���ɹ�\",\"1\":{\"uid\":58,\"continued\":3,\"sum\":38,\"last_time\":1397270302},\"2\":{\"success\":{\"1\":{\"id\":1,\"name\":\"��������1S\",\"info\":\"����\n   ������Ϣ\",\"detail\":\"�������������������:����ǩ��3��,...\",\"stat\":\"Ŀǰ�Ѿ�����ǩ��1��\n����ǩ��1\n������ 2014-04-11 18:11:48 ���\"},\"2\":{\"id\":2,\"name\":\"��������2S\",\"info\":\"����\n   ������Ϣ\",\"detail\":\"�������������������:����ǩ��3��,...\",\"stat\":\"Ŀǰ�Ѿ�����ǩ��1��\n����ǩ��1\n������ 2014-04-11 18:11:48 ���\"}},\"available\":{\"1\":{\"id\":1,\"name\":\"��������1A\",\"info\":\"����\n   ������Ϣ\",\"detail\":\"�������������������:����ǩ��3��,...\",\"stat\":\"Ŀǰ�Ѿ�����ǩ��1��\n����ǩ��1\n������ 2014-04-11 18:11:48 ���\"},\"2\":{\"id\":2,\"name\":\"��������2A\",\"info\":\"����\n   ������Ϣ\",\"detail\":\"�������������������:����ǩ��3��,...\",\"stat\":\"Ŀǰ�Ѿ�����ǩ��1��\n����ǩ��1\n������ 2014-04-11 18:11:48 ���\"}}}},\"time\":1397452410}";
		// js="window.script_muti_get_var_store={\"error\":{\"0\":\"������Ѿ�ǩ����(�Է�����ʱ�����)\"},\"time\":1397704424}";//����ģʽ
		// js="{\"data\":{\"0\":\"ǩ���ɹ�\",\"1\":{\"uid\":15776622,\"continued\":1,\"sum\":1,\"last_time\":0},\"2\":{\"available\":{\"1\":{\"id\":1,\"name\":\"��������\",\"info\":\"����\n	������Ϣ\",\"detail\":\"�������������������:\n����ǩ��3��(ȫ��), \n\n������Ի�����½���:\nͭ��:1, \n\n��������ظ����, ÿ2��һ��\n\n\",\"stat\":\"Ŀǰ�Ѿ�����ǩ��1��\n����ǩ��1\n\n\"}}}},\"time\":1397949753}";
		// js="S)DB ERROR";
		// js="{\"data\":{\"0\":\"ǩ���ɹ�\",\"1\":{\"uid\":15776622,\"continued\":1,\"sum\":2,\"last_time\":1397949753},\"2\":{\"available\":{\"1\":{\"id\":1,\"name\":\"��������\",\"info\":\"����\n	������Ϣ\",\"detail\":\"�������������������:\n����ǩ��3��(ȫ��), \n\n������Ի�����½���:\nͭ��:1, \n\n��������ظ����, ÿ2��һ��\n\n\",\"stat\":\"Ŀǰ�Ѿ�����ǩ��1��\n����ǩ��2\n\n\"}}}},\"time\":1398578538}";
		if (null == js) {
			error = context.getString(R.string.network_error);
			return null;
		}
		if (js.indexOf("DB ERROR") >= 0) {
			error = "����ѹ��ûŪ��,���ݿ����";
			return null;
		}// ����ģʽ
		js = js.replaceAll("window.script_muti_get_var_store=", "");
		if (js.indexOf("/*error fill content") > 0)
			js = js.substring(0, js.indexOf("/*error fill content"));// ���粻����ô������,�����������Ű�
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
				error = "�����µ�¼";
				return null;
			} else {
				if (oerror.getString("0") != null) {
					error = oerror.getString("0");
					if(error.startsWith("������Ѿ�ǩ����"))
						ret.set__today_alreadysign(true);
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
			if (StringUtil.isEmpty(SignResult)) {
				error = "�����滵�˻�������Ҫ���µ�¼";
				return null;
			} else {
				error = SignResult;
				ret.set__is_json_signsuccess(true);
				ret.set__SignResult(error);
				ret.setEntryList(EntryList);
				ret.set__Successrows(0);
				ret.set__Availablerows(0);
				ret.set__Totalrows(0);
				return ret;
			}
		}
		ret.set__Uid(Integer.parseInt(o1.getString("uid")));
		ret.set__Continued(Integer.parseInt(o1.getString("continued")));
		ret.set__Sum(Integer.parseInt(o1.getString("sum")));
		ret.set__Uid(Integer.parseInt(o1.getString("uid")));
		if (!StringUtil.isEmpty(o1.getString("last_time"))) {
			if (o1.getString("last_time").equals("0")) {
				ret.set__Last_time("��δ");
			} else {
				ret.set__Last_time(TimeStamp2Date(o1.getString("last_time")));
			}
		}
		JSONObject o2 = null;
		if (o.get("2") instanceof JSONObject) {
			o2 = (JSONObject) o.get("2");
		} else {
			error = "�����滵�˻�������Ҫ���µ�¼";
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
				realstart = 1;
			} else if (o2success.get(String.valueOf(i + 1)) != null) {
				o2successdetail = (JSONObject) o2success.get(String
						.valueOf(i + 1));
				realstart = 2;
			}
			for (i = realstart + 1; o2successdetail != null; i++) {
				try {
					MissionDetialData entry = new MissionDetialData();
					entry.set__id(Integer.parseInt(o2successdetail
							.getString("id")));
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
			ret.set__Successrows(i - 1 - realstart);
			total += i - 1 - realstart;
		} else {
			ret.set__Successrows(0);
		}

		i = 1;
		realstart = 0;

		JSONObject o2available = null;
		if (o2.get("available") instanceof JSONObject) {
			o2available = (JSONObject) o2.get("available");
			JSONObject o2availabledetail = null;
			if (o2available.get(String.valueOf(i)) != null) {
				o2availabledetail = (JSONObject) o2available.get(String
						.valueOf(i));
				realstart = 1;
			} else if (o2available.get(String.valueOf(i + 1)) != null) {
				o2availabledetail = (JSONObject) o2available.get(String
						.valueOf(i + 1));
				realstart = 2;
			}

			for (i = realstart + 1; o2availabledetail != null; i++) {
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
			ret.set__Availablerows(i - 1 - realstart);
			total += i - 1 - realstart;
		} else {
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
		} else if (result.get__is_json_error() && !result.get__today_alreadysign()) {
			ActivityUtil.getInstance().noticeError(error, context);
		} else if (result.get__is_json_signsuccess()) {
			if (toast != null) {
				toast.setText(result.get__SignResult());
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
			} else {
				toast = Toast.makeText(context, result.get__SignResult(),
						Toast.LENGTH_SHORT);
				toast.show();
			}
		} else {
			if (result.get__SignResult().equals("ǩ���ɹ�")) {
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
