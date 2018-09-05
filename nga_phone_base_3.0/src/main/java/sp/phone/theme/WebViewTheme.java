package sp.phone.theme;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import gov.anzong.androidnga.R;

/**
 * Created by Justwen on 2018/9/5.
 */
public class WebViewTheme {

    private int mWebTextColor;

    private int mQuoteBackgroundColor;

    public WebViewTheme(Context context) {
        mWebTextColor = ContextCompat.getColor(context, R.color.web_text_color);
        mQuoteBackgroundColor = ContextCompat.getColor(context, R.color.web_quote_background_color);
    }

    public int getWebTextColor() {
        return mWebTextColor;
    }

    public int getQuoteBackgroundColor() {
        return mQuoteBackgroundColor;
    }
}
