package sp.phone.utils;

import gov.anzong.androidnga.activity.ImageViewerActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

import sp.phone.task.TudouVideoLoadTask;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ArticleListWebClient extends WebViewClient {
	static private final String NGACN_BOARD_PREFIX ="http://bbs.ngacn.cc/thread.php?"; 
	static private final String NGA178_BOARD_PREFIX ="http://nga.178.com/thread.php?"; 
	static private final String NGACN_THREAD_PREFIX ="http://bbs.ngacn.cc/read.php?"; 
	static private final String NGA178_THREAD_PREFIX ="http://nga.178.com/read.php?"; 
	static private final String YOUKU_END= "/v.swf";
	static private final String YOUKU_START = "http://player.youku.com/player.php/sid/";
	static private final String TUDOU_END= "/";
	static private final String TUDOU_START = "http://www.tudou.com/v/";
	//http://www.tudou.com/a/YRxj-HoTxT0/&resourceId=0_04_05_99&iid=146525460/v.swf
	//http://www.tudou.com/v/Qw74nyAg1wU/&resourceId=0_04_05_99/v.swf
	private final FragmentActivity fa ;
	
	public ArticleListWebClient(FragmentActivity fa) {
		super();
		this.fa = fa;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String origurl) {
		if(!origurl.startsWith("http")){
			return true;
		}
		PhoneConfiguration conf = PhoneConfiguration.getInstance();
		final String url = origurl.toLowerCase(Locale.US);
		if(url.startsWith(NGACN_BOARD_PREFIX)
				|| url.startsWith(NGA178_BOARD_PREFIX ) ){
			Intent intent = new Intent();
			intent.setData(Uri.parse(origurl));
			intent.setClass(view.getContext(), conf.topicActivityClass);
			view.getContext().startActivity(intent);

		}else if(url.startsWith(NGACN_THREAD_PREFIX)
				|| url.startsWith(NGA178_THREAD_PREFIX ) ){
			Intent intent = new Intent();
			intent.setData(Uri.parse(origurl));
			intent.setClass(view.getContext(), conf.articleActivityClass);
			view.getContext().startActivity(intent);

			
		}else if(url.endsWith(".gif")||url.endsWith(".jpg")||
				url.endsWith(".png")||url.endsWith(".jpeg")||
				url.endsWith(".bmp")
				){
			Intent intent = new Intent();
			intent.putExtra("path", origurl);
			intent.setClass(view.getContext(), ImageViewerActivity.class);
			view.getContext().startActivity(intent);

		}else if(url.startsWith(YOUKU_START)){
			String id = StringUtil.getStringBetween(origurl, 0, YOUKU_START, YOUKU_END).result;
			String htmlUrl = "http://v.youku.com/player/getRealM3U8/vid/"
					+id +
					"/type/mp4/video.m3u8";
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(htmlUrl));
			//intent.setType("application/x-mpegURL");
			view.getContext().startActivity(intent);
		}else if(url.startsWith(TUDOU_START)){
			String id = StringUtil.getStringBetween(origurl, 0, TUDOU_START, TUDOU_END).result;
			
			TudouVideoLoadTask loader = new TudouVideoLoadTask(fa);
			if(ActivityUtil.isGreaterThan_2_3_3()){
				runOnExcutor(loader,id);
			}else{
				loader.execute(id);
			}
		}
		else{
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(origurl));
            boolean isIntentSafe = fa.getPackageManager().queryIntentActivities(intent,0).size() > 0;
            if(isIntentSafe)
			    view.getContext().startActivity(intent);
			//return false;
		}
		return true;
	}
	
	
	@TargetApi(11)
	private void runOnExcutor(TudouVideoLoadTask loader, String id){
		loader.executeOnExecutor(TudouVideoLoadTask.THREAD_POOL_EXECUTOR, id);
		
	}

	private static final String ips[] = {"74.125.129.141",
		"74.125.129.142",
		"74.125.129.143",
		"74.125.129.144",
		"74.125.129.145"
		};
	
	/*@Override
	@TargetApi(11)	
	public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
		String path = ExtensionEmotionAdapter.getPathByURI(url);
		InputStream is= null;
		boolean showImage = PhoneConfiguration.getInstance().isDownImgNoWifi() || isInWifi(view.getContext());
		
		try{
			if(path == null && !showImage)
				is = view.getContext().getAssets().open("ic_offline_image.png");
			if(path != null)
				is = view.getContext().getAssets().open(path);
		}catch (IOException e) {
			
		}
		if(is != null){
			WebResourceResponse ret = new WebResourceResponse("image/*", "utf-8", is);
			return ret;
		}
		if(showImage && !isInWifi(view.getContext()) )
		{
			String origUrl = url;
			url = url.toLowerCase();
			if(url.endsWith(".gif")||url.endsWith(".jpg")||
					url.endsWith(".png")||url.endsWith(".jpeg")||
					url.endsWith(".bmp")
					){
				
				is = getSmallImgStream(origUrl);
				
			}
			
			if(is != null){
				WebResourceResponse ret = new WebResourceResponse("image/png", "utf-8", is);
				return ret;
			}
			
		}
		
		return null;
		
				
	}*/
	
	private InputStream getSmallImgStream(final String url){
		InputStream is = null;
		try{
			
			String imgUri = "https://" + ips[0]+"/?url="
					+ URLEncoder.encode(url, "utf-8");
			URL imUrl = new URL(imgUri);
			HttpURLConnection conn = (HttpURLConnection) imUrl.openConnection();
			conn.setConnectTimeout(3000);
			//conn.setReadTimeout(4000);
			conn.setRequestProperty("Host", "ngatupian.appspot.com");
			conn.connect();
			is = conn.getInputStream();
			}catch(Exception e){
				Log.e("ArticleListWebClient", "fail to load small img " + Log.getStackTraceString(e));
				
			}
		return is;
	}

	@SuppressWarnings("unused")
	private boolean isInWifi(final Context activity) {
		ConnectivityManager conMan = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		return wifi == State.CONNECTED;
	}

	

}
