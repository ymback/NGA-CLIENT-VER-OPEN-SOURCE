package sp.phone.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import sp.phone.bean.ThreadData;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.ArticleUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import gov.anzong.androidnga.R;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


public class JsonThreadLoadTask extends AsyncTask<String, Integer, ThreadData> {
	static final String TAG = JsonThreadLoadTask.class.getSimpleName();
	final private Context context;
	private String errorStr;
	final private OnThreadPageLoadFinishedListener notifier;

	public JsonThreadLoadTask(Context context,
			OnThreadPageLoadFinishedListener notifier) {
		super();
		this.context = context;
		this.notifier = notifier;
	}


	@Override
	protected ThreadData doInBackground(String... params) {
		if(params.length == 0)
			return null;
		
		final String url = params[0];
		Log.d(TAG, "start to load:" + url);
		
		ThreadData result = this.loadAndParseJsonPage(url);
		int orignalTid  = 0;
		if(null != result && null !=result.getThreadInfo())
		{
			orignalTid = result.getThreadInfo().getQuote_from();
		}
		if(null != result &&  orignalTid !=0){
			
			String origUrl = url.replaceAll("tid=(\\d+)", "tid=" +orignalTid);
			Log.i(TAG,"quoted page,load from orignal article,tid=" + orignalTid);
			result = loadAndParseJsonPage(origUrl);
		}
		
		
		return result;
	}
	
	private ThreadData loadAndParseJsonPage(String uri){
		//Log.d(TAG, "start to load:" + uri);
		String js = HttpUtil.getHtml(uri, PhoneConfiguration.getInstance().getCookie());
		if(null == js){
			errorStr = context.getString(R.string.network_error);
			return null;
		}
		if(js.indexOf("/*error fill content")>0)
			js=js.substring(0, js.indexOf("/*error fill content"));
		js=js.replaceAll("/\\*\\$js\\$\\*/","");
		ThreadData result = new ArticleUtil(context).parseJsonThreadPage(js);
		if(null == result){
			errorStr = context.getResources().getString(R.string.thread_load_error);
			do{
			try{
			JSONObject o = (JSONObject) JSON.parseObject(js);
			if(o == null)
				break;
			 o = (JSONObject) o.get("data");
			 if(o == null)
					break;
             String message = null;
             Object tmp =    o.get("__MESSAGE");
             if(tmp instanceof String ){
                 message =  (String) o.get("__MESSAGE");
             }else if (tmp instanceof JSONObject){
                 o = (JSONObject)tmp;
                 message = (String)o.get("1");
                 if(message==null){
                     message = (String)o.get("0");
                 }
             }else {
                 break;
             }
			 if(message == null)
					break;
			 
			 int pos = message.indexOf("<a href=");
			 if(pos >0){
				 message = message.substring(0, pos);
			 }
			 pos = message.indexOf("<br/>");
			 if(pos>0)
				 errorStr = message.replace("<br/>", "");
			 else
				 errorStr = message;
			 
			}catch(Exception e){
				
			}
			 
			}while(false);
			
		}
			
			

		
		
		return result;
		
		
	}


	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}


	@Override
	protected void onPostExecute(ThreadData result) {
		ActivityUtil.getInstance().dismiss();
		if(result == null){
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
