package sp.phone.task;

import gov.anzong.androidnga2.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sp.phone.bean.MeiziTopicMData;
import sp.phone.bean.MeiziUrlData;
import sp.phone.bean.MeiziTopicMData.ContentItemType;
import sp.phone.bean.MeiziTopicMData.TopicContentItem;
import sp.phone.interfaces.OnMeiziTopicLoadFinishedListener;
import sp.phone.proxy.MeiziHtmlDecoderBase;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.MeiziDateUtil;
import sp.phone.utils.MeiziStringUtils;
import sp.phone.utils.PhoneConfiguration;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.text.TextUtils;
import sp.phone.utils.StringUtil;

public class HTMLMeiziTopicLoadTask extends AsyncTask<String, Integer, MeiziTopicMData> {
	private final static String TAG = HTMLMeiziTopicLoadTask.class.getSimpleName();
	private final Context context;
	final private OnMeiziTopicLoadFinishedListener notifier;
    private static String sHost = "http://www.dbmeizi.com";

	
	public HTMLMeiziTopicLoadTask(Context context,
			OnMeiziTopicLoadFinishedListener notifier) {
		super();
		this.context = context;
		this.notifier = notifier;
	}
	
	@Override
	protected MeiziTopicMData doInBackground(String... params) {
		String url = params[0];
    	String htmlString;
    	if(!StringUtil.isEmpty(PhoneConfiguration.getInstance().getDb_Cookie())){
    		htmlString = HttpUtil.getHtmlForDbmeizi(url,PhoneConfiguration.getInstance().getDb_Cookie());
    	}else{
    		htmlString = HttpUtil.getHtmlForDbmeizi(url,PhoneConfiguration.getInstance().getDb_Cookie());
    	}
    	if(!StringUtil.isEmpty(htmlString)){
        	MeiziTopicMData resulTopicM = null;
    		if(url.toLowerCase(Locale.US).indexOf("baozhao")>0){
    			BaozhaoTopicDecoder mDecoder = new BaozhaoTopicDecoder();
    			resulTopicM = mDecoder.decode(htmlString);
    		}else{
            	TopicDecoder mDecoder = new TopicDecoder();
        		resulTopicM = mDecoder.decode(htmlString);
    		}
        	return resulTopicM;
    	}else{
    		return null;
    	}
	}
	
	@Override
	protected void onPostExecute(MeiziTopicMData result) {
		ActivityUtil.getInstance().dismiss();
		if(null != notifier)
			notifier.datafinishLoad(result);
		super.onPostExecute(result);
	}
	@Override
	protected void onCancelled() {
		ActivityUtil.getInstance().dismiss();
		super.onCancelled();
	}
	

    public static class TopicDecoder extends MeiziHtmlDecoderBase<MeiziTopicMData> {

        @Override
        public MeiziTopicMData decode(String html) {
            MeiziTopicMData resulTopicM = new MeiziTopicMData();

            Document document = Jsoup.parse(html);

            // get title string
            resulTopicM.title = document.select("div.row div.span9 h4").html().trim();
            // get doubanPosterUrl and doubanTopicurl
            Elements urlElements = document.select("div.row div.span6 div.content-meta a");
            if (urlElements.size() >= 1) {
                resulTopicM.doubanPosterUrl = urlElements.get(0).attr("href");
            }
            if (urlElements.size() >= 2) {
                resulTopicM.doubanTopicUrl = urlElements.get(1).attr("href");
            }
            // get post time
            String dateString = document.select("div.row div.span6").html();
            String startString = "title=\"最后更新时间\"></span> ";
            int startPos = dateString.indexOf(startString) + startString.length();
            String endString = "<span class=\"icon-arrow-right";
            int endPos = dateString.indexOf(endString);
            dateString = dateString.substring(startPos, endPos).trim();
            try {
                resulTopicM.date = MeiziDateUtil.getDate(dateString);
            } catch (ParseException e) {
            }

            // get the main content, pictures and messages
            Elements contentElements = document.select("div.row div.span6 div.content").first()
                    .children();

            for (int i = 0; i < contentElements.size(); i++) {
                Element element = contentElements.get(i);
                String tagName = element.tagName();
                if (tagName.equals("p")) {
                    // in case incorrect syntax
                    Elements pImgElements = element.select("img");
                    if (pImgElements.size() != 0) {
                        for (int j = 0; j < pImgElements.size(); j++) {
                            TopicContentItem item = new TopicContentItem();
                            item.type = ContentItemType.IMAGE;
                            item.imgUrl = pImgElements.get(j).attr("src");

                            if (!TextUtils.isEmpty(item.imgUrl)) {
                                resulTopicM.content.add(item);
                            }
                        }
                    } else {
                        TopicContentItem item = new TopicContentItem();
                        item.type = ContentItemType.MSG;
                        String pContent = contentElements.get(i).html();
                        item.msg = Html.fromHtml(pContent).toString().trim();

                        if (!TextUtils.isEmpty(item.msg)) {
                            resulTopicM.content.add(item);
                        }
                    }
                } else if (tagName.equals("div")) {
                    TopicContentItem item = new TopicContentItem();
                    item.type = ContentItemType.IMAGE;
                    Elements divElements = element.select("img");
                    Element imgElement = divElements.first();
                    if (imgElement != null) {
                        item.imgUrl = imgElement.attr("src");
                    }

                    if (!TextUtils.isEmpty(item.imgUrl)) {
                        resulTopicM.content.add(item);
                    }
                } else if (tagName.equals("img")) {
                    TopicContentItem item = new TopicContentItem();
                    item.type = ContentItemType.IMAGE;
                    item.imgUrl = element.attr("src");

                    if (!TextUtils.isEmpty(item.imgUrl)) {
                        resulTopicM.content.add(item);
                    }
                }
            }

            return resulTopicM;
        }
    }
    

