package sp.phone.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Locale;

import gov.anzong.androidnga.gallery.ImageZoomActivity;
import sp.phone.common.PhoneConfiguration;

public class ArticleListWebClient extends WebViewClient {
    static private final String NGACN_BOARD_PREFIX = "http://bbs.ngacn.cc/thread.php?";
    static private final String NGA178_BOARD_PREFIX = "http://nga.178.com/thread.php?";
    static private final String NGACN_THREAD_PREFIX = "http://bbs.ngacn.cc/read.php?";
    static private final String NGA178_THREAD_PREFIX = "http://nga.178.com/read.php?";
    static private final String NGACN_BOARD_PREFIX_NOHTTP = "bbs.ngacn.cc/thread.php?";
    static private final String NGA178_BOARD_PREFIX_NOHTTP = "nga.178.com/thread.php?";
    static private final String NGACN_THREAD_PREFIX_NOHTTP = "bbs.ngacn.cc/read.php?";
    static private final String NGA178_THREAD_PREFIX_NOHTTP = "nga.178.com/read.php?";
    static private final String ANDROIDNGAUSERNAME_START = "http://bbs.ngacn.cc/nuke.php?func=ucp&username=";
    static private final String ANDROIDNGAUSERNAME_END = "&";


    private final Context fa;

    public ArticleListWebClient(Context fa) {
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
                intent.setData(Uri.parse("http://" + origurl));
                intent.setClass(view.getContext(), conf.topicActivityClass);
                view.getContext().startActivity(intent);
                return true;
            } else if (url.startsWith(NGACN_THREAD_PREFIX_NOHTTP)
                    || url.startsWith(NGA178_THREAD_PREFIX_NOHTTP)) {
                intent.setData(Uri.parse("http://" + origurl));
                intent.putExtra("fromreplyactivity", 1);
                intent.setClass(view.getContext(), conf.articleActivityClass);
                view.getContext().startActivity(intent);
                return true;
            } else if (url.endsWith(".gif") || url.endsWith(".jpg")
                    || url.endsWith(".png") || url.endsWith(".jpeg")
                    || url.endsWith(".bmp")) {
                String imgUrl = "http://" + origurl;
                intent.putExtra(ImageZoomActivity.KEY_GALLERY_URLS, new String[] {imgUrl});
                intent.putExtra(ImageZoomActivity.KEY_GALLERY_CUR_URL, imgUrl);
                intent.setClass(view.getContext(), ImageZoomActivity.class);
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
            intent.putExtra(ImageZoomActivity.KEY_GALLERY_URLS, new String[] {origurl});
            intent.putExtra(ImageZoomActivity.KEY_GALLERY_CUR_URL, origurl);
            intent.setClass(view.getContext(), ImageZoomActivity.class);
            view.getContext().startActivity(intent);
        } else if (url.startsWith(ANDROIDNGAUSERNAME_START)) {
            String data = StringUtils.getStringBetween(origurl, 0,
                    ANDROIDNGAUSERNAME_START, ANDROIDNGAUSERNAME_END).result;
            try {
                data = URLDecoder.decode(data, "utf-8");
            } catch (UnsupportedEncodingException e) {
            }
            if (!StringUtils.isEmpty(data)) {
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
