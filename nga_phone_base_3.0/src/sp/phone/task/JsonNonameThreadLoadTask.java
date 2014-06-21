package sp.phone.task;

import noname.gson.parse.NonameParseJson;
import noname.gson.parse.NonameReadResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import sp.phone.bean.ThreadData;
import sp.phone.interfaces.OnNonameThreadPageLoadFinishedListener;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ArticleUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import gov.anzong.androidnga.R;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class JsonNonameThreadLoadTask extends AsyncTask<String, Integer, NonameReadResponse> {
	static final String TAG = JsonNonameThreadLoadTask.class.getSimpleName();
	final private Context context;
	private String errorStr;
	final private OnNonameThreadPageLoadFinishedListener notifier;

	public JsonNonameThreadLoadTask(Context context,
			OnNonameThreadPageLoadFinishedListener notifier) {
		super();
		this.context = context;
		this.notifier = notifier;
	}

	@Override
	protected NonameReadResponse doInBackground(String... params) {
		if (params.length == 0)
			return null;

		final String url = params[0];
		Log.d(TAG, "start to load:" + url);
		NonameReadResponse result = this.loadAndParseJsonPage(url);

		return result;
	}

	private NonameReadResponse loadAndParseJsonPage(String uri) {
		// Log.d(TAG, "start to load:" + uri);
		String js = HttpUtil.getHtml(uri, PhoneConfiguration.getInstance()
				.getCookie());
		if (null == js) {
			errorStr = context.getString(R.string.network_error);
			return null;
		}
		NonameReadResponse result = NonameParseJson.parseRead(js);
		if(result.error){
			errorStr = result.errorinfo;
			return null;
			
		}
		return result;

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(NonameReadResponse result) {
		ActivityUtil.getInstance().dismiss();
		if (result == null) {
			ActivityUtil.getInstance().noticeError(errorStr, context);
		}
		notifier.finishLoad(result);

		super.onPostExecute(result);
	}

	@Override
	protected void onCancelled() {
		ActivityUtil.getInstance().dismiss();
		super.onCancelled();
	}

}
