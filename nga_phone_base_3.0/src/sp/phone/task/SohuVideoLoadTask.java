package sp.phone.task;

import gov.anzong.androidnga.R;
import gov.anzong.mediaplayer.VideoActivity;
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

public class SohuVideoLoadTask extends AsyncTask<String, Integer, String> {

	final FragmentActivity fa ;
	static final String dialogTag = "load_sohu";
	public SohuVideoLoadTask(FragmentActivity fa) {
		super();
		this.fa = fa;
	}
	private boolean startIntent = true;
	@Override
	protected void onPreExecute() {
		//create progress view
		 ProgressDialogFragment pd = new  ProgressDialogFragment();
		 
		Bundle args = new Bundle();
		final String content = fa.getResources().getString(R.string.load_sohu_video);
		args.putString("content", content);
		pd.setArguments(args );
		pd.show(fa.getSupportFragmentManager(), dialogTag);
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		if(!startIntent){
			Toast.makeText(fa.getBaseContext(), "创建视频窗口失败",	Toast.LENGTH_LONG).show();
			return;
		}
		
		if(result != null){
		    VideoActivity.openVideo(fa, Uri.parse(result), "搜狐视频");
		}else{
			Toast.makeText(fa.getBaseContext(), "抱歉,该视频无法解析",	Toast.LENGTH_LONG).show();
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
		final String uri = params[0];
		final String htmlString = HttpUtil.iosGetHtml(uri, null);
		final String id = StringUtil.getStringBetween(
				htmlString, 0, "vid=\"", "\";").result;
		if(StringUtil.isEmpty(id))
			return null;
		String m3u8Url = "http://hot.vrs.sohu.com/ipad"
				+id +
				".m3u8";
		return m3u8Url;
	}

}
