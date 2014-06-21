package sp.phone.fragment;

import gov.anzong.androidnga.activity.MainActivity;
import android.support.v7.app.ActionBarActivity;
import gov.anzong.androidnga.activity.PostActivity;
import gov.anzong.androidnga.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sp.phone.adapter.AppendableMessageAdapter;
import sp.phone.adapter.AppendableTopicAdapter;
import sp.phone.bean.MessageListInfo;
import sp.phone.bean.PerferenceConstant;
import sp.phone.interfaces.NextJsonMessageListLoader;
import sp.phone.interfaces.OnMessageListLoadFinishedListener;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.task.JsonMessageListLoadTask;
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
import android.widget.ListView;

public class MessagelistContainer extends Fragment implements
		OnMessageListLoadFinishedListener, NextJsonMessageListLoader,PerferenceConstant {
	final String TAG = MessagelistContainer.class.getSimpleName();
	static final int MESSAGE_SENT = 1;

	PullToRefreshAttacher attacher = null;
	private ListView listView;
	AppendableMessageAdapter adapter;
	boolean canDismiss = true;
	int category = 0;
	final private String ALERT_DIALOG_TAG = "alertdialog";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			category = savedInstanceState.getInt("category", 0);
		}
		if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
			container.setBackgroundResource(R.color.night_bg_color);
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
		adapter = new AppendableMessageAdapter(this.getActivity(), attacher, this);
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
		JsonMessageListLoadTask task = new JsonMessageListLoadTask(getActivity(),
				this);
		// ActivityUtil.getInstance().noticeSaying(this.getActivity());
		refresh_saying();
		task.execute(getUrl(1, true, true));
	}

	public String getUrl(int page, boolean isend, boolean restart) {

		String jsonUri = HttpUtil.Server + "/nuke.php?__lib=message&__act=message&act=list&";
		
		jsonUri += "page=" + page + "&lite=js&noprefix";

		return jsonUri;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		int menuId;
		if (PhoneConfiguration.getInstance().HandSide == 1) {// lefthand
			int flag = PhoneConfiguration.getInstance().getUiFlag();
			if (flag == 1 || flag == 3 || flag == 5 || flag == 7) {// �����б�UIFLAGΪ1����1+2����1+4����1+2+4
				menuId = R.menu.messagelist_menu_left;
			} else {
				menuId = R.menu.messagelist_menu;
			}
		} else {
			menuId = R.menu.messagelist_menu;
		}
		inflater.inflate(menuId, menu);

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
		case R.id.threadlist_menu_item2:
			this.refresh();
			break;
		case R.id.threadlist_menu_newthread:
			Intent intent_bookmark = new Intent();
			intent_bookmark.putExtra("action", "new");
			intent_bookmark.putExtra("messagemode", "yes");
		if (!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)) {// �����˲��ܷ�
			intent_bookmark.setClass(getActivity(),
					PhoneConfiguration.getInstance().messagePostActivityClass);
		} else {
			intent_bookmark.setClass(getActivity(),
					PhoneConfiguration.getInstance().loginActivityClass);
		}
		startActivityForResult(intent_bookmark,321);
			break;
		case R.id.night_mode:
			nightMode(item);
			break;
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
	
		String alertString = getString(R.string.change_nigmtmode_string_message);
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
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==321){
			refresh();
		}
	}

//	private boolean handlePostThread(MenuItem item) {
//		Intent intent = new Intent();
//		intent.putExtra("fid", fid);
//		intent.putExtra("action", "new");
//		if(!StringUtil.isEmpty(PhoneConfiguration.getInstance().userName)){//�����˲��ܷ�
//			intent.setClass(getActivity(),
//					PhoneConfiguration.getInstance().postActivityClass);
//		}else{
//			intent.setClass(getActivity(),
//				PhoneConfiguration.getInstance().loginActivityClass);
//		}
//		startActivity(intent);
//		if (PhoneConfiguration.getInstance().showAnimation) {
//			getActivity().overridePendingTransition(R.anim.zoom_enter,
//					R.anim.zoom_exit);
//		}
//		return true;
//	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("category", category);
		canDismiss = false;
		super.onSaveInstanceState(outState);
	}

	public void onCategoryChanged(int position) {
		if (position != category) {
			category = position;
			refresh();
		}
	}

	@Override
	public void jsonfinishLoad(MessageListInfo result) {
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
	private void RunParallen(JsonMessageListLoadTask task) {
		task.executeOnExecutor(JsonMessageListLoadTask.THREAD_POOL_EXECUTOR,
				getUrl(adapter.getNextPage(), adapter.getIsEnd(), false));
	}

	@Override
	public void loadNextPage(OnMessageListLoadFinishedListener callback) {
		JsonMessageListLoadTask task = new JsonMessageListLoadTask(getActivity(),
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

		/*
		 * @Override public void onPullDownToRefresh(
		 * PullToRefreshBase<ListView> refreshView) { refresh();
		 * 
		 * }
		 * 
		 * @Override public void onPullUpToRefresh( PullToRefreshBase<ListView>
		 * refreshView) { JsonTopicListLoadTask task = new
		 * JsonTopicListLoadTask(getActivity(), new
		 * OnTopListLoadFinishedListener(){
		 * 
		 * @Override public void jsonfinishLoad( TopicListInfo result) {
		 * mPullRefreshListView.onRefreshComplete(); if(result == null) return;
		 * ActivityUtil.getInstance().dismiss(); adapter.jsonfinishLoad(result);
		 * 
		 * }
		 * 
		 * } ); ActivityUtil.getInstance().noticeSaying(getActivity());
		 * task.execute(getUrl(adapter.getNextPage()));
		 * 
		 * }
		 */

		@Override
		public void onRefreshStarted(View view) {

			refresh();
		}
	}
}
