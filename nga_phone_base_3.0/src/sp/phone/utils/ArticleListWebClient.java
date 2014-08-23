package sp.phone.utils;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.ImageViewerActivity;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;

import android.annotation.TargetApi;
import android.content.ComponentName;
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
import android.widget.Toast;

public class ArticleListWebClient extends WebViewClient {
	static private final String NGACN_BOARD_PREFIX = "http://bbs.ngacn.cc/thread.php?";
	static private final String NGA178_BOARD_PREFIX = "http://nga.178.com/thread.php?";
	static private final String NGACN_THREAD_PREFIX = "http://bbs.ngacn.cc/read.php?";
	static private final String NGA178_THREAD_PREFIX = "http://nga.178.com/read.php?";
	static private final String NGACN_BOARD_PREFIX_NOHTTP = "bbs.ngacn.cc/thread.php?";
	static private final String NGA178_BOARD_PREFIX_NOHTTP = "nga.178.com/thread.php?";
	static private final String NGACN_THREAD_PREFIX_NOHTTP = "bbs.ngacn.cc/read.php?";
	static private final String NGA178_THREAD_PREFIX_NOHTTP = "nga.178.com/read.php?";
	static private final String ANDROIDNGAUSERNAME_START = "http://nga.178.com/nuke.php?func=ucp&username=";
	static private final String ANDROIDNGAUSERNAME_END = "&";

	
	private final FragmentActivity fa;
	static final String dialogTag = "load";

	public ArticleListWebClient(FragmentActivity fa) {
		super();
		this.fa = fa;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String origurl) {
		final String url = origurl.toLowerCase(Locale.US);
		PhoneConfiguration conf = PhoneConfiguration.getInstance();
		if (!url.startsWith("http") && !url.startsWith("market")) {
			if (url.startsWith(NGACN_BOARD_PREFIX_NOHTTP)
					|| url.startsWith(NGA178_BOARD_PREFIX_NOHTTP)) {
				Intent intent = new Intent();
				intent.setData(Uri.parse("http://"+origurl));
				intent.setClass(view.getContext(), conf.topicActivityClass);
				view.getContext().startActivity(intent);
			} else if (url.startsWith(NGACN_THREAD_PREFIX_NOHTTP)
					|| url.startsWith(NGA178_THREAD_PREFIX_NOHTTP)) {
				Intent intent = new Intent();
				intent.setData(Uri.parse("http://"+origurl));
				intent.putExtra("fromreplyactivity", 1);
				intent.setClass(view.getContext(), conf.articleActivityClass);
				view.getContext().startActivity(intent);
			}else if (url.endsWith(".gif") || url.endsWith(".jpg")
					|| url.endsWith(".png") || url.endsWith(".jpeg")
					|| url.endsWith(".bmp")) {
				Intent intent = new Intent();
				intent.putExtra("path", "http://"+origurl);
				intent.setClass(view.getContext(), ImageViewerActivity.class);
				view.getContext().startActivity(intent);
			}
			return true;
		}
		if (url.startsWith(NGACN_BOARD_PREFIX)
				|| url.startsWith(NGA178_BOARD_PREFIX)) {
			Intent intent = new Intent();
			intent.setData(Uri.parse(origurl));
			intent.setClass(view.getContext(), conf.topicActivityClass);
			view.getContext().startActivity(intent);

		} else if (url.startsWith(NGACN_THREAD_PREFIX)
				|| url.startsWith(NGA178_THREAD_PREFIX)) {
			Intent intent = new Intent();
			intent.setData(Uri.parse(origurl));
			intent.putExtra("fromreplyactivity", 1);
			intent.setClass(view.getContext(), conf.articleActivityClass);
			view.getContext().startActivity(intent);
		} else if (url.endsWith(".gif") || url.endsWith(".jpg")
				|| url.endsWith(".png") || url.endsWith(".jpeg")
				|| url.endsWith(".bmp")) {
			Intent intent = new Intent();
			intent.putExtra("path", origurl);
			intent.setClass(view.getContext(), ImageViewerActivity.class);
			view.getContext().startActivity(intent);
		} else if (url.startsWith(ANDROIDNGAUSERNAME_START)) {
			String data = StringUtil.getStringBetween(origurl, 0,
					ANDROIDNGAUSERNAME_START, ANDROIDNGAUSERNAME_END).result;
			try {
				data = URLDecoder.decode(data, "utf-8");
			} catch (UnsupportedEncodingException e) {
			}
			if (!StringUtil.isEmpty(data)) {
				Intent intent = new Intent();
				intent.putExtra("mode", "username");
				intent.putExtra("username", data);
				intent.setClass(view.getContext(),
						PhoneConfiguration.getInstance().profileActivityClass);
				view.getContext().startActivity(intent);
			}
		} else {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(origurl));
			boolean isIntentSafe = fa.getPackageManager()
					.queryIntentActivities(intent, 0).size() > 0;
			if (isIntentSafe)
				view.getContext().startActivity(intent);
			// return false;
		}
		return true;
	}

	private int StrTotalCount(String str, String key) {
		int count = 0;
		int index = 0;
		while ((index = str.indexOf(key, index)) != -1) {
			index = index + key.length();
			count++;
		}
		return count;
	}


	private static final String ips[] = { "74.125.129.141", "74.125.129.142",
			"74.125.129.143", "74.125.129.144", "74.125.129.145" };

	/*
	 * @Override
	 * 
	 * @TargetApi(11) public WebResourceResponse shouldInterceptRequest(WebView
	 * view, String url) { String path =
	 * ExtensionEmotionAdapter.getPathByURI(url); InputStream is= null; boolean
	 * showImage = PhoneConfiguration.getInstance().isDownImgNoWifi() ||
	 * isInWifi(view.getContext());
	 * 
	 * try{ if(path == null && !showImage) is =
	 * view.getContext().getAssets().open("ic_offline_image.png"); if(path !=
	 * null) is = view.getContext().getAssets().open(path); }catch (IOException
	 * e) {
	 * 
	 * } if(is != null){ WebResourceResponse ret = new
	 * WebResourceResponse("image/*", "utf-8", is); return ret; } if(showImage
	 * && !isInWifi(view.getContext()) ) { String origUrl = url; url =
	 * url.toLowerCase(); if(url.endsWith(".gif")||url.endsWith(".jpg")||
	 * url.endsWith(".png")||url.endsWith(".jpeg")|| url.endsWith(".bmp") ){
	 * 
	 * is = getSmallImgStream(origUrl);
	 * 
	 * }
	 * 
	 * if(is != null){ WebResourceResponse ret = new
	 * WebResourceResponse("image/png", "utf-8", is); return ret; }
	 * 
	 * }
	 * 
	 * return null;
	 * 
	 * 
	 * }
	 */

	private InputStream getSmallImgStream(final String url) {
		InputStream is = null;
		try {

			String imgUri = "https://" + ips[0] + "/?url="
					+ StringUtil.encodeUrl(url, "utf-8");
			URL imUrl = new URL(imgUri);
			HttpURLConnection conn = (HttpURLConnection) imUrl.openConnection();
			conn.setConnectTimeout(3000);
			// conn.setReadTimeout(4000);
			conn.setRequestProperty("Host", "ngatupian.appspot.com");
			conn.connect();
			is = conn.getInputStream();
		} catch (Exception e) {
			Log.e("ArticleListWebClient",
					"fail to load small img " + Log.getStackTraceString(e));

		}
		return is;
	}

	@SuppressWarnings("unused")
	private boolean isInWifi(final Context activity) {
		ConnectivityManager conMan = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		return wifi == State.CONNECTED;
	}
}
