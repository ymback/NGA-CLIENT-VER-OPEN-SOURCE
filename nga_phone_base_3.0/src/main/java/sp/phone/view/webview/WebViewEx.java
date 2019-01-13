package sp.phone.view.webview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewEx extends WebView implements DownloadListener {

    private WebViewClientEx mWebViewClientEx;

    public WebViewEx(Context context) {
        this(context, null);
    }

    public WebViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        setDownloadListener(this);
        try {
            setFocusable(false);
            setVerticalScrollBarEnabled(false);
        } catch (Exception e) {
            // 某些机型的WebView不支持以上方法的调用
        }
    }

    private void downloadByBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        getContext().startActivity(intent);
    }

    public void setLocalMode() {
        mWebViewClientEx = new WebViewClientEx();
        setWebViewClient(mWebViewClientEx);
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);

        setFocusableInTouchMode(false);
        setFocusable(false);
        setLongClickable(false);
        setBackgroundColor(Color.TRANSPARENT);
    }

    public WebViewClientEx getWebViewClientEx() {
        return mWebViewClientEx;
    }

    public void setTextSize(int size) {
        getSettings().setDefaultFontSize(size);
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        downloadByBrowser(url);
    }
}
