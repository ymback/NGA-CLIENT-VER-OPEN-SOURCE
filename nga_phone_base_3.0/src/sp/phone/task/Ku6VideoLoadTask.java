package sp.phone.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import gov.anzong.androidnga2.R;
import gov.anzong.androidnga2.activity.Media_Player;
import sp.phone.fragment.ProgressDialogFragment;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.StringUtil;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

public class Ku6VideoLoadTask extends AsyncTask<String, Integer, String> {

	final FragmentActivity fa ;
	final String origurl;
	static final String dialogTag = "load_ku6";
	public Ku6VideoLoadTask(FragmentActivity fa,String origurl) {
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
		final String content = fa.getResources().getString(R.string.load_ku6_video);
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

	public void saveToTemp(String filename,String content) throws Exception{  
        File file=new File(HttpUtil.PATH_AVATAR, filename);  
        OutputStream out=new FileOutputStream(file);  
        out.write(content.getBytes());  
        out.close();  
    }  
	@Override
	protected String doInBackground(String... params) {
		final String uri = "http://v.ku6.com/fetchwebm/"
				+ params[0]+"...m3u8";
		final String htmlString = HttpUtil.iosGetHtml(uri, null);
		if(StringUtil.isEmpty(htmlString))
			return null;
		try {  
			saveToTemp("ku6.m3u8", htmlString);   
        } catch (Exception e) {  
        }  
		String URL = HttpUtil.PATH_AVATAR+"/ku6.m3u8";
		return URL;
	}

}
