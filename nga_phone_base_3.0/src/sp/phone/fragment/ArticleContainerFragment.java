package sp.phone.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TabWidget;
import gov.anzong.androidnga.activity.PostActivity;
import gov.anzong.androidnga.R;
import sp.phone.adapter.ThreadFragmentAdapter;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.ThreadData;
import sp.phone.interfaces.OnChildFragmentRemovedListener;
import sp.phone.interfaces.OnThreadPageLoadFinishedListener;
import sp.phone.interfaces.PagerOwnner;
import sp.phone.task.BookmarkTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;

public class ArticleContainerFragment extends Fragment 
implements OnThreadPageLoadFinishedListener,PerferenceConstant,
PagerOwnner{
	public static ArticleContainerFragment create(int tid, int pid, int authorid){
		ArticleContainerFragment f = new ArticleContainerFragment();
		Bundle args = new Bundle ();
		args.putInt("tid", tid);
		args.putInt("pid", pid);
		args.putInt("authorid", authorid);
		f.setArguments(args);
		return f;
	}

	public static ArticleContainerFragment createshowall(int tid){
		ArticleContainerFragment f = new ArticleContainerFragment();
		Bundle args = new Bundle ();
		args.putInt("tid", tid);
		f.setArguments(args);
		return f;
	}
	
	public static ArticleContainerFragment createshowonly(int tid, int authorid){
		ArticleContainerFragment f = new ArticleContainerFragment();
		Bundle args = new Bundle ();
		args.putInt("tid", tid);
		args.putInt("authorid", authorid);
		args.putInt("tab", 1);
		f.setArguments(args);
		return f;
	}
	
	public ArticleContainerFragment() {
		super();
	}
	
	//TabHost tabhost;
	ViewPager  mViewPager;
	ThreadFragmentAdapter mTabsAdapter;
    int tid;
    int pid;
    TabWidget tabs;
    String title;
    int authorid;
    String url;
	private static final String TAG= "ArticleContainerFragment";
	private static final String GOTO_TAG = "goto";
	final private String ALERT_DIALOG_TAG = "alertdialog";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v  = inflater.inflate(R.layout.article_viewpager, container,false);


		/*PullToRefreshViewPager
		refreshPager = (PullToRefreshViewPager) v.findViewById(R.id.pull_refresh_viewpager);
		//refreshPager.setMode(Mode.DISABLED);
		mViewPager = refreshPager.getRefreshableView();*/
		mViewPager = (ViewPager) v.findViewById(R.id.pager);
		int pageFromUrl = 0;
		url = getArguments().getString("url");
		if(null != url){
			tid = this.getUrlParameter(url, "tid");		
			pid = this.getUrlParameter(url, "pid");		
			authorid = this.getUrlParameter(url, "authorid");
			pageFromUrl = this.getUrlParameter(url, "page");
		}else
		{
			tid = this.getArguments().getInt("tid", 0);		
			pid = this.getArguments().getInt("pid", 0);
			authorid = this.getArguments().getInt("authorid", 0);
		}

		

		mTabsAdapter = new ThreadFragmentAdapter(getActivity(),
				getChildFragmentManager(),
				mViewPager,ArticleListFragment.class);
				//new TabsAdapter(getActivity(), tabhost, mViewPager,ArticleListFragment.class);
		
		
		
		mTabsAdapter.setArgument("id", tid);
		mTabsAdapter.setArgument("pid", pid);
		mTabsAdapter.setArgument("authorid", authorid);
		
		//ActivityUtil.getInstance().noticeSaying(getActivity());
		
        if (savedInstanceState != null) {
        	int pageCount = savedInstanceState.getInt("pageCount");
        	if(pageCount!=0)
        	{
        		mTabsAdapter.setCount(pageCount);
        		mViewPager.setCurrentItem(savedInstanceState.getInt("tab"));
        	}
        	
        }
        else if(pageFromUrl !=0)
        {
        	mTabsAdapter.setCount(pageFromUrl+1);
        	mViewPager.setCurrentItem(pageFromUrl);
        }
        else{
        	mTabsAdapter.setCount(1);
        }
		
		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pageCount",mTabsAdapter.getCount());
        outState.putInt("tab",mViewPager.getCurrentItem());
	}
	
	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		if(PhoneConfiguration.getInstance().HandSide==1){//lefthand
			int flag = PhoneConfiguration.getInstance().getUiFlag();
			if(flag==1 || flag==3||flag==5||flag==7){//�����б�UIFLAGΪ1����1+2����1+4����1+2+4
				inflater.inflate(R.menu.articlelist_menu_left, menu);
				}
			else{
				inflater.inflate(R.menu.articlelist_menu, menu);
			}
		}else{
			inflater.inflate(R.menu.articlelist_menu, menu);
		}


		MenuItem lock = menu.findItem(R.id.article_menuitem_lock);
		int orentation = ThemeManager.getInstance().screenOrentation;
		if(orentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				orentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		{
			lock.setTitle(R.string.unlock_orientation);
			lock.setIcon(R.drawable.ic_menu_always_landscape_portrait);
			
		}
		
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
    public void onPrepareOptionsMenu(Menu menu) {  
        if( menu.findItem(R.id.night_mode)!=null){
            if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {  
                menu.findItem(R.id.night_mode).setIcon(  
                        R.drawable.ic_action_brightness_high);    
                menu.findItem(R.id.night_mode).setTitle(R.string.change_daily_mode);
            }else{
                menu.findItem(R.id.night_mode).setIcon(  
                        R.drawable.ic_action_bightness_low);    
                menu.findItem(R.id.night_mode).setTitle(R.string.change_night_mode);
            }
        }
        // getSupportMenuInflater().inflate(R.menu.book_detail, menu);  
        super.onPrepareOptionsMenu(menu);  
    }  
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch( item.getItemId())
		{
			case R.id.article_menuitem_reply:
				//if(articleAdpater.getData() == null)
				//	return false;
				if(!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)){//�����˲��ܷ�
					String tid = String.valueOf(this.tid);
					intent.putExtra("prefix", "" );
					intent.putExtra("tid", tid);
					intent.putExtra("action", "reply");
					if (!StringUtil
							.isEmpty(PhoneConfiguration.getInstance().userName)) {// �����˲��ܷ�
					intent.setClass(getActivity(), PhoneConfiguration.getInstance().postActivityClass);}
					else{
						intent.setClass(
							getActivity(),
							PhoneConfiguration.getInstance().loginActivityClass);
					}
					startActivity(intent);
					if(PhoneConfiguration.getInstance().showAnimation)
						getActivity().overridePendingTransition(R.anim.zoom_enter,
								R.anim.zoom_exit);
				}else{
					intent.setClass(getActivity(),
							PhoneConfiguration.getInstance().loginActivityClass);
					startActivity(intent);
					if (PhoneConfiguration.getInstance().showAnimation) {
						getActivity().overridePendingTransition(R.anim.zoom_enter,
								R.anim.zoom_exit);
					}
				}
				break;
			case R.id.article_menuitem_refresh:
				int current = mViewPager.getCurrentItem();
				ActivityUtil.getInstance().noticeSaying(getActivity());
				mViewPager.setAdapter(mTabsAdapter);
				mViewPager.setCurrentItem(current);
				break;
			case R.id.article_menuitem_addbookmark:				
				BookmarkTask bt = new BookmarkTask(getActivity());
				bt.execute(String.valueOf(this.tid));
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
			case R.id.item_share:
				intent.setAction(Intent.ACTION_SEND);
				intent.setType("text/plain");
				String shareUrl = "http://nga.178.com/read.php?";
				if(this.pid != 0){
					shareUrl = shareUrl + "pid="+this.pid+" (������NGA�ͻ��˿�Դ��)";
				}
				else
				{
					shareUrl = shareUrl + "tid="+this.tid+" (������NGA�ͻ��˿�Դ��)";
				}
				if(!StringUtil.isEmpty(this.title)){
					shareUrl = "��"+this.title+"�� - ������˹���ҵ�����̳,��ַ:"+shareUrl;
				}
				intent.putExtra(Intent.EXTRA_TEXT, shareUrl);
				String text = getResources().getString(R.string.share);
				getActivity().startActivity(Intent.createChooser(intent, text));
				break;
			case R.id.article_menuitem_back:
			default:
				getActivity().getSupportFragmentManager()
					.beginTransaction().remove(this).commit();
				OnChildFragmentRemovedListener father = null;
        		try{
        			 father = (OnChildFragmentRemovedListener) getActivity();
        			 father.OnChildFragmentRemoved(getId());
        		}catch(ClassCastException e){
        			Log.e(TAG,"father activity does not implements interface " 
        					+ OnChildFragmentRemovedListener.class.getName());
        			
        		}
				break;
		}
		return true;
	}

	private void nightMode(final MenuItem menu) {
	
		String alertString = getString(R.string.change_nigmtmode_string);
		final AlertDialogFragment f = AlertDialogFragment.create(alertString);
		f.setOkListener(new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {

				
				ThemeManager tm = ThemeManager.getInstance();
				SharedPreferences share = getActivity().getSharedPreferences(PERFERENCE,
						Activity.MODE_PRIVATE);
				int mode = ThemeManager.MODE_NORMAL;
				if (tm.getMode() == ThemeManager.MODE_NIGHT) {//������ģʽ���İ����
					menu.setIcon(  
		                    R.drawable.ic_action_bightness_low); 
					menu.setTitle(R.string.change_night_mode);
					Editor editor = share.edit();
					editor.putBoolean(NIGHT_MODE, false);
					editor.commit();
				}else{
					menu.setIcon(  
		                    R.drawable.ic_action_brightness_high); 
					menu.setTitle(R.string.change_daily_mode);
					Editor editor = share.edit();
					editor.putBoolean(NIGHT_MODE, true);
					editor.commit();
					mode = ThemeManager.MODE_NIGHT;
				}
				Log.i(TAG,"frag");
				ThemeManager.getInstance().setMode(mode);
				Intent intent = getActivity().getIntent();
				intent.putExtra("daulscrshowmode", 1);
				intent.putExtra("tid", tid);
				intent.putExtra("pid", pid);
				intent.putExtra("authorid", authorid);
				intent.putExtra("url", url);
				
				getActivity().overridePendingTransition(0, 0);
				getActivity().finish();
				getActivity().overridePendingTransition(0, 0);
				getActivity().startActivity(intent);
			}
			
		});
		f.setCancleListener(new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				f.dismiss();
			}
			
		});
		f.show(getActivity().getSupportFragmentManager(),ALERT_DIALOG_TAG);
	}
	private ImageButton getActionItem(int id){
		//View actionbar_compat = getActivity().findViewById(R.id.actionbar_compat);
		View ret = null;
		/*if(actionbar_compat != null)
		{
			ret = actionbar_compat.findViewById(id);
		}*/
		return (ImageButton) ret;
	}
	
	private void handleLockOrientation(MenuItem item){
		int preOrentation = ThemeManager.getInstance().screenOrentation;
		int newOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
		ImageButton compat_item = null;//getActionItem(R.id.actionbar_compat_item_lock);
		
		if(preOrentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				preOrentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
			//restore
			//int newOrientation = ActivityInfo.SCREEN_ORIENTATION_USER;
			ThemeManager.getInstance().screenOrentation = newOrientation;
			
			getActivity().setRequestedOrientation(newOrientation);
			item.setTitle(R.string.lock_orientation);
			item.setIcon(R.drawable.ic_lock_screen);
			if(compat_item !=null)
				compat_item.setImageResource(R.drawable.ic_lock_screen);
			
		}else{
			newOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			Display dis = getActivity().getWindowManager().getDefaultDisplay();
			//Point p = new Point();
			//dis.getSize(p);
			if(dis.getWidth() < dis.getHeight()){
				newOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			}
			
			ThemeManager.getInstance().screenOrentation = newOrientation;
			getActivity().setRequestedOrientation(newOrientation);			
			item.setTitle(R.string.unlock_orientation);
			item.setIcon(R.drawable.ic_menu_always_landscape_portrait);
			if(compat_item !=null)
				compat_item.setImageResource(R.drawable.ic_menu_always_landscape_portrait);
		}
		
		
		
		SharedPreferences share = getActivity().getSharedPreferences(PERFERENCE,
				Activity.MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putInt(SCREEN_ORENTATION, newOrientation);
		editor.commit();
		
	}

	@Override
	public void finishLoad(ThreadData data) {
		int exactCount = 1 + data.getThreadInfo().getReplies()/20;
		if(mTabsAdapter.getCount() != exactCount
				&&this.authorid == 0){
			mTabsAdapter.setCount(exactCount);
		}
		if(this.authorid>0){
			exactCount = 1 + data.get__ROWS()/20;
			mTabsAdapter.setCount(exactCount);
		}
		if( tid != data.getThreadInfo().getTid()) // mirror thread
			tid = data.getThreadInfo().getTid();
		title=data.getThreadInfo().getSubject();
		
		
	}
	
	private void createGotoDialog(){

		int count = mTabsAdapter.getCount();
		Bundle args = new Bundle();
		args.putInt("count", count);
		
		DialogFragment df = new GotoDialogFragment();
		df.setArguments(args);
		
		FragmentManager fm = getActivity().getSupportFragmentManager();
		
		Fragment prev = fm.findFragmentByTag(GOTO_TAG);
		if(prev != null){
			fm.beginTransaction().remove(prev).commit();
		}
		df.show(fm, GOTO_TAG);
		
	}

	@Override
	public int getCurrentPage() {
		return mViewPager.getCurrentItem()+1;
		
	}

	@Override
	public void setCurrentItem(int index) {
		mViewPager.setCurrentItem(index);
		
	}

	
	
}
