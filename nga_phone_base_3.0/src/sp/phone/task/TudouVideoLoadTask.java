package sp.phone.task;

import gov.anzong.androidnga.R;
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

public class TudouVideoLoadTask extends AsyncTask<String, Integer, String> {

	final FragmentActivity fa ;
	static final String dialogTag = "load_tudou";
	public TudouVideoLoadTask(FragmentActivity fa) {
		super();
		this.fa = fa;
	}
	private boolean startIntent = true;
	@Override
	protected void onPreExecute() {
		//create progress view
		 ProgressDialogFragment pd = new  ProgressDialogFragment();
		 
		Bundle args = new Bundle();
		final String content = fa.getResources().getString(R.string.load_tudou_video);
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
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(result));
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
		if(params.length ==0)
		{
			return null;
		}
		int index  = params[0].indexOf("/v.swf");
		if(index != -1){
			params[0] = params[0].substring(0, index);
		}
		final String uri = "http://www.tudou.com/programs/view/html5embed.action?code="
				+ params[0];
		final String htmlString = HttpUtil.iosGetHtml(uri, null);
		final String imgUrl = StringUtil.getStringBetween(
				htmlString, 0, "poster=\"", "\"").result;
		if(StringUtil.isEmpty(imgUrl))
			return null;
		String m3u8Url = imgUrl.replace("http://i2.tdimg.com", "http://m3u8.tdimg.com");
		index  = m3u8Url.lastIndexOf('/'); 
		if(index ==-1){
			return null;
		}
		m3u8Url = m3u8Url.substring(0, index);
		return m3u8Url+"/3.m3u8";
	}

}
