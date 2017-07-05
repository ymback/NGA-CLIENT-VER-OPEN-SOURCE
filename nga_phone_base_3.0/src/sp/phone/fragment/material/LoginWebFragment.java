package sp.phone.fragment.material;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import gov.anzong.androidnga.R;
import sp.phone.fragment.BaseFragment;
import sp.phone.presenter.contract.LoginContract;
import sp.phone.utils.StringUtil;

/**
 * Created by Yang Yihang on 2017/7/5.
 */

public class LoginWebFragment extends BaseFragment implements LoginContract.View{

    private ProgressBar mProgressBar;

    private static final int MAX_PROGRESS = 100;

    private static final String LOGIN_URL = "https://bbs.ngacn.cc/nuke.php?__lib=login&__act=login_ui";

    private LoginContract.Presenter mPresenter;

    private class LoginWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress > 0 && newProgress < MAX_PROGRESS) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else if (newProgress >= MAX_PROGRESS) {
                mProgressBar.setVisibility(View.GONE);
            }
            mProgressBar.setProgress(newProgress);
            String cookieStr = CookieManager.getInstance().getCookie(view.getUrl());
            if (!StringUtil.isEmpty(cookieStr)) {
                mPresenter.parseCookie(cookieStr);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    private class LoginWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return shouldOverrideUrlLoading(view,request.getUrl().toString());
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
        return inflater.inflate(R.layout.fragment_login_web,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        WebView webView = (WebView) view.findViewById(R.id.webview);
        webView.setWebChromeClient(new LoginWebChromeClient());
        webView.setWebViewClient(new LoginWebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        CookieManager.getInstance().removeAllCookie();
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setMax(MAX_PROGRESS);
        webView.loadUrl(LOGIN_URL);
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void setPresenter(LoginContract.Presenter presenter) {

        mPresenter = presenter;
    }

    @Override
    public void setAuthCodeImg(Bitmap bitmap) {

    }

    @Override
    public void setAuthCodeImg(int resId) {

    }

    @Override
    public void setAuthCode(String text) {

    }

    @Override
    public void setResult(boolean isChanged) {
        if (isChanged) {
            getActivity().setResult(Activity.RESULT_OK);
        }
    }



}
