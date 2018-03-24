package sp.phone.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.Locale;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.activity.ArticleListActivity;
import gov.anzong.androidnga.activity.TopicListActivity;

public class UrlInputDialogFragment extends BaseDialogFragment {

    private EditText mUrlAddEditText;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mUrlAddEditText.requestFocus();
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
            String clipData = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
            if (!TextUtils.isEmpty(clipData)) {
                mUrlAddEditText.setText(clipData);
                mUrlAddEditText.selectAll();
            }
        }
        setPositiveClickListener((View v) -> {
            String url = mUrlAddEditText.getText().toString().trim();
            if (TextUtils.isEmpty(url)) {// 空
                showToast("请输入URL地址");
                mUrlAddEditText.setFocusable(true);
            } else {
                url = url.toLowerCase(Locale.US).trim();
                if (url.contains("thread.php")) {
                    url = url.replaceAll("(?i)[^\\[|\\]]+fid=(-{0,1}\\d+)[^\\[|\\]]{0,}", Utils.getNGAHost() + "thread.php?fid=$1");
                    Intent intent = new Intent(getContext(), TopicListActivity.class);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    dismiss();
                } else if (url.contains("read.php")) {
                    if (url.contains("tid") && url.contains("pid")) {
                        if (url.indexOf("tid") < url.indexOf("pid")) {
                            url = url.replaceAll("(?i)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]{0,}", Utils.getNGAHost() + "read.php?pid=$2&tid=$1");
                        } else {
                            url = url.replaceAll("(?i)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]{0,}", Utils.getNGAHost() + "read.php?pid=$1&tid=$2");
                        }
                    } else if (url.contains("tid") && !url.contains("pid")) {
                        url = url.replaceAll("(?i)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]{0,}", Utils.getNGAHost() + "read.php?tid=$1");
                    } else if (url.contains("pid") && !url.contains("tid")) {
                        url = url.replaceAll("(?i)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]{0,}", Utils.getNGAHost() + "read.php?pid=$1");
                    }
                    Intent intent = new Intent(getContext(),ArticleListActivity.class);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    dismiss();
                } else {
                    showToast("输入的地址并非NGA的板块地址或帖子地址,或缺少fid/pid/tid信息,请检查后再试");
                    mUrlAddEditText.setFocusable(true);
                }
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_url_to, null);
        mUrlAddEditText = view.findViewById(R.id.urladd);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .setView(view)
                .setTitle(R.string.urlto_title_hint);

        return builder.create();
    }

}
