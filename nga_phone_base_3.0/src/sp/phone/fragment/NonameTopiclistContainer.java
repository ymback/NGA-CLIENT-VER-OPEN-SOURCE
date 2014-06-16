package sp.phone.fragment;

import android.support.v7.app.ActionBarActivity;
import gov.anzong.androidnga2.R;
import gov.anzong.androidnga2.activity.MainActivity;
import gov.anzong.androidnga2.activity.PostActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import noname.gson.parse.NonameThreadResponse;
import sp.phone.adapter.AppendableNonameTopicAdapter;
import sp.phone.adapter.AppendableTopicAdapter;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.NextJsonNonameTopicListLoader;
import sp.phone.interfaces.NextJsonTopicListLoader;
import sp.phone.interfaces.OnNonameTopListLoadFinishedListener;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.task.JsonNonameTopicListLoadTask;
import sp.phone.task.JsonTopicListLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class NonameTopiclistContainer extends Fragment implements
	OnNonameTopListLoadFinishedListener, NextJsonNonameTopicListLoader,PerferenceConstant {
	final String TAG = NonameTopiclistContainer.class.getSimpleName();
	final private String ALERT_DIALOG_TAG = "alertdialog";
	static final int MESSAGE_SENT = 1;
	int fid;
	int authorid;
	int searchpost;
	int favor;
	String key;
	String table;
	String fidgroup;
	String author;

	PullToRefreshAttacher attacher = null;
	private ListView listView;
	AppendableNonameTopicAdapter adapter;
	boolean canDismiss = true;
	int category = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			category = savedInstanceState.getInt("category", 0);
		}

		try {
			PullToRefreshAttacherOnwer attacherOnwer = (PullToRefreshAttacherOnwer) getActivity();
			attacher = attacherOnwer.getAttacher();

		} catch (ClassCastException e) {
			Log.e(TAG,
					"father activity should implement PullToRefreshAttacherOnwer");
		}

		listView = new ListView(getActivity());
		listView.setDivider(null);
		adapter = new AppendableNonameTopicAdapter(this.getActivity(), attacher, this);
		listView.setAdapter(adapter);
		// mPullRefreshListView.setAdapter(adapter);
		try {
			OnItemClickListener listener = (OnItemClickListener) getActivity();
			// mPullRefreshListView.setOnItemClickListener(listener);
			listView.setOnItemClickListener(listener);
		} catch (ClassCastException e) {
			Log.e(TAG, "father activity should implenent OnItemClickListener");
		}

		// mPullRefreshListView.setOnRefreshListener(new
		// ListRefreshListener());\
		if (attacher != null)
			attacher.addRefreshableView(listView, new ListRefreshListener());

		fid = 0;
		authorid = 0;
		String url = getArguments().getString("url");

		if (url != null) {

			fid = getUrlParameter(url, "fid");
			authorid = getUrlParameter(url, "authorid");
			searchpost = getUrlParameter(url, "searchpost");
			favor = getUrlParameter(url, "favor");
			key = StringUtil.getStringBetween(url, 0, "key=", "&").result;
			author = StringUtil.getStringBetween(url, 0, "author=", "&").result;
			table = StringUtil.getStringBetween(url, 0, "table=", "&").result;
			fidgroup = StringUtil.getStringBetween(url, 0, "fidgroup=", "&").result;
		} else {
			fid = getArguments().getInt("fid", 0);
			authorid = getArguments().getInt("authorid", 0);
			searchpost = getArguments().getInt("searchpost", 0);
			favor = getArguments().getInt("favor", 0);
			key = getArguments().getString("key");
			author = getArguments().getString("author");
			table = getArguments().getString("table");
			fidgroup = getArguments().getString("fidgroup");
		}

		if (favor != 0) {
			Toast.makeText(
					getActivity(),
					"长按可删除收藏的帖子", Toast.LENGTH_SHORT).show();
			if (getActivity() instanceof OnItemLongClickListener) {
			 listView.setLongClickable(true);
			 listView.setOnItemLongClickListener((OnItemLongClickListener)
			 getActivity()); }
		}

		// JsonTopicListLoadTask task = new
		// JsonTopicListLoadTask(getActivity(),this);
		// task.execute(getUrl(1));
		return listView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		canDismiss = true;
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		this.refresh();
		super.onViewCreated(view, savedInstanceState);
	}

	private void refresh_saying() {
		DefaultHeaderTransformer transformer = null;

		if (attacher != null) {
			uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.HeaderTransformer headerTransformer;
			headerTransformer = attacher.getHeaderTransformer();
			if (headerTransformer != null
					&& headerTransformer instanceof DefaultHeaderTransformer)
				transformer = (DefaultHeaderTransformer) headerTransformer;
		}

		if (transformer == null)
			ActivityUtil.getInstance().noticeSaying(this.getActivity());
		else
			transformer.setRefreshingText(ActivityUtil.getSaying());
		if (attacher != null)
			attacher.setRefreshing(true);
	}

	void refresh() {
		JsonNonameTopicListLoadTask task = new JsonNonameTopicListLoadTask(getActivity(),
				this);
		// ActivityUtil.getInstance().noticeSaying(this.getActivity());
		refresh_saying();
		task.execute(getUrl(1, true, true));
	}

	public String getNfcUrl() {
		final String scheme = getResources().getString(R.string.myscheme);
		final StringBuilder sb = new StringBuilder(scheme);
		sb.append("://nga.178.com/thread.php?");
		if (fid != 0) {
			sb.append("fid=");
			sb.append(fid);
			sb.append('&');
		}
		if (authorid != 0) {
			sb.append("authorid=");
			sb.append(authorid);
			sb.append('&');
		}
		if (this.searchpost != 0) {
			sb.append("searchpost=");
			sb.append(searchpost);
			sb.append('&');
		}

		return sb.toString();
	}

	public String getUrl(int page, boolean isend, boolean restart) {

		String jsonUri = HttpUtil.NonameServer + "/thread.php?";
		jsonUri += "page=" + page;

		return jsonUri;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		int menuId;
		if (PhoneConfiguration.getInstance().HandSide == 1) {// lefthand
			int flag = PhoneConfiguration.getInstance().getUiFlag();
			if (flag == 1 || flag == 3 || flag == 5 || flag == 7) {// 主题列表，UIFLAG为1或者1+2或者1+4或者1+2+4
				menuId = R.menu.nonamethreadlist_menu_left;
			} else {
				menuId = R.menu.nonamethreadlist_menu;
			}
		} else {
			menuId = R.menu.nonamethreadlist_menu;
		}
		inflater.inflate(menuId, menu);
		/*
		 * if(ActivityUtil.isLessThan_3_0()) { for(int i=0; i< menu.size();
		 * ++i){ menu.getItem(i).setVisible(false); } }
		 */

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
		switch (item.getItemId()) {
		case R.id.threadlist_menu_newthread:
			handlePostThread(item);
			break;
		case R.id.threadlist_menu_item2:
			this.refresh();
			break;
//		case R.id.goto_bookmark_item:
//			Intent intent_bookmark = new Intent(getActivity(),
//					PhoneConfiguration.getInstance().topicActivityClass);
//			intent_bookmark.putExtra("favor", 1);
//			startActivity(intent_bookmark);
//			break;
		case R.id.night_mode:
			nightMode(item);
			break;
//		case R.id.search:
//			handleSearch();
//			break;
		case R.id.threadlist_menu_item3:
		default:
			// case android.R.id.home:
			Intent intent = new Intent(getActivity(), MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
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
				if (tm.getMode() == ThemeManager.MODE_NIGHT) {//是晚上模式，改白天的
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
	
	private boolean handlePostThread(MenuItem item) {
		Intent intent = new Intent();
		intent.putExtra("fid", fid);
		intent.putExtra("action", "new");
		if(!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)){//登入了才能发
			intent.setClass(getActivity(),
					PhoneConfiguration.getInstance().nonamePostActivityClass);
		}else{
			intent.setClass(getActivity(),
				PhoneConfiguration.getInstance().loginActivityClass);
		}
		startActivity(intent);
		if (PhoneConfiguration.getInstance().showAnimation) {
			getActivity().overridePendingTransition(R.anim.zoom_enter,
					R.anim.zoom_exit);
		}
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("category", category);
		canDismiss = false;
		super.onSaveInstanceState(outState);
	}

	private void handleSearch() {
		Bundle arg = new Bundle();
		arg.putInt("id", fid);
		arg.putInt("authorid", authorid);
		DialogFragment df = new SearchDialogFragment();
		df.setArguments(arg);
		final String dialogTag = "search_dialog";
		FragmentManager fm = getActivity().getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment prev = fm.findFragmentByTag(dialogTag);
		if (prev != null) {
			ft.remove(prev);
		}
		try {
			df.show(ft, dialogTag);
		} catch (Exception e) {
			Log.e(NonameTopiclistContainer.class.getSimpleName(),
					Log.getStackTraceString(e));

		}
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

	public void onCategoryChanged(int position) {
		if (position != category) {
			category = position;
			refresh();
		}
	}

	@Override
	public void jsonfinishLoad(NonameThreadResponse result) {
		if (attacher != null)
			attacher.setRefreshComplete();

		if (result == null)
			return;
		adapter.clear();
		adapter.jsonfinishLoad(result);
		listView.setAdapter(adapter);
		if (canDismiss)
			ActivityUtil.getInstance().dismiss();

	}

	@TargetApi(11)
	private void RunParallen(JsonNonameTopicListLoadTask task) {
		task.executeOnExecutor(JsonNonameTopicListLoadTask.THREAD_POOL_EXECUTOR,
				getUrl(adapter.getNextPage(), adapter.getIsEnd(), false));
	}

	@Override
	public void loadNextPage(OnNonameTopListLoadFinishedListener callback) {
		JsonNonameTopicListLoadTask task = new JsonNonameTopicListLoadTask(getActivity(),
				callback);
		refresh_saying();
		if (ActivityUtil.isGreaterThan_2_3_3())
			RunParallen(task);
		else
			task.execute(getUrl(adapter.getNextPage(), adapter.getIsEnd(),
					false));
	}

	class ListRefreshListener implements
			PullToRefreshAttacher.OnRefreshListener {

		@Override
		public void onRefreshStarted(View view) {

			refresh();
		}
	}


}
