package sp.phone.task;

import gov.anzong.androidnga.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import noname.gson.parse.NonameParseJson;
import noname.gson.parse.NonameThreadResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.OnNonameTopListLoadFinishedListener;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import sp.phone.utils.StringUtil;

public class JsonNonameTopicListLoadTask extends AsyncTask<String, Integer, NonameThreadResponse> {
	private final static String TAG = JsonNonameTopicListLoadTask.class.getSimpleName();
	private final Context context;
	final private OnNonameTopListLoadFinishedListener notifier;
	private String error;
	
	
	public JsonNonameTopicListLoadTask(Context context,
			OnNonameTopListLoadFinishedListener notifier) {
		super();
		this.context = context;
		this.notifier = notifier;
	}
	@Override
	protected NonameThreadResponse doInBackground(String... params) {
		
		
		if(params.length == 0)
			return null;
		Log.d(TAG, "start to load " + params[0]);
		String uri = params[0];
		String js = HttpUtil.getHtml(uri, PhoneConfiguration.getInstance().getCookie());
        boolean filter = false;
		if(js == null){
			if(context!=null)
				error = context.getResources().getString(R.string.network_error);
			return null;
		}
		NonameThreadResponse ret = NonameParseJson.parseThreadRead(js);
		
		return ret;
	}
	@Override
	protected void onPostExecute(NonameThreadResponse result) {
		ActivityUtil.getInstance().dismiss();
		if(result == null)
		{
			ActivityUtil.getInstance().noticeError
			(error, context);
			return;
		}
		if(result.error){
			ActivityUtil.getInstance().noticeError
			(result.errorinfo, context);
			return;
		}
		if(null != notifier)
			notifier.jsonfinishLoad(result);
		super.onPostExecute(result);
	}
	
	@Override
	protected void onCancelled() {
		ActivityUtil.getInstance().dismiss();
		super.onCancelled();
	}
	
	

}
