package gov.anzong.androidnga.activity;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import gov.anzong.androidnga.NgaClientApp;
import gov.anzong.androidnga.R;
import sp.phone.theme.ThemeManager;
import sp.phone.task.DownloadImageTask;
import sp.phone.utils.ArticleListWebClient;
import sp.phone.utils.ReflectionUtil;

public class WebViewerActivity extends SwipeBackAppCompatActivity {
    private WebView wv;

    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle arg0) {
        requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(arg0);
        View view = LayoutInflater.from(this).inflate(R.layout.webview_layout, null, false);
        this.setContentView(view);
        wv = (WebView) findViewById(R.id.webview);
        WebViewClient client = new ArticleListWebClient(this);
        wv.setWebViewClient(client);
        wv.getSettings().setUserAgentString(getString(R.string.clientua) + ((NgaClientApp) getApplication()).version);
        wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {

                WebViewerActivity.this.setProgress(progress * 100);
            }
        });


    }

    @Override
    protected void onResume() {
        load();
        super.onResume();
    }

    private String getPath() {
        return getIntent().getStringExtra("path");
    }

    @TargetApi(8)
    private void load() {
        final String uri = getPath();
        final WebSettings settings = wv.getSettings();
        getSupportActionBar().setTitle("查看内容");

        if (uri.endsWith(".swf"))//android 2.2
        {
            wv.setWebChromeClient(new WebChromeClient());
            //settings.setPluginState(PluginState.ON);
            wv.loadUrl(uri);

        } else {//images
            settings.setSupportZoom(true);
            settings.setJavaScriptEnabled(true);
            settings.setBuiltInZoomControls(true);
//			settings.setUseWideViewPort(true); 
            settings.setLoadWithOverviewMode(true);
            //settings.setUserAgentString(IPHONE_UA);
            wv.loadUrl(uri);
        }

    }


    @Override
    protected void onPause() {
        wv.stopLoading();
        wv.loadUrl("about:blank");
        super.onPause();
    }


    @Override
    protected void onStop() {
        wv.stopLoading();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.webview_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(11)
    private void runOnExecutor(DownloadImageTask task, String path) {
        task.executeOnExecutor(DownloadImageTask.THREAD_POOL_EXECUTOR, path);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        //super.onSaveInstanceState(outState);
    }


}
