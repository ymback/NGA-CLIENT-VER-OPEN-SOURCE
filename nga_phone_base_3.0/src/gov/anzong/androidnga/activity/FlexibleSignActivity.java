package gov.anzong.androidnga.activity;

import gov.anzong.androidnga.R;
import sp.phone.adapter.ActionBarUserListAdapter;
import sp.phone.adapter.SpinnerUserListAdapter;
import sp.phone.bean.SignData;
import sp.phone.bean.User;
import sp.phone.fragment.SignContainer;
import sp.phone.interfaces.OnChildFragmentRemovedListener;
import sp.phone.interfaces.OnSignPageLoadFinishedListener;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.ThemeManager;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class FlexibleSignActivity extends SwipeBackAppCompatActivity implements
		OnSignPageLoadFinishedListener,
		OnChildFragmentRemovedListener,PullToRefreshAttacherOnwer {

	private String TAG = FlexibleTopicListActivity.class.getSimpleName();
	boolean dualScreen = true;
	int flags = 7;
	private Spinner userList;
	ArrayAdapter<String> categoryAdapter;
	private PullToRefreshAttacher mPullToRefreshAttacher;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		this.setContentView(R.layout.sign_activity);// OK
		if (null == findViewById(R.id.item_mission_container)) {
			dualScreen = false;
		}// ok

		PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this, options);
		FragmentManager fm = getSupportFragmentManager();
		Fragment f1 = fm.findFragmentById(R.id.sign_list);// ok
		if (f1 == null) {
			f1 = new SignContainer();
			Bundle args = new Bundle();// (getIntent().getExtras());
			if (null != getIntent().getExtras()) {
				args.putAll(getIntent().getExtras());
			}
			args.putString("url", getIntent().getDataString());
			f1.setArguments(args);
			FragmentTransaction ft = fm.beginTransaction().add(R.id.sign_list,
					f1);
			// .add(R.id.item_detail_container, f);
			ft.commit();
		}// 生成左边
		Fragment f2 = fm.findFragmentById(R.id.item_mission_container);
		if (null == f2) {
			f1.setHasOptionsMenu(true);
		} else if (!dualScreen) {
			this.setTitle(R.string.app_name);
			fm.beginTransaction().remove(f2).commit();
			f1.setHasOptionsMenu(true);
		} else {
			f1.setHasOptionsMenu(false);
			f2.setHasOptionsMenu(true);
		}
		setNavigation();
		
		// 右边有货就menuicon右边的

		/**/
		// int favor = getIntent().getIntExtra("favor", 0);
		// int authorid = getIntent().getIntExtra("authorid", 0);
		// if(favor ==0 && authorid ==0)
		// {
		// setNavigation();
		// }
		// else
		// {
		// flags = ThemeManager.ACTION_BAR_FLAG;
		// }//不搭嘎的东西,这边找死才去弄收藏推荐呢

		userList = (Spinner) findViewById(R.id.user_list);
		
		
		if (userList != null) {
			SpinnerUserListAdapter adapter = new SpinnerUserListAdapter(this);
			userList.setAdapter(adapter);
			userList.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					User u = (User) parent.getItemAtPosition(position);
					MyApp app = (MyApp) getApplication();
					app.addToUserList(u.getUserId(), u.getCid(),
							u.getNickName());
					PhoneConfiguration.getInstance().setUid(u.getUserId());
					PhoneConfiguration.getInstance().setCid(u.getCid());
					PhoneConfiguration.getInstance().setNickname(u.getNickName());

				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}

			});
		}else{
			this.setNavigation();
		}
	}

	@TargetApi(11)
	private void setNavigation() {
		 ActionBar actionBar = getSupportActionBar();
		 actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		 final SpinnerUserListAdapter categoryAdapter = new ActionBarUserListAdapter(this);
		
		 OnNavigationListener callback = new OnNavigationListener(){

				@Override
			public boolean onNavigationItemSelected(int itemPosition,
					long itemId) {
					User u = (User)categoryAdapter.getItem(itemPosition);
					MyApp app = (MyApp) getApplication();
					app.addToUserList(u.getUserId(), u.getCid(),
							u.getNickName());
					PhoneConfiguration.getInstance().setUid(u.getUserId());
					PhoneConfiguration.getInstance().setCid(u.getCid());
					PhoneConfiguration.getInstance().setNickname(u.getNickName());
					SignContainer f1 = (SignContainer) getSupportFragmentManager().findFragmentById(R.id.sign_list);
					if (f1 != null) {
						f1.onCategoryChanged(itemPosition);
					}
				return true;
			}

		};
		actionBar.setListNavigationCallbacks(categoryAdapter, callback);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		return false;// super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();// 关闭activity
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public PullToRefreshAttacher getAttacher() {
		return mPullToRefreshAttacher;
	}
	@Override
	protected void onResume() {
		int orentation = ThemeManager.getInstance().screenOrentation;
		if (orentation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
				|| orentation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(orentation);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}

		View view = findViewById(R.id.item_list);
		if (PhoneConfiguration.getInstance().fullscreen) {
			ActivityUtil.getInstance().setFullScreen(view);
		}
		super.onResume();
	}

	@Override
	public void OnChildFragmentRemoved(int id) {
		if (id == R.id.item_mission_container) {
			FragmentManager fm = getSupportFragmentManager();
			Fragment f1 = fm.findFragmentById(R.id.sign_list);
			f1.setHasOptionsMenu(true);
			setTitle(R.string.app_name);
		}

	}// 竖屏变横屏就干这个

	// @Override
	// public void jsonfinishLoad(SignData data) {
	// // TODO Auto-generated method stub
	//
	// Fragment SignMissionContainer = getSupportFragmentManager()
	// .findFragmentById(R.id.item_mission_container);
	//
	// OnSignPageLoadFinishedListener listener = null;
	// try{
	// listener = (OnSignPageLoadFinishedListener)SignMissionContainer;
	// if(listener != null){
	// listener.finishLoad(data);
	// }
	// }catch(ClassCastException e){
	// Log.e(TAG ,
	// "detailContainer should implements OnThreadPageLoadFinishedListener");
	// }
	// }//解析JSON

	@Override
	public void jsonfinishLoad(SignData result) {// 给左边SIGN信息用的
		Fragment SignContainer = getSupportFragmentManager().findFragmentById(
				R.id.sign_list);

		OnSignPageLoadFinishedListener listener = null;
		try {
			listener = (OnSignPageLoadFinishedListener) SignContainer;
			if (listener != null)
				listener.jsonfinishLoad(result);
		} catch (ClassCastException e) {
			Log.e(TAG, "topicContainer should implements "
					+ OnSignPageLoadFinishedListener.class.getCanonicalName());
		}
	}

//	@Override
//	public void finishLoad(MissionData data) {
//		Fragment missionContainer = getSupportFragmentManager()
//				.findFragmentById(R.id.item_mission_container);
//
//		OnMissionLoadFinishedListener listener = null;
//		try {
//			listener = (OnMissionLoadFinishedListener) missionContainer;
//			if (listener != null) {
//				listener.finishLoad(data);
//			}
//		} catch (ClassCastException e) {
//			Log.e(TAG,
//					"detailContainer should implements OnThreadPageLoadFinishedListener");
//		}
//	}
}
