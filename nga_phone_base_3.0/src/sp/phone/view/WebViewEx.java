package sp.phone.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.DownloadListener;
import android.webkit.WebView;

/**
 * Created by Justwen on 2018/2/25.
 */

public class WebViewEx extends WebView implements DownloadListener {

    public WebViewEx(Context context) {
        this(context, null);
    }

    public WebViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        setDownloadListener(this);
    }

    private void downloadByBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        getContext().startActivity(intent);
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        downloadByBrowser(url);
    }
}
