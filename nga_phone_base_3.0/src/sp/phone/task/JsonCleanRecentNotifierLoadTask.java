package sp.phone.task;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import gov.anzong.androidnga.Utils;
import noname.gson.parse.NonameParseJson;
import noname.gson.parse.NonameReadResponse;


import sp.phone.bean.NotificationObject;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.User;
import sp.phone.interfaces.OnNonameThreadPageLoadFinishedListener;
import sp.phone.interfaces.OnRecentNotifierFinishedListener;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.MyApp;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

public class JsonCleanRecentNotifierLoadTask extends AsyncTask<String, Integer, String> implements PerferenceConstant {
	static final String TAG = JsonCleanRecentNotifierLoadTask.class.getSimpleName();
	final private Context context;
	final String url = Utils.getNGAHost() + "nuke.php?__lib=noti&raw=3&__act=del";

	public JsonCleanRecentNotifierLoadTask(Context context) {
		super();
		this.context = context;
	}

	@Override
	protected String doInBackground(String... params) {
		final String cookie = params[0];
		HttpUtil.getHtml(url, cookie, null, 3000);
		return null;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
	}
	
	@Override
	protected void onCancelled() {
		ActivityUtil.getInstance().dismiss();
		super.onCancelled();
	}

}
