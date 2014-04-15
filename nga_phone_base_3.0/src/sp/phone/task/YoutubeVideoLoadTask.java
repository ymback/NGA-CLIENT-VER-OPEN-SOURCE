package sp.phone.task;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.Media_Player;
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

public class YoutubeVideoLoadTask extends AsyncTask<String, Integer, String> {

	final FragmentActivity fa ;
	static final String dialogTag = "load_youtube";
	public YoutubeVideoLoadTask(FragmentActivity fa) {
		super();
		this.fa = fa;
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
		if(!startIntent)
			return;
		
		if(result != null){
			Intent intent = new Intent(fa.getBaseContext(),Media_Player.class);
			Bundle b = new Bundle();
			b.putString("MEDIAPATH", result);
			intent.putExtras(b);
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
		int i=0,si=-1;
		while (i<sta.length) {  
			if(sta[i].indexOf("video/mp4")>0 && sta[i].indexOf("medium")<0 ){
				si=i;
				break;
			}
			i++;
        }  
		String m3u8Url=null;
		if(si>=0){
			 m3u8Url = sta[si];
		}else{
			return null;
		}
		m3u8Url = StringUtil.getStringBetween(
				m3u8Url, 0, "url=", "\\").result;
		return m3u8Url;
	}

}
