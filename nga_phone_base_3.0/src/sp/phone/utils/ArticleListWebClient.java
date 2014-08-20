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

import sp.phone.task.AcfunVideoLoadTask;
import sp.phone.task.BilibiliCidVideoLoadTask;
import sp.phone.task.BilibiliVideoLoadTask;
import sp.phone.task.FiveSixVideoLoadTask;
import sp.phone.task.Ku6VideoLoadTask;
import sp.phone.task.LetvVideoLoadTask;
import sp.phone.task.NeteaseVideoLoadTask;
import sp.phone.task.PPSVideoLoadTask;
import sp.phone.task.QQVideoLoadTask;
import sp.phone.task.SinaVideoLoadTask;
import sp.phone.task.SohuVideoLoadTask;
import sp.phone.task.TudouVideoLoadTask;
import sp.phone.task.WASUVideoLoadTask;
import sp.phone.task.YoukuVideoLoadTask;
import sp.phone.task.YoutubeVideoLoadTask;
import sp.phone.task.YouxiaVideoLoadTask;
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
	static private final String YOUKUSWF_END = "/v.swf";
	static private final String YOUKUSWF_START = "http://player.youku.com/player.php/";
	static private final String YOUKUSWF2_START = "http://static.youku.com";
	static private final String YOUKUSWF3_START = "http://player.youku.com/embed/";
	static private final String YOUKU_END = ".html";
	static private final String YOUKU_START = "http://v.youku.com/v_show/id_";
	static private final String TUDOU_END = "/";
	static private final String TUDOU_START = "http://www.tudou.com/programs/view/";
	static private final String TUDOUWITHOUTWWW_START = "http://tudou.com/programs/view/";// 没有www一样能用OK
	static private final String TUDOUSWF_END = "/";
	static private final String TUDOUSWF_START = "http://www.tudou.com/v/";
	static private final String TUDOUSWFWITHOUTWWW_START = "http://tudou.com/v/";// 没有www一样能用OK
	static private final String MYSOHU_END = ".shtml";
	static private final String MYSOHU_START = "http://my.tv.sohu.com/us/";
	static private final String MYSOHU2_END = ".shtml";
	static private final String MYSOHU2_START = "http://my.tv.sohu.com/us/";
	static private final String SOHU_END = ".shtml";
	static private final String SOHU_START = "http://tv.sohu.com/";
	static private final String SOHUSWF_END = "/v.swf";
	static private final String SOHUSWF_START = "http://share.vrs.sohu.com/";
	static private final String MYSOHUSWF_START = "http://share.vrs.sohu.com/my/v.swf";
	static private final String A56_END = ".html";
	static private final String A56_START = "http://www.56.com/u";
	static private final String A56WITHOUTWWW_START = "http://56.com/u";// 没有www一样能用OK
	static private final String A56SWF_END = ".swf";
	static private final String A56SWF_START = "http://player.56.com/v_";
	static private final String KU6_END = "..";
	static private final String KU6_START = "http://v.ku6.com/show/";
	static private final String KU6SWF_END = "..";
	static private final String KU6SWF_START = "http://player.ku6.com/refer/";
	static private final String LETV_START = "http://www.letv.com/ptv/vplay/";// 确认没有www是不行的
	static private final String LETVSWF_INCLUDE = "letv.com/player/swfplayer.swf";
	static private final String QQ_START = "http://v.qq.com/boke/page/";
	static private final String QQ2_START = "http://v.qq.com/cover/";
	static private final String QQSWF_START = "http://static.video.qq.com/TPout.swf";
	static private final String WASU_START = "http://www.wasu.cn/play/show/id/";// 确认没有www是不行的
	static private final String YOUTUBE_WITH = "http://www.youtube.com/watch?v=";
	static private final String YOUTUBENOWWW_WITH = "http://youtube.com/watch?v=";
	static private final String YOUTUBESHARE_START = "http://youtu.be/";
	static private final String YOUTUBESHAREEMBED_START = "http://www.youtube.com/embed/";
	static private final String YOUTUBESHAREEMBEDNOWWW_START = "http://youtube.com/embed/";
	static private final String YOUTUBESHAREEMBEDNOCOOKIE_START = "http://www.youtube-nocookie.com/embed/";
	static private final String YOUTUBESHAREEMBEDNOCOOKIENOWWW_START = "http://youtube-nocookie.com/embed/";
	static private final String YOUTUBESHAREEMBEDOLD_START = "http://www.youtube.com/v/";
	static private final String YOUTUBESHAREEMBEDNOWWWOLD_START = "http://youtube.com/v/";
	static private final String YOUTUBESHAREEMBEDNOCOOKIEOLD_START = "http://www.youtube-nocookie.com/v/";
	static private final String YOUTUBESHAREEMBEDNOCOOKIENOWWWOLD_START = "http://youtube-nocookie.com/v/";
	static private final String YOUTUBE_END = "?";

	static private final String NETEASE_START = "http://v.163.com/";// 确认没有www是不行的
	static private final String NETEASEGAME_START = "http://v.game.163.com/video/";// 确认没有www是不行的
	static private final String NETEASE_END = ".html";// 确认没有www是不行的

	static private final String BILIBILI_START = "http://www.bilibili.tv/video/av";
	static private final String BILIBILINOWWW_START = "http://bilibili.tv/video/av";
	static private final String BILIBILI2_START = "http://bilibili.kankanews.com/video/av";
	static private final String BILIBILI_END = "/";
	static private final String BILIBILICID_START = "https://secure.bilibili.tv/secure,";
	static private final String BILIBILICID_END = "&";

	static private final String PPS_START = "http://v.pps.tv/play_";
	static private final String PPS_END = ".html";
	static private final String PPSSWF_START = "http://player.pps.tv/player/sid/";
	static private final String PPSSWF_END = "/v.swf";
	static private final String SINA_START = "http://video.sina.com.cn/";
	static private final String SINAYOU_START = "http://you.video.sina.com.cn/b/";
	static private final String SINAENT_START = "http://ent.sina.com.cn/";
	static private final String SINASWF_START = "http://you.video.sina.com.cn/api/sinawebApi/outplayrefer.php";

	static private final String ANDROIDNGAUSERNAME_START = "http://nga.178.com/nuke.php?func=ucp&username=";
	static private final String ANDROIDNGAUSERNAME_END = "&";

	static private final String ACFUN_START = "http://www.acfun.com/v/ac";
	static private final String ACFUNSWF_START = "http://static.acfun.com/player/ACFlashPlayer.out.swf";
	static private final String ACFUNTV_START = "http://www.acfun.tv/v/ac";
	static private final String ACFUNTVSWF_START = "http://static.acfun.tv/player/ACFlashPlayer.out.swf";

	static private final String YOUXIA_START = "http://v.ali213.net/video/";
	private final FragmentActivity fa;
	static final String dialogTag = "load_tudou";

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
		} else if (url.startsWith(YOUKU_START) && showPlayMode() != 4) {// 优酷,可以直接拿VID解析的
			String id = StringUtil.getStringBetween(origurl, 0, YOUKU_START,
					YOUKU_END).result;
			YoukuVideoLoadTask loader = new YoukuVideoLoadTask(fa,origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforYouku(loader, id);
			} else {
				loader.execute(url);
			}
		} else if (url.startsWith(YOUKUSWF_START) && showPlayMode() != 4) {// 优酷,可以直接拿VID解析的
			String id = StringUtil.getStringBetween(origurl, 0, "sid/",
					YOUKUSWF_END).result;
			YoukuVideoLoadTask loader = new YoukuVideoLoadTask(fa,origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforYouku(loader, id);
			} else {
				loader.execute(url);
			}
		} else if (url.startsWith(YOUKUSWF2_START) && showPlayMode() != 4
				&& url.indexOf("VideoIDS=".toLowerCase(Locale.US)) > 0
				&& StrTotalCount(url, "/") > 4) {// 优酷,可以直接拿VID解析的
			String id = StringUtil.getStringBetween(origurl, 0, "VideoIDS=",
					"&").result;
			YoukuVideoLoadTask loader = new YoukuVideoLoadTask(fa,origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforYouku(loader, id);
			} else {
				loader.execute(url);
			}
		} else if (url.startsWith(YOUKUSWF3_START) && showPlayMode() != 4) {// 优酷,可以直接拿VID解析的
			String id = StringUtil.getStringBetween(origurl, 0,
					YOUKUSWF3_START, "/").result;
			YoukuVideoLoadTask loader = new YoukuVideoLoadTask(fa,origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforYouku(loader, id);
			} else {
				loader.execute(url);
			}
		} else if (url.startsWith(MYSOHU_START) && showPlayMode() != 4) {// 搜狐,可以直接拿ID解析的
			String id = StringUtil.getStringBetween(origurl, 0, MYSOHU_START,
					MYSOHU_END).result;
			id = id.substring(id.lastIndexOf("/") + 1);
			String htmlUrl = "http://my.tv.sohu.com/ipad/" + id + ".m3u8";
			try {
				Intent intent = new Intent();
				ComponentName comp = new ComponentName(
						"gov.anzong.mediaplayer",
						"gov.anzong.mediaplayer.ReceiveIntentActivity");
				intent.setComponent(comp);
				intent.putExtra("uri", htmlUrl);
				intent.putExtra("title", "搜狐视频");
				view.getContext().startActivity(intent);
			} catch (Exception e) {
				// TODO
				Toast.makeText(view.getContext(),
						R.string.videoplay_ngaplayernotinstall_error,
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(origurl));
				boolean isIntentSafe = fa.getPackageManager()
						.queryIntentActivities(intent, 0).size() > 0;
				if (isIntentSafe)
					view.getContext().startActivity(intent);
			}
		} else if (url.startsWith(MYSOHU2_START) && showPlayMode() != 4) {// 搜狐,可以直接拿ID解析的
			String id = StringUtil.getStringBetween(origurl, 0, MYSOHU2_START,
					MYSOHU2_END).result;
			id = id.substring(id.lastIndexOf("/") + 1);
			String htmlUrl = "http://my.tv.sohu.com/ipad/" + id + ".m3u8";
			try {
				Intent intent = new Intent();
				ComponentName comp = new ComponentName(
						"gov.anzong.mediaplayer",
						"gov.anzong.mediaplayer.ReceiveIntentActivity");
				intent.setComponent(comp);
				intent.putExtra("uri", htmlUrl);
				intent.putExtra("title", "搜狐视频");
				view.getContext().startActivity(intent);
			} catch (Exception e) {
				// TODO
				Toast.makeText(view.getContext(),
						R.string.videoplay_ngaplayernotinstall_error,
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(origurl));
				boolean isIntentSafe = fa.getPackageManager()
						.queryIntentActivities(intent, 0).size() > 0;
				if (isIntentSafe)
					view.getContext().startActivity(intent);
			}
		} else if (url.startsWith(MYSOHUSWF_START) && showPlayMode() != 4) {// 搜狐,可以直接拿ID解析的
			String id = StringUtil.getStringBetween(origurl, 0, "id=", "&").result;
			String htmlUrl = "http://my.tv.sohu.com/ipad/" + id + ".m3u8";
			try {
				Intent intent = new Intent();
				ComponentName comp = new ComponentName(
						"gov.anzong.mediaplayer",
						"gov.anzong.mediaplayer.ReceiveIntentActivity");
				intent.setComponent(comp);
				intent.putExtra("uri", htmlUrl);
				intent.putExtra("title", "搜狐视频");
				view.getContext().startActivity(intent);
			} catch (Exception e) {
				// TODO
				Toast.makeText(view.getContext(),
						R.string.videoplay_ngaplayernotinstall_error,
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(origurl));
				boolean isIntentSafe = fa.getPackageManager()
						.queryIntentActivities(intent, 0).size() > 0;
				if (isIntentSafe)
					view.getContext().startActivity(intent);
			}
		} else if (url.startsWith(SOHU_START) && showPlayMode() != 4) {// 搜狐,可以直接拿ID解析的
			SohuVideoLoadTask loader = new SohuVideoLoadTask(fa);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforSohu(loader, url);
			} else {
				loader.execute(url);
			}
		} else if (url.startsWith(SOHUSWF_START) && showPlayMode() != 4) {// 搜狐,可以直接拿ID解析的
			String id = StringUtil.getStringBetween(origurl, 0, SOHUSWF_START,
					SOHUSWF_END).result;
			String htmlUrl = "http://hot.vrs.sohu.com/ipad" + id + ".m3u8";
			try {
				Intent intent = new Intent();
				ComponentName comp = new ComponentName(
						"gov.anzong.mediaplayer",
						"gov.anzong.mediaplayer.ReceiveIntentActivity");
				intent.setComponent(comp);
				intent.putExtra("uri", htmlUrl);
				intent.putExtra("title", "搜狐视频");
				view.getContext().startActivity(intent);
			} catch (Exception e) {
				// TODO
				Toast.makeText(view.getContext(),
						R.string.videoplay_ngaplayernotinstall_error,
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(origurl));
				boolean isIntentSafe = fa.getPackageManager()
						.queryIntentActivities(intent, 0).size() > 0;
				if (isIntentSafe)
					view.getContext().startActivity(intent);
			}
		} else if (url.startsWith(TUDOU_START) && showPlayMode() != 4) {// 土豆,需要解析后获取id然后获取M3U8地址
			String id = StringUtil.getStringBetween(origurl, 0, TUDOU_START,
					TUDOU_END).result;
			TudouVideoLoadTask loader = new TudouVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforTudou(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(TUDOUWITHOUTWWW_START) && showPlayMode() != 4) {// 土豆,需要解析后获取id然后获取M3U8地址
			String id = StringUtil.getStringBetween(origurl, 0,
					TUDOUWITHOUTWWW_START, TUDOU_END).result;
			TudouVideoLoadTask loader = new TudouVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforTudou(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(TUDOUSWF_START) && showPlayMode() != 4) {// 土豆,需要解析后获取id然后获取M3U8地址
			String id = StringUtil.getStringBetween(origurl, 0, TUDOUSWF_START,
					TUDOUSWF_END).result;
			TudouVideoLoadTask loader = new TudouVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforTudou(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(TUDOUSWFWITHOUTWWW_START)
				&& showPlayMode() != 4) {// 土豆,需要解析后获取id然后获取M3U8地址
			String id = StringUtil.getStringBetween(origurl, 0,
					TUDOUSWFWITHOUTWWW_START, TUDOUSWF_END).result;
			TudouVideoLoadTask loader = new TudouVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforTudou(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(A56_START) && showPlayMode() != 4) {// 通过JSON截取地址
			String id = StringUtil.getStringBetween(origurl, 0, "v_", A56_END).result;
			FiveSixVideoLoadTask loader = new FiveSixVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorfor56(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(A56WITHOUTWWW_START) && showPlayMode() != 4) {// 通过JSON截取地址
			String id = StringUtil.getStringBetween(origurl, 0, "v_", A56_END).result;
			FiveSixVideoLoadTask loader = new FiveSixVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorfor56(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(A56SWF_START) && showPlayMode() != 4) {// 通过JSON截取地址
			String id = StringUtil.getStringBetween(origurl, 0, A56SWF_START,
					A56SWF_END).result;
			FiveSixVideoLoadTask loader = new FiveSixVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorfor56(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(KU6_START) && showPlayMode() != 4) {// 酷六会把M3U8转存为F4V，所以讲获得的M3U8转存到缓存再读取缓存
			String id = StringUtil.getStringBetween(origurl, 0, KU6_START,
					KU6_END).result;
			Ku6VideoLoadTask loader = new Ku6VideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforku6(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(KU6SWF_START) && showPlayMode() != 4) {// 酷六会把M3U8转存为F4V，所以讲获得的M3U8转存到缓存再读取缓存
			String id = StringUtil.getStringBetween(origurl, 0, KU6SWF_START,
					KU6SWF_END).result;
			Ku6VideoLoadTask loader = new Ku6VideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforku6(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(LETV_START) && showPlayMode() != 4) {// 无解，然后拿FLVCD的内容截取出来了
			String id = url;
			LetvVideoLoadTask loader = new LetvVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforletv(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.indexOf(LETVSWF_INCLUDE.toLowerCase(Locale.US)) > 0
				&& showPlayMode() != 4) {// 无解，然后拿FLVCD的内容截取出来了
			String id = url;
			LetvVideoLoadTask loader = new LetvVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforletv(loader, id);
			} else {
				loader.execute(id);
			}
		} else if ((url.startsWith(QQ_START) || url.indexOf(QQSWF_START
				.toLowerCase(Locale.US)) == 0) && showPlayMode() != 4) {// 也用FLVCD的
			String id = url;
			QQVideoLoadTask loader = new QQVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforqq(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(QQ2_START) && showPlayMode() != 4) {// 也用FLVCD的
			String id = url;
			QQVideoLoadTask loader = new QQVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforqq(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(WASU_START) && showPlayMode() != 4) {// 也FLVCD
			String id = url;
			WASUVideoLoadTask loader = new WASUVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforwasu(loader, id);
			} else {
				loader.execute(id);
			}
		}
		/* youtube太恶心了 */
		else if (url.startsWith(YOUTUBE_WITH) && showPlayMode() != 4) {// 先获取视频id,读取youtube的数据接口,将数据分割,转换,提取不同清晰度地址
			String id = StringUtil.getStringBetween(origurl, 0, YOUTUBE_WITH,
					YOUTUBE_END).result;
			YoutubeVideoLoadTask loader = new YoutubeVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforyoutube(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(YOUTUBENOWWW_WITH) && showPlayMode() != 4) {// 先获取视频id,读取youtube的数据接口,将数据分割,转换,提取不同清晰度地址
			String id = StringUtil.getStringBetween(origurl, 0,
					YOUTUBENOWWW_WITH, YOUTUBE_END).result;
			YoutubeVideoLoadTask loader = new YoutubeVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforyoutube(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(YOUTUBESHARE_START) && showPlayMode() != 4) {// 先获取视频id,读取youtube的数据接口,将数据分割,转换,提取不同清晰度地址
			String id = StringUtil.getStringBetween(origurl, 0,
					YOUTUBESHARE_START, YOUTUBE_END).result;
			YoutubeVideoLoadTask loader = new YoutubeVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforyoutube(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(YOUTUBESHAREEMBED_START)
				&& showPlayMode() != 4) {// 先获取视频id,读取youtube的数据接口,将数据分割,转换,提取不同清晰度地址
			String id = StringUtil.getStringBetween(origurl, 0,
					YOUTUBESHAREEMBED_START, YOUTUBE_END).result;
			YoutubeVideoLoadTask loader = new YoutubeVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforyoutube(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(YOUTUBESHAREEMBEDNOWWW_START)
				&& showPlayMode() != 4) {// 先获取视频id,读取youtube的数据接口,将数据分割,转换,提取不同清晰度地址
			String id = StringUtil.getStringBetween(origurl, 0,
					YOUTUBESHAREEMBEDNOWWW_START, YOUTUBE_END).result;
			YoutubeVideoLoadTask loader = new YoutubeVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforyoutube(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(YOUTUBESHAREEMBEDNOCOOKIE_START)
				&& showPlayMode() != 4) {// 先获取视频id,读取youtube的数据接口,将数据分割,转换,提取不同清晰度地址
			String id = StringUtil.getStringBetween(origurl, 0,
					YOUTUBESHAREEMBEDNOCOOKIE_START, YOUTUBE_END).result;
			YoutubeVideoLoadTask loader = new YoutubeVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforyoutube(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(YOUTUBESHAREEMBEDNOCOOKIENOWWW_START)
				&& showPlayMode() != 4) {// 先获取视频id,读取youtube的数据接口,将数据分割,转换,提取不同清晰度地址
			String id = StringUtil.getStringBetween(origurl, 0,
					YOUTUBESHAREEMBEDNOCOOKIENOWWW_START, YOUTUBE_END).result;
			YoutubeVideoLoadTask loader = new YoutubeVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforyoutube(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(YOUTUBESHAREEMBEDOLD_START)
				&& showPlayMode() != 4) {// 先获取视频id,读取youtube的数据接口,将数据分割,转换,提取不同清晰度地址
			String id = StringUtil.getStringBetween(origurl, 0,
					YOUTUBESHAREEMBEDOLD_START, YOUTUBE_END).result;
			YoutubeVideoLoadTask loader = new YoutubeVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforyoutube(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(YOUTUBESHAREEMBEDNOWWWOLD_START)
				&& showPlayMode() != 4) {// 先获取视频id,读取youtube的数据接口,将数据分割,转换,提取不同清晰度地址
			String id = StringUtil.getStringBetween(origurl, 0,
					YOUTUBESHAREEMBEDNOWWWOLD_START, YOUTUBE_END).result;
			YoutubeVideoLoadTask loader = new YoutubeVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforyoutube(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(YOUTUBESHAREEMBEDNOCOOKIEOLD_START)
				&& showPlayMode() != 4) {// 先获取视频id,读取youtube的数据接口,将数据分割,转换,提取不同清晰度地址
			String id = StringUtil.getStringBetween(origurl, 0,
					YOUTUBESHAREEMBEDNOCOOKIEOLD_START, YOUTUBE_END).result;
			YoutubeVideoLoadTask loader = new YoutubeVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforyoutube(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(YOUTUBESHAREEMBEDNOCOOKIENOWWWOLD_START)
				&& showPlayMode() != 4) {// 先获取视频id,读取youtube的数据接口,将数据分割,转换,提取不同清晰度地址
			String id = StringUtil.getStringBetween(origurl, 0,
					YOUTUBESHAREEMBEDNOCOOKIENOWWWOLD_START, YOUTUBE_END).result;
			YoutubeVideoLoadTask loader = new YoutubeVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforyoutube(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(YOUXIA_START) && showPlayMode() != 4) {// FLVCD
			String id = origurl;
			YouxiaVideoLoadTask loader = new YouxiaVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforyouxia(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(NETEASE_START) && StrTotalCount(url, "/") > 3
				&& showPlayMode() != 4) {// FLVCD
			String id = origurl;
			NeteaseVideoLoadTask loader = new NeteaseVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorfornetease(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(NETEASEGAME_START)
				&& StrTotalCount(url, "/") > 5 && showPlayMode() != 4) {// FLVCD
			String id = origurl;
			NeteaseVideoLoadTask loader = new NeteaseVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorfornetease(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(ACFUN_START)
				&& (showPlayMode() == 0 || showPlayMode() == 2)) {// 由设置,ACFUN用其接口换算出视频源地址
			String id = origurl;
			AcfunVideoLoadTask loader = new AcfunVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforacfun(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(ACFUNSWF_START.toLowerCase(Locale.US))
				&& (showPlayMode() == 0 || showPlayMode() == 2)) {// 由设置,ACFUN用其接口换算出视频源地址
			String id = StringUtil.getStringBetween(origurl, 0, "url=", "&").result;
			AcfunVideoLoadTask loader = new AcfunVideoLoadTask(fa, id);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforacfun(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(ACFUNTV_START)
				&& (showPlayMode() == 0 || showPlayMode() == 2)) {// 由设置,ACFUN用其接口换算出视频源地址
			String id = origurl;
			AcfunVideoLoadTask loader = new AcfunVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforacfun(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(ACFUNTVSWF_START.toLowerCase(Locale.US))
				&& (showPlayMode() == 0 || showPlayMode() == 2)) {// 由设置,ACFUN用其接口换算出视频源地址
			String id = StringUtil.getStringBetween(origurl, 0, "url=", "&").result;
			AcfunVideoLoadTask loader = new AcfunVideoLoadTask(fa, id);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforacfun(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(BILIBILI_START) && showPlayMode() < 2) {// 由设置,BILIBILI用其接口读取直接截取出视频地址
			String id = StringUtil.getStringBetween(origurl, 0, BILIBILI_START,
					BILIBILI_END).result;
			BilibiliVideoLoadTask loader = new BilibiliVideoLoadTask(fa,
					origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforbili(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(BILIBILINOWWW_START) && showPlayMode() < 2) {// 由设置,BILIBILI用其接口读取直接截取出视频地址
			String id = StringUtil.getStringBetween(origurl, 0,
					BILIBILINOWWW_START, BILIBILI_END).result;
			BilibiliVideoLoadTask loader = new BilibiliVideoLoadTask(fa,
					origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforbili(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(BILIBILI2_START) && showPlayMode() < 2) {// 由设置,BILIBILI用其接口读取直接截取出视频地址
			String id = StringUtil.getStringBetween(origurl, 0,
					BILIBILI2_START, BILIBILI_END).result;
			BilibiliVideoLoadTask loader = new BilibiliVideoLoadTask(fa,
					origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforbili(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(BILIBILICID_START) && showPlayMode() < 2) {// 由设置,BILIBILI的CID和VID的接口不一样
			String id = StringUtil.getStringBetween(origurl, 0, "cid=",
					BILIBILICID_END).result;
			BilibiliCidVideoLoadTask loader = new BilibiliCidVideoLoadTask(fa,
					origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforbilicid(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(PPS_START) && showPlayMode() != 4) {//
			String id = StringUtil.getStringBetween(origurl, 0, PPS_START,
					PPS_END).result;
			PPSVideoLoadTask loader = new PPSVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforpps(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(PPSSWF_START) && showPlayMode() != 4) {//
			String id = StringUtil.getStringBetween(origurl, 0, PPSSWF_START,
					PPSSWF_END).result;
			PPSVideoLoadTask loader = new PPSVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforpps(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (((url.startsWith(SINA_START) && StrTotalCount(url, "/") > 4) || (url
				.startsWith(SINAYOU_START) && StrTotalCount(url, "/") > 3))
				&& showPlayMode() != 4) {// SINA
			String id = "----";
			SinaVideoLoadTask loader = new SinaVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforsina(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(SINAENT_START) && StrTotalCount(url, "/") > 4
				&& showPlayMode() != 4) {// SINA
			String id = StringUtil.getStringBetween(origurl, 0, "#", "&").result;
			SinaVideoLoadTask loader = new SinaVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforsina(loader, id);
			} else {
				loader.execute(id);
			}
		} else if (url.startsWith(SINASWF_START.toLowerCase(Locale.US))
				&& showPlayMode() != 4) {// SINASWF
			String id = StringUtil.getStringBetween(origurl, 0, "vid=", "\\_").result;
			SinaVideoLoadTask loader = new SinaVideoLoadTask(fa, origurl);
			if (ActivityUtil.isGreaterThan_2_3_3()) {
				runOnExcutorforsina(loader, id);
			} else {
				loader.execute(id);
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

	@TargetApi(11)
	private void runOnExcutorforTudou(TudouVideoLoadTask loader, String id) {
		loader.executeOnExecutor(TudouVideoLoadTask.THREAD_POOL_EXECUTOR, id);

	}

	@TargetApi(11)
	private void runOnExcutorforSohu(SohuVideoLoadTask loader, String id) {
		loader.executeOnExecutor(SohuVideoLoadTask.THREAD_POOL_EXECUTOR, id);

	}

	@TargetApi(11)
	private void runOnExcutorforYouku(YoukuVideoLoadTask loader, String id) {
		loader.executeOnExecutor(YoukuVideoLoadTask.THREAD_POOL_EXECUTOR, id);

	}

	@TargetApi(11)
	private void runOnExcutorforqq(QQVideoLoadTask loader, String id) {
		loader.executeOnExecutor(QQVideoLoadTask.THREAD_POOL_EXECUTOR, id);

	}

	@TargetApi(11)
	private void runOnExcutorforwasu(WASUVideoLoadTask loader, String id) {
		loader.executeOnExecutor(WASUVideoLoadTask.THREAD_POOL_EXECUTOR, id);

	}

	@TargetApi(11)
	private void runOnExcutorforyoutube(YoutubeVideoLoadTask loader, String id) {
		loader.executeOnExecutor(YoutubeVideoLoadTask.THREAD_POOL_EXECUTOR, id);

	}

	@TargetApi(11)
	private void runOnExcutorfornetease(NeteaseVideoLoadTask loader, String id) {
		loader.executeOnExecutor(NeteaseVideoLoadTask.THREAD_POOL_EXECUTOR, id);
	}

	@TargetApi(11)
	private void runOnExcutorforyouxia(YouxiaVideoLoadTask loader, String id) {
		loader.executeOnExecutor(YouxiaVideoLoadTask.THREAD_POOL_EXECUTOR, id);
	}

	@TargetApi(11)
	private void runOnExcutorforbili(BilibiliVideoLoadTask loader, String id) {
		loader.executeOnExecutor(BilibiliVideoLoadTask.THREAD_POOL_EXECUTOR, id);
	}

	@TargetApi(11)
	private void runOnExcutorforacfun(AcfunVideoLoadTask loader, String id) {
		loader.executeOnExecutor(AcfunVideoLoadTask.THREAD_POOL_EXECUTOR, id);
	}

	@TargetApi(11)
	private void runOnExcutorforbilicid(BilibiliCidVideoLoadTask loader,
			String id) {
		loader.executeOnExecutor(BilibiliCidVideoLoadTask.THREAD_POOL_EXECUTOR,
				id);
	}

	@TargetApi(11)
	private void runOnExcutorforpps(PPSVideoLoadTask loader, String id) {
		loader.executeOnExecutor(PPSVideoLoadTask.THREAD_POOL_EXECUTOR, id);
	}

	@TargetApi(11)
	private void runOnExcutorforsina(SinaVideoLoadTask loader, String id) {
		loader.executeOnExecutor(SinaVideoLoadTask.THREAD_POOL_EXECUTOR, id);
	}

	@TargetApi(11)
	private void runOnExcutorfor56(FiveSixVideoLoadTask loader, String id) {
		loader.executeOnExecutor(FiveSixVideoLoadTask.THREAD_POOL_EXECUTOR, id);

	}

	@TargetApi(11)
	private void runOnExcutorforku6(Ku6VideoLoadTask loader, String id) {
		loader.executeOnExecutor(Ku6VideoLoadTask.THREAD_POOL_EXECUTOR, id);

	}

	@TargetApi(11)
	private void runOnExcutorforletv(LetvVideoLoadTask loader, String id) {
		loader.executeOnExecutor(LetvVideoLoadTask.THREAD_POOL_EXECUTOR, id);

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

	private int showPlayMode() {
		return PhoneConfiguration.getInstance().playMode;
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
