package gov.anzong.androidnga.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashSet;

import gov.anzong.androidnga.R;
import sp.phone.forumoperation.AvatarPostAction;
import sp.phone.forumoperation.HttpPostClient;
import sp.phone.interfaces.ChangeAvatarLoadCompleteCallBack;
import sp.phone.task.AvatarFileUploadTask;
import sp.phone.task.ChangeAvatarLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

public class AvatarPostActivity extends SwipeBackAppCompatActivity implements
        AvatarFileUploadTask.onFileUploaded, ChangeAvatarLoadCompleteCallBack {

    final int REQUEST_CODE_SELECT_PIC = 1;
    private final String LOG_TAG = Activity.class.getSimpleName();
    private final Object lock = new Object();
    private final HashSet<String> urlSet = new HashSet<String>();
    // private Button button_commit;
    // private Button button_cancel;
    // private ImageButton button_upload;
    // private ImageButton button_emotion;
    Object commit_lock = new Object();
    private EditText titleText;
    private AvatarPostAction act;
    private TextView add_title, avatarpreview;
    private ImageView avatarImage;
    private Button selectpic_button, submit_button;
    private Bitmap resultbitmap;
    private String REPLY_URL = "http://nga.178.com/nuke.php?";
    private View v;
    private boolean loading;
    private AvatarFileUploadTask uploadTask = null;
    private ButtonCommitListener commitListener = null;

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int orentation = ThemeManager.getInstance().screenOrentation;
        if (orentation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                || orentation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(orentation);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        super.onCreate(savedInstanceState);
        v = this.getLayoutInflater().inflate(R.layout.changeavatar, null);
        v.setBackgroundColor(getResources().getColor(
                ThemeManager.getInstance().getBackgroundColor()));
        this.setContentView(v);

        if (PhoneConfiguration.getInstance().uploadLocation
                && PhoneConfiguration.getInstance().location == null) {
            ActivityUtil.reflushLocation(this);
        }
        act = new AvatarPostAction();
        loading = false;

        titleText = (EditText) findViewById(R.id.urladd);
        add_title = (TextView) findViewById(R.id.add_title);
        avatarpreview = (TextView) findViewById(R.id.avatarpreview);
        avatarImage = (ImageView) findViewById(R.id.avatarImage);
        selectpic_button = (Button) findViewById(R.id.selectpic_button);
        submit_button = (Button) findViewById(R.id.submit_button);
        titleText.setSelected(true);
        ThemeManager tm = ThemeManager.getInstance();
        if (tm.getMode() == ThemeManager.MODE_NIGHT) {
            titleText.setBackgroundResource(tm.getBackgroundColor());
            int textColor = this.getResources().getColor(
                    tm.getForegroundColor());
            add_title.setTextColor(textColor);
            avatarpreview.setTextColor(textColor);
            titleText.setTextColor(textColor);
        }
        avatarImage.setVisibility(View.GONE);
        avatarpreview.setVisibility(View.GONE);
        selectpic_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_SELECT_PIC);
            }

        });
        submit_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (commitListener == null) {
                    commitListener = new ButtonCommitListener(REPLY_URL);
                }
                commitListener.onClick(null);
            }

        });

        final int flags = ThemeManager.ACTION_BAR_FLAG;
        ReflectionUtil.actionBar_setDisplayOption(this, flags);
        getSupportActionBar().setTitle("更改头像");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            default:
                finish();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED || data == null)
            return;
        switch (requestCode) {
            case REQUEST_CODE_SELECT_PIC:
                Log.i(LOG_TAG, " select file :" + data.getDataString());
                uploadTask = new AvatarFileUploadTask(this, this, data.getData());
                break;
            default:
                ;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        titleText.requestFocus();
        if (uploadTask != null) {
            AvatarFileUploadTask temp = uploadTask;
            uploadTask = null;
            if (ActivityUtil.isGreaterThan_2_3_3()) {
                RunParallel(temp);
            } else {
                temp.execute();
            }
        }
        if (PhoneConfiguration.getInstance().fullscreen) {
            ActivityUtil.getInstance().setFullScreen(v);
        }
        super.onResume();
    }

    @TargetApi(11)
    private void RunParallel(AvatarFileUploadTask task) {
        task.executeOnExecutor(AvatarFileUploadTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public int finishUpload(String picUrl, Uri uri) {
        titleText.setText(picUrl);
        avatarImage.setVisibility(View.VISIBLE);
        avatarpreview.setVisibility(View.VISIBLE);
        handleAvatar(avatarImage, picUrl);
        return 1;
    }

    private void handleAvatar(ImageView avatarIV, String avatarUrl) {
        final String userId = PhoneConfiguration.getInstance().uid;
        if (!StringUtil.isEmpty(avatarUrl)) {
            final String avatarPath = ImageUtil.newImage(avatarUrl, userId);
            new ChangeAvatarLoadTask(avatarIV, 0, this)
                    .execute(avatarUrl, avatarPath, userId);
        } else {
            avatarImage.setVisibility(View.GONE);
            avatarpreview.setVisibility(View.GONE);
        }

    }

    @Override
    public void OnAvatarLoadStart(String url) {
        // TODO Auto-generated method stub

        synchronized (lock) {
            this.urlSet.add(url);
        }
    }

    @Override
    public void OnAvatarLoadComplete(String url, Bitmap result) {
        // TODO Auto-generated method stub
        synchronized (lock) {
            this.urlSet.remove(url);
            if (result != null) {
                resultbitmap = result;
            } else {
                avatarImage.setVisibility(View.GONE);
                avatarpreview.setVisibility(View.GONE);
            }
        }
    }

    class ButtonCommitListener implements OnClickListener {

        private final String url;

        ButtonCommitListener(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View v) {
            if (titleText.getText().toString().length() > 0) {
                if (titleText.getText().toString().startsWith("http")) {
                    synchronized (commit_lock) {
                        if (loading == true) {
                            showToast(R.string.avoidWindfury);
                            return;
                        }
                        loading = true;
                    }
                    handleReply(v);
                } else {
                    showToast("请输入正确的头像URL地址");
                }
            } else {
                showToast("请输入正确的头像URL地址");
            }
        }

        public void handleReply(View v1) {

            act.seticon_(titleText.getText().toString());
            new ArticlePostTask(AvatarPostActivity.this).execute(url,
                    act.toString());

        }

    }

    private class ArticlePostTask extends AsyncTask<String, Integer, String> {

        final Context c;
        private boolean keepActivity = false;

        public ArticlePostTask(Context context) {
            super();
            this.c = context;
        }

        @Override
        protected void onPreExecute() {
            ActivityUtil.getInstance().noticeSaying(c);
            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
            synchronized (commit_lock) {
                loading = false;
            }
            ActivityUtil.getInstance().dismiss();
            super.onCancelled();
        }

        @Override
        protected void onCancelled(String result) {
            synchronized (commit_lock) {
                loading = false;
            }
            ActivityUtil.getInstance().dismiss();
            super.onCancelled();
        }

        @Override
        protected String doInBackground(String... params) {
            if (params.length < 2)
                return "parameter error";
            String ret = "网络错误";
            String url = params[0];
            String body = params[1];

            HttpPostClient c = new HttpPostClient(url);
            String cookie = PhoneConfiguration.getInstance().getCookie();
            c.setCookie(cookie);
            try {
                InputStream input = null;
                HttpURLConnection conn = c.post_body(body);
                if (conn != null) {
                    if (conn.getResponseCode() >= 500) {
                        input = null;
                        keepActivity = true;
                        ret = "二哥在用服务器下毛片";
                    } else {
                        if (conn.getResponseCode() >= 400) {
                            input = conn.getErrorStream();
                            keepActivity = true;
                        } else
                            input = conn.getInputStream();
                    }
                } else
                    keepActivity = true;

                if (input != null) {
                    String html = IOUtils.toString(input, "gbk");
                    ret = getReplyResult(html);
                } else
                    keepActivity = true;
            } catch (IOException e) {
                keepActivity = true;
                Log.e(LOG_TAG, Log.getStackTraceString(e));

            }
            return ret;
        }


        private String getReplyResult(String js) {
            if (null == js) {
                return "发送失败";
            }
            js = js.replaceAll("window.script_muti_get_var_store=", "");
            if (js.indexOf("/*error fill content") > 0)
                js = js.substring(0, js.indexOf("/*error fill content"));
            js = js.replaceAll("\"content\":\\+(\\d+),", "\"content\":\"+$1\",");
            js = js.replaceAll("\"subject\":\\+(\\d+),", "\"subject\":\"+$1\",");
            js = js.replaceAll("/\\*\\$js\\$\\*/", "");
            JSONObject o = null;
            try {
                o = (JSONObject) JSON.parseObject(js).get("data");
            } catch (Exception e) {
                Log.e("TAG", "can not parse :\n" + js);
            }
            if (o == null) {
                try {
                    o = (JSONObject) JSON.parseObject(js).get("error");
                } catch (Exception e) {
                    Log.e("TAG", "can not parse :\n" + js);
                }
                if (o == null) {
                    return "发送失败";
                }
                return o.getString("0");
            }
            return o.getString("0");
        }

        @Override
        protected void onPostExecute(String result) {
            String success_results[] = {"操作成功 你可能需要重新登录以显示新的头像"};
            if (keepActivity == false) {
                boolean success = false;
                for (int i = 0; i < success_results.length; ++i) {
                    if (result.contains(success_results[i])) {
                        success = true;
                        break;
                    }
                }
                if (!success)
                    keepActivity = true;
            }
            showToast("操作成功");
            ActivityUtil.getInstance().dismiss();
            String userId = PhoneConfiguration.getInstance().uid;
            String avatarPath = HttpUtil.PATH_AVATAR + "/" + userId + ".jpg";
            HttpUtil.downImage3(resultbitmap, avatarPath);
            if (!keepActivity) {
                Intent intent = new Intent();
                AvatarPostActivity.this.setResult(123, intent);
                intent.putExtra("avatar", act.geticon_());
                AvatarPostActivity.this.finish();
            }
            synchronized (commit_lock) {
                loading = false;
            }

            super.onPostExecute(result);
        }

    }

}