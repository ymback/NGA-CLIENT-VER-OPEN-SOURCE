package sp.phone.utils;

import gov.anzong.androidnga.activity.ImageViewerActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Locale;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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

	public ArticleListWebClient(FragmentActivity fa) {
		super();
		this.fa = fa;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String origurl) {
		final String url = origurl.toLowerCase(Locale.US);
		PhoneConfiguration conf = PhoneConfiguration.getInstance();
		if (!url.startsWith("http") && !url.startsWith("market")) {
			Intent intent = new Intent();
			if (url.startsWith(NGACN_BOARD_PREFIX_NOHTTP)
					|| url.startsWith(NGA178_BOARD_PREFIX_NOHTTP)) {
				intent.setData(Uri.parse("http://"+origurl));
				intent.setClass(view.getContext(), conf.topicActivityClass);
				view.getContext().startActivity(intent);
				return true;
			} else if (url.startsWith(NGACN_THREAD_PREFIX_NOHTTP)
					|| url.startsWith(NGA178_THREAD_PREFIX_NOHTTP)) {
				intent.setData(Uri.parse("http://"+origurl));
				intent.putExtra("fromreplyactivity", 1);
				intent.setClass(view.getContext(), conf.articleActivityClass);
				view.getContext().startActivity(intent);
				return true;
			}else if (url.endsWith(".gif") || url.endsWith(".jpg")
					|| url.endsWith(".png") || url.endsWith(".jpeg")
					|| url.endsWith(".bmp")) {
				intent.putExtra("path", "http://"+origurl);
				intent.setClass(view.getContext(), ImageViewerActivity.class);
				view.getContext().startActivity(intent);
				return true;
			}
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
		}
		return true;
	}

}
