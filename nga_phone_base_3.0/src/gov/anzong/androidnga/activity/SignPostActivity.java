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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import sp.phone.adapter.ExtensionEmotionAdapter;
import sp.phone.forumoperation.HttpPostClient;
import sp.phone.forumoperation.SignPostAction;
import sp.phone.fragment.EmotionCategorySelectFragment;
import sp.phone.interfaces.OnEmotionPickedListener;
import sp.phone.task.FileUploadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.FunctionUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

public class SignPostActivity extends BasePostActivity implements
        FileUploadTask.onFileUploaded, OnEmotionPickedListener {

    final int REQUEST_CODE_SELECT_PIC = 1;
    private final String LOG_TAG = Activity.class.getSimpleName();
    // private Button button_commit;
    // private Button button_cancel;
    // private ImageButton button_upload;
    // private ImageButton button_emotion;
    Object commit_lock = new Object();
    private String prefix;
    private EditText bodyText;
    private SignPostAction act;
    private String REPLY_URL = Utils.getNGAHost() + "nuke.php?";
    private View v;
    private boolean loading;
    private FileUploadTask uploadTask = null;
    private ButtonCommitListener commitListener = null;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @SuppressWarnings("WrongConstant")
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
        v = this.getLayoutInflater().inflate(R.layout.changesignreply, null);
        v.setBackgroundColor(getResources().getColor(
                ThemeManager.getInstance().getBackgroundColor()));
        this.setContentView(v);

        if (PhoneConfiguration.getInstance().uploadLocation
                && PhoneConfiguration.getInstance().location == null) {
            ActivityUtil.reflushLocation(this);
        }

        Intent intent = this.getIntent();
        prefix = intent.getStringExtra("prefix");
        bodyText = (EditText) findViewById(R.id.reply_body_edittext);

        bodyText.setSelected(true);

        act = new SignPostAction();
        this.act.set__ngaClientChecksum(FunctionUtil.getngaClientChecksum(this));
        loading = false;
        if (prefix != null) {
            if (prefix.startsWith("[quote][pid=")
                    && prefix.endsWith("[/quote]\n")) {
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
            int textColor = this.getResources().getColor(
                    tm.getForegroundColor());
            bodyText.setTextColor(textColor);
        }
        getSupportActionBar().setTitle("更改签名");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (PhoneConfiguration.getInstance().HandSide == 1) {// lefthand
            int flag = PhoneConfiguration.getInstance().getUiFlag();
            if (flag >= 4) {// 大于等于4肯定有
                getMenuInflater().inflate(R.menu.messagepost_menu_left, menu);
            } else {
                getMenuInflater().inflate(R.menu.messagepost_menu, menu);
            }
        } else {
            getMenuInflater().inflate(R.menu.messagepost_menu, menu);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_SELECT_PIC);
                break;
            case R.id.emotion:
                android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager()
                        .beginTransaction();
                android.support.v4.app.Fragment prev = getSupportFragmentManager().findFragmentByTag(
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
                if (StringUtil.isEmpty(bodyText.getText().toString())) {
                    showToast("请输入内容");
                } else {
                    if (commitListener == null) {
                        commitListener = new ButtonCommitListener(REPLY_URL);
                    }
                    commitListener.onClick(null);
                }
                break;
            default:
                finish();
        }
        return true;
    }// OK

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
    }// OK

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED || data == null)
            return;
        switch (requestCode) {
            case REQUEST_CODE_SELECT_PIC:
                Log.i(LOG_TAG, " select file :" + data.getDataString());
                uploadTask = new FileUploadTask(this, this, data.getData());
                break;
            default:
                ;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        bodyText.requestFocus();
        bodyText.selectAll();
        if (uploadTask != null) {
            FileUploadTask temp = uploadTask;
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
    private void RunParallel(FileUploadTask task) {
        task.executeOnExecutor(FileUploadTask.THREAD_POOL_EXECUTOR);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int finishUpload(String attachments, String attachmentsCheck,
                            String picUrl, Uri uri) {
        String selectedImagePath2 = FunctionUtil.getPath(this, uri);
        final int index = bodyText.getSelectionStart();
        String spantmp = "[img]./" + picUrl + "[/img]";
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
                bodyText.append("[img]./" + picUrl + "[/img]\n");
            } else {
                if (index <= 0 || index >= bodyText.length()) {// pos @ begin /
                    // end
                    if (bodyText.getText().toString().endsWith("\n")) {
                        bodyText.append("[img]./" + picUrl + "[/img]\n");
                    } else {
                        bodyText.append("\n[img]./" + picUrl + "[/img]\n");
                    }
                } else {
                    bodyText.getText().insert(index,
                            "[img]./" + picUrl + "[/img]");
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
            synchronized (commit_lock) {
                if (loading) {
                    showToast(R.string.avoidWindfury);
                    return;
                }
                loading = true;
            }
            handleReply(v);
        }

        public void handleReply(View v1) {

            if (bodyText.getText().toString().length() > 0) {
                act.setsign_(FunctionUtil.ColorTxtCheck(bodyText.getText()
                        .toString()));
                new SignPostTask(SignPostActivity.this).execute(url,
                        act.toString());
            }
        }
    }

    private class SignPostTask extends AsyncTask<String, Integer, String> {

        final Context c;
        private boolean keepActivity = false;

        public SignPostTask(Context context) {
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
            String success_results[] = {"操作成功"};
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
            showToast(result);
            ActivityUtil.getInstance().dismiss();
            if (!keepActivity) {
                Intent intent = new Intent();
                intent.putExtra("sign", act.getsign_());
                SignPostActivity.this.setResult(321, intent);
                SignPostActivity.this.finish();
            }
            synchronized (commit_lock) {
                loading = false;
            }

            super.onPostExecute(result);
        }

    }
}