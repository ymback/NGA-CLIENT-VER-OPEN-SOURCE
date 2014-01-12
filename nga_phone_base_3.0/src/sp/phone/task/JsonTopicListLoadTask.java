package sp.phone.task;

import gov.anzong.androidnga.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import sp.phone.utils.StringUtil;

public class JsonTopicListLoadTask extends AsyncTask<String, Integer, TopicListInfo> {
	private final static String TAG = JsonTopicListLoadTask.class.getSimpleName();
	private final Context context;
	final private OnTopListLoadFinishedListener notifier;
	private String error;
	private String table;
	
	
	public JsonTopicListLoadTask(Context context,
			OnTopListLoadFinishedListener notifier) {
		super();
		this.context = context;
		this.notifier = notifier;
	}
	@Override
	protected TopicListInfo doInBackground(String... params) {
		
		
		if(params.length == 0)
			return null;
		Log.d(TAG, "start to load " + params[0]);
		String uri = params[0];
		String js = HttpUtil.getHtml(uri, PhoneConfiguration.getInstance().getCookie());
        boolean filter = false;
        final  String greatSeaUri = "http://bbs.ngacn.cc/thread.php?fid=-7&page=1&lite=js&noprefix";
        if(greatSeaUri.equals(uri)){
            filter = true;
        }
        if(uri.indexOf("table=")>0){
        	table = StringUtil.getStringBetween(uri, 0, "table=", "&").result.trim();
        	String pattern1 = "^[0-6]{1}$";
    		Pattern pattern = Pattern.compile(pattern1);
    		Matcher mat = pattern.matcher(table);
        	if(!mat.find()){
        		table=null;
        	}
        }
		if(js == null){
			error = context.getResources().getString(R.string.network_error);
			return null;
		}
		if(js.indexOf("/*error fill content")>0)
			js=js.substring(0, js.indexOf("/*error fill content"));
		js = js.replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
		js = js.replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",");
		js=js.replaceAll("/\\*\\$js\\$\\*/","");
		JSONObject o = null;
		try{
				o = (JSONObject) JSON.parseObject(js).get("data");
		}catch(Exception e){
			Log.e(TAG, "can not parse :\n" +js );
		}
		if(o == null){
			error = "请重新登录";
			return null;
		}
		
		TopicListInfo ret = new TopicListInfo();
		

		/**
		 * 再见垃圾板开始
		 * **/
		Object topiclistftmp=o.get("__F");
		int topiclistfid=0;
		if(topiclistftmp instanceof JSONObject){
			JSONObject otmp = (JSONObject)topiclistftmp;
			if(otmp.get("fid")!=null){
			topiclistfid=(Integer)otmp.get("fid");
			}
			if(table!=null && otmp.get("extra_html")!=null){
				ret.set__TABLE(Integer.parseInt(table)+1);
			}
		}/**
		 * 再见垃圾板结束
		 * **/
		
		
		Object rows = o.get("__ROWS");
		
		if(rows instanceof Integer)
		{
			ret.set__ROWS((Integer)o.get("__ROWS"));
		}else{
			ret.set__ROWS(10000);
		}
		
		rows = o.get("__T__ROWS");
		if(rows != null)
			ret.set__T__ROWS((Integer)rows);
		else
		{
			String message =  null;
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
                error = "二哥玩坏了或者你需要重新登录";
            }
			 if(message !=null)
             {
				 int pos = message.indexOf("<a href=");
				 if(pos >0){
					 message = message.substring(0, pos);
				 }
				 error = message.replace("<br/>", "");
			 
			 }
			 if(error.trim().endsWith("中没有符合条件的结果")){
				 ret.set__TABLE(Integer.parseInt(table));
				 ret.set__SEARCHNORESULT(true);
				 return ret;
			 }else{
				 ret.set__SEARCHNORESULT(false);
				 ret.set__TABLE(-999);//NOT SEARCHING IN TABLE X
					return null;
			 }
		}
			
		Object forum = o.get("__SELECTED_FORUM");
		if(forum instanceof Integer)
			ret.set__SELECTED_FORUM((Integer)forum);
		else
			ret.set__SELECTED_FORUM(0);
		
		JSONObject o1 = (JSONObject) o.get("__T");
		
		if( o1 == null)
		{
			error = "请重新登录";
			return null;
		}
			
			
		List<ThreadPageInfo> articleEntryList = new  ArrayList<ThreadPageInfo>();
		for(int i = 0; i <ret.get__T__ROWS(); i++){
			JSONObject rowObj  = (JSONObject) o1.get(String.valueOf(i));
			try{
			ThreadPageInfo entry = JSONObject.toJavaObject(rowObj,ThreadPageInfo.class);
			JSONObject rowObj__P = (JSONObject) rowObj.get("__P");
			if(null != rowObj__P){
				if(rowObj__P.getInteger("pid")>0){
					entry.setPid(rowObj__P.getInteger("pid"));
				}
			}
            if(PhoneConfiguration.getInstance().showStatic ||
                        (StringUtil.isEmpty(entry.getTop_level()) && StringUtil.isEmpty(entry.getStatic_topic())) )
			    {
            	if(PhoneConfiguration.getInstance().showLajibankuai){
				    	articleEntryList.add(entry);//显示的话，不用管直接加
			    }else{
	            	if(topiclistfid==-7){//不显示的话，判断是否不要垃圾版
	            		if(entry.getFid()==-7)
			    	articleEntryList.add(entry);
			    	}
	            	else{
				    	articleEntryList.add(entry);
	            	}
			    }
			    }
			}catch(Exception e){
				/*ThreadPageInfo entry = new ThreadPageInfo();
				String error = rowObj.getString("error");
				entry.setSubject(error);
				entry.setAuthor("");
				entry.setLastposter("");
				articleEntryList.add(entry);*/
			}
		}
        if(!PhoneConfiguration.getInstance().showStatic && filter){

            int j = articleEntryList.size()-1;
            while(j >=0){
                ThreadPageInfo info = articleEntryList.get(j);
                if(info == null){
                    break;
                }
                if(info.getRecommend() >9){
                    articleEntryList.remove(j);
                }
                --j;
            }

        }
        ret.set__T__ROWS(articleEntryList.size());
		ret.setArticleEntryList(articleEntryList);
		
		return ret;
	}
	@Override
	protected void onPostExecute(TopicListInfo result) {
		ActivityUtil.getInstance().dismiss();
		if(result == null)
		{
			ActivityUtil.getInstance().noticeError
			(error, context);
			//return;
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
