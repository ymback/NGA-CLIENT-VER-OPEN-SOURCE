package sp.phone.fragment;

import gov.anzong.androidnga.activity.MyApp;
import gov.anzong.androidnga.R;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import sp.phone.adapter.UserListAdapter;
import sp.phone.bean.PerferenceConstant;
import sp.phone.forumoperation.HttpPostClient;
import sp.phone.utils.PhoneConfiguration;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.widget.ListView;
import android.widget.Toast;

public class LoginFragment extends DialogFragment implements
PerferenceConstant{

	private View view;
	
	EditText userText;
	EditText passwordText;
	ListView userList ;

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
		Button button_login = (Button) view.findViewById(R.id.login_button);
		userText = (EditText) view.findViewById(R.id.login_user_edittext);
		passwordText = (EditText) view.findViewById(R.id.login_password_edittext);
		userList = (ListView) view.findViewById(R.id.user_list);
		userList.setAdapter(new UserListAdapter(getActivity()));
		
		String postUrl = "http://account.178.com/q_account.php?_act=login";


		String userName = PhoneConfiguration.getInstance().userName;
		if (userName != "")
			userText.setText(userName);

		LoginButtonListener listener = new LoginButtonListener(postUrl);
		button_login.setOnClickListener(listener);
		this.getDialog().setTitle(R.string.login);
		super.onViewCreated(view, savedInstanceState);
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
			// TODO Auto-generated method stub
			StringBuffer bodyBuffer = new StringBuffer();
			bodyBuffer.append("type=username&email=");
			
			try {
				bodyBuffer.append(URLEncoder.encode(userText.getText().toString(),"utf-8"));
				bodyBuffer.append("&password=");
				bodyBuffer.append(URLEncoder.encode(passwordText.getText()
						.toString(),"utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			new LoginTask(v).execute(loginUrl,bodyBuffer.toString());
			

		}


		private class LoginTask extends AsyncTask<String, Integer,  Boolean>{
			final View v;
			private String uid=null;
			private String cid=null;
			public LoginTask(View v) {
				super();
				this.v = v;
			}
			@Override
			protected  Boolean doInBackground(String... params) {
				String url = params[0];
				String body =params[1];
				HttpURLConnection conn = new HttpPostClient(url).post_body(body);
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
					Log.d(LOG_TAG, conn.getHeaderFieldKey(i) + ":"
							+ conn.getHeaderField(i));
					if (key.equalsIgnoreCase("set-cookie")) {
						cookieVal = conn.getHeaderField(i);
						cookieVal = cookieVal.substring(0, cookieVal.indexOf(';'));
						if (cookieVal.indexOf("_sid=") == 0)
							cid = cookieVal.substring(5);
						if (cookieVal.indexOf("_178c=") == 0)
							uid = cookieVal.substring(6, cookieVal.indexOf('%'));

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
			protected void onPostExecute( Boolean result) {
				if(result.booleanValue()){
				Toast.makeText(v.getContext(), R.string.login_successfully,
						Toast.LENGTH_LONG).show();
				/*Intent intent = new Intent();
				intent.setClass(v.getContext(), MainActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
				SharedPreferences share = getActivity()
						.getSharedPreferences(PERFERENCE, Context.MODE_PRIVATE);
				Editor editor = share.edit();
				editor.putString(UID, uid);
				editor.putString(CID, cid);
				final String name  = userText.getText().toString();
				editor.putString(USER_NAME, name );
				editor.commit();
				MyApp app = (MyApp) getActivity().getApplication();
				app.addToUserList(uid, cid, name);
				
				PhoneConfiguration.getInstance().setUid(uid);
				PhoneConfiguration.getInstance().setCid(cid);
				PhoneConfiguration.getInstance().userName = name;
				
				LoginFragment.this.dismiss();
				//startActivity(intent);
				super.onPostExecute(result);
				}else{
					Toast.makeText(v.getContext(), R.string.login_failed,
							Toast.LENGTH_LONG).show();
				}
			}
			
			
		}

	}

}
