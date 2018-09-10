package sp.phone.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import sp.phone.mvp.presenter.LoginPresenter;
import sp.phone.util.StringUtils;

/**
 * Created by Justwen on 2017/7/5.
 */

public class LoginWebFragment extends BaseFragment {

    private ProgressBar mProgressBar;

    private WebView mWebView;

    private static final int MAX_PROGRESS = 100;

    //"https://bbs.ngacn.cc/nuke.php?__lib=login&__act=account&login";
    private static final String LOGIN_URL = Utils.getNGAHost() + "nuke.php?__lib=login&__act=login_ui";


    private LoginPresenter mLoginPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mLoginPresenter = new LoginPresenter();
        super.onCreate(savedInstanceState);
    }

    private class LoginWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress > 0 && newProgress < MAX_PROGRESS) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else if (newProgress >= MAX_PROGRESS) {
                mProgressBar.setVisibility(View.GONE);
            }
            mProgressBar.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            if (message.contains("成功")) {
                String cookieStr = CookieManager.getInstance().getCookie(view.getUrl());
                if (!StringUtils.isEmpty(cookieStr)) {
                    mLoginPresenter.parseCookie(cookieStr);
                    if (mActivity != null) {
                        mActivity.setResult(Activity.RESULT_OK);
                    }
                }
            }
            return super.onJsAlert(view, url, message, result);
        }
    }

    private class LoginWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return shouldOverrideUrlLoading(view, request.getUrl().toString());
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mWebView != null) {
            mWebView.destroy();
        }
        return inflater.inflate(R.layout.fragment_login_web, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mWebView = view.findViewById(R.id.webview);
        mWebView.setWebChromeClient(new LoginWebChromeClient());
        mWebView.setWebViewClient(new LoginWebViewClient());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mProgressBar = view.findViewById(R.id.progressBar);
        mProgressBar.setMax(MAX_PROGRESS);
        mWebView.loadUrl(LOGIN_URL);
        super.onViewCreated(view, savedInstanceState);
    }


    /**
     * Called when the fragment is visible to the user and actively running. Resumes the WebView.
     */
    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    /**
     * Called when the fragment is no longer resumed. Pauses the WebView.
     */
    @Override
    public void onResume() {
        mWebView.onResume();
        super.onResume();
    }

    /**
     * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
     */
    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    /**
     * Gets the WebView.
     */
    public WebView getWebView() {
        return mWebView;
    }

    @Override
    public boolean onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else {
            return super.onBackPressed();
        }
    }
}
