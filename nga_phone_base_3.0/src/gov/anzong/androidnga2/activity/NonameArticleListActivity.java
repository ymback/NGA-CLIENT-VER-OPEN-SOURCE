package gov.anzong.androidnga2.activity;

import noname.gson.parse.NonameReadResponse;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TabHost;
import gov.anzong.androidnga2.R;
import sp.phone.adapter.TabsAdapter;
import sp.phone.adapter.ThreadFragmentAdapter;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.ThreadData;
import sp.phone.fragment.AlertDialogFragment;
import sp.phone.fragment.ArticleListFragment;
import sp.phone.fragment.GotoDialogFragment;
import sp.phone.fragment.NonameArticleListFragment;
import sp.phone.interfaces.OnNonameThreadPageLoadFinishedListener;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.interfaces.PagerOwnner;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.interfaces.ResetableArticle;
import sp.phone.task.BookmarkTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;

public class NonameArticleListActivity extends SwipeBackAppCompatActivity
		implements PagerOwnner, OnNonameThreadPageLoadFinishedListener,
		PullToRefreshAttacherOnwer, PerferenceConstant {
	TabHost tabhost;
	ViewPager mViewPager;
	ThreadFragmentAdapter mTabsAdapter;
	int tid;
	String title;
	int nightmode;
	private static final String TAG = "ArticleListActivity";
	private static final String GOTO_TAG = "goto";
	private PullToRefreshAttacher mPullToRefreshAttacher;
	final private String ALERT_DIALOG_TAG = "alertdialog";

	PullToRefreshAttacher attacher = null;

	protected int getViewId() {
		return R.layout.pagerview_article_list;
		// return R.layout.article_viewpager;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getViewId());

		if (PhoneConfiguration.getInstance().uploadLocation
				&& PhoneConfiguration.getInstance().location == null) {
			ActivityUtil.reflushLocation(this);
		}
		nightmode = ThemeManager.getInstance().getMode();

		/*
		 * PullToRefreshViewPager refreshPager = (PullToRefreshViewPager)
		 * findViewById(R.id.pull_refresh_viewpager);
		 * refreshPager.setMode(Mode.PULL_FROM_START);
		 * refreshPager.setOnRefreshListener(new OnRefreshListener<ViewPager>(){
		 * 
		 * @Override public void onRefresh(PullToRefreshBase<ViewPager>
		 * refreshView) { finish();
		 * 
		 * }
		 * 
		 * });
		 * 
		 * mViewPager = refreshPager.getRefreshableView();
		 */

		mViewPager = (ViewPager) findViewById(R.id.pager);
		int pageFromUrl = 0;
		String url = this.getIntent().getDataString();
		if (null != url) {
			tid = this.getUrlParameter(url, "tid");
			pageFromUrl = this.getUrlParameter(url, "page");
		} else {
			tid = this.getIntent().getIntExtra("tid", 0);
		}

		tabhost = (TabHost) findViewById(android.R.id.tabhost);

		if (tabhost != null) {
			tabhost.setup();
			mTabsAdapter = new TabsAdapter(this, tabhost, mViewPager,
					NonameArticleListFragment.class);
		} else {
			mTabsAdapter = new ThreadFragmentAdapter(this,
					getSupportFragmentManager(), mViewPager,
					NonameArticleListFragment.class);
		}

		mTabsAdapter.setArgument("id", tid);

		if (savedInstanceState != null) {
			int pageCount = savedInstanceState.getInt("pageCount");
			if (pageCount != 0) {
				mTabsAdapter.setCount(pageCount);
				mViewPager.setCurrentItem(savedInstanceState.getInt("tab"));
			}

		} else if (0 != getUrlParameter(url, "page")) {

			mTabsAdapter.setCount(pageFromUrl);
			mViewPager.setCurrentItem(pageFromUrl);
		}
		try {
			PullToRefreshAttacherOnwer attacherOnwer = (PullToRefreshAttacherOnwer) this;
			attacher = attacherOnwer.getAttacher();

		} catch (ClassCastException e) {
			Log.e(TAG,
					"father activity should implement PullToRefreshAttacherOnwer");
		}

		PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
		options.refreshScrollDistance = 0.3f;
		options.refreshOnUp = true;
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this, options);
		try {
			PullToRefreshAttacherOnwer attacherOnwer = (PullToRefreshAttacherOnwer) this;
			attacher = attacherOnwer.getAttacher();

		} catch (ClassCastException e) {
			Log.e(TAG,
					"father activity should implement PullToRefreshAttacherOnwer");
		}

		if (PhoneConfiguration.getInstance().fullscreen) {
			refresh_saying();
		} else {
			ActivityUtil.getInstance().noticeSaying(this);
		}

	}

	private void refresh_saying() {
		DefaultHeaderTransformer transformer = null;

		if (attacher != null) {
			uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.HeaderTransformer headerTransformer;
			headerTransformer = attacher.getHeaderTransformer();
			if (headerTransformer != null
					&& headerTransformer instanceof DefaultHeaderTransformer)
				transformer = (DefaultHeaderTransformer) headerTransformer;
		} else {
		}

		if (transformer == null)
			ActivityUtil.getInstance().noticeSaying(this);
		else
			transformer.setRefreshingText(ActivityUtil.getSaying());
		if (attacher != null)
			attacher.setRefreshing(true);
	}

	private int getUrlParameter(String url, String paraName) {
		if (StringUtil.isEmpty(url)) {
			return 0;
		}
		final String pattern = paraName + "=";
		int start = url.indexOf(pattern);
		if (start == -1)
			return 0;
		start += pattern.length();
		int end = url.indexOf("&", start);
		if (end == -1)
			end = url.length();
		String value = url.substring(start, end);
		int ret = 0;
		try {
			ret = Integer.parseInt(value);
		} catch (Exception e) {
			Log.e(TAG, "invalid url:" + url);
		}

		return ret;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		outState.putInt("pageCount", mTabsAdapter.getCount());
		outState.putInt("tab", mViewPager.getCurrentItem());
		// outState.putInt("tid",tid);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if (PhoneConfiguration.getInstance().HandSide == 1) {// lefthand
			int flag = PhoneConfiguration.getInstance().getUiFlag();
			if (flag == 1 || flag == 3 || flag == 5 || flag == 7) {// 文章列表，UIFLAG为1或者1+2或者1+4或者1+2+4
				inflater.inflate(R.menu.nonamearticlelist_menu_left, menu);
			} else {
				inflater.inflate(R.menu.nonamearticlelist_menu, menu);
			}
		} else {
			inflater.inflate(R.menu.nonamearticlelist_menu, menu);
		}
		final int flags = ThemeManager.ACTION_BAR_FLAG;

		MenuItem lock = menu.findItem(R.id.article_menuitem_lock);
		int orentation = ThemeManager.getInstance().screenOrentation;
		if (orentation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
				|| orentation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			lock.setTitle(R.string.unlock_orientation);
			lock.setIcon(R.drawable.ic_menu_always_landscape_portrait);

		}

		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (menu.findItem(R.id.night_mode) != null) {
			if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
				menu.findItem(R.id.night_mode).setIcon(
						R.drawable.ic_action_brightness_high);
				menu.findItem(R.id.night_mode).setTitle(
						R.string.change_daily_mode);
			} else {
				menu.findItem(R.id.night_mode).setIcon(
						R.drawable.ic_action_bightness_low);
				menu.findItem(R.id.night_mode).setTitle(
						R.string.change_night_mode);
			}
		}
		// getSupportMenuInflater().inflate(R.menu.book_detail, menu);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch (item.getItemId()) {
		case R.id.article_menuitem_reply:
			// if(articleAdpater.getData() == null)
			// return false;
			String tid = String.valueOf(this.tid);
			intent.putExtra("prefix", "");
			intent.putExtra("tid", tid);
			intent.putExtra("action", "reply");
			if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// 登入了才能发
				intent.setClass(this,
						PhoneConfiguration.getInstance().nonamePostActivityClass);
			} else {
				intent.setClass(this,
						PhoneConfiguration.getInstance().loginActivityClass);
			}
			startActivity(intent);
			if (PhoneConfiguration.getInstance().showAnimation) {
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			}
			break;
		case R.id.article_menuitem_refresh:
			int current = mViewPager.getCurrentItem();
			if (PhoneConfiguration.getInstance().fullscreen) {
				refresh_saying();
			} else {
				ActivityUtil.getInstance().noticeSaying(this);
			}
			mViewPager.setAdapter(mTabsAdapter);
			mViewPager.setCurrentItem(current);

			break;
		case R.id.article_menuitem_lock:
			handleLockOrientation(item);
			break;
		case R.id.goto_floor:
			createGotoDialog();
			break;
		case R.id.night_mode:
			nightMode(item);
			break;
		case R.id.article_menuitem_back:
		default:
			finish();
			break;
		}
		return true;

	}

	private void handleLockOrientation(MenuItem item) {
		int preOrentation = ThemeManager.getInstance().screenOrentation;
		int newOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
		ImageButton compat_item = null;// getActionItem(R.id.actionbar_compat_item_lock);

		if (preOrentation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
				|| preOrentation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			// restore
			// int newOrientation = ActivityInfo.SCREEN_ORIENTATION_USER;
			ThemeManager.getInstance().screenOrentation = newOrientation;

			setRequestedOrientation(newOrientation);
			item.setTitle(R.string.lock_orientation);
			item.setIcon(R.drawable.ic_lock_screen);
			if (compat_item != null)
				compat_item.setImageResource(R.drawable.ic_lock_screen);

		} else {
			newOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			Display dis = getWindowManager().getDefaultDisplay();
			// Point p = new Point();
			// dis.getSize(p);
			if (dis.getWidth() < dis.getHeight()) {
				newOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			}

			ThemeManager.getInstance().screenOrentation = newOrientation;
			setRequestedOrientation(newOrientation);
			item.setTitle(R.string.unlock_orientation);
			item.setIcon(R.drawable.ic_menu_always_landscape_portrait);
			if (compat_item != null)
				compat_item
						.setImageResource(R.drawable.ic_menu_always_landscape_portrait);
		}

		SharedPreferences share = getSharedPreferences(PERFERENCE,
				MODE_MULTI_PROCESS);
		Editor editor = share.edit();
		editor.putInt(SCREEN_ORENTATION, newOrientation);
		editor.commit();

	}

	/*
	 * private ImageButton getActionItem(int id){ View actionbar_compat =
	 * findViewById(R.id.actionbar_compat); View ret = null; if(actionbar_compat
	 * != null) { ret = actionbar_compat.findViewById(id); } return
	 * (ImageButton) ret; }
	 */

	private void nightMode(final MenuItem menu) {

		String alertString = getString(R.string.change_nigmtmode_string);
		final AlertDialogFragment f = AlertDialogFragment.create(alertString);
		f.setOkListener(new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				ThemeManager tm = ThemeManager.getInstance();
				SharedPreferences share = getSharedPreferences(PERFERENCE,
						MODE_PRIVATE);
				int mode = ThemeManager.MODE_NORMAL;
				if (tm.getMode() == ThemeManager.MODE_NIGHT) {// 是晚上模式，改白天的
					menu.setIcon(R.drawable.ic_action_bightness_low);
					menu.setTitle(R.string.change_night_mode);
					Editor editor = share.edit();
					editor.putBoolean(NIGHT_MODE, false);
					editor.commit();
				} else {
					menu.setIcon(R.drawable.ic_action_brightness_high);
					menu.setTitle(R.string.change_daily_mode);
					Editor editor = share.edit();
					editor.putBoolean(NIGHT_MODE, true);
					editor.commit();
					mode = ThemeManager.MODE_NIGHT;
				}
				ThemeManager.getInstance().setMode(mode);
				Log.i(TAG, "acti");
				Intent intent = getIntent();
				overridePendingTransition(0, 0);
				finish();
				overridePendingTransition(0, 0);
				startActivity(intent);
			}

		});
		f.setCancleListener(new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				f.dismiss();
			}

		});
		f.show(getSupportFragmentManager(), ALERT_DIALOG_TAG);
	}

	private void createGotoDialog() {

		int count = mTabsAdapter.getCount();
		Bundle args = new Bundle();
		args.putInt("count", count);

		DialogFragment df = new GotoDialogFragment();
		df.setArguments(args);

		FragmentManager fm = getSupportFragmentManager();

		Fragment prev = fm.findFragmentByTag(GOTO_TAG);
		if (prev != null) {
			fm.beginTransaction().remove(prev).commit();
		}
		df.show(fm, GOTO_TAG);

	}

	@Override
	protected void onResume() {

		if (nightmode != ThemeManager.getInstance().getMode()) {
			Intent intent = getIntent();
			overridePendingTransition(0, 0);
			finish();
			overridePendingTransition(0, 0);
			startActivity(intent);
		} else {
			int orentation = ThemeManager.getInstance().screenOrentation;
			if (orentation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
					|| orentation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				setRequestedOrientation(orentation);
			} else {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			}
			if (PhoneConfiguration.getInstance().fullscreen) {
				ActivityUtil.getInstance().setFullScreen(mViewPager);
			}
		}
		super.onResume();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		return super.onContextItemSelected(item);
	}

	/*
	 * public ThreadFragmentAdapter getmTabsAdapter() { return mTabsAdapter; }
	 */

	@Override
	public int getCurrentPage() {

		return mViewPager.getCurrentItem() + 1;
	}

	@Override
	public void setCurrentItem(int index) {
		mViewPager.setCurrentItem(index);
	}

	@Override
	public PullToRefreshAttacher getAttacher() {
		// TODO Auto-generated method stub
		return mPullToRefreshAttacher;
	}

	@Override
	public void finishLoad(NonameReadResponse data) {
		// TODO Auto-generated method stub

		int exactCount = data.data.totalpage;
		Log.i(TAG, String.valueOf(exactCount));
		if (mTabsAdapter.getCount() != exactCount) {
			mTabsAdapter.setCount(exactCount);
		}

		title = data.data.title;
		if (!StringUtil.isEmpty(title)) {
			getSupportActionBar().setTitle(StringUtil.unEscapeHtml(title));
		} else {
			getSupportActionBar().setTitle("无题");

		}

		attacher.setRefreshComplete();
	}

}
