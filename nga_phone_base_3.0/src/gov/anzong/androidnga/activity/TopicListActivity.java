package gov.anzong.androidnga.activity;

import gov.anzong.androidnga.R;
import sp.phone.adapter.TabsAdapter;
import sp.phone.bean.TopicListInfo;
import sp.phone.fragment.SearchDialogFragment;
import sp.phone.fragment.TopicListFragment;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.task.CheckReplyNotificationTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.support.v7.app.ActionBarActivity;


class TopicListActivity extends SwipeBackAppCompatActivity
	implements OnTopListLoadFinishedListener{
	static final private String TAG = PhoneConfiguration.getInstance().topicActivityClass.getSimpleName();
	static final int MESSAGE_SENT = 1;
	TabHost tabhost;
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter=null;
	private CheckReplyNotificationTask asynTask;
	int fid;
	int authorid;
	int searchpost;
	int favor;
	String key;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pagerview_article_list);
		tabhost = (TabHost) findViewById(android.R.id.tabhost);
		tabhost.setup();
		mViewPager = (ViewPager) findViewById(R.id.pager);
		if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
			setNfcCallBack();
		}
		
		fid = 0;
		authorid = 0;
		int pageInUrl = 0;
		String url = this.getIntent().getDataString();
		
		if(url != null){
			
			fid = getUrlParameter(url,"fid");
			pageInUrl =getUrlParameter(url,"page");
			authorid = getUrlParameter(url,"authorid");
			searchpost = getUrlParameter(url,"searchpost");
			favor = getUrlParameter(url,"favor");
			key = StringUtil.getStringBetween(url, 0, "key=", "&").result;
		}
		else
		{
			fid = this.getIntent().getIntExtra("fid", 0);
			authorid = this.getIntent().getIntExtra("authorid", 0);
			searchpost = this.getIntent().getIntExtra("searchpost", 0);
			favor = getIntent().getIntExtra("favor", 0);
			key = getIntent().getStringExtra("key");
		}
		
		if (null != savedInstanceState)
			fid = savedInstanceState.getInt("fid");

		mTabsAdapter = new TabsAdapter(this, tabhost, mViewPager,
				TopicListFragment.class);
		mTabsAdapter.setArgument("id", fid);
		mTabsAdapter.setArgument("authorid", authorid);
		mTabsAdapter.setArgument("searchpost", searchpost);
		mTabsAdapter.setArgument("favor", favor);
		mTabsAdapter.setArgument("key", key);
		//mTabsAdapter.setCount(100);
		
		if(favor != 0){
			this.setTitle(R.string.bookmark_title);
		}
		
		if(!StringUtil.isEmpty(key))
		{
			final String title = this.getResources().getString(android.R.string.search_go)
					+ ":"+key;
			setTitle(title);
		}

		ActivityUtil.getInstance().noticeSaying(this);

		if (savedInstanceState != null) {
			int currentPageInex = savedInstanceState.getInt("tab");
			mTabsAdapter.setCount(currentPageInex + 1);
			mViewPager.setCurrentItem(currentPageInex);
		}else if(pageInUrl !=0){
			mViewPager.setCurrentItem(pageInUrl -1);
		}

	}
	
	@TargetApi(14)
	void setNfcCallBack(){
		NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
		CreateNdefMessageCallback callback = new CreateNdefMessageCallback(){

			@Override
			public NdefMessage createNdefMessage(NfcEvent event) {
				final String url = getUrl();
				NdefMessage msg = new NdefMessage(
		                new NdefRecord[]{NdefRecord.createUri(url)}
		                );
				return msg;
			}
			
		};
		if (adapter != null) {
			adapter.setNdefPushMessageCallback(callback, this);

		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		int menuId;
		if(PhoneConfiguration.getInstance().HandSide==1){//lefthand
			int flag = PhoneConfiguration.getInstance().getUiFlag();
			if(flag==1 || flag==3||flag==5||flag==7){//主题列表，UIFLAG为1或者1+2或者1+4或者1+2+4
				menuId= R.menu.threadlist_menu_left;
			}
			else{
				menuId= R.menu.threadlist_menu;
			}
		}else{
			menuId= R.menu.threadlist_menu;
		}
		getMenuInflater().inflate(menuId, menu);
		final int flags = ThemeManager.ACTION_BAR_FLAG;
		 ReflectionUtil.actionBar_setDisplayOption(this, flags);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	private void handleSearch(){
		Bundle arg  = new Bundle();
		arg.putInt("id",fid);
		arg.putInt("authorid", authorid);
		DialogFragment df = new SearchDialogFragment();
		df.setArguments(arg);
		final String dialogTag = "search_dialog";
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment prev = fm.findFragmentByTag(dialogTag);
        if (prev != null) {
            ft.remove(prev);
        }

        try{
        	df.show(ft, dialogTag);
		}catch(Exception e){
			Log.e(this.getClass().getSimpleName(),Log.getStackTraceString(e));

		}
	}
	
	private boolean handlePostThread(MenuItem item){
		
		
		
		Intent intent = new Intent();
		//intent.putExtra("prefix",postPrefix.toString());
		intent.putExtra("fid", fid);
		intent.putExtra("action", "new");
		
		intent.setClass(this, PhoneConfiguration.getInstance().postActivityClass);
		startActivity(intent);
		if(PhoneConfiguration.getInstance().showAnimation)
		{
			overridePendingTransition(R.anim.zoom_enter,
					R.anim.zoom_exit);
		}
		return true;
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId())
		{
			case R.id.threadlist_menu_newthread :
				handlePostThread(item);
				break;
			case R.id.threadlist_menu_item2 :
				int current = this.mViewPager.getCurrentItem();
				ActivityUtil.getInstance().noticeSaying(this);
				this.mViewPager.setAdapter(this.mTabsAdapter);
				this.mViewPager.setCurrentItem(current, true);
				break;
			case R.id.goto_bookmark_item:
				Intent intent_bookmark = new Intent(this, PhoneConfiguration.getInstance().topicActivityClass);
				intent_bookmark.putExtra("favor", 1);
				startActivity(intent_bookmark);
				break;
			case R.id.search:
				handleSearch();
				break;
			case R.id.threadlist_menu_item3 :
			default:
				//case android.R.id.home:
				Intent intent = new Intent(this, MainActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", mViewPager.getCurrentItem());
		outState.putInt("fid", fid);

	}
	
	

	@Override
	protected void onResume() {
		int orentation = ThemeManager.getInstance().screenOrentation;
		if(orentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				orentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		{
			setRequestedOrientation(orentation);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
		
		
		if(asynTask !=null){
			asynTask.cancel(true);
			asynTask = null;
		}
		long now = System.currentTimeMillis();
		PhoneConfiguration config = PhoneConfiguration.getInstance();
		if(now - config.lastMessageCheck > 60*1000 && config.notification)
		{
			Log.d(TAG, "start to check Reply Notification");
			asynTask = new CheckReplyNotificationTask(this);
			asynTask.execute(config.getCookie());
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		//SActivityUtil.getInstance().dismiss();
		super.onDestroy();
	}
	
	
	
	private int getUrlParameter(String url, String paraName){
		if(StringUtil.isEmpty(url))
		{
			return 0;
		}
		final String pattern = paraName+"=" ;
		int start = url.indexOf(pattern);
		if(start == -1)
			return 0;
		start +=pattern.length();
		int end = url.indexOf("&",start);
		if(end == -1)
			end = url.length();
		String value = url.substring(start,end);
		int ret = 0;
		try{
			ret = Integer.parseInt(value);
		}catch(Exception e){
			Log.e(TAG, "invalid url:" + url);
		}
		
		return ret;
	}



	@Override
	public void jsonfinishLoad(TopicListInfo result) {
		int lines = 35;
		if(authorid !=0)
			lines = 20;
		int pageCount = result.get__ROWS() / lines ;
		if( pageCount * lines < result.get__ROWS() )
			pageCount++;
		
		if(searchpost !=0)//can not get exact row counts
		{
			int page = result.get__ROWS();
			pageCount = page;
			if(result.get__T__ROWS() == lines)
				pageCount++;
		}
		
		if( mTabsAdapter.getCount() != pageCount)
			mTabsAdapter.setCount(pageCount);
	}


	public String getUrl() {
		final String scheme = getResources().getString(R.string.myscheme);
		final StringBuilder sb = new StringBuilder(scheme);
		sb.append("://bbs.ngacn.cc/thread.php?");
		if(fid!=0){
			sb.append("fid=");
			sb.append(fid);
			sb.append('&');
		}
		if(authorid !=0){
			sb.append("authorid=");
			sb.append(authorid);
			sb.append('&');
		}
		if(this.searchpost != 0){
			sb.append("searchpost=");
			sb.append(searchpost);
			sb.append('&');
		}
		if(this.mViewPager.getCurrentItem() != 0){
			sb.append("page=");
			sb.append(mViewPager.getCurrentItem());
			sb.append('&');
		}

		return sb.toString();
	}
	

	


}
