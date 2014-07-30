package sp.phone.task;


import gov.anzong.androidnga.R;
import sp.phone.fragment.ProgressDialogFragment;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.StringUtil;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

public class BilibiliCidVideoLoadTask extends AsyncTask<String, Integer, String> {

	final FragmentActivity fa ;
	final String origurl;
	static final String dialogTag = "load_bilicid";
	public BilibiliCidVideoLoadTask(FragmentActivity fa,String origurl) {
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
		final String content = fa.getResources().getString(R.string.load_bili_video);
		args.putString("content", content);
		pd.setArguments(args );
		pd.show(fa.getSupportFragmentManager(), dialogTag);
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		if(!startIntent){
			Toast.makeText(fa.getBaseContext(), R.string.videoplay_createwindow_error,	Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(origurl));
            boolean isIntentSafe = fa.getPackageManager().queryIntentActivities(intent,0).size() > 0;
            if(isIntentSafe)
			    fa.startActivity(intent);
			return;
		}
		
		if(result != null){
		    try{
		        Intent intent = new Intent();
		        ComponentName comp = new ComponentName("gov.anzong.mediaplayer","gov.anzong.mediaplayer.ReceiveIntentActivity");
		        intent.setComponent(comp);
		        intent.putExtra("uri", result);
		        intent.putExtra("title", "BILIBILI");
				fa.startActivity(intent);
		    }catch(Exception e){
		    	//TODO
		    	Toast.makeText(fa.getBaseContext(), R.string.videoplay_ngaplayernotinstall_error,
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(origurl));
				boolean isIntentSafe = fa.getPackageManager()
						.queryIntentActivities(intent, 0).size() > 0;
				if (isIntentSafe)
					fa.startActivity(intent);
		    }
		}else{
			Toast.makeText(fa.getBaseContext(), R.string.videoplay_urlgetfailed_error,	Toast.LENGTH_LONG).show();
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
		final String uri = "http://www.bilibili.tv/m/html5?cid="
				+ params[0];
		final String htmlString = HttpUtil.iosGetHtml(uri, null);
		String videourl = StringUtil.getStringBetween(
				htmlString, 0, "\"src\":\"", "\"").result;
		if(StringUtil.isEmpty(videourl)){
			return null;
		}
		return videourl;
	}

}
