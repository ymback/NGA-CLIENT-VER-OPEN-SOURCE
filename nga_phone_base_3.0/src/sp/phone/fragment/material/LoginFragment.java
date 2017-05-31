package sp.phone.fragment.material;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.MainActivity;
import gov.anzong.androidnga.activity.MyApp;
import sp.phone.adapter.UserRecycleListAdapter;
import sp.phone.bean.PreferenceConstant;
import sp.phone.forumoperation.HttpPostClient;
import sp.phone.interfaces.OnAuthcodeLoadFinishedListener;
import sp.phone.task.AccountAuthcodeImageReloadTask;
import sp.phone.utils.StringUtil;

public class LoginFragment extends MaterialCompatFragment implements OnAuthcodeLoadFinishedListener, View.OnClickListener {

    private EditText mPasswordView;

    private EditText mUserNameView;

    private EditText mAuthCodeView;

    private ImageView mAuthCodeImg;

    private AccountAuthcodeImageReloadTask mLoadAuthCodeTask;

    private String mAuthCodeCookie;

    private String mAction;

    private boolean mLogined;

    private boolean mLoading;

    private String mUserName;

    private String mTid;
    private int mFid;
    private boolean mNeedToPost;
    private String mPrefix;
    private String mTo;
    private String mPid;
    private String mTitle;
    private int mMid;

    private RecyclerView mListView;

    private static final Object COMMIT_LOCK = new Object();

