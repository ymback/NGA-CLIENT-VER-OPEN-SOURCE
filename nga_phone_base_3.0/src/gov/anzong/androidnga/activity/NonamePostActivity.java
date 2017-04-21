package gov.anzong.androidnga.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import gov.anzong.androidnga.R;
import noname.gson.parse.NonameParseJson;
import noname.gson.parse.NonamePostResponse;
import sp.phone.adapter.ExtensionEmotionAdapter;
import sp.phone.forumoperation.HttpPostClient;
import sp.phone.forumoperation.NonameThreadPostAction;
import sp.phone.fragment.EmotionCategorySelectFragment;
import sp.phone.fragment.EmotionDialogFragment;
import sp.phone.fragment.ExtensionEmotionFragment;
import sp.phone.interfaces.EmotionCategorySelectedListener;
import sp.phone.interfaces.OnEmotionPickedListener;
import sp.phone.task.NonameFileUploadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.FunctionUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

public class NonamePostActivity extends BasePostActivity implements
        OnEmotionPickedListener, NonameFileUploadTask.onFileUploaded {

    final int REQUEST_CODE_SELECT_PIC = 1;
    private final String LOG_TAG = Activity.class.getSimpleName();
    // private Button button_commit;
    // private Button button_cancel;
    // private ImageButton button_upload;
    // private ImageButton button_emotion;
    Object commit_lock = new Object();
    private String prefix;
    private EditText titleText;
    private EditText bodyText;
    private NonameThreadPostAction act;
    private String action;
    private String tid;
    private String REPLY_URL = "http://ngac.sinaapp.com/nganoname/post.php?";
    private View v;
    private boolean loading;
    private NonameFileUploadTask uploadTask = null;
    private ButtonCommitListener commitListener = null;

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
        v = this.getLayoutInflater().inflate(R.layout.nonamereply, null);
        v.setBackgroundColor(getResources().getColor(
                ThemeManager.getInstance().getBackgroundColor()));
        this.setContentView(v);

        if (PhoneConfiguration.getInstance().uploadLocation
                && PhoneConfiguration.getInstance().location == null) {
            ActivityUtil.reflushLocation(this);
        }

        Intent intent = this.getIntent();
        prefix = intent.getStringExtra("prefix");
        // if(prefix!=null){
        // prefix=prefix.replaceAll("\\n\\n", "\n");
        // }
        action = intent.getStringExtra("action");
        if (action.equals("new")) {
            getSupportActionBar().setTitle("发新匿名帖");
            tid = "-1";
        } else if (action.equals("reply")) {
            getSupportActionBar().setTitle("回复匿名帖");
            tid = intent.getStringExtra("tid");
        }
        String title = intent.getStringExtra("title");
        if (tid == null)
            tid = "";

        act = new NonameThreadPostAction(tid, "", "");
        loading = false;

        titleText = (EditText) findViewById(R.id.reply_titile_edittext);
        if (title != null) {
            titleText.setText(title);
        }
        if (!action.equals("new")) {
            titleText.setHint(R.string.titlecannull);
        }
        titleText.setSelected(true);
        bodyText = (EditText) findViewById(R.id.reply_body_edittext);
        if (prefix != null) {
            if (prefix.startsWith("[quote]") && prefix.endsWith("[/quote]\n")) {
                SpannableString spanString = new SpannableString(prefix);
                spanString.setSpan(new BackgroundColorSpan(-1513240), 0,
                        prefix.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanString.setSpan(
                        new StyleSpan(android.graphics.Typeface.BOLD),
                        prefix.indexOf("[b]Post by"),
                        prefix.indexOf("):[/b]") + 5,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                bodyText.append(spanString);
            } else {
                bodyText.append(prefix);
            }
            bodyText.setSelection(prefix.length());
        }
        ThemeManager tm = ThemeManager.getInstance();
        if (tm.getMode() == ThemeManager.MODE_NIGHT) {
            bodyText.setBackgroundResource(tm.getBackgroundColor());
            titleText.setBackgroundResource(tm.getBackgroundColor());
            int textColor = this.getResources().getColor(
                    tm.getForegroundColor());
            bodyText.setTextColor(textColor);
            titleText.setTextColor(textColor);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (PhoneConfiguration.getInstance().HandSide == 1) {// lefthand
            int flag = PhoneConfiguration.getInstance().getUiFlag();
            if (flag >= 4) {// 大于等于4肯定有
                getMenuInflater().inflate(R.menu.post_menu_left, menu);
            } else {
                getMenuInflater().inflate(R.menu.post_menu, menu);
            }
        } else {
            getMenuInflater().inflate(R.menu.post_menu, menu);
        }
        final int flags = ThemeManager.ACTION_BAR_FLAG;
        /*
         * ActionBar.DISPLAY_SHOW_HOME;//2 flags |=
		 * ActionBar.DISPLAY_USE_LOGO;//1 flags |=
		 * ActionBar.DISPLAY_HOME_AS_UP;//4
		 */
        ReflectionUtil.actionBar_setDisplayOption(this, flags);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED || data == null)
            return;
        switch (requestCode) {
            case REQUEST_CODE_SELECT_PIC:
                Log.i(LOG_TAG, " select file :" + data.getDataString());
                uploadTask = new NonameFileUploadTask(this, this, data.getData());
                break;
            default:
                ;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // case R.id.upload :
            // Intent intent = new Intent();
            // intent.setType("image/*");
            // intent.setAction(Intent.ACTION_GET_CONTENT);
            // startActivityForResult(intent, REQUEST_CODE_SELECT_PIC);
            // break;
            case R.id.upload:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_SELECT_PIC);
                break;
            case R.id.emotion:
                FragmentTransaction ft = getSupportFragmentManager()
                        .beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag(
                        EMOTION_CATEGORY_TAG);
                if (prev != null) {
                    ft.remove(prev);
                }

                DialogFragment newFragment = new EmotionCategorySelectFragment();
                newFragment.show(ft, EMOTION_CATEGORY_TAG);
                break;
            case R.id.supertext:
                FunctionUtil.handleSupertext(bodyText, this, v);
                break;
            case R.id.send:
                if (commitListener == null) {
                    commitListener = new ButtonCommitListener(REPLY_URL);
                }
                commitListener.onClick(null);
                break;
            default:
                finish();
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onEmotionPicked(String emotion) {
        final int index = bodyText.getSelectionStart();
        String urltemp = emotion.replaceAll("\\n", "");
        if (urltemp.indexOf("http") > 0) {
            urltemp = urltemp.substring(5, urltemp.length() - 6);
            String sourcefile = ExtensionEmotionAdapter.getPathByURI(urltemp);
            InputStream is = null;
            try {
                is = getResources().getAssets().open(sourcefile);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (is != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                BitmapDrawable bd = new BitmapDrawable(bitmap);
                Drawable drawable = (Drawable) bd;
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
                SpannableString spanString = new SpannableString(emotion);
                ImageSpan span = new ImageSpan(drawable,
                        ImageSpan.ALIGN_BASELINE);
                spanString.setSpan(span, 0, emotion.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (bodyText.getText().toString().replaceAll("\\n", "").trim()
                        .equals("")) {
                    bodyText.append(spanString);
                } else {
                    if (index <= 0 || index >= bodyText.length()) {// pos @
                        // begin /
                        // end
                        bodyText.append(spanString);
                    } else {
                        bodyText.getText().insert(index, spanString);
                    }
                }
            }
        } else {
            int[] emotions = {1, 2, 3, 24, 25, 26, 27, 28, 29, 30, 32, 33, 34,
                    35, 36, 37, 38, 39, 4, 40, 41, 42, 43, 5, 6, 7, 8};
            for (int i = 0; i < 27; i++) {
                if (emotion.indexOf("[s:" + String.valueOf(emotions[i]) + "]") == 0) {
                    String sourcefile = "a" + String.valueOf(emotions[i])
                            + ".gif";
                    InputStream is = null;
                    try {
                        is = getResources().getAssets().open(sourcefile);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (is != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        BitmapDrawable bd = new BitmapDrawable(bitmap);
                        Drawable drawable = (Drawable) bd;
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight());
                        SpannableString spanString = new SpannableString(
                                emotion);
                        ImageSpan span = new ImageSpan(drawable,
                                ImageSpan.ALIGN_BASELINE);
                        spanString.setSpan(span, 0, emotion.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if (index <= 0 || index >= bodyText.length()) {// pos @
                            // begin
                            // / end
                            bodyText.append(spanString);
                        } else {
                            bodyText.getText().insert(index, spanString);
                        }
                    } else {
                        bodyText.append(emotion);
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        if (action.equals("new")) {
            titleText.requestFocus();
        } else {
            bodyText.requestFocus();
        }
        if (uploadTask != null) {
            NonameFileUploadTask temp = uploadTask;
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
    private void RunParallel(NonameFileUploadTask task) {
        task.executeOnExecutor(NonameFileUploadTask.THREAD_POOL_EXECUTOR);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int finishUpload(String picUrl, Uri uri) {
        String selectedImagePath2 = FunctionUtil.getPath(this, uri);
        final int index = bodyText.getSelectionStart();
        String spantmp = "[img]" + picUrl + "[/img]";
        if (!StringUtil.isEmpty(selectedImagePath2)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath2,
                    options); // 此时返回 bm 为空
            options.inJustDecodeBounds = false;
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int screenwidth = (int) (dm.widthPixels * 0.75);
            int screenheigth = (int) (dm.heightPixels * 0.75);
            int width = options.outWidth;
            int height = options.outHeight;
            float scaleWidth = ((float) screenwidth) / width;
            float scaleHeight = ((float) screenheigth) / height;
            if (scaleWidth < scaleHeight && scaleWidth < 1f) {// 不能放大啊,然后主要是哪个小缩放到哪个就行了
                options.inSampleSize = (int) (1 / scaleWidth);
            } else if (scaleWidth >= scaleHeight && scaleHeight < 1f) {
                options.inSampleSize = (int) (1 / scaleHeight);
            } else {
                options.inSampleSize = 1;
            }
            bitmap = BitmapFactory.decodeFile(selectedImagePath2, options);
            BitmapDrawable bd = new BitmapDrawable(bitmap);
            Drawable drawable = (Drawable) bd;
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            SpannableString spanStringS = new SpannableString(spantmp);
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            spanStringS.setSpan(span, 0, spantmp.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (bodyText.getText().toString().replaceAll("\\n", "").trim()
                    .equals("")) {// NO INPUT DATA
                bodyText.append(spanStringS);
                bodyText.append("\n");
            } else {
                if (index <= 0 || index >= bodyText.length()) {// pos @ begin /
                    // end
                    if (bodyText.getText().toString().endsWith("\n")) {
                        bodyText.append(spanStringS);
                        bodyText.append("\n");
                    } else {
                        bodyText.append("\n");
                        bodyText.append(spanStringS);
                        bodyText.append("\n");
                    }
                } else {
                    bodyText.getText().insert(index, spanStringS);
                }
            }
        } else {
            if (bodyText.getText().toString().replaceAll("\\n", "").trim()
                    .equals("")) {// NO INPUT DATA
                bodyText.append(spantmp + "\n");
            } else {
                if (index <= 0 || index >= bodyText.length()) {// pos @ begin /
                    // end
                    if (bodyText.getText().toString().endsWith("\n")) {
                        bodyText.append(spantmp + "\n");
                    } else {
                        bodyText.append("\n" + spantmp + "\n");
                    }
                } else {
                    bodyText.getText().insert(index, spantmp);
                }
            }
        }
        InputMethodManager imm = (InputMethodManager) bodyText.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
        return 1;
    }

    class ButtonCommitListener implements OnClickListener {

        private final String url;

        ButtonCommitListener(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View v) {
            if (action.equals("reply")) {
                if (bodyText.getText().toString().length() > 2) {
                    synchronized (commit_lock) {
                        if (loading) {
                            showToast(R.string.avoidWindfury);
                            return;
                        }
                        loading = true;
                    }
                    handleReply(v);
                } else {
                    showToast("正文内容至少3个字符");
                }
            } else if (action.equals("new")) {
                if (titleText.getText().toString().length() > 0
                        && bodyText.getText().toString().length() > 2) {
                    synchronized (commit_lock) {
                        if (loading) {
                            showToast(R.string.avoidWindfury);
                            return;
                        }
                        loading = true;
                    }
                    handleNewThread(v);
                } else {
                    showToast("请输入正确的标题和正文内容，正文内容至少3个字符");
                }
            }
        }

        public void handleNewThread(View v) {
            handleReply(v);

        }

        public void handleReply(View v1) {

            act.setPost_subject_(titleText.getText().toString());
            if (bodyText.getText().toString().length() > 0) {
                act.setPost_content_(FunctionUtil.ColorTxtCheck(bodyText
                        .getText().toString()));
                new ArticlePostTask(NonamePostActivity.this).execute(url,
                        act.toString());
            }

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
            String ret = "{\"error\":true,\"errorinfo\":\"\u7f51\u7edc\u9519\u8bef\"}";// 网络错误
            String url = params[0];
            String body = params[1];

            HttpPostClient c = new HttpPostClient(url);
            c.setCookie("");
            try {
                InputStream input = null;
                HttpURLConnection conn = c.post_body(body);
                if (conn != null) {
                    if (conn.getResponseCode() != 200) {
                        input = null;
                        keepActivity = true;
                        ret = "{\"error\":true,\"errorinfo\":\"\u533f\u540d\u670d\u52a1\u5668\u51fa\u9519\u4e86\"}";// 匿名服务器出错
                    } else {
                        input = conn.getInputStream();
                    }
                } else
                    keepActivity = true;

                if (input != null) {
                    ret = IOUtils.toString(input, "utf-8");
                } else
                    keepActivity = true;
            } catch (IOException e) {
                keepActivity = true;
                Log.e(LOG_TAG, Log.getStackTraceString(e));

            }
            return ret;
        }

        @Override
        protected void onPostExecute(String result) {
            NonamePostResponse s = NonameParseJson.parsePost(result);
            if (keepActivity == false) {
                boolean success = !s.error;
                if (!success)
                    keepActivity = true;
            }
            if (s.error) {// 出错
                showToast(s.errorinfo);
            } else {
                showToast(s.data);
            }
            if (PhoneConfiguration.getInstance().refresh_after_post_setting_mode) {
                PhoneConfiguration.getInstance().setRefreshAfterPost(true);
            }
            ActivityUtil.getInstance().dismiss();
            if (!keepActivity)
                NonamePostActivity.this.finish();
            synchronized (commit_lock) {
                loading = false;
            }

            super.onPostExecute(result);
        }

    }

}