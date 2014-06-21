package sp.phone.task;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.Media_Player;
import sp.phone.fragment.ProgressDialogFragment;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.MD5Util;
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

public class SinaVideoLoadTask extends AsyncTask<String, Integer, String> {

	final FragmentActivity fa;
	final String origurl;
	static final String dialogTag = "load_sina";

	public SinaVideoLoadTask(FragmentActivity fa, String origurl) {
		super();
		this.fa = fa;
		this.origurl = origurl;
	}

	private boolean startIntent = true;

	@Override
	protected void onPreExecute() {
		// create progress view
		ProgressDialogFragment pd = new ProgressDialogFragment();

		Bundle args = new Bundle();
		final String content = fa.getResources().getString(
				R.string.load_sina_video);
		args.putString("content", content);
		pd.setArguments(args);
		pd.show(fa.getSupportFragmentManager(), dialogTag);
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		if (!startIntent) {
			Toast.makeText(fa.getBaseContext(), "������Ƶ����ʧ��,������ϵͳ������",
					Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(origurl));
			boolean isIntentSafe = fa.getPackageManager()
					.queryIntentActivities(intent, 0).size() > 0;
			if (isIntentSafe)
				fa.startActivity(intent);
			return;
		}

		if (result != null) {
			Intent intent = new Intent(fa.getBaseContext(), Media_Player.class);
			Bundle b = new Bundle();
			b.putString("MEDIAPATH", result);
			intent.putExtras(b);
			fa.startActivity(intent);
		} else {
			Toast.makeText(fa.getBaseContext(), "��Ǹ,����Ƶ�޷�����,������ϵͳ������",
					Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(origurl));
			boolean isIntentSafe = fa.getPackageManager()
					.queryIntentActivities(intent, 0).size() > 0;
			if (isIntentSafe)
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
		try {
			ft.commit();
		} catch (Exception e) {

		}
	}

	@Override
	protected String doInBackground(String... params) {
		String videoid=null;
		if(params[0].equals("----")){
			final String htmlString = HttpUtil.iosGetHtml(origurl, null);
			videoid = StringUtil.getStringBetween(htmlString, 0,
					"['ipad_vid'] = \"", "\"").result;
			if (StringUtil.isEmpty(videoid)) {// ��,ץvid
				videoid = StringUtil.getStringBetween(htmlString, 0, "['vid'] = \"", "\"").result;
				if (StringUtil.isEmpty(videoid)) {// vid��,NULL
					return null;
				} else {
					if (videoid.equals("0")) {// vid��,NULL
						return null;
					}
				}
			} else {
				if (videoid.equals("0")) {// 0��VID
					videoid = StringUtil.getStringBetween(htmlString, 0, "['vid'] = \"", "\"").result;
					if (StringUtil.isEmpty(videoid)) {// vid��,NULL
						return null;
					} else {
						if (videoid.equals("0")) {// vid��,NULL
							return null;
						}
					}
				}
			}
		}else{
			videoid=params[0];
		}
		String ka=videoid+"Z6prk18aWxP278cVAH"+"0"+"0";
		String key = MD5Util.MD5(ka).substring(0,16)+"0";
		String xmldata = HttpUtil.iosGetHtml(
				"http://v.iask.com/v_play.php?vid=" + videoid+"&ran=0&k="+key, null);
		String mp4add = StringUtil.getStringBetween(xmldata, 0, "<url>",
				"</url>").result;
		mp4add = StringUtil.getStringBetween(mp4add, 0, "CDATA[", "]").result;
		return mp4add;
	}

}
