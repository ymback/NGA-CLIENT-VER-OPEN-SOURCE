package sp.phone.ui.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import gov.anzong.androidnga.BuildConfig;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.WebViewerActivity;


public class AboutClientDialogFragment extends BaseDialogFragment {

    private  class UrlSpan extends ClickableSpan {
        private String mUrl;

        UrlSpan(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            Intent intent = new Intent();
            intent.putExtra("path", mUrl);
            intent.setClass(getContext(), WebViewerActivity.class);
            startActivity(intent);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_client, null);
        TextView contentView = view.findViewById(R.id.client_device_dialog);
        String content = String.format(getString(R.string.about_client), BuildConfig.VERSION_NAME);
        contentView.setText(Html.fromHtml(content));
        contentView.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = contentView.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) contentView.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();// should clear old spans
            for (URLSpan url : urls) {
                style.setSpan(new UrlSpan(url.getURL()), sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            contentView.setText(style);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view)
                .setTitle(R.string.about)
                .setPositiveButton(android.R.string.ok, null);
        return builder.create();
    }
}
