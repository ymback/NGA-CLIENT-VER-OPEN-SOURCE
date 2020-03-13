package gov.anzong.androidnga.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import gov.anzong.androidnga.R;
import sp.phone.view.webview.WebViewClientEx;

public class WebViewerActivity extends BaseActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_layout);
        setupActionBar();
        mWebView = findViewById(R.id.webview);
        WebViewClient client = new WebViewClientEx(this);
        mWebView.setWebViewClient(client);
        setTitle("查看内容");
        load();
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
        if (item.getItemId() == R.id.item_refresh) {
            load();
        }
        return super.onOptionsItemSelected(item);
    }

}
