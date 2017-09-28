package gov.anzong.androidnga.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Locale;

import gov.anzong.androidnga.BuildConfig;
import gov.anzong.androidnga.NgaClientApp;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.ThemeManager;
import sp.phone.fragment.BoardFragment;
import sp.phone.fragment.ProfileSearchDialogFragment;
import sp.phone.presenter.BoardPresenter;
import sp.phone.presenter.contract.BoardContract;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.NLog;
import sp.phone.utils.PermissionUtils;
import sp.phone.utils.StringUtils;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private BoardContract.Presenter mPresenter;

    private boolean mIsNightMode;

    private PhoneConfiguration mConfig = PhoneConfiguration.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDate();
        initView();
        checkNewVersion();
        mIsNightMode = ThemeManager.getInstance().isNightMode();
    }

    @Override
    protected void onResume() {
        if (mIsNightMode != ThemeManager.getInstance().isNightMode()) {
            finish();
            startActivity(getIntent());
        }
        super.onResume();
    }

    //OK
    private void checkNewVersion() {
        NgaClientApp app = (NgaClientApp) getApplication();
        if (app.isNewVersion()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.prompt).setMessage(StringUtils.getTips())
                    .setPositiveButton(R.string.i_know, null);
            builder.create().show();
            app.setNewVersion(false);
            showToast(getString(R.string.player_plugin_hint));
        }
    }

    private void initView() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(BoardFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new BoardFragment();
            fm.beginTransaction().replace(android.R.id.content, fragment, BoardFragment.class.getSimpleName()).commit();
        }
        mPresenter = new BoardPresenter((BoardContract.View) fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.menu_setting:
                jumpToSetting();
                break;
            case R.id.menu_bookmark:
                jumpToBookmark();
                break;
            case R.id.menu_msg:
                myMessage();
                break;
            case R.id.menu_post:
                jumpToMyPost(false);
                break;
            case R.id.menu_reply:
                jumpToMyPost(true);
                break;
            case R.id.menu_about:
                aboutNgaClient();
                break;
            case R.id.menu_noname:
                noname();
                break;
            case R.id.menu_search:
                searchProfile();
                break;
            case R.id.menu_location:
                jumpToNearby();
                break;
            case R.id.menu_forward:
                toActivityByUrl();
                break;
            case R.id.menu_gun:
                jumpToRecentReply();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void searchProfile() {
        DialogFragment df;
        final String dialogTag = "searchpaofile_dialog";
        FragmentManager fm = getSupportFragmentManager();
        df = (DialogFragment) fm.findFragmentByTag(dialogTag);
        if (df == null) {
            df = new ProfileSearchDialogFragment();
            df.show(fm, dialogTag);
        }
    }

    private void signmission() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, mConfig.signActivityClass);
        startActivity(intent);
    }

    private void myMessage() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, mConfig.messageActivityClass);
        startActivity(intent);
    }

    private void noname() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, mConfig.nonameActivityClass);
        startActivity(intent);
    }

    private void aboutNgaClient() {
        final View view = getLayoutInflater().inflate(R.layout.client_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view).setTitle(R.string.about);
        String versionName = BuildConfig.VERSION_NAME;
        int versionCode = BuildConfig.VERSION_CODE;
        TextView contentView = (TextView) view
                .findViewById(R.id.client_device_dialog);
        String content = String.format(MainActivity.this
                .getString(R.string.about_client), versionName, versionCode);
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
                MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
                style.setSpan(myURLSpan, sp.getSpanStart(url),
                        sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            contentView.setText(style);
        }
        builder.setPositiveButton("知道了", null);
        builder.create().show();
    }

    private void delay(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(text);
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initDate();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initDate() {
        if (!PermissionUtils.hasStoragePermission(this)) {
            PermissionUtils.requestStoragePermission(this);
            return;
        }
        new Thread() {
            public void run() {
                File fileBase = new File(HttpUtil.PATH);
                if (!fileBase.exists()) {
                    delay(getString(R.string.create_cache_dir));
                    fileBase.mkdirs();
                }
                File f = new File(HttpUtil.PATH_AVATAR_OLD);
                if (f.exists()) {
                    f.renameTo(new File(HttpUtil.PATH_AVATAR));
                    delay(getString(R.string.move_avatar));
                }

                File file = new File(HttpUtil.PATH_NOMEDIA);
                if (!file.exists()) {
                    NLog.i(getClass().getSimpleName(), "create .nomedia");
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void jumpToSetting() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SettingsActivity.class);
        startActivityForResult(intent, ActivityUtils.REQUEST_CODE_SETTING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ActivityUtils.REQUEST_CODE_SETTING && resultCode == Activity.RESULT_OK) {
            mPresenter.notifyDataSetChanged();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void jumpToNearby() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, NearbyUserActivity.class);
        startActivity(intent);
    }

    private void jumpToRecentReply() {
        Intent intent = new Intent();
        intent.putExtra("recentmode", "recentmode");
        intent.setClass(MainActivity.this, mConfig.recentReplyListActivityClass);
        startActivity(intent);
    }

    private void jumpToMyPost(boolean isReply) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, mConfig.topicActivityClass);
        String userName = mConfig.userName;
        if (TextUtils.isEmpty(userName)) {
            showToast("你还没有登录");
            return;
        }

        if (isReply) {
            intent.putExtra("author", userName + "&searchpost=1");
        } else {
            intent.putExtra("author", userName);
        }
        startActivity(intent);
    }

    private void jumpToBookmark() {
        Intent intent_bookmark = new Intent(this, mConfig.topicActivityClass);
        intent_bookmark.putExtra("favor", 1);
        startActivity(intent_bookmark);
    }

    private void toActivityByUrl() {
        final View view = getLayoutInflater().inflate(R.layout.useurlto_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view).setTitle(R.string.urlto_title_hint);
        final EditText urlAdd = (EditText) view.findViewById(R.id.urladd);
        urlAdd.requestFocus();
        String clipData = null;
        android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboardManager.hasPrimaryClip()) {
            try {
                clipData = clipboardManager.getPrimaryClip().getItemAt(0)
                        .getText().toString();
            } catch (Exception e) {
                clipData = "";
            }

        }
        if (!StringUtils.isEmpty(clipData)) {
            urlAdd.setText(clipData);
            urlAdd.selectAll();
        }

        builder.setPositiveButton("进入", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = urlAdd.getText().toString().trim();
                if (StringUtils.isEmpty(url)) {// 空
                    showToast("请输入URL地址");
                    urlAdd.setFocusable(true);
                    try {
                        Field field = dialog.getClass().getSuperclass()
                                .getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    url = url.toLowerCase(Locale.US).trim();
                    if (url.indexOf("thread.php") > 0) {
                        url = url
                                .replaceAll(
                                        "(?i)[^\\[|\\]]+fid=(-{0,1}\\d+)[^\\[|\\]]{0,}",
                                        Utils.getNGAHost() + "thread.php?fid=$1");
                        Intent intent = new Intent();
                        intent.setData(Uri.parse(url));
                        intent.setClass(view.getContext(), mConfig.topicActivityClass);
                        startActivity(intent);
                    } else if (url.indexOf("read.php") > 0) {
                        if (url.indexOf("tid") > 0
                                && url.indexOf("pid") > 0) {
                            if (url.indexOf("tid") < url.indexOf("pid"))
                                url = url
                                        .replaceAll(
                                                "(?i)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]{0,}",
                                                Utils.getNGAHost() + "read.php?pid=$2&tid=$1");
                            else
                                url = url
                                        .replaceAll(
                                                "(?i)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]{0,}",
                                                Utils.getNGAHost() + "read.php?pid=$1&tid=$2");
                        } else if (url.indexOf("tid") > 0
                                && url.indexOf("pid") <= 0) {
                            url = url
                                    .replaceAll(
                                            "(?i)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]{0,}",
                                            Utils.getNGAHost() + "read.php?tid=$1");
                        } else if (url.indexOf("pid") > 0
                                && url.indexOf("tid") <= 0) {
                            url = url
                                    .replaceAll(
                                            "(?i)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]{0,}",
                                            Utils.getNGAHost() + "read.php?pid=$1");
                        }
                        Intent intent = new Intent();
                        intent.setData(Uri.parse(url));
                        intent.setClass(view.getContext(), mConfig.articleActivityClass);
                        startActivity(intent);
                    } else {
                        showToast("输入的地址并非NGA的板块地址或帖子地址,或缺少fid/pid/tid信息,请检查后再试");
                        urlAdd.setFocusable(true);
                        try {
                            Field field = dialog.getClass().getSuperclass()
                                    .getDeclaredField("mShowing");
                            field.setAccessible(true);
                            field.set(dialog, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Field field = dialog.getClass().getSuperclass()
                            .getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.create().show();
    }


    public class MyURLSpan extends ClickableSpan {
        private String mUrl;

        MyURLSpan(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {

            Intent intent = new Intent();
            intent.putExtra("path", mUrl);
            intent.setClass(MainActivity.this, WebViewerActivity.class);
            startActivity(intent);
        }
    }
}
