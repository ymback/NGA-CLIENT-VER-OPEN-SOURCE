package sp.phone.utils;

import gov.anzong.androidnga.activity.ImageViewerActivity;
import gov.anzong.androidnga.activity.Media_Player;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

import sp.phone.task.FiveSixVideoLoadTask;
import sp.phone.task.Ku6VideoLoadTask;
import sp.phone.task.LetvVideoLoadTask;
import sp.phone.task.QQVideoLoadTask;
import sp.phone.task.SohuVideoLoadTask;
import sp.phone.task.TudouVideoLoadTask;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ArticleListWebClient extends WebViewClient {
	static private final String NGACN_BOARD_PREFIX ="http://bbs.ngacn.cc/thread.php?"; 
	static private final String NGA178_BOARD_PREFIX ="http://nga.178.com/thread.php?"; 
	static private final String NGACN_THREAD_PREFIX ="http://bbs.ngacn.cc/read.php?"; 
	static private final String NGA178_THREAD_PREFIX ="http://nga.178.com/read.php?"; 
	static private final String YOUKUSWF_END= "/v.swf";
	static private final String YOUKUSWF_START = "http://player.youku.com/player.php/sid/";
	static private final String YOUKU_END= ".html";
	static private final String YOUKU_START = "http://v.youku.com/v_show/id_";
	static private final String TUDOU_END= "/";
	static private final String TUDOU_START = "http://www.tudou.com/programs/view/";
	static private final String TUDOUSWF_END= "/";
	static private final String TUDOUSWF_START = "http://www.tudou.com/v/";
	static private final String MYSOHU_END= ".shtml";
	static private final String MYSOHU_START = "http://my.tv.sohu.com/us/";
	static private final String SOHU_END= ".shtml";
	static private final String SOHU_START = "http://tv.sohu.com/";
	static private final String SOHUSWF_END= "/v.swf";
	static private final String SOHUSWF_START = "http://share.vrs.sohu.com/";
	static private final String MYSOHUSWF_START = "http://share.vrs.sohu.com/my/v.swf";
	static private final String A56_END= ".html";
	static private final String A56_START = "http://www.56.com/u";
	static private final String A56SWF_END= ".swf";
	static private final String A56SWF_START = "http://player.56.com/v_";
	static private final String KU6_END= "..";
	static private final String KU6_START = "http://v.ku6.com/show/";
	static private final String KU6SWF_END= "..";
	static private final String KU6SWF_START = "http://player.ku6.com/refer/";
	static private final String LETV_START = "http://www.letv.com/ptv/vplay/";
	static private final String LETVSWF_INCLUDE = "letv.com/player/swfplayer.swf";
	static private final String QQ_START = "http://v.qq.com/boke/page/";
	static private final String QQSWF_START = "http://static.video.qq.com/TPout.swf";
	
	//http://www.tudou.com/a/YRxj-HoTxT0/&resourceId=0_04_05_99&iid=146525460/v.swf
	//http://www.tudou.com/v/Qw74nyAg1wU/&resourceId=0_04_05_99/v.swf
	private final FragmentActivity fa ;
	static final String dialogTag = "load_tudou";
	
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
					"/type/mp4/v.m3u8";
			Intent intent = new Intent(view.getContext(),Media_Player.class);
			Bundle b = new Bundle();
			b.putString("MEDIAPATH", htmlUrl);
			intent.putExtras(b);
			view.getContext().startActivity(intent);
		}else if(url.startsWith(YOUKUSWF_START)){
			String id = StringUtil.getStringBetween(origurl, 0, YOUKUSWF_START, YOUKUSWF_END).result;
			String htmlUrl = "http://v.youku.com/player/getRealM3U8/vid/"
					+id +
					"/type/mp4/v.m3u8";
			Intent intent = new Intent(view.getContext(),Media_Player.class);
			Bundle b = new Bundle();
			b.putString("MEDIAPATH", htmlUrl);
			intent.putExtras(b);
			view.getContext().startActivity(intent);
		}else if(url.startsWith(MYSOHU_START)){
			String id = StringUtil.getStringBetween(origurl, 0, MYSOHU_START, MYSOHU_END).result;
			id=id.substring(id.lastIndexOf("/")+1);
			String htmlUrl = "http://my.tv.sohu.com/ipad/"
					+id +
					".m3u8";
			Intent intent = new Intent(view.getContext(),Media_Player.class);
			Bundle b = new Bundle();
			b.putString("MEDIAPATH", htmlUrl);
			intent.putExtras(b);
			view.getContext().startActivity(intent);
		}else if(url.startsWith(MYSOHUSWF_START)){
			String id = StringUtil.getStringBetween(origurl, 0, "id=", "&").result;
			String htmlUrl = "http://my.tv.sohu.com/ipad/"
					+id +
					".m3u8";
			Intent intent = new Intent(view.getContext(),Media_Player.class);
			Bundle b = new Bundle();
			b.putString("MEDIAPATH", htmlUrl);
			intent.putExtras(b);
			view.getContext().startActivity(intent);
		}else if(url.startsWith(SOHU_START)){
			SohuVideoLoadTask loader = new SohuVideoLoadTask(fa);
			if(ActivityUtil.isGreaterThan_2_3_3()){
				runOnExcutorforSohu(loader,url);
			}else{
				loader.execute(url);
			}
		}else if(url.startsWith(SOHUSWF_START)){
			String id = StringUtil.getStringBetween(origurl, 0, SOHUSWF_START, SOHUSWF_END).result;
			String htmlUrl = "http://hot.vrs.sohu.com/ipad"
					+id +
					".m3u8";
			Intent intent = new Intent(view.getContext(),Media_Player.class);
			Bundle b = new Bundle();
			b.putString("MEDIAPATH", htmlUrl);
			intent.putExtras(b);
			view.getContext().startActivity(intent);
		}else if(url.startsWith(TUDOU_START)){
			String id = StringUtil.getStringBetween(origurl, 0, TUDOU_START, TUDOU_END).result;
			TudouVideoLoadTask loader = new TudouVideoLoadTask(fa);
			if(ActivityUtil.isGreaterThan_2_3_3()){
				runOnExcutorforTudou(loader,id);
			}else{
				loader.execute(id);
			}
		}else if(url.startsWith(TUDOUSWF_START)){
			String id = StringUtil.getStringBetween(origurl, 0, TUDOUSWF_START, TUDOUSWF_END).result;
			TudouVideoLoadTask loader = new TudouVideoLoadTask(fa);
			if(ActivityUtil.isGreaterThan_2_3_3()){
				runOnExcutorforTudou(loader,id);
			}else{
				loader.execute(id);
			}
		}
		else if(url.startsWith(A56_START)){
			String id = StringUtil.getStringBetween(origurl, 0, "v_", A56_END).result;
			FiveSixVideoLoadTask loader = new FiveSixVideoLoadTask(fa);
			if(ActivityUtil.isGreaterThan_2_3_3()){
				runOnExcutorfor56(loader,id);
			}else{
				loader.execute(id);
			}
		}
		else if(url.startsWith(A56SWF_START)){
			String id = StringUtil.getStringBetween(origurl, 0, A56SWF_START, A56SWF_END).result;
			FiveSixVideoLoadTask loader = new FiveSixVideoLoadTask(fa);
			if(ActivityUtil.isGreaterThan_2_3_3()){
				runOnExcutorfor56(loader,id);
			}else{
				loader.execute(id);
			}
		}
		else if(url.startsWith(KU6_START)){
			String id = StringUtil.getStringBetween(origurl, 0, KU6_START, KU6_END).result;
			Ku6VideoLoadTask loader = new Ku6VideoLoadTask(fa);
			if(ActivityUtil.isGreaterThan_2_3_3()){
				runOnExcutorforku6(loader,id);
			}else{
				loader.execute(id);
			}
		}
		else if(url.startsWith(KU6SWF_START)){
			String id = StringUtil.getStringBetween(origurl, 0, KU6SWF_START, KU6SWF_END).result;
			Ku6VideoLoadTask loader = new Ku6VideoLoadTask(fa);
			if(ActivityUtil.isGreaterThan_2_3_3()){
				runOnExcutorforku6(loader,id);
			}else{
				loader.execute(id);
			}
		}
		else if(url.startsWith(LETV_START)){
			String id = url;
			LetvVideoLoadTask loader = new LetvVideoLoadTask(fa);
			if(ActivityUtil.isGreaterThan_2_3_3()){
				runOnExcutorforletv(loader,id);
			}else{
				loader.execute(id);
			}
		}
		else if(url.indexOf(LETVSWF_INCLUDE.toLowerCase(Locale.US))>0){
			String id = url;
			LetvVideoLoadTask loader = new LetvVideoLoadTask(fa);
			if(ActivityUtil.isGreaterThan_2_3_3()){
				runOnExcutorforletv(loader,id);
			}else{
				loader.execute(id);
			}
		}
		else if(url.startsWith(QQ_START) || url.indexOf(QQSWF_START.toLowerCase(Locale.US))==0){
			String id = url;
			QQVideoLoadTask loader = new QQVideoLoadTask(fa);
			if(ActivityUtil.isGreaterThan_2_3_3()){
				runOnExcutorforqq(loader,id);
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
	private void runOnExcutorforTudou(TudouVideoLoadTask loader, String id){
		loader.executeOnExecutor(TudouVideoLoadTask.THREAD_POOL_EXECUTOR, id);
		
	}

	@TargetApi(11)
	private void runOnExcutorforSohu(SohuVideoLoadTask loader, String id){
		loader.executeOnExecutor(SohuVideoLoadTask.THREAD_POOL_EXECUTOR, id);
		
	}
	@TargetApi(11)
	private void runOnExcutorforqq(QQVideoLoadTask loader, String id){
		loader.executeOnExecutor(QQVideoLoadTask.THREAD_POOL_EXECUTOR, id);
		
	}
	
	@TargetApi(11)
	private void runOnExcutorfor56(FiveSixVideoLoadTask loader, String id){
		loader.executeOnExecutor(FiveSixVideoLoadTask.THREAD_POOL_EXECUTOR, id);
		
	}
	@TargetApi(11)
	private void runOnExcutorforku6(Ku6VideoLoadTask loader, String id){
		loader.executeOnExecutor(Ku6VideoLoadTask.THREAD_POOL_EXECUTOR, id);
		
	}

	@TargetApi(11)
	private void runOnExcutorforletv(LetvVideoLoadTask loader, String id){
		loader.executeOnExecutor(LetvVideoLoadTask.THREAD_POOL_EXECUTOR, id);
		
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
					+StringUtil.encodeUrl(url, "utf-8");
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
