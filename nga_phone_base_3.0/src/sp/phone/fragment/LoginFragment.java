package sp.phone.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.MyApp;
import sp.phone.adapter.UserListAdapter;
import sp.phone.bean.PerferenceConstant;
import sp.phone.forumoperation.HttpPostClient;
import sp.phone.interfaces.OnAuthcodeLoadFinishedListener;
import sp.phone.task.AccountAuthcodeImageReloadTask;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;

public class LoginFragment extends DialogFragment implements
        PerferenceConstant, OnAuthcodeLoadFinishedListener {

    EditText userText;
    EditText passwordText;
    EditText authcodeText;
    ImageView authcodeImg;
    Button button_login;
    ImageButton authcodeimg_refresh;
    AccountAuthcodeImageReloadTask loadauthcodetask;
    ListView userList;
    String name;
    Object commit_lock = new Object();
    private View view;
    private String authcode_cookie;
    private boolean loading = false;
    private Toast toast = null;

    public LoginFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login, null);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        button_login = (Button) view.findViewById(R.id.login_button);
        authcodeimg_refresh = (ImageButton) view
                .findViewById(R.id.authcode_refresh);
        userText = (EditText) view.findViewById(R.id.login_user_edittext);
        passwordText = (EditText) view
                .findViewById(R.id.login_password_edittext);
        authcodeText = (EditText) view
                .findViewById(R.id.login_authcode_edittext);
        authcodeImg = (ImageView) view.findViewById(R.id.authcode_img);
        userList = (ListView) view.findViewById(R.id.user_list);
        userList.setAdapter(new UserListAdapter(getActivity(), userText));
        String postUrl = "http://account.178.com/q_account.php?_act=login&print=login";

        String userName = PhoneConfiguration.getInstance().userName;
        if (userName != "") {
            userText.setText(userName);
            userText.selectAll();
        }

        LoginButtonListener listener = new LoginButtonListener(postUrl);
        button_login.setOnClickListener(listener);
        this.getDialog().setTitle(R.string.login);
        reloadauthcode();
        authcodeimg_refresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                reloadauthcode();
            }

        });
        authcodeImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                reloadauthcode();
            }

        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void reloadauthcode() {
        authcode_cookie = "";
        try {
            authcodeText.setText("");
        } catch (Exception e) {
        }
        if (loadauthcodetask != null) {
            loadauthcodetask.cancel(true);
        }
        authcodeImg.setImageDrawable(getResources().getDrawable(
                R.drawable.q_vcode));
        loadauthcodetask = new AccountAuthcodeImageReloadTask(getActivity(),
                this);
        loadauthcodetask.execute();
    }

    private void reloadauthcode(String error) {
        if (!StringUtil.isEmpty(error)) {
            if (toast != null) {
                toast.setText(error);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            } else {
                toast = Toast
                        .makeText(getActivity(), error, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        reloadauthcode();
    }

    @Override
    public void authcodefinishLoad(Bitmap authimg, String authcode) {
        // TODO Auto-generated method stub
        Log.i("TAG", authcode);
        this.authcode_cookie = authcode;
        authcodeImg.setImageBitmap(authimg);
    }

    @Override
    public void authcodefinishLoadError() {
        // TODO Auto-generated method stub
        if (toast != null) {
            toast.setText("载入验证码失败，请点击刷新重新加载");
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast = Toast.makeText(getActivity(), "载入验证码失败，请点击刷新重新加载",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        authcodeImg.setImageDrawable(getResources().getDrawable(
                R.drawable.q_vcode_retry));
        authcode_cookie = "";
        authcodeText.setText("");
    }

    class LoginButtonListener implements OnClickListener {
        final private String loginUrl;
        private final String LOG_TAG = LoginButtonListener.class
                .getSimpleName();

        public LoginButtonListener(String loginUrl) {
            super();
            this.loginUrl = loginUrl;
        }

        @Override
        public void onClick(View v) {

            synchronized (commit_lock) {
                if (loading == true) {
                    String avoidWindfury = getActivity().getString(
                            R.string.avoidWindfury);
                    if (toast != null) {
                        toast.setText(avoidWindfury);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        toast = Toast.makeText(getActivity(), avoidWindfury,
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    return;
                } else {
                    StringBuffer bodyBuffer = new StringBuffer();
                    bodyBuffer.append("email=");
                    if (StringUtil.isEmpty(authcode_cookie)) {
                        if (toast != null) {
                            toast.setText("验证码信息错误，请重试");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            toast = Toast.makeText(getActivity(),
                                    "验证码信息错误，请重试", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        reloadauthcode();
                        return;
                    }
                    name = userText.getText().toString();
                    if (StringUtil.isEmpty(name)
                            || StringUtil.isEmpty(passwordText.getText()
                            .toString())
                            || StringUtil.isEmpty(authcodeText.getText()
                            .toString())) {
                        if (toast != null) {
                            toast.setText("内容缺少，请检查后再试");
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            toast = Toast.makeText(getActivity(),
                                    "内容缺少，请检查后再试", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        reloadauthcode();
                        return;
                    }
                    try {
                        bodyBuffer.append(URLEncoder.encode(userText.getText()
                                .toString(), "utf-8"));
                        bodyBuffer.append("&password=");
                        bodyBuffer.append(URLEncoder.encode(passwordText
                                .getText().toString(), "utf-8"));
                        bodyBuffer.append("&vcode=");
                        bodyBuffer.append(URLEncoder.encode(authcodeText
                                .getText().toString(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    new LoginTask(v).execute(loginUrl, bodyBuffer.toString());

                }
                loading = true;
            }
        }

        private class LoginTask extends AsyncTask<String, Integer, Boolean> {
            private String uid = null;
            private String cid = null;
            private String errorstr = "";

            public LoginTask(View v) {
                super();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                String url = params[0];
                String body = params[1];
                String cookie = "reg_vcode=" + authcode_cookie;
                HttpURLConnection conn = new HttpPostClient(url, cookie)
                        .post_body(body);
                return validate(conn);

            }

            private boolean validate(HttpURLConnection conn) {
                if (conn == null)
                    return false;

                String cookieVal = null;
                String key = null;

                String uid = "";
                String cid = "";
                String location = "";

                for (int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++) {
                    Log.d(LOG_TAG,
                            conn.getHeaderFieldKey(i) + ":"
                                    + conn.getHeaderField(i));
                    if (key.equalsIgnoreCase("location")) {
                        String re301location = conn.getHeaderField(i);
                        if (re301location.indexOf("login_failed") > 0) {
                            if (re301location.indexOf("error_vcode") > 0) {
                                errorstr = ("验证码错误");
                            } else if (re301location.indexOf("e_login") > 0) {
                                errorstr = ("用户名或密码错误");
                            } else {
                                errorstr = "未知错误";
                            }
                            return false;
                        }
                    }
                    if (key.equalsIgnoreCase("set-cookie")) {
                        cookieVal = conn.getHeaderField(i);
                        cookieVal = cookieVal.substring(0,
                                cookieVal.indexOf(';'));
                        Log.i("loginac", cookieVal);
                        if (cookieVal.indexOf("_sid=") == 0)
                            cid = cookieVal.substring(5);
                        if (cookieVal.indexOf("_178c=") == 0) {
                            uid = cookieVal
                                    .substring(6, cookieVal.indexOf('%'));
                            if (StringUtil.isEmail(name)) {
                                try {
                                    String nametmp = cookieVal
                                            .substring(cookieVal.indexOf("%23") + 3);
                                    nametmp = URLDecoder.decode(nametmp,
                                            "utf-8");
                                    String[] stemp = nametmp.split("#");
                                    for (int ia = 0; ia < stemp.length; ia++) {
                                        if (!StringUtil.isEmail(stemp[ia])) {
                                            name = stemp[ia];
                                            ia = stemp.length;
                                        }
                                    }

                                } catch (UnsupportedEncodingException e) {
                                }
                            }
                        }

                    }
                    if (key.equalsIgnoreCase("Location")) {
                        location = conn.getHeaderField(i);

                    }
                }
                if (cid != "" && uid != ""
                        && location.indexOf("login_success&error=0") != -1) {
                    this.uid = uid;
                    this.cid = cid;
                    Log.i(LOG_TAG, "uid =" + uid + ",csid=" + cid);
                    return true;
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                synchronized (commit_lock) {
                    loading = false;
                }
                if (!StringUtil.isEmpty(errorstr)) {
                    reloadauthcode(errorstr);
                    super.onPostExecute(result);
                } else {
                    if (result.booleanValue()) {
                        if (toast != null) {
                            toast.setText(R.string.login_successfully);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            toast = Toast.makeText(getActivity(),
                                    R.string.login_successfully,
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        /*
						 * Intent intent = new Intent();
						 * intent.setClass(v.getContext(), MainActivity.class);
						 * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						 */
                        SharedPreferences share = getActivity()
                                .getSharedPreferences(PERFERENCE,
                                        Context.MODE_PRIVATE);
                        Editor editor = share.edit();
                        editor.putString(UID, uid);
                        editor.putString(CID, cid);
                        editor.putString(PENDING_REPLYS, "");
                        editor.putString(REPLYTOTALNUM, "0");
                        editor.putString(USER_NAME, name);
                        editor.putString(BLACK_LIST, "");
                        editor.apply();
                        MyApp app = (MyApp) getActivity().getApplication();
                        app.addToUserList(uid, cid, name, "", 0, "");

                        PhoneConfiguration.getInstance().setUid(uid);
                        PhoneConfiguration.getInstance().setCid(cid);
                        PhoneConfiguration.getInstance().userName = name;
                        PhoneConfiguration.getInstance().setReplyString("");
                        PhoneConfiguration.getInstance().setReplyTotalNum(0);
                        PhoneConfiguration.getInstance().blacklist = StringUtil
                                .blackliststringtolisttohashset("");

                        LoginFragment.this.dismiss();
                        // startActivity(intent);
                        super.onPostExecute(result);
                    } else {
                        if (toast != null) {
                            toast.setText(R.string.login_failed);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            toast = Toast.makeText(getActivity(),
                                    R.string.login_failed, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
            }

        }

    }

}