    private static final String POST_URL = "http://account.178.com/q_account.php?_act=login&print=login";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentInfo();
    }

    private void getIntentInfo() {
        Intent intent = getActivity().getIntent();
        mAction = intent.getStringExtra("action");
        String messageMode = intent.getStringExtra("messagemode");
        if (!StringUtil.isEmpty(mAction)) {
            showToast("你需要登录才能进行下一步操作");
            if (mAction.equals("search")) {
                mFid = intent.getIntExtra("fid", -7);
                mNeedToPost = true;
            }
            if (StringUtil.isEmpty(messageMode)) {
                if (mAction.equals("new") || mAction.equals("reply")
                        || mAction.equals("modify")) {
                    mNeedToPost = true;
                    mPrefix = intent.getStringExtra("prefix");
                    mTid = intent.getStringExtra("tid");
                    mFid = intent.getIntExtra("fid", -7);
                    mTitle = intent.getStringExtra("title");
                    mPid = intent.getStringExtra("pid");
                }
            } else {
                if (mAction.equals("new") || mAction.equals("reply")) {
                    mNeedToPost = true;
                    mTo = intent.getStringExtra("to");
                    mTitle = intent.getStringExtra("title");
                    mMid = intent.getIntExtra("mid", 0);
                }
            }
        }
    }

    @Override
    public View onCreateContainerView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        mPasswordView = (EditText) rootView.findViewById(R.id.login_password_edittext);
        mUserNameView = (EditText) rootView.findViewById(R.id.login_user_edittext);
        mAuthCodeView = (EditText) rootView.findViewById(R.id.login_authcode_edittext);
        mAuthCodeImg = (ImageView) rootView.findViewById(R.id.authcode_img);
        mAuthCodeImg.setOnClickListener(this);
        rootView.findViewById(R.id.login_button).setOnClickListener(this);
        RecyclerView listView = (RecyclerView) rootView.findViewById(R.id.user_list);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.setAdapter(new UserRecycleListAdapter(getContext(),this,listView));
        reloadAuthCode();
        return rootView;
    }

    @Override
    public void onResume() {
        if (mLogined && mNeedToPost) {
            getActivity().finish();
        }
        super.onResume();
    }

    @Override
    public void authcodefinishLoad(Bitmap authimg, String authcode) {
        mAuthCodeCookie = authcode;
        mAuthCodeImg.setImageBitmap(authimg);
    }

    @Override
    public void authcodefinishLoadError() {
        showToast("载入验证码失败，请点击刷新重新加载");
        mAuthCodeImg.setImageResource(R.drawable.q_vcode);
        mAuthCodeCookie = "";
        mAuthCodeView.setText("");
        mAuthCodeView.setSelected(true);
    }

    private void reloadAuthCode() {
        mAuthCodeCookie = "";
        mAuthCodeView.setText("");
        if (mLoadAuthCodeTask != null) {
            mLoadAuthCodeTask.cancel(true);
        }
        mAuthCodeImg.setImageResource(R.drawable.q_vcode);
        mLoadAuthCodeTask = new AccountAuthcodeImageReloadTask(getContext(), this);
        mLoadAuthCodeTask.execute();
    }

    private void reloadAuthCode(String error) {
        if (!StringUtil.isEmpty(error)) {
            showToast(error);
        }
        reloadAuthCode();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.authcode_img:
                reloadAuthCode();
                break;
            case R.id.login_button:
                login();
                break;
            case R.id.user_name:
                mUserNameView.setText(((TextView) v).getText());
                mUserNameView.selectAll();
                break;
        }
    }

    private void login() {
        synchronized (COMMIT_LOCK) {
            if (mLoading) {
                showToast(R.string.avoidWindfury);
                return;
            } else {
                StringBuilder bodyBuffer = new StringBuilder();
                bodyBuffer.append("email=");
                if (StringUtil.isEmpty(mAuthCodeCookie)) {
                    showToast("验证码信息错误，请重试");
                    reloadAuthCode();
                    return;
                }
                mUserName = mUserNameView.getText().toString();
                if (StringUtil.isEmpty(mUserName) ||
                        StringUtil.isEmpty(mPasswordView.getText().toString()) ||
                        StringUtil.isEmpty(mAuthCodeView.getText().toString())) {
                    showToast("内容缺少，请检查后再试");
                    reloadAuthCode();
                    return;
                }
                try {
                    bodyBuffer.append(URLEncoder.encode(mUserNameView.getText()
                            .toString(), "utf-8"));
                    bodyBuffer.append("&password=");
                    bodyBuffer.append(URLEncoder.encode(mPasswordView
                            .getText().toString(), "utf-8"));
                    bodyBuffer.append("&vcode=");
                    bodyBuffer.append(URLEncoder.encode(mAuthCodeView
                            .getText().toString(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                new LoginTask().execute(POST_URL, bodyBuffer.toString());

            }
            mLoading = true;
        }
    }


    private class LoginTask extends AsyncTask<String, Integer, Boolean> {
        private String uid = null;
        private String cid = null;
        private String errorstr = "";

        private final String LOG_TAG = LoginTask.class.getSimpleName();


        @Override
        protected Boolean doInBackground(String... params) {
            String url = params[0];
            String body = params[1];
            String cookie = "reg_vcode=" +mAuthCodeCookie;
            HttpURLConnection conn = new HttpPostClient(url, cookie).post_body(body);
            return validate(conn);

        }

        private boolean validate(HttpURLConnection conn) {
            if (conn == null)
                return false;

            String cookieVal;
            String key;

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
                        }
                        if (re301location.indexOf("e_login") > 0) {
                            errorstr = ("用户名或密码错误");
                        }
                        errorstr = "未知错误";
                        return false;
                    }
                }
                if (key.equalsIgnoreCase("set-cookie")) {
                    cookieVal = conn.getHeaderField(i);
                    cookieVal = cookieVal.substring(0,
                            cookieVal.indexOf(';'));
                    if (cookieVal.indexOf("_sid=") == 0)
                        cid = cookieVal.substring(5);
                    if (cookieVal.indexOf("_178c=") == 0) {
                        uid = cookieVal.substring(6, cookieVal.indexOf('%'));
                        if (StringUtil.isEmail(mUserName)) {
                            try {
                                String nametmp = cookieVal
                                        .substring(cookieVal.indexOf("%23") + 3);
                                nametmp = URLDecoder.decode(nametmp,
                                        "utf-8");
                                String[] stemp = nametmp.split("#");
                                for (int ia = 0; ia < stemp.length; ia++) {
                                    if (!StringUtil.isEmail(stemp[ia])) {
                                        mUserName = stemp[ia];
                                        ia = stemp.length;
                                    }
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
                if (key.equalsIgnoreCase("Location")) {
                    location = conn.getHeaderField(i);

                }
            }
            if (cid != "" && uid != ""
                    && location.contains("login_success&error=0")) {
                this.uid = uid;
                this.cid = cid;
                Log.i(LOG_TAG, "uid =" + uid + ",csid=" + cid);
                return true;
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            synchronized (COMMIT_LOCK) {
                mLoading = false;
            }
            if (!StringUtil.isEmpty(errorstr)) {
                reloadAuthCode(errorstr);
                super.onPostExecute(result);
            } else {
                if (result) {
                    showToast(R.string.login_successfully);
                    SharedPreferences share = getContext()
                            .getSharedPreferences(PreferenceConstant.PERFERENCE,
                                    Context.MODE_MULTI_PROCESS);
                    SharedPreferences.Editor editor = share.edit().putString(PreferenceConstant.UID, uid)
                            .putString(PreferenceConstant.CID, cid).putString(PreferenceConstant.PENDING_REPLYS, "")
                            .putString(PreferenceConstant.REPLYTOTALNUM, "0")
                            .putString(PreferenceConstant.USER_NAME, mUserName)
                            .putString(PreferenceConstant.BLACK_LIST, "");
                    editor.apply();
                    MyApp app = (MyApp) getActivity().getApplication();
                    app.addToUserList(uid, cid, mUserName, "", 0, "");

                    mConfiguration.setUid(uid);
                    mConfiguration.setCid(cid);
                    mConfiguration.userName = mUserName;
                    mConfiguration.setReplyTotalNum(0);
                    mConfiguration.setReplyString("");
                    mConfiguration.blacklist = StringUtil
                            .blackliststringtolisttohashset("");
                    mLogined = true;
                    Intent intent = new Intent();
                    if (mNeedToPost) {
                        if (StringUtil.isEmpty(mTo)) {
                            if (mAction.equals("search")) {
                                intent.putExtra("fid", mFid);
                                intent.putExtra("searchmode", "true");
                                intent.setClass(
                                        getContext(),
                                        mConfiguration.topicActivityClass);
                                startActivity(intent);
                            } else {
                                 if (mAction.equals("new")) {
                                    intent.putExtra("fid", mFid);
                                    intent.putExtra("action", "new");
                                } else if (mAction.equals("reply")) {
                                    intent.putExtra("prefix", "");
                                    intent.putExtra("tid", mTid);
                                    intent.putExtra("action", "reply");
                                } else if (mAction.equals("modify")) {
                                    intent.putExtra("prefix", mPrefix);
                                    intent.putExtra("tid", mTid);
                                    intent.putExtra("pid", mPid);
                                    intent.putExtra("title", mTitle);
                                    intent.putExtra("action", "modify");
                                }
                                intent.setClass(
                                        getContext(),
                                        mConfiguration.postActivityClass);
                                startActivity(intent);
                            }
                        } else {
                            if (mTo.equals(mUserName)) {
                                showToast(R.string.not_to_send_to_self);
                                getActivity().finish();
                            } else {
                                if (mAction.equals("new")) {
                                    intent.putExtra("to", mTo);
                                    intent.putExtra("action", "new");
                                } else if (mAction.equals("reply")) {
                                    intent.putExtra("mid", mMid);
                                    intent.putExtra("title", mTitle);
                                    intent.putExtra("to", mTo);
                                    intent.putExtra("action", "reply");
                                }
                                intent.setClass(
                                        getContext(),
                                        mConfiguration.messagePostActivityClass);
                                startActivity(intent);
                            }
                        }
                    } else {
                        intent.setClass(getContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                } else {
                    showToast(R.string.login_failed);
                }
                super.onPostExecute(result);
            }
        }


    }
}
