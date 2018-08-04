package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import gov.anzong.androidnga.BuildConfig;
import gov.anzong.androidnga.R;
import sp.phone.view.webview.WebViewClientEx;

public class WebViewerActivity extends BaseActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_layout);
        mWebView = (WebView) findViewById(R.id.webview);
        WebViewClient client = new WebViewClientEx(this);
        mWebView.setWebViewClient(client);
        mWebView.getSettings().setUserAgentString(getString(R.string.clientua) + BuildConfig.VERSION_CODE);
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {

                WebViewerActivity.this.setProgress(progress * 100);
            }
        });
        setTitle("查看内容");
    }

    @Override
    protected void onResume() {
        load();
        super.onResume();
    }

    private String getPath() {
        return getIntent().getStringExtra("path");
    }

    private void load() {
        final String uri = getPath();
        final WebSettings settings = mWebView.getSettings();

        if (uri.endsWith(".swf")) {
            mWebView.setWebChromeClient(new WebChromeClient());
            //settings.setPluginState(PluginState.ON);
            mWebView.loadUrl(uri);

        } else {//images
            settings.setSupportZoom(true);
            settings.setJavaScriptEnabled(true);
            settings.setBuiltInZoomControls(true);
//			settings.setUseWideViewPort(true); 
            settings.setLoadWithOverviewMode(true);
            //settings.setUserAgentString(IPHONE_UA);
            mWebView.loadUrl(uri);
        }

    }


    @Override
    protected void onPause() {
        mWebView.stopLoading();
        mWebView.loadUrl("about:blank");
        super.onPause();
    }


    @Override
    protected void onStop() {
        mWebView.stopLoading();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.webview_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_refresh:
                load();
                break;
            default:
                this.finish();

        }
        return super.onOptionsItemSelected(item);
    }

}
