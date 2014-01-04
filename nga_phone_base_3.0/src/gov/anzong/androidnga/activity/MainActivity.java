package gov.anzong.androidnga.activity;

import gov.anzong.androidnga.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sp.phone.adapter.BoardPagerAdapter;
import sp.phone.bean.Board;
import sp.phone.bean.BoardCategory;
import sp.phone.bean.BoardHolder;
import sp.phone.bean.PerferenceConstant;
import sp.phone.fragment.LoginFragment;
import sp.phone.interfaces.PageCategoryOwnner;
import sp.phone.task.AppUpdateCheckTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import android.support.v7.app.ActionBarActivity;



public class MainActivity extends ActionBarActivity
	implements PerferenceConstant,OnItemClickListener,PageCategoryOwnner{
	static final String TAG = MainActivity.class.getSimpleName();
	ActivityUtil activityUtil =ActivityUtil.getInstance();
	private MyApp app;
	ViewPager pager;
	View view;
	AppUpdateCheckTask task = null;
	OnItemClickListener onItemClickListenerlistener = new EnterToplistLintener();


	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setTheme(R.style.AppTheme);
		Intent intent = getIntent();
		app = ((MyApp) getApplication());
		loadConfig(intent);
		initDate();
		initView();

		//task = new AppUpdateCheckTask(this);
		//task.execute("");

	}


	private void loadConfig(Intent intent) {
		//initUserInfo(intent);
		this.boardInfo = this.loadDefaultBoard();



	}


	@Override
	protected void onStop() {
		if(task != null){
			Log.d(TAG,"cancel update check task");
			task.cancel(true);
			task= null;
		}
		super.onStop();
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);

		final int flags = ThemeManager.ACTION_BAR_FLAG;
		/*
		int actionNum = ThemeManager.ACTION_IF_ROOM;//SHOW_AS_ACTION_IF_ROOM
		int i = 0;
		for(i = 0;i< menu.size();i++){
			ReflectionUtil.setShowAsAction(
					menu.getItem(i), actionNum);
		}
		*/
		//this.getSupportActionBar().setDisplayOptions(flags);
		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		

		return super.onCreateOptionsMenu(menu);
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		int orentation = ThemeManager.getInstance().screenOrentation;
		if(orentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				orentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		{
			if( getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
				setRequestedOrientation(orentation);
		}else{
			if(getRequestedOrientation() ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
					getRequestedOrientation() ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}

		int width = getResources().getInteger(R.integer.page_category_width);
		pager.setAdapter(
				new BoardPagerAdapter( getSupportFragmentManager(),this,width) );
		super.onResume();
	}

	public boolean isTablet() {
	    boolean xlarge = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 0x04);//Configuration.SCREENLAYOUT_SIZE_XLARGE);
	    boolean large = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
	    return (xlarge || large) && ActivityUtil.isGreaterThan_2_3_3();
	}
	private void jumpToLogin() {
		if(isTablet())
		{
			DialogFragment df = new LoginFragment();
			df.show(getSupportFragmentManager(), "login");
			return;
		}


		Intent intent = new Intent();
		intent.setClass(MainActivity.this, LoginActivity.class);
		try {
			startActivity(intent);
			if(PhoneConfiguration.getInstance().showAnimation)
			{
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			}
		} catch (Exception e) {

		}

	}

	private void jumpToSetting() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, SettingsActivity.class);
		try {
			startActivity(intent);
			if(PhoneConfiguration.getInstance().showAnimation)
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);

		} catch (Exception e) {


		}

	}

	void jumpToNearby(){
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, NearbyUserActivity.class);

			startActivity(intent);
			if(PhoneConfiguration.getInstance().showAnimation)
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);

	}


	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mainmenu_login:
			this.jumpToLogin();
			break;
		case R.id.mainmenu_setting:
			this.jumpToSetting();
			break;
		case R.id.mainmenu_exit:
			//case android.R.id.home: //this is a system id
			//this.finish();
			jumpToNearby();
			break;
		default:
			/*Intent MyIntent = new Intent(Intent.ACTION_MAIN);
			MyIntent.addCategory(Intent.CATEGORY_HOME);
			startActivity(MyIntent);*/
			finish();
			break;

		}
		return true;
	}

	private void initView() {

		setTitle(R.string.start_title);

		ThemeManager.SetContextTheme(this);
		view = LayoutInflater.from(this).inflate(R.layout.viewpager_main, null);
		view.setBackgroundResource(
			ThemeManager.getInstance().getBackgroundColor());
		setContentView(view);

		pager = (ViewPager) findViewById(R.id.pager);




		if(app.isNewVersion()){
			new AlertDialog.Builder(this).setTitle(R.string.prompt)
			.setMessage(StringUtil.getTips())
			.setPositiveButton(R.string.i_know, null).show();

			app.setNewVersion(false);

		}


	}






	private void initDate() {



		new Thread() {
			public void run() {





				File filebase = new File(HttpUtil.PATH);
				if (!filebase.exists()) {
					delay(getString(R.string.create_cache_dir));
					filebase.mkdirs();
				}
				if(ActivityUtil.isGreaterThan_2_1())
				{
					File f = new File(HttpUtil.PATH_AVATAR_OLD);
					if(f.exists()){
						f.renameTo(new File(HttpUtil.PATH_AVATAR));
						delay(getString(R.string.move_avatar));
					}
				}


				File file = new File(HttpUtil.PATH_NOMEDIA);
				if (!file.exists()) {
					Log.i(getClass().getSimpleName(),"create .nomedia");
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}


			}
		}.start();

	}

	/*public BoardCategory getCategory(int page){
		if(this.boardInfo == null)
			return null;
		return boardInfo.getCategory(page);
	}*/
	private BoardHolder loadDefaultBoard(){

		MyApp app = (MyApp) getApplication();
		return app.loadDefaultBoard();

	}


	private BoardHolder boardInfo;






	private void delay(String text) {
		final String msg = text;
		this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
			}

		});
	}









	class EnterToplistLintener implements OnItemClickListener , OnClickListener {
		int position;
		String fidString;

		public EnterToplistLintener(int position, String fidString) {
			super();
			this.position = position;
			this.fidString = fidString;
		}
		public EnterToplistLintener(){//constructoer
		}

		public void onClick(View v) {

			if (position != 0 && !HttpUtil.HOST_PORT.equals("")) {
				HttpUtil.HOST = HttpUtil.HOST_PORT + HttpUtil.Servlet_timer;
			}
			int fid = 0;
			try {
				fid = Integer.parseInt(fidString);
			} catch (Exception e) {
				final String tag = this.getClass().getSimpleName();
				Log.e(tag, Log.getStackTraceString(e));
				Log.e(tag, "invalid fid " + fidString);
			}
			if (fid == 0) {
				String tip = fidString + "坑爹一万年哈哈哈";//这边以前是乱码我不知道是干嘛的
				Toast.makeText(app, tip, Toast.LENGTH_LONG).show();
				return;
			}

			Log.i(this.getClass().getSimpleName(), "set host:" + HttpUtil.HOST);

			String url = HttpUtil.Server + "/thread.php?fid=" + fidString
					+ "&rss=1";
			PhoneConfiguration config = PhoneConfiguration.getInstance();
			if ( !StringUtil.isEmpty(config.getCookie())) {

				url = url + "&" + config.getCookie().replace("; ", "&");
			}else if(fid<0){
				jumpToLogin();
				return;
			}
			
			if(StringUtil.isEmpty(getSharedPreferences(PERFERENCE,
					Activity.MODE_PRIVATE).getString(RECENT_BOARD, ""))){
				Intent intenta = getIntent();
				loadConfig(intenta);
				initView();
			}
			addToRecent();
			if (!StringUtil.isEmpty(url)) {
				Intent intent = new Intent();
				intent.putExtra("tab", "1");
				intent.putExtra("fid", fid);
				intent.setClass(MainActivity.this, config.topicActivityClass);
				//intent.setClass(MainActivity.this, TopicListActivity.class);
				startActivity(intent);
				if(PhoneConfiguration.getInstance().showAnimation)
				{
					overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
				}
			}
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			position = arg2;
			fidString=(String) arg0.getItemAtPosition(position);
			onClick(arg1);
			
		}
		
		private void saveRecent(List<Board> boardList){
			String rescentStr = JSON.toJSONString(boardList);
			SharedPreferences share = getSharedPreferences(PERFERENCE,
					MODE_PRIVATE);
			Editor editor = share.edit();
			editor.putString(RECENT_BOARD, rescentStr);
			editor.commit();
		}
		
		private void addToRecent() {
			
			boolean recentAlreadExist = boardInfo.getCategoryName(0).equals(getString(R.string.recent));
			
			BoardCategory recent = boardInfo.getCategory(0);
			if(recent != null && recentAlreadExist)
				recent.remove(fidString);
			//int i = 0;
			for (int i = 0; i < boardInfo.getCategoryCount(); i++) {
				BoardCategory curr = boardInfo.getCategory(i);
				for (int j = 0; j < curr.size(); j++) {
					Board b = curr.get(j);
					if (b.getUrl().equals(fidString)) {
						Board b1 =new Board(0, b.getUrl(), b.getName(), b
								.getIcon());

						if(!recentAlreadExist){//删除后第一次会在这边出问题,因为滑动导致首页没写好
							List<Board> boardList = new ArrayList<Board>();
							boardList.add(b1);
							saveRecent(boardList);
							boardInfo = loadDefaultBoard();
							return;
						}else{
							recent.addFront(b1);
						}
						recent = boardInfo.getCategory(0);
						this.saveRecent(recent.getBoardList());

						return;
					}//if
				}//for j
				
			}//for i
			
		}
	}




	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		this.onItemClickListenerlistener.onItemClick(parent, view, position, id);
		
	}


	@Override
	public int getCategoryCount() {
		if(boardInfo == null)
			return 0;
		return boardInfo.getCategoryCount();
	}


	@Override
	public String getCategoryName(int position) {
		if(boardInfo == null)
			return "";
		return boardInfo.getCategoryName(position);
	}


	@Override
	public BoardCategory getCategory(int category) {
		if(boardInfo == null)
			return null;
		return boardInfo.getCategory(category);
	}

}


