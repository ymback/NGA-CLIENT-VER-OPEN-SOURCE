package sp.phone.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

    private View.OnClickListener mPositiveClickListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.useurlto_dialog, null);
        final EditText urlAdd = view.findViewById(R.id.urladd);
        urlAdd.requestFocus();
        String clipData = null;
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboardManager.hasPrimaryClip()) {
            try {
                clipData = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
            } catch (Exception e) {
                clipData = "";
            }

        }
        if (!TextUtils.isEmpty(clipData)) {
            urlAdd.setText(clipData);
            urlAdd.selectAll();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .setView(view)
                .setTitle(R.string.urlto_title_hint);

        AlertDialog dialog = builder.create();
        mPositiveClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = urlAdd.getText().toString().trim();
                if (TextUtils.isEmpty(url)) {// 空
                    showToast("请输入URL地址");
                    urlAdd.setFocusable(true);

                } else {
                    url = url.toLowerCase(Locale.US).trim();
                    if (url.indexOf("thread.php") > 0) {
                        url = url.replaceAll("(?i)[^\\[|\\]]+fid=(-{0,1}\\d+)[^\\[|\\]]{0,}", Utils.getNGAHost() + "thread.php?fid=$1");
                        Intent intent = new Intent();
                        intent.setData(Uri.parse(url));
                        intent.setClass(getContext(), TopicListActivity.class);
                        startActivity(intent);
                        dismiss();
                    } else if (url.indexOf("read.php") > 0) {
                        if (url.indexOf("tid") > 0 && url.indexOf("pid") > 0) {
                            if (url.indexOf("tid") < url.indexOf("pid")) {
                                url = url.replaceAll("(?i)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]{0,}", Utils.getNGAHost() + "read.php?pid=$2&tid=$1");
                            } else {
                                url = url.replaceAll("(?i)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]{0,}", Utils.getNGAHost() + "read.php?pid=$1&tid=$2");
                            }
                        } else if (url.indexOf("tid") > 0 && url.indexOf("pid") <= 0) {
                            url = url.replaceAll("(?i)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]{0,}", Utils.getNGAHost() + "read.php?tid=$1");
                        } else if (url.indexOf("pid") > 0 && url.indexOf("tid") <= 0) {
                            url = url.replaceAll("(?i)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]{0,}", Utils.getNGAHost() + "read.php?pid=$1");
                        }
                        Intent intent = new Intent();
                        intent.setData(Uri.parse(url));
                        intent.setClass(view.getContext(), ArticleListActivity.class);
                        startActivity(intent);
                        dismiss();
                    } else {
                        showToast("输入的地址并非NGA的板块地址或帖子地址,或缺少fid/pid/tid信息,请检查后再试");
                        urlAdd.setFocusable(true);

                    }
                }
            }
        };
        return dialog;
    }

    @Override
    public void onResume() {
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(mPositiveClickListener);
        super.onResume();
    }
}
