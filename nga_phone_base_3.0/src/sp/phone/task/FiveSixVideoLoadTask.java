package sp.phone.task;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.MediaPlayer;
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

public class FiveSixVideoLoadTask extends AsyncTask<String, Integer, String> {

	final FragmentActivity fa ;
	static final String dialogTag = "load_56";
	public FiveSixVideoLoadTask(FragmentActivity fa) {
		super();
		this.fa = fa;
	}
	private boolean startIntent = true;
	@Override
	protected void onPreExecute() {
		//create progress view
		 ProgressDialogFragment pd = new  ProgressDialogFragment();
		 
		Bundle args = new Bundle();
		final String content = fa.getResources().getString(R.string.load_56_video);
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
			Intent intent = new Intent(fa.getBaseContext(),MediaPlayer.class);
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
		final String uri = "http://vxml.56.com/json/"
				+ params[0]+"/?src=out";
		final String htmlString = HttpUtil.iosGetHtml(uri, null);
		final String flvurl = StringUtil.getStringBetween(
				htmlString, 0, "\"url\":\"", "\"").result;
		Log.i(dialogTag,flvurl);
		return flvurl;
	}

}
