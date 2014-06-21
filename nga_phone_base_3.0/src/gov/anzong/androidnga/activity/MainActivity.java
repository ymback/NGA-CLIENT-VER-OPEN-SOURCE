package gov.anzong.androidnga.activity;

import gov.anzong.androidnga.R;
import io.vov.vitamio.LibsChecker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sp.phone.bean.User;
import sp.phone.adapter.BoardPagerAdapter;
import sp.phone.bean.AvatarTag;
import sp.phone.bean.Board;
import sp.phone.bean.BoardCategory;
import sp.phone.bean.BoardHolder;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.User;
import sp.phone.fragment.LoginFragment;
import sp.phone.fragment.ProfileSearchDialogFragment;
import sp.phone.fragment.TopiclistContainer;
import sp.phone.interfaces.PageCategoryOwnner;
import sp.phone.task.DeleteBookmarkTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.view.GestureDetector;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.alibaba.fastjson.JSON;
import com.readystatesoftware.viewbadger.BadgeView;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends ActionBarActivity implements
		PerferenceConstant, PageCategoryOwnner, OnItemClickListener {
	static final String TAG = MainActivity.class.getSimpleName();
	private ActivityUtil activityUtil = ActivityUtil.getInstance();
	private MyApp app;
	private List<Object> items = new ArrayList<Object>();
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private View headview;
	private MenuAdapter mAdapter;
	private int mActivePosition = 0;
	private BoardHolder boardInfo;
	private PageIndicator mIndicator;
	private ViewPager pager;
	private View view;
	private LinearLayout mLinearLayout;
	private boolean tabletloginfragmentshowed = false;
	private Toast toast = null;
	private ViewFlipper flipper;
	private int activedposition = 0;
	private SharedPreferences share;
	private int dragonballnum = 0;
	private MediaPlayer mp = new MediaPlayer();
	private Animation rightInAnimation;
	private Animation rightOutAnimation;
	private int secondstart;
	private int secondnow;
	private int menucishu = 0;
	private String fulimode = "0";
	private ThemeManager tm = ThemeManager.getInstance();
	private int ifRecentExist = 0;// ifRecentExist��menu item click right
	private OnItemClickListener onItemClickListenerlistener = new EnterToplistLintener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		this.setTheme(R.style.AppTheme);
		share = getSharedPreferences(PERFERENCE, Activity.MODE_PRIVATE);
		dragonballnum = Integer.parseInt(share.getString(DRAGON_BALL, "0"));
		Intent intent = getIntent();
		app = ((MyApp) getApplication());
		fulimode = share.getString(CAN_SHOW_FULI, "0");
		loadConfig(intent);
		initDate();
		setitem();
		initView();
		getSupportActionBar().setTitle(R.string.start_title);
		if (boardInfo.getCategoryName(0).equals("�������")) {
			setLocItem(7, "�������", R.drawable.ic_queue);
			if (boardInfo.getCategoryCount() > 12) {
				if (boardInfo.getCategoryName(12).equals("�û��Զ���")) {
					setLocItem(20, "�û��Զ���", R.drawable.ic_queue);
				}
			}
		} else {
			if (boardInfo.getCategoryCount() == 12) {
				setLocItem(19, "�û��Զ���", R.drawable.ic_queue);
			}
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		// mIndicator = (TabPageIndicator)findViewById(R.id.indicator);
		// mIndicator.setViewPager(mPager);

		view = LayoutInflater.from(this).inflate(R.layout.mainfragment, null);

		setContentView(view);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mLinearLayout = (LinearLayout) findViewById(R.id.drawer_linearlayout);

		LayoutInflater layoutInflater = getLayoutInflater();
		headview = layoutInflater.inflate(R.layout.maindrawer_viewlipper, null);
		mDrawerList.addHeaderView(headview, null, false);
		updatemDrawerList();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(R.string.start_title);
				if (ActivityUtil.isLessThan_3_0())
					supportInvalidateOptionsMenu();
				else
					invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				if (tabletloginfragmentshowed) {
					refreshheadview();
					tabletloginfragmentshowed = false;
				}
				getSupportActionBar().setTitle("����Ƭ��");
				if (ActivityUtil.isLessThan_3_0())
					supportInvalidateOptionsMenu();
				else
					invalidateOptionsMenu();
			}
		};
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		pager = (ViewPager) findViewById(R.id.pager);
		mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		if (app.isNewVersion()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.prompt).setMessage(StringUtil.getTips())
					.setPositiveButton(R.string.i_know, null);
			final AlertDialog dialog = builder.create();
			dialog.show();
			dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface arg0) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					if (PhoneConfiguration.getInstance().fullscreen) {
						ActivityUtil.getInstance().setFullScreen(view);
					}
				}

			});
			app.setNewVersion(false);

		}
	}

	public void updatemDrawerList() {
		mAdapter = new MenuAdapter(this, items);
		mDrawerList.setCacheColorHint(0x00000000);
		if (tm.getMode() == ThemeManager.MODE_NIGHT) {
			ColorDrawable sage = new ColorDrawable(mDrawerList.getResources()
					.getColor(R.color.night_link_color));
			mDrawerList.setDivider(sage);
			mDrawerList.setDividerHeight(1);
		} else {
			ColorDrawable sage = new ColorDrawable(mDrawerList.getResources()
					.getColor(R.color.white));
			mDrawerList.setDivider(sage);
			mDrawerList.setDividerHeight(1);
		}
		mDrawerList.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		if (mActivePosition <= mAdapter.getCount()) {
			mDrawerList.setSelection(mActivePosition);
		} else {
			mDrawerList.setSelection(mAdapter.getCount());
		}
		if (activedposition <= mAdapter.getCount() && activedposition >= 1) {
			mDrawerList.setItemChecked(activedposition, true);
		} else {
			activedposition = 0;
		}
	}

	public void updateView(int itemIndex) {
		Item target = (Item) mDrawerList.getItemAtPosition(itemIndex);
		int start = mDrawerList.getFirstVisiblePosition();
		for (int i = start, j = mDrawerList.getLastVisiblePosition(); i <= j; i++)
			if (target == mDrawerList.getItemAtPosition(i)) {
				View view = mDrawerList.getChildAt(i - start);
				mDrawerList.getAdapter().getView(i, view, mDrawerList);
				break;
			}
	}

	public void updatepager() {
		mLinearLayout.setBackgroundResource(ThemeManager.getInstance()
				.getBackgroundColor());
		int width = getResources().getInteger(R.integer.page_category_width);
		pager.setAdapter(new BoardPagerAdapter(getSupportFragmentManager(),
				this, width));
		mIndicator.setViewPager(pager);
		mIndicator.setCurrentItem(0);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mActivePosition = position;
			activedposition = position;
			selectItem(position, (Item) mAdapter.getItem(position - 1));
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void selectItem(int position, Item item) {
		//
		if (!boardInfo.getCategoryName(0).equals("�������")) {
			ifRecentExist = 1;
		}
		if (item.mTitle.equals("��¼�˺�")) {
			jumpToLogin();
		} else if (item.mTitle.equals("Yoooo~")) {
			jumpToNearby();
		} else if (item.mTitle.equals("�������")) {
			jumpToRecentReply();
		} else if (item.mTitle.equals("�������")) {
			pager.setCurrentItem(0 - ifRecentExist);
		} else if (item.mTitle.equals("ǩ������")) {
			signmission();
		} else if (item.mTitle.equals("����Ϣ")) {
			mymessage();
		}else if (item.mTitle.equals("������������")) {
			noname();
		} else if (item.mTitle.equals("�����û���Ϣ")) {
			search_profile();
		} else if (item.mTitle.equals("�ۺ�����")) {
			pager.setCurrentItem(1 - ifRecentExist);
		} else if (item.mTitle.equals("������ϵ��")) {
			pager.setCurrentItem(2 - ifRecentExist);
		} else if (item.mTitle.equals("ְҵ������")) {
			pager.setCurrentItem(3 - ifRecentExist);
		} else if (item.mTitle.equals("ð���ĵ�")) {
			pager.setCurrentItem(4 - ifRecentExist);
		} else if (item.mTitle.equals("�����֮��")) {
			pager.setCurrentItem(5 - ifRecentExist);
		} else if (item.mTitle.equals("ϵͳ��Ӳ������")) {
			pager.setCurrentItem(6 - ifRecentExist);
		} else if (item.mTitle.equals("������Ϸ")) {
			pager.setCurrentItem(7 - ifRecentExist);
		} else if (item.mTitle.equals("�����ƻ���")) {
			pager.setCurrentItem(8 - ifRecentExist);
		} else if (item.mTitle.equals("¯ʯ��˵")) {
			pager.setCurrentItem(9 - ifRecentExist);
		} else if (item.mTitle.equals("Ӣ������")) {
			pager.setCurrentItem(10 - ifRecentExist);
		} else if (item.mTitle.equals("���˰���")) {
			pager.setCurrentItem(11 - ifRecentExist);
		} else if (item.mTitle.equals("�û��Զ���")) {
			pager.setCurrentItem(12 - ifRecentExist);
		} else if (item.mTitle.equals("��������")) {
			jumpToSetting();
		} else if (item.mTitle.equals("��Ӱ���")) {
			add_fid_dialog();
		} else if (item.mTitle.equals("��URL��ȡ")) {
			useurltoactivity_dialog();
		} else if (item.mTitle.equals("����������")) {
			clear_recent_board();
		} else if (item.mTitle.equals("����")) {
			about_ngaclient();
		} else if (item.mTitle.equals("��Ҫ����~ߣ~")) {
			collect_dragon_ball();
		}
		mDrawerList.setItemChecked(position, true);
		if (!item.mTitle.equals("��Ҫ����~ߣ~")) {
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}

	private void search_profile() {

		Bundle arg = new Bundle();
		DialogFragment df = new ProfileSearchDialogFragment();
		df.setArguments(arg);
		final String dialogTag = "searchpaofile_dialog";
		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment prev = fm.findFragmentByTag(dialogTag);
		if (prev != null) {
			ft.remove(prev);
		}

		try {
			df.show(ft, dialogTag);
		} catch (Exception e) {
			Log.e(TopiclistContainer.class.getSimpleName(),
					Log.getStackTraceString(e));

		}
	}

	private void signmission() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		PhoneConfiguration config = PhoneConfiguration.getInstance();
		intent.setClass(MainActivity.this, config.signActivityClass);
		startActivity(intent);
		if (PhoneConfiguration.getInstance().showAnimation) {
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		}

	}
	

	private void mymessage() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		PhoneConfiguration config = PhoneConfiguration.getInstance();
		intent.setClass(MainActivity.this, config.messageActivityClass);
		
		startActivity(intent);
		if (PhoneConfiguration.getInstance().showAnimation) {
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		}

	}
	private void noname() {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		PhoneConfiguration config = PhoneConfiguration.getInstance();
		intent.setClass(MainActivity.this, config.nonameActivityClass);
		
		startActivity(intent);
		if (PhoneConfiguration.getInstance().showAnimation) {
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
		}

	}

	public void setLocItem(int loc, String itemname, int id) {
		// set item on loc position
		items.add(loc, new Item(itemname, id));
		// reset menu
		if (loc + 1 <= mActivePosition) {
			mActivePosition++;
		}
		if (loc + 1 <= activedposition) {
			activedposition++;
		}
		updatemDrawerList();
	}

	private void about_ngaclient() {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = getLayoutInflater();
		final View view = layoutInflater.inflate(R.layout.client_dialog, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setView(view);
		alert.setTitle("����");
		String versionName = null;
		TextView textview = (TextView) view
				.findViewById(R.id.client_device_dialog);
		try {
			PackageManager pm = MainActivity.this.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(
					MainActivity.this.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				versionName = pi.versionName == null ? "null" : pi.versionName;
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		String textviewtext = MainActivity.this
				.getString(R.string.about_client) + versionName;
		textview.setText(textviewtext);
		alert.setPositiveButton("֪����", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (PhoneConfiguration.getInstance().fullscreen) {
					ActivityUtil.getInstance().setFullScreen(view);
				}
			}
		});
		final AlertDialog dialog = alert.create();
		dialog.show();
		dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if (PhoneConfiguration.getInstance().fullscreen) {
					ActivityUtil.getInstance().setFullScreen(view);
				}
			}

		});
	}

	public void setitem() {
		items.add(new Item("��¼�˺�", R.drawable.ic_login));
		items.add(new Item("Yoooo~", R.drawable.ic_menu_mylocation));
		items.add(new Item("������������", R.drawable.ic_action_person_dark));
		items.add(new Item("ǩ������", R.drawable.ic_action_go_to_today));
		items.add(new Item("����Ϣ", R.drawable.ic_action_email));
		items.add(new Item("�����û���Ϣ", R.drawable.action_search));
		items.add(new Item("�������", R.drawable.ic_action_gun));
		// items.add(new Item("�������", R.drawable.ic_queue));
		items.add(new Category("������̳"));
		items.add(new Item("�ۺ�����", R.drawable.ic_queue));
		items.add(new Item("������ϵ��", R.drawable.ic_queue));
		items.add(new Item("ְҵ������", R.drawable.ic_queue));
		items.add(new Item("ð���ĵ�", R.drawable.ic_queue));
		items.add(new Item("�����֮��", R.drawable.ic_queue));
		items.add(new Item("ϵͳ��Ӳ������", R.drawable.ic_queue));
		items.add(new Item("������Ϸ", R.drawable.ic_queue));
		items.add(new Item("�����ƻ���", R.drawable.ic_queue));
		items.add(new Item("¯ʯ��˵", R.drawable.ic_queue));
		items.add(new Item("Ӣ������", R.drawable.ic_queue));
		items.add(new Item("���˰���", R.drawable.ic_queue));
		items.add(new Category("����"));
		items.add(new Item("��������", R.drawable.action_settings));
		items.add(new Item("��Ӱ���", R.drawable.ic_action_add_to_queue));
		items.add(new Item("��URL��ȡ", R.drawable.ic_action_forward));
		items.add(new Item("����������", R.drawable.ic_action_warning));
		items.add(new Item("����", R.drawable.ic_action_about));
		if (!fulimode.equals("0")) {
			items.add(new Item("��Ҫ����~ߣ~", R.drawable.ic_action_dragon_ball));
		}
	}

	void updateflipper(String userListString) {
		flipper.removeAllViews();
		BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
		bfoOptions.inScaled = false;
		Bitmap bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.userdrawback, bfoOptions);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		width = (int) (width * 0.8);
		if (width < 800) {
			bmp = Bitmap.createBitmap(bmp, (int) 400 - width / 2, 0, width, 55);
		}
		BitmapDrawable bd = new BitmapDrawable(bmp);
		List<User> userList;
		if (StringUtil.isEmpty(userListString)) {
			flipper.addView(getUserView(null, 0, bd));// ���ݻ�һ��δ�����
		} else {
			userList = JSON.parseArray(userListString, User.class);
			if (userList.size() == 0) {
				flipper.addView(getUserView(null, 0, bd));// ���ݻ�һ��δ�����
			} else {
				for (int i = 1; i <= userList.size(); i++) {
					flipper.addView(getUserView(userList, i - 1, bd));// ���ݻ�һ��δ�����
				}
			}
		}

		// ����Ч��
		rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.right_in);
		rightOutAnimation = AnimationUtils
				.loadAnimation(this, R.anim.right_out);
	}

	public View getUserView(List<User> userList, int position, BitmapDrawable bd) {
		View privateview = getLayoutInflater().inflate(
				R.layout.drawerloginuser, null);
		RelativeLayout mRelativeLayout = (RelativeLayout) privateview
				.findViewById(R.id.mainlisthead);
		mRelativeLayout.setBackgroundDrawable(bd);
		TextView loginstate = (TextView) privateview
				.findViewById(R.id.loginstate);
		TextView loginnameandid = (TextView) privateview
				.findViewById(R.id.loginnameandid);
		ImageView avatarImage = (ImageView) privateview
				.findViewById(R.id.avatarImage);
		ImageView nextImage = (ImageView) privateview
				.findViewById(R.id.nextImage);
		if (userList == null) {
			loginstate.setText("δ��¼");
			loginnameandid.setText("�������ĵ�¼�˺ŵ�¼");
			avatarImage.setImageDrawable(getResources().getDrawable(
					R.drawable.drawerdefaulticon));
			nextImage.setVisibility(View.GONE);
		} else {
			if (userList.size() <= 1) {
				nextImage.setVisibility(View.GONE);
			}
			if (userList.size() == 1) {
				loginstate.setText("�ѵ�¼1���˻�");
			} else {
				loginstate.setText("�ѵ�¼"
						+ String.valueOf(userList.size() + "���˻�,����л�"));
			}
			if (userList.size() > 0) {
				User user = userList.get(position);
				loginnameandid.setText("��ǰ:" + user.getNickName() + "("
						+ user.getUserId() + ")");
				handleUserAvatat(avatarImage, user.getUserId());
			}
		}
		return privateview;
	}

	public void refreshheadview() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		width = (int) (width * 0.8);
		if (width >= 800) {
			mDrawerList.getLayoutParams().width = 800;
		} else {
			mDrawerList.getLayoutParams().width = width;
		}
		flipper = (ViewFlipper) this.findViewById(R.id.viewFlipper);
		String userListString = share.getString(USER_LIST, "");
		updateflipper(userListString);
		headview.setOnClickListener(new HeadViewClickListener(userListString));

	}

	class HeadViewClickListener implements OnClickListener {
		List<User> userList;

		public HeadViewClickListener(String userListString) {
			// TODO Auto-generated constructor stub
			if (!StringUtil.isEmpty(userListString)) {
				this.userList = JSON.parseArray(userListString, User.class);
			}
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (userList != null) {
				if (userList.size() > 1) {
					flipper.setInAnimation(rightInAnimation);
					flipper.setOutAnimation(rightOutAnimation);
					flipper.showPrevious();
					User u = userList.get(flipper.getDisplayedChild());
					app.addToUserList(u.getUserId(), u.getCid(),
							u.getNickName(), u.getReplyString(),
							u.getReplyTotalNum());
					PhoneConfiguration.getInstance().setUid(u.getUserId());
					PhoneConfiguration.getInstance().setNickname(
							u.getNickName());
					PhoneConfiguration.getInstance().setCid(u.getCid());
					PhoneConfiguration.getInstance().setReplyString(
							u.getReplyString());
					PhoneConfiguration.getInstance().setReplyTotalNum(
							u.getReplyTotalNum());
					if (toast != null) {
						toast.setText("�л��˻��ɹ�,��ǰ�˻���:" + u.getNickName());
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(MainActivity.this,
								"�л��˻��ɹ�,��ǰ�˻���:" + u.getNickName(),
								Toast.LENGTH_SHORT);

						toast.show();
					}
				}
				updateView(5);
			}

		}

	}

	public void handleUserAvatat(ImageView avatarIV, String userId) {// ��������
		Bitmap defaultAvatar = null, bitmap = null;
		if (PhoneConfiguration.getInstance().nikeWidth < 3) {
			return;
		}
		if (defaultAvatar == null
				|| defaultAvatar.getWidth() != PhoneConfiguration.getInstance().nikeWidth) {
			Resources res = getLayoutInflater().getContext().getResources();
			InputStream is = res.openRawResource(R.drawable.default_avatar);
			InputStream is2 = res.openRawResource(R.drawable.default_avatar);
			defaultAvatar = ImageUtil.loadAvatarFromStream(is, is2);
		}
		Object tagObj = avatarIV.getTag();
		if (tagObj instanceof AvatarTag) {
			AvatarTag origTag = (AvatarTag) tagObj;
			if (origTag.isDefault == false) {
				ImageUtil.recycleImageView(avatarIV);
				// Log.d(TAG, "recycle avatar:" + origTag.lou);
			} else {
				// Log.d(TAG, "default avatar, skip recycle");
			}
		}
		AvatarTag tag = new AvatarTag(Integer.parseInt(userId), true);
		avatarIV.setImageBitmap(defaultAvatar);
		avatarIV.setTag(tag);
		String avatarPath = HttpUtil.PATH_AVATAR + "/" + userId;
		String[] extension = { ".jpg", ".png", ".gif", ".jpeg", ".bmp" };
		for (int i = 0; i < 5; i++) {
			File f = new File(avatarPath + extension[i]);
			if (f.exists()) {

				bitmap = ImageUtil.loadAvatarFromSdcard(avatarPath
						+ extension[i]);
				if (bitmap == null) {
					f.delete();
				}
				long date = f.lastModified();
				if ((System.currentTimeMillis() - date) / 1000 > 30 * 24 * 3600) {
					f.delete();
				}
				break;
			}
		}
		if (bitmap != null) {
			avatarIV.setImageBitmap(toRoundCorner(bitmap, 2));
			tag.isDefault = false;
		} else {
			avatarIV.setImageDrawable(getResources().getDrawable(
					R.drawable.drawerdefaulticon));
			tag.isDefault = true;
		}

	}

	public static Bitmap toRoundCorner(Bitmap bitmap, float ratio) { // ��������
		if (bitmap.getWidth() > bitmap.getHeight()) {
			bitmap = Bitmap.createBitmap(bitmap,
					(int) (bitmap.getWidth() - bitmap.getHeight()) / 2, 0,
					bitmap.getHeight(), bitmap.getHeight());
		} else if (bitmap.getWidth() < bitmap.getHeight()) {
			bitmap = Bitmap.createBitmap(bitmap, 0,
					(int) (bitmap.getHeight() - bitmap.getWidth()) / 2,
					bitmap.getWidth(), bitmap.getWidth());
		}
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, bitmap.getWidth() / ratio,
				bitmap.getHeight() / ratio, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	void loadConfig(Intent intent) {
		// initUserInfo(intent);
		this.boardInfo = this.loadDefaultBoard();

	}

	private BoardHolder loadDefaultBoard() {
		if(PhoneConfiguration.getInstance().iconmode){Log.i(TAG,"asa");
			return app.loadDefaultBoardOld();
		}else{Log.i(TAG,"121");
			return app.loadDefaultBoard();
		}

	}

	private void delay(String text) {
		final String msg = text;
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (toast != null) {
					toast.setText(msg);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.show();
				} else {
					toast = Toast.makeText(MainActivity.this, msg,
							Toast.LENGTH_SHORT);

					toast.show();
				}
			}

		});
	}

	private void initDate() {

		new Thread() {
			public void run() {

				File filebase = new File(HttpUtil.PATH);
				if (!filebase.exists()) {
					delay(getString(R.string.create_cache_dir));
					filebase.mkdirs();
				}
				if (ActivityUtil.isGreaterThan_2_1()) {
					File f = new File(HttpUtil.PATH_AVATAR_OLD);
					if (f.exists()) {
						f.renameTo(new File(HttpUtil.PATH_AVATAR));
						delay(getString(R.string.move_avatar));
					}
				}

				File file = new File(HttpUtil.PATH_NOMEDIA);
				if (!file.exists()) {
					Log.i(getClass().getSimpleName(), "create .nomedia");
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}.start();

	}

	public boolean isTablet() {
		boolean xlarge = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 0x04);// Configuration.SCREENLAYOUT_SIZE_XLARGE);
		boolean large = ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
		return (xlarge || large) && ActivityUtil.isGreaterThan_2_3_3();
	}

	private void jumpToLogin() {
		if (isTablet()) {
			tabletloginfragmentshowed = true;
			DialogFragment df = new LoginFragment();
			df.show(getSupportFragmentManager(), "login");
			return;
		}

		Intent intent = new Intent();
		intent.setClass(MainActivity.this, LoginActivity.class);
		try {
			startActivity(intent);
			if (PhoneConfiguration.getInstance().showAnimation) {
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
			}
		} catch (Exception e) {

		}

	}

	private void collect_dragon_ball() {
		if (dragonballnum < 6) {
			dragonballnum++;
			Editor editor = share.edit();
			editor.putString(DRAGON_BALL, String.valueOf(dragonballnum));
			editor.commit();
			if (toast != null) {
				toast.setText("���ռ�����" + String.valueOf(dragonballnum) + "������");
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
			} else {
				toast = Toast.makeText(MainActivity.this,
						"���ռ�����" + String.valueOf(dragonballnum) + "������",
						Toast.LENGTH_SHORT);

				toast.show();
			}
		} else if (dragonballnum == 6) {
			if (toast != null) {
				toast.setText("���ռ�����7������");
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
			} else {
				toast = Toast.makeText(MainActivity.this, "���ռ�����7������",
						Toast.LENGTH_SHORT);

				toast.show();
			}
			AudioManager audioManager = (AudioManager) view.getContext()
					.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
				AssetFileDescriptor afd = getResources().openRawResourceFd(
						R.raw.dragon_ball);
				try {
					mp.reset();
					mp.setDataSource(afd.getFileDescriptor(),
							afd.getStartOffset(), afd.getLength());
					mp.prepare();
					mp.start();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			final Editor editor = share.edit();

			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					mp.release();
					mp = new MediaPlayer();
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						dragonballnum = 7;
						editor.putString(DRAGON_BALL,
								String.valueOf(dragonballnum));
						editor.commit();
						Intent intent = new Intent();
						intent.setClass(
								MainActivity.this,
								PhoneConfiguration.getInstance().MeiziMainActivityClass);
						startActivity(intent);
						if (PhoneConfiguration.getInstance().showAnimation)
							overridePendingTransition(R.anim.zoom_enter,
									R.anim.zoom_exit);

						break;

					case DialogInterface.BUTTON_NEGATIVE:
						// Do nothing
						dragonballnum = 0;
						editor.putString(DRAGON_BALL,
								String.valueOf(dragonballnum));
						editor.commit();
						if (toast != null) {
							toast.setText("��ѡ���˲�ʹ��,������ɢ���ķ���");
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.show();
						} else {
							toast = Toast.makeText(MainActivity.this,
									"��ѡ���˲�ʹ��,������ɢ���ķ���", Toast.LENGTH_SHORT);

							toast.show();
						}
						break;
					}
				}
			};

			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					this.getString(R.string.dragon_ball_collect_complete))
					.setPositiveButton(R.string.confirm, dialogClickListener)
					.setNegativeButton(R.string.cancle, dialogClickListener);
			builder.setOnCancelListener(new AlertDialog.OnCancelListener() {

				@Override
				public void onCancel(DialogInterface arg0) {
					// TODO Auto-generated method stub
				}

			});
			final AlertDialog dialog = builder.create();
			dialog.show();
			dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface arg0) {
					// TODO Auto-generated method stub
					mp.release();
					mp = new MediaPlayer();
					dialog.dismiss();
					if (PhoneConfiguration.getInstance().fullscreen) {
						ActivityUtil.getInstance().setFullScreen(view);
					}
				}

			});

		} else if (dragonballnum >= 7) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, MeiziMainActivity.class);
			startActivity(intent);
			if (PhoneConfiguration.getInstance().showAnimation)
				overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);

		}
	}

	private void jumpToSetting() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, SettingsActivity.class);
		startActivity(intent);
		if (PhoneConfiguration.getInstance().showAnimation)
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
	}

	void jumpToNearby() {
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, NearbyUserActivity.class);

		startActivity(intent);
		if (PhoneConfiguration.getInstance().showAnimation)
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);

	}

	void jumpToRecentReply() {
		Intent intent = new Intent();
		intent.putExtra("recentmode", "recentmode");
		intent.setClass(MainActivity.this,
				PhoneConfiguration.getInstance().recentReplyListActivityClass);

		startActivity(intent);
		if (PhoneConfiguration.getInstance().showAnimation)
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);

	}

	private void clear_recent_board() {
		if (mActivePosition == 4) {
			mActivePosition = 0;
		} else if (mActivePosition > 5) {
			mActivePosition--;
		}
		if (activedposition == 4) {
			activedposition = 0;
		} else if (activedposition > 5) {
			activedposition--;
		}

		Editor editor = share.edit();
		editor.putString(RECENT_BOARD, "");
		editor.commit();
		Intent iareboot = getBaseContext().getPackageManager()
				.getLaunchIntentForPackage(getBaseContext().getPackageName());
		iareboot.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(iareboot);
	}

	private void useurltoactivity_dialog() {
		LayoutInflater layoutInflater = getLayoutInflater();
		final View view = layoutInflater
				.inflate(R.layout.useurlto_dialog, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setView(view);
		alert.setTitle(R.string.urlto_title_hint);
		final EditText urladd = (EditText) view.findViewById(R.id.urladd);
		urladd.requestFocus();
		String clipdata = null;
		if (ActivityUtil.isLessThan_3_0()) {
			android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
			if (clipboardManager.hasText()) {
				clipdata = clipboardManager.getText().toString();
			}
		} else {
			android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

			if (clipboardManager.hasPrimaryClip()) {
				android.content.ClipData.Item item = clipboardManager
						.getPrimaryClip().getItemAt(0);
				try {
					clipdata = clipboardManager.getPrimaryClip().getItemAt(0)
							.getText().toString();
				} catch (Exception e) {
					clipdata = "";
				}

			}
		}
		if (!StringUtil.isEmpty(clipdata)) {
			urladd.setText(clipdata);
			urladd.selectAll();
		}

		alert.setPositiveButton("����", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String url = urladd.getText().toString().trim();
				if (StringUtil.isEmpty(url)) {// ��
					if (toast != null) {
						toast.setText("������URL��ַ");
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(MainActivity.this, "������URL��ַ",
								Toast.LENGTH_SHORT);

						toast.show();
					}
					urladd.setFocusable(true);
					try {
						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					if (url.toLowerCase(Locale.US).indexOf("dbmeizi.com") >= 0
							|| url.toLowerCase(Locale.US).indexOf("baozhao.me") >= 0
							|| url.toLowerCase(Locale.US).indexOf("dadanshai.com") >= 0
							|| url.indexOf("��������") >= 0
							|| url.indexOf("����") >= 0
							|| url.indexOf("��ɹ") >= 0 || url.equals("1024")) {
						if (toast != null) {
							toast.setText("��ϲ���ҵ���һ�ַ���,֪������֪����,��Ҫȥ��̳����,�Լ��þ�����,Ϊ�˿����ߵİ�ȫ");
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.show();
						} else {
							toast = Toast
									.makeText(
											MainActivity.this,
											"��ϲ���ҵ���һ�ַ���,֪������֪����,��Ҫȥ��̳����,�Լ��þ�����,Ϊ�˿����ߵİ�ȫ",
											Toast.LENGTH_SHORT);

							toast.show();
						}
						Intent intent = new Intent();
						intent.setClass(
								MainActivity.this,
								PhoneConfiguration.getInstance().MeiziMainActivityClass);
						startActivity(intent);
						if (PhoneConfiguration.getInstance().showAnimation)
							overridePendingTransition(R.anim.zoom_enter,
									R.anim.zoom_exit);
					} else {
						PhoneConfiguration conf = PhoneConfiguration
								.getInstance();
						url = url.toLowerCase(Locale.US).trim();
						if (url.indexOf("thread.php") > 0) {
							url = url
									.replaceAll(
											"(?i)[^\\[|\\]]+fid=(-{0,1}\\d+)[^\\[|\\]]{0,}",
											"http://nga.178.com/thread.php?fid=$1");
							Intent intent = new Intent();
							intent.setData(Uri.parse(url));
							intent.setClass(view.getContext(),
									conf.topicActivityClass);
							view.getContext().startActivity(intent);
						} else if (url.indexOf("read.php") > 0) {
							if (url.indexOf("tid") > 0
									&& url.indexOf("pid") > 0) {
								if (url.indexOf("tid") < url.indexOf("pid"))
									url = url
											.replaceAll(
													"(?i)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]{0,}",
													"http://nga.178.com/read.php?pid=$2&tid=$1");
								else
									url = url
											.replaceAll(
													"(?i)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]{0,}",
													"http://nga.178.com/read.php?pid=$1&tid=$2");
							} else if (url.indexOf("tid") > 0
									&& url.indexOf("pid") <= 0) {
								url = url
										.replaceAll(
												"(?i)[^\\[|\\]]+tid=(\\d+)[^\\[|\\]]{0,}",
												"http://nga.178.com/read.php?tid=$1");
							} else if (url.indexOf("pid") > 0
									&& url.indexOf("tid") <= 0) {
								url = url
										.replaceAll(
												"(?i)[^\\[|\\]]+pid=(\\d+)[^\\[|\\]]{0,}",
												"http://nga.178.com/read.php?pid=$1");
							}
							Intent intent = new Intent();
							intent.setData(Uri.parse(url));
							intent.setClass(view.getContext(),
									conf.articleActivityClass);
							view.getContext().startActivity(intent);
						} else {
							if (toast != null) {
								toast.setText("����ĵ�ַ����NGA�İ���ַ�����ӵ�ַ,��ȱ��fid/pid/tid��Ϣ,���������");
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.show();
							} else {
								toast = Toast
										.makeText(
												MainActivity.this,
												"����ĵ�ַ����NGA�İ���ַ�����ӵ�ַ,��ȱ��fid/pid/tid��Ϣ,���������",
												Toast.LENGTH_SHORT);

								toast.show();
							}
							urladd.setFocusable(true);
							try {
								Field field = dialog.getClass().getSuperclass()
										.getDeclaredField("mShowing");
								field.setAccessible(true);
								field.set(dialog, false);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		});

		alert.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		final AlertDialog dialog = alert.create();
		dialog.show();
		Date d = new Date();
		int hours = d.getHours();
		if (hours > 22 || hours < 6) {
			String toastdata = "1024";
			boolean showtextbool = new Random().nextBoolean();
			if (showtextbool) {
				showtextbool = new Random().nextBoolean();
				if(showtextbool){
					toastdata = "MENU��7";
				}else{
					toastdata = "��������";
				}
			}else{
				showtextbool = new Random().nextBoolean();
				if(showtextbool){
					toastdata = "dbmeizi.com";
				}
			}
			if (toast != null) {
				toast.setText(toastdata);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
			} else {
				toast = Toast.makeText(MainActivity.this, toastdata,
						Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if (PhoneConfiguration.getInstance().fullscreen) {
					ActivityUtil.getInstance().setFullScreen(view);
				}
			}

		});
	}

	private void add_fid_dialog() {
		LayoutInflater layoutInflater = getLayoutInflater();
		final View view = layoutInflater.inflate(R.layout.addfid_dialog, null);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setView(view);
		alert.setTitle(R.string.addfid_title_hint);
		final EditText addfid_name = (EditText) view
				.findViewById(R.id.addfid_name);
		final EditText addfid_id = (EditText) view.findViewById(R.id.addfid_id);
		alert.setPositiveButton("���", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String name = addfid_name.getText().toString();
				String fid = addfid_id.getText().toString();
				if (name.equals("")) {
					if (toast != null) {
						toast.setText("�������������");
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.show();
					} else {
						toast = Toast.makeText(MainActivity.this, "�������������",
								Toast.LENGTH_SHORT);

						toast.show();
					}
					addfid_name.setFocusable(true);
					try {
						Field field = dialog.getClass().getSuperclass()
								.getDeclaredField("mShowing");
						field.setAccessible(true);
						field.set(dialog, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {

					Pattern pattern = Pattern.compile("-{0,1}[0-9]*");
					Matcher match = pattern.matcher(fid);
					if (match.matches() == false || fid.equals("")) {
						addfid_id.setText("");
						addfid_id.setFocusable(true);
						if (toast != null) {
							toast.setText("��������ȷ�İ���ID(���˰���Ҫ�Ӹ���)");
							toast.setDuration(Toast.LENGTH_SHORT);
							toast.show();
						} else {
							toast = Toast.makeText(MainActivity.this,
									"��������ȷ�İ���ID(���˰���Ҫ�Ӹ���)", Toast.LENGTH_SHORT);

							toast.show();
						}
						try {
							Field field = dialog.getClass().getSuperclass()
									.getDeclaredField("mShowing");
							field.setAccessible(true);
							field.set(dialog, false);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {// CHECK PASS, READY TO ADD FID
						boolean FidAllreadyExist = false;
						int i = 0;
						for (i = 0; i < boardInfo.getCategoryCount(); i++) {
							BoardCategory curr = boardInfo.getCategory(i);
							for (int j = 0; j < curr.size(); j++) {
								String URL = curr.get(j).getUrl();
								if (URL.equals(fid)) {
									FidAllreadyExist = true;
									addfid_id.setText("");
									addfid_id.setFocusable(true);
									if (toast != null) {
										toast.setText("�ð����Ѿ��������б�");
										toast.setDuration(Toast.LENGTH_SHORT);
										toast.show();
									} else {
										toast = Toast.makeText(
												MainActivity.this,
												"�ð����Ѿ��������б�"
														+ boardInfo
																.getCategoryName(i)
														+ "��",
												Toast.LENGTH_SHORT);

										toast.show();
									}
									try {
										Field field = dialog.getClass()
												.getSuperclass()
												.getDeclaredField("mShowing");
										field.setAccessible(true);
										field.set(dialog, false);
									} catch (Exception e) {
										e.printStackTrace();
									}
									break;
								}
							}// for j
						}// for i
						if (!FidAllreadyExist) {
							addToaddFid(name, fid);
							if (toast != null) {
								toast.setText("��ӳɹ�");
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.show();
							} else {
								toast = Toast.makeText(MainActivity.this,
										"��ӳɹ�" + boardInfo.getCategoryName(i)
												+ "��", Toast.LENGTH_SHORT);

								toast.show();
							}
							try {
								Field field = dialog.getClass().getSuperclass()
										.getDeclaredField("mShowing");
								field.setAccessible(true);
								field.set(dialog, true);
							} catch (Exception e) {
								e.printStackTrace();
							}
							updatepager();
							updatemDrawerList();
						}
					}
				}

			}
		});
		alert.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				try {
					Field field = dialog.getClass().getSuperclass()
							.getDeclaredField("mShowing");
					field.setAccessible(true);
					field.set(dialog, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		final AlertDialog dialog = alert.create();
		dialog.show();
		dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if (PhoneConfiguration.getInstance().fullscreen) {
					ActivityUtil.getInstance().setFullScreen(view);
				}
			}

		});
	}

	// private boolean isBoardExist(String boardname){
	// for(int i =0; i < boardInfo.getCategoryCount(); i++){
	// //System.out.println(boardname+boardInfo.getCategoryName(i));
	// if(boardname.equals(boardInfo.getCategoryName(i))){
	// return true;
	// }
	// }
	// return false;
	// }
	private void addToaddFid(String Name, String Fid) {
		boolean addFidAlreadExist = false;
		BoardCategory addFid = null;
		int i = 0;
		for (i = 0; i < boardInfo.getCategoryCount(); i++) {
			if (boardInfo.getCategoryName(i).equals(getString(R.string.addfid))) {
				addFidAlreadExist = true;
				addFid = boardInfo.getCategory(i);
				break;
			}
			;
		}

		if (!addFidAlreadExist) {// û��
//			MyApp.fddicon[1][1];
			List<Board> boardList = new ArrayList<Board>();
			Board b;
			if(PhoneConfiguration.getInstance().iconmode){
				 b= new Board(i + 1, Fid, Name, R.drawable.oldpdefault);
			}else{
				 b = new Board(i + 1, Fid, Name, R.drawable.pdefault);
			}
			boardList.add(b);
			saveaddFid(boardList);
			boardInfo = loadDefaultBoard();
			// add menu item
			if (boardInfo.getCategoryCount() == 12) {
				setLocItem(19, "�û��Զ���", R.drawable.ic_queue);
			} else {
				setLocItem(20, "�û��Զ���", R.drawable.ic_queue);
			}
			return;
		} else {// ����
			Board b;
			if(PhoneConfiguration.getInstance().iconmode){
				b = new Board(i, Fid, Name, R.drawable.oldpdefault);
			}else{
				b = new Board(i, Fid, Name, R.drawable.pdefault);
			}
			addFid.add(b);
		}
		addFid = boardInfo.getCategory(i);
		this.saveaddFid(addFid.getBoardList());
		return;

	}

	private void saveaddFid(List<Board> boardList) {
		// TODO Auto-generated method stub

		String addFidStr = JSON.toJSONString(boardList);
		Editor editor = share.edit();
		editor.putString(ADD_FID, addFidStr);
		editor.commit();
	}

	@Override
	protected void onStop() {
		mp.release();
		mp = new MediaPlayer();
		super.onStop();
	}

	@Override
	protected void onResume() {
		int orentation = ThemeManager.getInstance().screenOrentation;
		if (orentation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
				|| orentation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
				setRequestedOrientation(orentation);
		} else {
			if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
					|| getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
		if (PhoneConfiguration.getInstance().fullscreen) {
			ActivityUtil.getInstance().setFullScreen(view);
		}
		updatemDrawerList();
		updatepager();
		refreshheadview();
		super.onResume();
	}

	@Override
	public int getCategoryCount() {
		if (boardInfo == null)
			return 0;
		return boardInfo.getCategoryCount();
	}

	@Override
	public String getCategoryName(int position) {
		if (boardInfo == null)
			return "";
		return boardInfo.getCategoryName(position);
	}

	@Override
	public BoardCategory getCategory(int category) {
		if (boardInfo == null)
			return null;
		return boardInfo.getCategory(category);
	}

	class EnterToplistLintener implements OnItemClickListener, OnClickListener {
		int position;
		String fidString;

		public EnterToplistLintener(int position, String fidString) {
			super();
			this.position = position;
			this.fidString = fidString;
		}

		public EnterToplistLintener() {// constructoer
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
				String tip = fidString + "���Բ�����";
				if (toast != null) {
					toast.setText(tip);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.show();
				} else {
					toast = Toast.makeText(MainActivity.this, tip,
							Toast.LENGTH_SHORT);

					toast.show();
				}
				return;
			}

			Log.i(this.getClass().getSimpleName(), "set host:" + HttpUtil.HOST);

			String url = HttpUtil.Server + "/thread.php?fid=" + fidString
					+ "&rss=1";
			PhoneConfiguration config = PhoneConfiguration.getInstance();
			if (!StringUtil.isEmpty(config.getCookie())) {

				url = url + "&" + config.getCookie().replace("; ", "&");
			} else if (fid < 0 && fid != -7) {
				jumpToLogin();
				return;
			}

			if (StringUtil.isEmpty(share.getString(RECENT_BOARD, ""))) {
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
				// intent.setClass(MainActivity.this, TopicListActivity.class);
				startActivity(intent);
				if (PhoneConfiguration.getInstance().showAnimation) {
					overridePendingTransition(R.anim.zoom_enter,
							R.anim.zoom_exit);
				}
			}
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			position = arg2;
			fidString = (String) arg0.getItemAtPosition(position);
			onClick(arg1);

		}

		private void saveRecent(List<Board> boardList) {
			String rescentStr = JSON.toJSONString(boardList);
			Editor editor = share.edit();
			editor.putString(RECENT_BOARD, rescentStr);
			editor.commit();
		}

		private void addToRecent() {

			boolean recentAlreadExist = boardInfo.getCategoryName(0).equals(
					getString(R.string.recent));

			BoardCategory recent = boardInfo.getCategory(0);
			if (recent != null && recentAlreadExist)
				recent.remove(fidString);
			// int i = 0;
			for (int i = 0; i < boardInfo.getCategoryCount(); i++) {
				BoardCategory curr = boardInfo.getCategory(i);
				for (int j = 0; j < curr.size(); j++) {
					Board b = curr.get(j);
					if (b.getUrl().equals(fidString)) {
						Board b1 = new Board(0, b.getUrl(), b.getName(),
								b.getIcon());

						if (!recentAlreadExist) {
							List<Board> boardList = new ArrayList<Board>();
							boardList.add(b1);
							saveRecent(boardList);
							// add recent menu item
							setLocItem(7, "�������", R.drawable.ic_queue);
							// set menu click right
							ifRecentExist = 0;
							boardInfo = loadDefaultBoard();
							updatemDrawerList();
							updatepager();
							return;
						} else {
							recent.addFront(b1);
						}
						recent = boardInfo.getCategory(0);
						this.saveRecent(recent.getBoardList());

						return;
					}// if
				}// for j

			}// for i

		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		this.onItemClickListenerlistener
				.onItemClick(parent, view, position, id);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
			if (fulimode.equals("0")) {
				if (menucishu == 0) {
					secondstart = (int) System.currentTimeMillis() / 1000;
					menucishu++;
				} else {
					secondnow = (int) System.currentTimeMillis() / 1000;
					if (secondnow - secondstart < 10) {
						menucishu++;
						if (menucishu >= 7) {
							Editor editor = share.edit();
							editor.putString(CAN_SHOW_FULI, "1");
							editor.commit();
							setLocItem(boardInfo.getCategoryCount() + 14,
									"��Ҫ����~ߣ~", R.drawable.ic_action_dragon_ball);
							if (toast != null) {
								toast.setText("�������֪��������ʲô\n�����֪����,��Ҫȥ��̳����,�Լ��þ�����,Ϊ�˿����ߵİ�ȫ");
								toast.setDuration(Toast.LENGTH_SHORT);
								toast.show();
							} else {
								toast = Toast
										.makeText(
												MainActivity.this,
												"�������֪��������ʲô\n�����֪����,��Ҫȥ��̳����,�Լ��þ�����,Ϊ�˿����ߵİ�ȫ",
												Toast.LENGTH_SHORT);

								toast.show();
							}
							fulimode = "1";
							menucishu = 0;
						}
					} else {
						secondstart = (int) System.currentTimeMillis() / 1000;
						secondnow = 0;
						menucishu = 0;
					}
				}
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

}
