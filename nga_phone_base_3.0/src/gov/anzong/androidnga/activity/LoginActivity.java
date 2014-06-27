package gov.anzong.androidnga.activity;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import gov.anzong.androidnga.R;
import sp.phone.adapter.UserListAdapter;
import sp.phone.bean.PerferenceConstant;
import sp.phone.forumoperation.HttpPostClient;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

public class LoginActivity extends SwipeBackAppCompatActivity implements
		PerferenceConstant {

	EditText userText;
	EditText passwordText;
	View view;
	ListView userList;
	private String action, messagemode;
	private String tid;
	private int fid;
	private boolean needtopost = false;
	private String prefix, to;
	private String pid;
	private String title;
	private int mid;
	private boolean alreadylogin = false;

	Object commit_lock = new Object();
	private boolean loading = false;
	private Toast toast = null;
	String name;

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

		view = LayoutInflater.from(this).inflate(R.layout.login, null);
		this.setContentView(view);
		this.setTitle("登录");
		Button button_login = (Button) findViewById(R.id.login_button);
		userText = (EditText) findViewById(R.id.login_user_edittext);
		passwordText = (EditText) findViewById(R.id.login_password_edittext);
		userList = (ListView) findViewById(R.id.user_list);
		userList.setAdapter(new UserListAdapter(this));

		String postUrl = "http://account.178.com/q_account.php?_act=login";

		String userName = PhoneConfiguration.getInstance().userName;
		if (userName != "") {
			userText.setText(userName);
			userText.selectAll();
		}

		LoginButtonListener listener = new LoginButtonListener(postUrl);
		button_login.setOnClickListener(listener);
		updateThemeUI();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			public void run() {
				InputMethodManager inputManager = (InputMethodManager) userText
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(userText, 0);
			}

		}, 500);
		Intent intent = this.getIntent();
		action = intent.getStringExtra("action");
		messagemode = intent.getStringExtra("messagemode");
		if (!StringUtil.isEmpty(action)) {
			if (toast != null) {
				toast.setText("你需要登录才能进行下一步操作");
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
			} else {
				toast = Toast.makeText(LoginActivity.this, "你需要登录才能进行下一步操作",
						Toast.LENGTH_SHORT);
				toast.show();
			}
			if(action.equals("search")){
				fid = intent.getIntExtra("fid", -7);
				needtopost = true;
			}
			if (StringUtil.isEmpty(messagemode)) {
				if (action.equals("new") || action.equals("reply")
						|| action.equals("modify")) {
					needtopost = true;
					prefix = intent.getStringExtra("prefix");
					tid = intent.getStringExtra("tid");
					fid = intent.getIntExtra("fid", -7);
					title = intent.getStringExtra("title");
					pid = intent.getStringExtra("pid");
				}
			} else {
				if (action.equals("new") || action.equals("reply")) {
					needtopost = true;
					to = intent.getStringExtra("to");
					title = intent.getStringExtra("title");
					mid = intent.getIntExtra("mid", 0);
				}
			}
		}
	}

	private void updateThemeUI() {
		ThemeManager tm = ThemeManager.getInstance();
		if (tm.getMode() == ThemeManager.MODE_NIGHT) {
			view.setBackgroundResource(ThemeManager.getInstance()
					.getBackgroundColor());
		} else {
			setbackgroundbitmap();
		}
	}

	private void setbackgroundbitmap() {
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { // 竖屏
			BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
			bfoOptions.inScaled = false;
			Bitmap bmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.login_background_shu, bfoOptions);
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int width = dm.widthPixels;
			int heigth = dm.heightPixels;
			double phoneheigthtoheigth = (double) heigth / width;
			int bitmapwidth = 640;
			int bitmapheigth = 1136;
			double bitmapheigthtoheigth = (double) bitmapheigth / bitmapwidth;
			;
			if (phoneheigthtoheigth > bitmapheigthtoheigth) {
				int backbitmapwidth = (int) (1136 / phoneheigthtoheigth);
				int cutwidth = (int) (640 - backbitmapwidth) / 2;
				bmp = Bitmap.createBitmap(bmp, cutwidth, 0, backbitmapwidth,
						bitmapheigth);
			} else if (phoneheigthtoheigth < bitmapheigthtoheigth) {
				int cutheigth = (int) (bitmapwidth * phoneheigthtoheigth);
				bmp = Bitmap.createBitmap(bmp, 0, 0, bitmapwidth, cutheigth);
			}
			BitmapDrawable bd = new BitmapDrawable(bmp);
			view.setBackgroundDrawable(bd);
		} else {
			BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
			bfoOptions.inScaled = false;
			Bitmap bmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.login_background_hen, bfoOptions);
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int width = dm.widthPixels;
			int heigth = dm.heightPixels;
			double phoneheigthtoheigth = (double) heigth / width;
			int bitmapwidth = 1920;
			int bitmapheigth = 1000;
			double bitmapheigthtoheigth = (double) bitmapheigth / bitmapwidth;
			;
			if (phoneheigthtoheigth > bitmapheigthtoheigth) {
				int backbitmapwidth = (int) (1000 / phoneheigthtoheigth);
				int cutwidth = (int) (1920 - backbitmapwidth) / 2;
				bmp = Bitmap.createBitmap(bmp, cutwidth, 0, backbitmapwidth,
						bitmapheigth);
			} else if (phoneheigthtoheigth < bitmapheigthtoheigth) {
				int cutheigth = (int) (bitmapwidth * phoneheigthtoheigth);
				bmp = Bitmap.createBitmap(bmp, 0, 0, bitmapwidth, cutheigth);
			}
			BitmapDrawable bd = new BitmapDrawable(bmp);
			view.setBackgroundDrawable(bd);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuInflater inflater = getMenuInflater();
		// inflater.inflate(R.menu.main_menu, menu);
		int flags = 15;/*
						 * ActionBar.DISPLAY_SHOW_HOME; flags |=
						 * ActionBar.DISPLAY_USE_LOGO; flags |=
						 * ActionBar.DISPLAY_SHOW_TITLE; flags |=
						 * ActionBar.DISPLAY_HOME_AS_UP; flags |=
						 * ActionBar.DISPLAY_SHOW_CUSTOM;
						 */
		// final ActionBar bar = getActionBar();
		// bar.setDisplayOptions(flags);
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
			// case android.R.id.home:
			// Intent intent = new Intent(this, MainActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivity(intent);
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	class LoginButtonListener implements OnClickListener {
		final private String loginUrl;

		public LoginButtonListener(String loginUrl) {
			super();
			this.loginUrl = loginUrl;
		}

		private final String LOG_TAG = LoginButtonListener.class
				.getSimpleName();

		@Override
		public void onClick(View v) {
			synchronized (commit_lock) {
				if (loading == true) {
					String avoidWindfury = LoginActivity.this
							.getString(R.string.avoidWindfury);
					if (toast != null) {
						toast.setText(avoidWindfury);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(LoginActivity.this,
								avoidWindfury, Toast.LENGTH_SHORT);
						toast.show();
					}
					return;
				} else {
					StringBuffer bodyBuffer = new StringBuffer();
					bodyBuffer.append("type=username&email=");
					name = userText.getText().toString();
					try {
						bodyBuffer.append(URLEncoder.encode(userText.getText()
								.toString(), "utf-8"));
						bodyBuffer.append("&password=");
						bodyBuffer.append(URLEncoder.encode(passwordText
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
			final View v;
			private String uid = null;
			private String cid = null;

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
				String cid = "";
				String location = "";

				for (int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++) {
					Log.d(LOG_TAG,
							conn.getHeaderFieldKey(i) + ":"
									+ conn.getHeaderField(i));
					if (key.equalsIgnoreCase("set-cookie")) {
						cookieVal = conn.getHeaderField(i);
						cookieVal = cookieVal.substring(0,
								cookieVal.indexOf(';'));
						if (cookieVal.indexOf("_sid=") == 0)
							cid = cookieVal.substring(5);
						if (cookieVal.indexOf("_178c=") == 0)
							uid = cookieVal
									.substring(6, cookieVal.indexOf('%'));

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
				if (result.booleanValue()) {
					if (toast != null) {
						toast.setText(R.string.login_successfully);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast
								.makeText(LoginActivity.this,
										R.string.login_successfully,
										Toast.LENGTH_SHORT);
						toast.show();
					}
					SharedPreferences share = LoginActivity.this
							.getSharedPreferences(PERFERENCE,
									MODE_MULTI_PROCESS);
					Editor editor = share.edit().putString(UID, uid)
							.putString(CID, cid).putString(PENDING_REPLYS, "")
							.putString(REPLYTOTALNUM, "0")
							.putString(USER_NAME, name)
							.putString(BLACK_LIST, "");
					editor.commit();
					MyApp app = (MyApp) LoginActivity.this.getApplication();
					app.addToUserList(uid, cid, name, "", 0,"");

					PhoneConfiguration.getInstance().setUid(uid);
					PhoneConfiguration.getInstance().setCid(cid);
					PhoneConfiguration.getInstance().userName = name;
					PhoneConfiguration.getInstance().setReplyTotalNum(0);
					PhoneConfiguration.getInstance().setReplyString("");
					PhoneConfiguration.getInstance().blacklist = StringUtil.blackliststringtolisttohashset("");
					alreadylogin = true;
					Intent intent = new Intent();
					if (needtopost) {
						if (StringUtil.isEmpty(to)) {
							if(action.equals("search")){
								intent.putExtra("fid", fid);
								intent.putExtra("searchmode", "true");
								intent.setClass(
										v.getContext(),
										PhoneConfiguration.getInstance().topicActivityClass);
								startActivity(intent);
							}else{
							if (action.equals("new")) {
								intent.putExtra("fid", fid);
								intent.putExtra("action", "new");
							} else if (action.equals("reply")) {
								intent.putExtra("prefix", "");
								intent.putExtra("tid", tid);
								intent.putExtra("action", "reply");
							} else if (action.equals("modify")) {
								intent.putExtra("prefix", prefix);
								intent.putExtra("tid", tid);
								intent.putExtra("pid", pid);
								intent.putExtra("title", title);
								intent.putExtra("action", "modify");
							}
							intent.setClass(
									v.getContext(),
									PhoneConfiguration.getInstance().postActivityClass);
							startActivity(intent);
							}
						} else {
							if(to.equals(name)){
								if (toast != null) {
									toast.setText(R.string.not_to_send_to_self);
									toast.setDuration(Toast.LENGTH_SHORT);
									toast.show();
								} else {
									toast = Toast.makeText(LoginActivity.this,
											R.string.not_to_send_to_self, Toast.LENGTH_SHORT);
									toast.show();
								}
								finish();
							}else{
								if (action.equals("new")) {
									intent.putExtra("to", to);
									intent.putExtra("action", "new");
								} else if (action.equals("reply")) {
									intent.putExtra("mid", mid);
									intent.putExtra("title", title);
									intent.putExtra("to", to);
									intent.putExtra("action", "reply");
								}
								intent.setClass(
										v.getContext(),
										PhoneConfiguration.getInstance().messagePostActivityClass);
								startActivity(intent);
							}
						}
					} else {
						intent.setClass(v.getContext(), MainActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
					super.onPostExecute(result);
				} else {
					if (toast != null) {
						toast.setText(R.string.login_failed);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(LoginActivity.this,
								R.string.login_failed, Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			}

		}

	}

}