    public static class BaozhaoTopicDecoder extends MeiziHtmlDecoderBase<MeiziTopicMData> {

        @Override
        public MeiziTopicMData decode(String html) {
            MeiziTopicMData resulTopicM = new MeiziTopicMData();

            Document document = Jsoup.parse(html);

            // get title string
            resulTopicM.title = document.select("div.main-header h2").html().trim();
            // get doubanPosterUrl and doubanTopicurl
            Elements urlElements = document.select("ul.clx li.widgets-views a");
            if (urlElements.size() >= 1) {
                resulTopicM.doubanTopicUrl = urlElements.get(0).attr("href");
            }
            String dateString = document.select("div.main-meta.clx span.post-span").html().trim();
            try {
            	if(!StringUtil.isEmpty(dateString))
                resulTopicM.date = MeiziDateUtil.getDate(dateString);
            } catch (ParseException e) {
            }

            // get the main content, pictures and messages
            Elements contentElements = document.select("div.main-body").first()
                    .children();

            for (int i = 0; i < contentElements.size(); i++) {
                Element element = contentElements.get(i);
                String tagName = element.tagName();
                if (tagName.equals("p")) {
                    // in case incorrect syntax
                    Elements pImgElements = element.select("img");
                    if (pImgElements.size() != 0) {
                        for (int j = 0; j < pImgElements.size(); j++) {
                            TopicContentItem item = new TopicContentItem();
                            item.type = ContentItemType.IMAGE;
                            item.imgUrl = pImgElements.get(j).attr("src");

                            if (!TextUtils.isEmpty(item.imgUrl)) {
                                resulTopicM.content.add(item);
                            }
                        }
                    } else {
                        TopicContentItem item = new TopicContentItem();
                        item.type = ContentItemType.MSG;
                        String pContent = contentElements.get(i).html();
                        item.msg = Html.fromHtml(pContent).toString().trim();

                        if (!TextUtils.isEmpty(item.msg)) {
                            resulTopicM.content.add(item);
                        }
                    }
                } else if (tagName.equals("div")) {
                    TopicContentItem item = new TopicContentItem();
                    item.type = ContentItemType.IMAGE;
                    Elements divElements = element.select("img");
                    Element imgElement = divElements.first();
                    if (imgElement != null) {
                        item.imgUrl = imgElement.attr("src");
                    }

                    if (!TextUtils.isEmpty(item.imgUrl)) {
                        resulTopicM.content.add(item);
                    }
                } else if (tagName.equals("img")) {
                    TopicContentItem item = new TopicContentItem();
                    item.type = ContentItemType.IMAGE;
                    item.imgUrl = element.attr("src");

                    if (!TextUtils.isEmpty(item.imgUrl)) {
                        resulTopicM.content.add(item);
                    }
                }
            }

            return resulTopicM;
        }
    }
}
