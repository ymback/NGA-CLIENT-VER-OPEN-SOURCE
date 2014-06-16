package sp.phone.task;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import gov.anzong.androidnga2.R;
import gov.anzong.androidnga2.activity.Media_Player;
import sp.phone.fragment.ProgressDialogFragment;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.StringUtil;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

public class YoutubeVideoLoadTask extends AsyncTask<String, Integer, String> {

	final FragmentActivity fa ;
	final String origurl;
	static final String dialogTag = "load_youtube";
	public YoutubeVideoLoadTask(FragmentActivity fa,String origurl) {
		super();
		this.fa = fa;
		this.origurl=origurl;
	}
	private boolean startIntent = true;
	@Override
	protected void onPreExecute() {
		//create progress view
		 ProgressDialogFragment pd = new  ProgressDialogFragment();
		 
		Bundle args = new Bundle();
		final String content = fa.getResources().getString(R.string.load_youtube_video);
		args.putString("content", content);
		pd.setArguments(args );
		pd.show(fa.getSupportFragmentManager(), dialogTag);
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		if(!startIntent){
			Toast.makeText(fa.getBaseContext(), "创建视频窗口失败,将调用系统打开链接",	Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(origurl));
            boolean isIntentSafe = fa.getPackageManager().queryIntentActivities(intent,0).size() > 0;
            if(isIntentSafe)
			    fa.startActivity(intent);
			return;
		}
		
		if(result != null){
			Intent intent = new Intent(fa.getBaseContext(),Media_Player.class);
			Bundle b = new Bundle();
			b.putString("MEDIAPATH", result);
			intent.putExtras(b);
			fa.startActivity(intent);
		}else{
			Toast.makeText(fa.getBaseContext(), "抱歉,该视频无法解析,将调用系统打开链接",	Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(origurl));
            boolean isIntentSafe = fa.getPackageManager().queryIntentActivities(intent,0).size() > 0;
            if(isIntentSafe)
			    fa.startActivity(intent);
		}

		this.onCancelled();

		super.onPostExecute(result);
	}

	@Override
	protected void onCancelled(String result) {
		
		this.onCancelled();
	}

	@Override
	protected void onCancelled() {
		FragmentManager fm = fa.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

        Fragment prev = fm.findFragmentByTag(dialogTag);
        if (prev != null) {
            ft.remove(prev);
            
        }
        try
        {
        	ft.commit();
        }catch(Exception e){
        	
        }
	}

	@Override
	protected String doInBackground(String... params) {
		final String uri = "https://www.youtube.com/get_video_info?video_id="
				+ params[0];
		final String htmlString = HttpUtil.iosGetHtml(uri, null);
		StringTokenizer	st = null;
		if(StringUtil.isEmpty(htmlString))
			return null;
		else{
			if(htmlString.lastIndexOf("&")>0){
				st = new StringTokenizer(htmlString, "&"); 
			}else{
				return null;
			}
		}
		String data = null;
		if(st==null)
			return null;
		while (st.hasMoreTokens()) {  
			data=st.nextToken();
			if(data.startsWith("url_encoded_fmt_stream_map=")){
				break;
			}
        }  
		if(data.length()>28)
			data=data.substring(27);
		else
			return null;
		try {
			data=URLDecoder.decode( data, "UTF-8" );
			data=URLDecoder.decode(data);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		String[] sta = data.split("quality=");
		String stb1080p=null,stb720p=null,stbmedium=null,stbsmall=null,stbother=null;
		int i=0;
		while (i<sta.length) {  
			if(sta[i].indexOf("video/mp4")>0 && sta[i].indexOf("medium")<0 ){
				if(sta[i].indexOf("hd1080")>=0){
					stb1080p=sta[i];
				}else if(sta[i].indexOf("hd720")>=0){
					stb720p=sta[i];
				}else if(sta[i].indexOf("medium")>=0){
					stbmedium=sta[i];
				}else if(sta[i].indexOf("small")>=0){
					stbsmall=sta[i];
				}else{
					stbother=sta[i];
				}
				break;
			}
			i++;
        }  
		String m3u8Url=null;
		if(StringUtil.isEmpty(stb1080p)){
			m3u8Url=stb1080p;
		}else if(StringUtil.isEmpty(stb720p)){
			m3u8Url=stb720p;
		}else if(StringUtil.isEmpty(stbmedium)){
			m3u8Url=stbmedium;
		}else if(StringUtil.isEmpty(stbother)){
			m3u8Url=stbother;
		}else if(StringUtil.isEmpty(stbsmall)){
			m3u8Url=stbsmall;
		}else{
			return null;
		}
		m3u8Url = StringUtil.getStringBetween(
				m3u8Url, 0, "url=", ";").result;
		if(StringUtil.isEmpty(m3u8Url))
			return null;
		return m3u8Url;
	}

}
