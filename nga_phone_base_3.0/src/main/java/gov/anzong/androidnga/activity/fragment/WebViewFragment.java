package gov.anzong.androidnga.activity.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.Strings;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.util.PreferenceUtils;
import gov.anzong.androidnga.common.PreferenceKey;
import gov.anzong.androidnga.ui.fragment.BaseFragment;

/**
 * @author yangyihang
 */
public class WebViewFragment extends BaseFragment {

    private WebView mWebView;

    private String mUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mUrl = bundle.getString("url", "");
        }
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mWebView = createWebView(inflater.getContext());
        return mWebView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_webview, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_open_by_browser) {
            startExternalBrowser(getContext(), mWebView.getUrl());
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean startExternalBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private WebView createWebView(Context context) {
        WebView webView = new WebView(context);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String host = Strings.nullToEmpty(request.getUrl().getHost());
                if(host.contains("nga") || host.contains("178")){
                    webView.loadUrl(request.getUrl().toString());
                } else {
                    startExternalBrowser(getContext(), request.getUrl().toString());
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                setTitle(view.getTitle());
                super.onPageFinished(view, url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setTextZoom(100);
        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(true);
        String ua = PreferenceUtils.getData(PreferenceKey.USER_AGENT, "");
        if (!TextUtils.isEmpty(ua)) {
            webSettings.setUserAgentString(ua);
        }
        return webView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (mUrl != null) {
            mWebView.loadUrl(mUrl);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }


    @Override
    public boolean onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mWebView.destroy();
        mWebView = null;
    }
}
