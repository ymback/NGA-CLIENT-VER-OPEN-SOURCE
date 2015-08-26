package gov.anzong.meizi;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.MyApp;
import gov.anzong.androidnga.activity.SwipeBackAppCompatActivity;
import sp.phone.bean.PerferenceConstant;
import sp.phone.forumoperation.HttpPostClient;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.MD5Util;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

public class MeiziLoginActivity extends SwipeBackAppCompatActivity implements
        PerferenceConstant {

    EditText userText;
    EditText passwordText;
    View view;
    TextView login_state;
    Object commit_lock = new Object();
    private boolean needtopost = false;
    private boolean alreadylogin = false;
    private Toast toast = null;
    private boolean loading = false;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        int orentation = ThemeManager.getInstance().screenOrentation;
        if (orentation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                || orentation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(orentation);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        ThemeManager.SetContextTheme(this);

        view = LayoutInflater.from(this).inflate(R.layout.dbmeizi_login, null);
        this.setContentView(view);
        this.setTitle("豆瓣妹子:登录");
        Button button_login = (Button) findViewById(R.id.login_button);
        userText = (EditText) findViewById(R.id.login_user_edittext);
        passwordText = (EditText) findViewById(R.id.login_password_edittext);

        login_state = (TextView) findViewById(R.id.login_state);

        String postUrl = "http://www.dbmeizi.com/ajax/login";

        LoginButtonListener listener = new LoginButtonListener(postUrl);
        button_login.setOnClickListener(listener);

        updateThemeUI();
    }

    private void updateThemeUI() {
        if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().db_cookie)) {
            login_state.setText("已经登陆,你就是神");
        }
        ThemeManager tm = ThemeManager.getInstance();
        if (tm.getMode() == ThemeManager.MODE_NIGHT) {
            view.setBackgroundResource(ThemeManager.getInstance()
                    .getBackgroundColor());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int flags = 15;
        ReflectionUtil.actionBar_setDisplayOption(this, flags);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        if (PhoneConfiguration.getInstance().fullscreen) {
            ActivityUtil.getInstance().setFullScreen(view);
        }
        if (alreadylogin && needtopost) {
            finish();
        }
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class LoginButtonListener implements OnClickListener {
        final private String loginUrl;
        private final String LOG_TAG = LoginButtonListener.class
                .getSimpleName();

        public LoginButtonListener(String loginUrl) {
            super();
            this.loginUrl = loginUrl;
        }

        private String md5passwd(String passwd) {
            return MD5Util.MD5("dbmeizi" + MD5Util.MD5(passwd));
        }

        @Override
        public void onClick(View v) {
            synchronized (commit_lock) {
                if (loading == true) {
                    if (toast != null) {
                        toast.setText(R.string.avoidWindfury);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        toast = Toast.makeText(v.getContext(),
                                R.string.avoidWindfury, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    return;
                } else {
                    StringBuffer bodyBuffer = new StringBuffer();
                    bodyBuffer.append("email=");

                    try {
                        bodyBuffer.append(URLEncoder.encode(userText.getText()
                                .toString(), "utf-8"));
                        bodyBuffer.append("&passwd=");
                        bodyBuffer.append(URLEncoder.encode(
                                md5passwd(passwordText.getText().toString())
                                        .toString(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    new LoginTask(v).execute(loginUrl, bodyBuffer.toString());

                }
                loading = true;
            }
        }

        private class LoginTask extends AsyncTask<String, Integer, Boolean> {
            final View v;
            private String uid = null;
            private String sess = null;

            public LoginTask(View v) {
                super();
                this.v = v;
            }

            @Override
            protected Boolean doInBackground(String... params) {
                String url = params[0];
                String body = params[1];
                HttpURLConnection conn = new HttpPostClient(url)
                        .post_body(body);
                return validate(conn);

            }

            private boolean validate(HttpURLConnection conn) {
                if (conn == null)
                    return false;

                String cookieVal = null;
                String key = null;

                String uid = "";
                String sess = "";

                for (int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++) {
                    Log.d(LOG_TAG,
                            conn.getHeaderFieldKey(i) + ":"
                                    + conn.getHeaderField(i));
                    if (key.equalsIgnoreCase("set-cookie")) {
                        cookieVal = conn.getHeaderField(i);
                        cookieVal = cookieVal.substring(0,
                                cookieVal.indexOf(';'));
                        if (cookieVal.indexOf("uid=") == 0)
                            uid = cookieVal.substring(4);
                        if (cookieVal.indexOf("sess=") == 0)
                            sess = cookieVal.substring(5);

                    }
                }
                if (sess != "" && uid != "") {
                    this.uid = uid;
                    this.sess = sess;
                    return true;
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                synchronized (commit_lock) {
                    loading = false;
                }
                if (result.booleanValue()) {
                    if (toast != null) {
                        toast.setText(R.string.login_successfully);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        toast = Toast
                                .makeText(v.getContext(),
                                        R.string.login_successfully,
                                        Toast.LENGTH_SHORT);

                        toast.show();
                    }
                    MyApp app = (MyApp) MeiziLoginActivity.this
                            .getApplication();
                    app.addToMeiziUserList(uid, sess);
                    alreadylogin = true;
                    super.onPostExecute(result);
                    finish();
                } else {
                    if (toast != null) {
                        toast.setText(R.string.login_failed);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        toast = Toast.makeText(v.getContext(),
                                R.string.login_failed, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }

        }

    }

}
