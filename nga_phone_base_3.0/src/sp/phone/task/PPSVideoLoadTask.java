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
import android.widget.Toast;

public class PPSVideoLoadTask extends AsyncTask<String, Integer, String> {

	final FragmentActivity fa ;
	final String origurl;
	static final String dialogTag = "load_pps";
	public PPSVideoLoadTask(FragmentActivity fa,String origurl) {
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
		final String content = fa.getResources().getString(R.string.load_pps_video);
		args.putString("content", content);
		pd.setArguments(args );
		pd.show(fa.getSupportFragmentManager(), dialogTag);
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		if(!startIntent){
			Toast.makeText(fa.getBaseContext(), "������Ƶ����ʧ��,������ϵͳ������",	Toast.LENGTH_LONG).show();
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
			Toast.makeText(fa.getBaseContext(), "��Ǹ,����Ƶ�޷�����,������ϵͳ������",	Toast.LENGTH_LONG).show();
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
		final String uri = "http://dp.ppstream.com/get_play_url_cdn.php?sid="
				+ params[0]+"&flash_type=1";
		final String htmlString = HttpUtil.iosGetHtml(uri, null);
		if(htmlString.indexOf(".pfv")<0){
			return null;
		}
		String url=null;
		try {
			url=URLDecoder.decode(htmlString,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return null;
		}
		return url;
	}

}
