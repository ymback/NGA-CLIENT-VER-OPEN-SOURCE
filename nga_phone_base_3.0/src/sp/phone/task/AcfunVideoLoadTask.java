package sp.phone.task;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import gov.anzong.androidnga2.R;
import gov.anzong.androidnga2.activity.Media_Player;
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

public class AcfunVideoLoadTask extends AsyncTask<String, Integer, String> {

	final FragmentActivity fa;
	final String origurl;
	boolean isdelected = false;
	static final String dialogTag = "load_acfun";
	static private final String TUDOU_START = "http://www.tudou.com/programs/view/";
	static private final String TUDOU_END = "/";
	static private final String YOUKU_START = "http://v.youku.com/v_show/id_";
	static private final String YOUKU_END = ".html";
	static private final String QQ_START = "http://v.qq.com/";
	static private final String SINA_START = "http://video.sina.com.cn/";
	static private final String SINAYOU_START = "http://you.video.sina.com.cn/";

	public AcfunVideoLoadTask(FragmentActivity fa, String origurl) {
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
				R.string.load_acfun_video);
		args.putString("content", content);
		pd.setArguments(args);
		pd.show(fa.getSupportFragmentManager(), dialogTag);
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		if (!startIntent) {
			Toast.makeText(fa.getBaseContext(), "创建视频窗口失败,将调用系统打开链接",
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
			b.putString("origurl", origurl);
			intent.putExtras(b);
			fa.startActivity(intent);
		} else {
			if (isdelected) {
				Toast.makeText(fa.getBaseContext(), "抱歉,该视频已经被删除",
						Toast.LENGTH_LONG).show();
				return;
			} else {
				Toast.makeText(fa.getBaseContext(), "抱歉,该视频无法解析,将调用系统打开链接",
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(origurl));
				boolean isIntentSafe = fa.getPackageManager()
						.queryIntentActivities(intent, 0).size() > 0;
				if (isIntentSafe)
					fa.startActivity(intent);
			}
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
		final String uri = params[0];
		final String htmlStringstep1 = HttpUtil.iosGetHtml(uri, null);
		String datavid = StringUtil.getStringBetween(htmlStringstep1, 0,
				"data-vid=\"", "\"").result;
		if (StringUtil.isEmpty(datavid)) {
			if(StringUtil.isEmpty(datavid))
				return null;
			if (htmlStringstep1.indexOf("该页面可能因为如下原因被删除") > 0) {
				this.isdelected = true;
			}
			return null;
		} else {
			String newurl = "http://www.acfun.com/video/getVideo.aspx?id="
					+ datavid;
			final String htmlStringstep2 = HttpUtil.iosGetHtml(newurl, null);
			String sourceType = StringUtil.getStringBetween(htmlStringstep2, 0,
					"\"sourceType\":\"", "\"").result;
			String sourceUrl = StringUtil.getStringBetween(htmlStringstep2, 0,
					"\"sourceUrl\":\"", "\"").result;
			if (StringUtil.isEmpty(sourceType)) {
				return null;
			}
			if (sourceType.toLowerCase(Locale.US).equals("sina")) {
				if (StringUtil.isEmpty(sourceUrl)) {
					return null;
				}
				if ((sourceUrl.toLowerCase(Locale.US).startsWith(SINA_START) || sourceUrl
						.toLowerCase(Locale.US).startsWith(SINAYOU_START))
						&& StrTotalCount(sourceUrl, "/") >= 4) {
					String videoid = StringUtil.getStringBetween(htmlStringstep2, 0,
							"\"sourceId\":\"", "\"").result;
					if(StringUtil.isEmpty(videoid)){
						final String htmlStringofsina = HttpUtil.iosGetHtml(
								sourceUrl, null);
						videoid = StringUtil.getStringBetween(
								htmlStringofsina, 0, "['ipad_vid'] = \"", "\"").result;
						if (StringUtil.isEmpty(videoid)) {// 空,抓vid
							videoid = StringUtil.getStringBetween(htmlStringofsina,
									0, "['vid'] = \"", "\"").result;
							if (StringUtil.isEmpty(videoid)) {// vid空,NULL
								return null;
							} else {
								if (videoid.equals("0")) {// vid空,NULL
									return null;
								}
							}
						} else {
							if (videoid.equals("0")) {// 0，VID
								videoid = StringUtil.getStringBetween(
										htmlStringofsina, 0, "['vid'] = \"", "\"").result;
								if (StringUtil.isEmpty(videoid)) {// vid空,NULL
									return null;
								} else {
									if (videoid.equals("0")) {// vid空,NULL
										return null;
									}
								}
							}
						}
					}else{
						if(!isNumeric(videoid)){
							final String htmlStringofsina = HttpUtil.iosGetHtml(
									sourceUrl, null);
							videoid = StringUtil.getStringBetween(
									htmlStringofsina, 0, "['ipad_vid'] = \"", "\"").result;
							if (StringUtil.isEmpty(videoid)) {// 空,抓vid
								videoid = StringUtil.getStringBetween(htmlStringofsina,
										0, "['vid'] = \"", "\"").result;
								if (StringUtil.isEmpty(videoid)) {// vid空,NULL
									return null;
								} else {
									if (videoid.equals("0")) {// vid空,NULL
										return null;
									}
								}
							} else {
								if (videoid.equals("0")) {// 0，VID
									videoid = StringUtil.getStringBetween(
											htmlStringofsina, 0, "['vid'] = \"", "\"").result;
									if (StringUtil.isEmpty(videoid)) {// vid空,NULL
										return null;
									} else {
										if (videoid.equals("0")) {// vid空,NULL
											return null;
										}
									}
								}
							}
						}
					}
					String ka = videoid + "Z6prk18aWxP278cVAH" + "0" + "0";
					String key = MD5Util.MD5(ka).substring(0, 16) + "0";
					String xmldata = HttpUtil.iosGetHtml(
							"http://v.iask.com/v_play.php?vid=" + videoid
									+ "&ran=0&k=" + key, null);
					String mp4add = StringUtil.getStringBetween(xmldata, 0,
							"<url>", "</url>").result;
					mp4add = StringUtil.getStringBetween(mp4add, 0, "CDATA[",
							"]").result;
					return mp4add;
				} else {
					if (isNumeric(sourceUrl)) {
						String ka = sourceUrl + "Z6prk18aWxP278cVAH" + "0"
								+ "0";
						String key = MD5Util.MD5(ka).substring(0, 16) + "0";
						String xmldata = HttpUtil.iosGetHtml(
								"http://v.iask.com/v_play.php?vid=" + sourceUrl
										+ "&ran=0&k=" + key, null);
						String mp4add = StringUtil.getStringBetween(xmldata, 0,
								"<url>", "</url>").result;
						mp4add = StringUtil.getStringBetween(mp4add, 0,
								"CDATA[", "]").result;
						return mp4add;
					} else {
						return null;
					}
				}
			} else if (sourceType.toLowerCase(Locale.US).equals("youku")) {
				if (StringUtil.isEmpty(sourceUrl)) {
					return null;
				}
				if (sourceUrl.toLowerCase(Locale.US).startsWith(TUDOU_START)) {
					String id = StringUtil.getStringBetween(sourceUrl, 0,
							TUDOU_START, TUDOU_END).result;
					final String tudouuri = "http://www.tudou.com/programs/view/"
							+ id + "/";
					final String htmlStringFortudou = HttpUtil.iosGetHtml(
							tudouuri, null);
					final String iid = StringUtil.getStringBetween(
							htmlStringFortudou, 0, "iid: ", " ").result;
					if (StringUtil.isEmpty(iid))
						return null;
					String m3u8Url = "http://vr.tudou.com/v2proxy/v2.m3u8?debug=1&st=2&pw=&it="
							+ iid;
					return m3u8Url;
				} else if (sourceUrl.toLowerCase(Locale.US).startsWith(
						YOUKU_START)) {// 优酷,可以直接拿VID解析的
					String id = StringUtil.getStringBetween(sourceUrl, 0,
							YOUKU_START, YOUKU_END).result;
					String htmlUrl = "http://v.youku.com/player/getM3U8/vid/"
							+ id + "/type/mp4/video.m3u8";
					return htmlUrl;
				}
			} else if (sourceType.toLowerCase(Locale.US).equals("tudou")) {
				if (StringUtil.isEmpty(sourceUrl)) {
					return null;
				}
				if (sourceUrl.toLowerCase(Locale.US).startsWith(TUDOU_START)) {
					String id = StringUtil.getStringBetween(sourceUrl, 0,
							TUDOU_START, TUDOU_END).result;
					final String tudouuri = "http://www.tudou.com/programs/view/"
							+ id + "/";
					final String htmlStringFortudou = HttpUtil.iosGetHtml(
							tudouuri, null);
					final String iid = StringUtil.getStringBetween(
							htmlStringFortudou, 0, "iid: ", " ").result;
					if (StringUtil.isEmpty(iid))
						return null;
					String m3u8Url = "http://vr.tudou.com/v2proxy/v2.m3u8?debug=1&st=2&pw=&it="
							+ iid;
					return m3u8Url;
				} else {
					return null;
				}
			} else if (sourceType.toLowerCase(Locale.US).equals("qq")) {
				if (StringUtil.isEmpty(sourceUrl)) {
					return null;
				}
				if (sourceUrl.toLowerCase(Locale.US).startsWith(QQ_START)) {
					String qqurl;
					try {
						qqurl = URLEncoder.encode(sourceUrl, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						return null;
					}
					qqurl = qqurl.replaceAll("tpout.swf", "TPout.swf");
					qqurl = "http://www.flvcd.com/parse.php?kw=" + qqurl;
					String htmlStringofQQ = HttpUtil.iosGetHtml(qqurl, null);
					String iid = StringUtil.getStringBetween(htmlStringofQQ, 0,
							"clipurl = \"", "\"").result;
					if (StringUtil.isEmpty(iid))
						return null;
					return iid;
				}
			} else {
				return null;
			}

		}
		// String videourl = StringUtil.getStringBetween(
		// htmlString, 0, "\"src\":\"", "\"").result;
		// if(StringUtil.isEmpty(videourl)){
		// return null;
		// }
		return null;
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
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
}
