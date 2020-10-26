package sp.phone.view.webview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;

import androidx.annotation.Nullable;

import gov.anzong.androidnga.common.util.LogUtils;
import gov.anzong.androidnga.common.view.WebViewEx;
import sp.phone.common.PhoneConfiguration;

/**
 * @author Justwen
 */
public class LocalWebView extends WebViewEx implements DownloadListener {

    private WebViewClientEx mWebViewClientEx;

    private String mEmotionSize;

    private String mContentData;

    public LocalWebView(Context context) {
        this(context, null);
    }

    public LocalWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        setDownloadListener(this);
        try {
            setLocalMode();
        } catch (Exception e) {
            // 某些机型的WebView不支持以上方法的调用
        }
        mEmotionSize = PhoneConfiguration.getInstance().getEmoticonSize() + "px";
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
        addJavascriptInterface(this, "action");
        settings.setTextZoom(PhoneConfiguration.getInstance().getWebViewTextZoom());
        settings.setBlockNetworkImage(true);

        setFocusableInTouchMode(false);
        setFocusable(false);
        setLongClickable(false);
        setBackgroundColor(Color.TRANSPARENT);
    }

    public WebViewClientEx getWebViewClientEx() {
        return mWebViewClientEx;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        downloadByBrowser(url);
    }

    @Override
    public void loadDataWithBaseURL(@Nullable String baseUrl, String data, @Nullable String mimeType, @Nullable String encoding, @Nullable String historyUrl) {
        if (data.equals(mContentData)) {
            LogUtils.d("Data is not changed, ignore this update");
            return;
        }
        mContentData = data;
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    @JavascriptInterface
    public String getEmotionSize() {
        return mEmotionSize;
    }
}
