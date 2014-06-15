package sp.phone.task;

import gov.anzong.androidnga.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sp.phone.bean.MeiziUrlData;
import sp.phone.interfaces.OnMeiziCategoryLoadFinishedListener;
import sp.phone.proxy.MeiziHtmlDecoderBase;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.MeiziStringUtils;
import sp.phone.utils.PhoneConfiguration;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import sp.phone.utils.StringUtil;

public class HTMLMeiziCategoryLoadTask extends
		AsyncTask<String, Integer, List<MeiziUrlData>> {
	private final static String TAG = HTMLMeiziCategoryLoadTask.class
			.getSimpleName();
	private final Context context;
	final private OnMeiziCategoryLoadFinishedListener notifier;
	private static String sHost = "http://www.dbmeizi.com";

	public HTMLMeiziCategoryLoadTask(Context context,
			OnMeiziCategoryLoadFinishedListener notifier) {
		super();
		this.context = context;
		this.notifier = notifier;
	}

	@Override
	protected List<MeiziUrlData> doInBackground(String... params) {
		String url = params[0];
		String htmlString;
		Log.i("TAG",url);
		htmlString = HttpUtil.getHtmlForDbmeizi(url, PhoneConfiguration
					.getInstance().getDb_Cookie());

		if (!StringUtil.isEmpty(htmlString)) {
			List<MeiziUrlData> result;
			if (url.toLowerCase(Locale.US).indexOf("baozhao") > 0) {
				BaozhaoCategoryDecoder mDecoder = new BaozhaoCategoryDecoder();
				result = mDecoder.decode(htmlString);
			}if (url.toLowerCase(Locale.US).indexOf("dadanshai") > 0) {
				ddShaiCategoryDecoder mDecoder = new ddShaiCategoryDecoder();
				result = mDecoder.decode(htmlString);
			} else {
				CategoryDecoder mDecoder = new CategoryDecoder();
				result = mDecoder.decode(htmlString);
			}
			return result;
		} else {
			return null;
		}
	}

	@Override
	protected void onPostExecute(List<MeiziUrlData> result) {
		ActivityUtil.getInstance().dismiss();
		if (null != notifier)
			notifier.datafinishLoad(result);
		super.onPostExecute(result);
	}

	@Override
	protected void onCancelled() {
		ActivityUtil.getInstance().dismiss();
		super.onCancelled();
	}

	public static class CategoryDecoder extends
			MeiziHtmlDecoderBase<List<MeiziUrlData>> {

		@Override
		public List<MeiziUrlData> decode(String html) {
			List<MeiziUrlData> result = new ArrayList<MeiziUrlData>();

			if (!TextUtils.isEmpty(html)) {
				Document document = Jsoup.parse(html);

				Elements meiziElements = document.select("div.pic");
				for (int i = 0; i < meiziElements.size(); i++) {
					Element meiziE = meiziElements.get(i);

					MeiziUrlData meiziM = new MeiziUrlData();
					meiziM.dataId = meiziE.attr("data-id");
					meiziM.smallPicUrl = meiziE.select("img").attr("src");
					meiziM.largePicUrl = meiziE.select("img").attr(
							"data-bigimg");
					// TODO [Ou Runqiang] starCount might be loaded from a js
					// script
					// meiziM.starCount =
					Elements urlHolder = meiziE
							.select("div.bottombar span.fr.p5");
					if (urlHolder.size() != 0) {
						meiziM.doubanPosterUrl = urlHolder.get(0).child(0)
								.attr("href");
						String topicUrl = urlHolder.get(0).child(1)
								.attr("href");
						if (!TextUtils.isEmpty(topicUrl)) {
							meiziM.topicUrl = sHost
									+ MeiziStringUtils.wrap(topicUrl);
						}
					}

					result.add(meiziM);
				}
			}

			return result;
		}
	}

	public static class BaozhaoCategoryDecoder extends
			MeiziHtmlDecoderBase<List<MeiziUrlData>> {

		@Override
		public List<MeiziUrlData> decode(String html) {
			List<MeiziUrlData> result = new ArrayList<MeiziUrlData>();

			if (!TextUtils.isEmpty(html)) {
				Document document = Jsoup.parse(html);

				Elements meiziElements = document.select("div.pin-coat");
				for (int i = 0; i < meiziElements.size(); i++) {
					Element meiziE = meiziElements.get(i);
					MeiziUrlData meiziM = new MeiziUrlData();
					meiziM.dataId = "";
					meiziM.smallPicUrl = meiziE.select("img").attr("original");
					meiziM.largePicUrl = meiziE.select("img").attr("original");
					// TODO [Ou Runqiang] starCount might be loaded from a js
					// script
					// meiziM.starCount =
					Elements urlHolder = meiziE
							.select("a.imageLink.image.loading");
					String topicUrl = urlHolder.attr("href");
					if (!TextUtils.isEmpty(topicUrl)) {
						meiziM.topicUrl = topicUrl;
					}
					urlHolder = meiziE.select("a.viewsButton");
					String doubanTopicUrl = urlHolder.attr("href");
					if (!TextUtils.isEmpty(doubanTopicUrl)) {
						meiziM.doubanTopicUrl = doubanTopicUrl;
					}

					result.add(meiziM);
				}
			}

			return result;
		}
	}

	


	public static class ddShaiCategoryDecoder extends
			MeiziHtmlDecoderBase<List<MeiziUrlData>> {

		@Override
		public List<MeiziUrlData> decode(String html) {
			List<MeiziUrlData> result = new ArrayList<MeiziUrlData>();

			if (!TextUtils.isEmpty(html)) {
				Document document = Jsoup.parse(html);

				Elements meiziElements = document.select("div.pic");
				for (int i = 0; i < meiziElements.size(); i++) {
					Element meiziE = meiziElements.get(i);
					MeiziUrlData meiziM = new MeiziUrlData();
					meiziM.dataId = "";
					meiziM.topicUrl = "";
					if(!StringUtil.isEmpty(meiziE.select("img.post-image").attr("src"))){
						if(meiziE.select("img.post-image").attr("src").indexOf("dadanshai.com")>0){
							meiziM.smallPicUrl = meiziE.select("img.post-image").attr("src");
							meiziM.largePicUrl = meiziE.select("img.post-image").attr("src");
						}else{
							meiziM.smallPicUrl = "http://www.dadanshai.com"+meiziE.select("img.post-image").attr("src");
							meiziM.largePicUrl = "http://www.dadanshai.com"+meiziE.select("img.post-image").attr("src");
						}
						Log.i("TAG",meiziM.largePicUrl);
						result.add(meiziM);
					}
				}
			}

			return result;
		}
	}

}
