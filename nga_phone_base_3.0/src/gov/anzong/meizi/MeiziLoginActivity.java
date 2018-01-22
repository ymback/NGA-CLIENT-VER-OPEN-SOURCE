package gov.anzong.meizi;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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
import gov.anzong.meizi.common.HttpPostClient;
import gov.anzong.meizi.common.MeiziCookieManager;
import gov.anzong.meizi.utils.MeiziStringUtils;

public class MeiziLoginActivity extends AppCompatActivity {

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

        view = LayoutInflater.from(this).inflate(R.layout.meizi_login, null);
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
        if (!TextUtils.isEmpty(MeiziCookieManager.getInstance().getMeiziCookie())) {
            login_state.setText("已经登陆,你就是神");
        }
    }

    @Override
    protected void onResume() {
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
            return MeiziStringUtils.MD5("dbmeizi" + MeiziStringUtils.MD5(passwd));
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
                    Log.d(LOG_TAG, conn.getHeaderFieldKey(i) + ":" + conn.getHeaderField(i));
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
                    MeiziCookieManager.getInstance().addToMeiziUserList(uid, sess);
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
