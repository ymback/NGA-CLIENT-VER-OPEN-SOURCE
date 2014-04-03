package sp.phone.fragment;

import gov.anzong.androidnga.activity.MainActivity;
import gov.anzong.androidnga.activity.PostActivity;
import gov.anzong.androidnga.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sp.phone.adapter.AppendableTopicAdapter;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.NextJsonTopicListLoader;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.task.JsonTopicListLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import android.annotation.TargetApi;
import android.content.Intent;
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



public class TopiclistContainer extends Fragment
implements OnTopListLoadFinishedListener,NextJsonTopicListLoader {
	final String TAG = TopiclistContainer.class.getSimpleName();
	static final int MESSAGE_SENT = 1;
	int fid;
	int authorid;
	int searchpost;
	int favor;
	String key;
	String table;
	String author;

    PullToRefreshAttacher attacher = null;
    private ListView listView;
	AppendableTopicAdapter adapter;
	boolean canDismiss = true;
	int category = 0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(savedInstanceState != null){
			category = savedInstanceState.getInt("category", 0);
		}

        try{
            PullToRefreshAttacherOnwer attacherOnwer = (PullToRefreshAttacherOnwer) getActivity();
            attacher = attacherOnwer.getAttacher();

        }catch(ClassCastException e){
            Log.e(TAG, "father activity should implement PullToRefreshAttacherOnwer");
        }

        listView  = new ListView(getActivity());
        listView.setDivider(null);
		adapter = new AppendableTopicAdapter(this.getActivity(),attacher,this);
        listView.setAdapter(adapter);
		//mPullRefreshListView.setAdapter(adapter);
		try{
			OnItemClickListener listener = (OnItemClickListener) getActivity();
			//mPullRefreshListView.setOnItemClickListener(listener);
            listView.setOnItemClickListener(listener);
		}catch(ClassCastException e){
			Log.e(TAG, "father activity should implenent OnItemClickListener");
		}
		
		
		//mPullRefreshListView.setOnRefreshListener(new ListRefreshListener());\
        if(attacher != null)
            attacher.addRefreshableView(listView,new ListRefreshListener());
		
		
		

		
		fid = 0;
		authorid = 0;
		String url = getArguments().getString("url");
		
		if(url != null){
			
			fid = getUrlParameter(url,"fid");
			authorid = getUrlParameter(url,"authorid");
			searchpost = getUrlParameter(url,"searchpost");
			favor = getUrlParameter(url,"favor");
			key = StringUtil.getStringBetween(url, 0, "key=", "&").result;
			author = StringUtil.getStringBetween(url, 0, "author=", "&").result;
			table = StringUtil.getStringBetween(url, 0, "table=", "&").result;
		}
		else
		{
			fid =  getArguments().getInt("fid", 0);
			authorid =  getArguments().getInt("authorid", 0);
			searchpost =  getArguments().getInt("searchpost", 0);
			favor =  getArguments().getInt("favor", 0);
			key =  getArguments().getString("key");
			author=getArguments().getString("author");
			table =  getArguments().getString("table");
		}
		



		
		if(favor != 0){
			this.getActivity().setTitle(R.string.bookmark_title);
			//删除收藏夹功能是个大坑，不要碰
			//好吧不要碰
			/*
			if (getActivity() instanceof OnItemLongClickListener)
			{
				listView.setLongClickable(true);
				listView.setOnItemLongClickListener((OnItemLongClickListener) getActivity());
			}
			*/
		}
		
		if(!StringUtil.isEmpty(key))
		{
			final String title = this.getResources().getString(android.R.string.search_go)
					+ ":"+key;
			this.getActivity().setTitle(title);
		}
		
		//JsonTopicListLoadTask task = new JsonTopicListLoadTask(getActivity(),this);
		//task.execute(getUrl(1));
		
		
		


		
		
		return listView;
	}
	
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		canDismiss = true;
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		this.refresh();
		super.onViewCreated(view, savedInstanceState);
	}

    private void  refresh_saying(){
        DefaultHeaderTransformer transformer = null;

        if(attacher != null)
        {
            uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.HeaderTransformer headerTransformer;
            headerTransformer = attacher.getHeaderTransformer();
            if(headerTransformer != null && headerTransformer instanceof DefaultHeaderTransformer )
                transformer = (DefaultHeaderTransformer) headerTransformer;
        }

        if(transformer == null)
            ActivityUtil.getInstance().noticeSaying(this.getActivity());
        else
            transformer.setRefreshingText(ActivityUtil.getSaying());
        if(attacher != null)
            attacher.setRefreshing(true);
    }

	void refresh(){
		JsonTopicListLoadTask task = new JsonTopicListLoadTask(getActivity(),this);
		//ActivityUtil.getInstance().noticeSaying(this.getActivity());
        refresh_saying();
		task.execute(getUrl(1,true,true));
	}
	
	public String getNfcUrl() {
		final String scheme = getResources().getString(R.string.myscheme);
		final StringBuilder sb = new StringBuilder(scheme);
		sb.append("://nga.178.com/thread.php?");
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


		return sb.toString();
	}
	
	public String getUrl(int page,boolean isend,boolean restart){
		
		String jsonUri = HttpUtil.Server + "/thread.php?";
		if(0!= authorid)
			jsonUri +="authorid=" + authorid + "&";
		if(searchpost !=0)
			jsonUri +="searchpost=" + searchpost + "&";
		if(favor !=0)
			jsonUri +="favor=" + favor + "&";
		if(!StringUtil.isEmpty(author)){
			try {
				if(author.endsWith("&searchpost=1")){
					jsonUri += "key=&author=" + URLEncoder.encode(author.substring(0, author.length()-13), "GBK") + "&searchpost=1&";
				}else{
					jsonUri += "key=&author=" + URLEncoder.encode(author, "GBK") + "&";
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			if( 0 != fid)
				jsonUri +="fid=" + fid + "&";
			if(!StringUtil.isEmpty(key)){
				jsonUri += "key=" +StringUtil.encodeUrl(key, "GBK") + "&";
		}}
		if(table!=null && !table.equals("")){
			if(isend){
				if(restart){
					table="6";
				}else{
					table=String.valueOf(Integer.parseInt(table)-1);
				}
				page=1;
			}
			jsonUri += "table=" + table + "&";
		}
		jsonUri += "page="+ page + "&lite=js&noprefix";
		switch(category){
			case 2:
				jsonUri += "&recommend=1&order_by=postdatedesc&admin=1";
				break;
			case 1:
				jsonUri += "&recommend=1&order_by=postdatedesc&user=1";
				break;
			case 0:
			default:
		}
		
		return jsonUri;
	}
	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
		inflater.inflate(menuId, menu);
		/*if(ActivityUtil.isLessThan_3_0())
		{
			for(int i=0; i< menu.size(); ++i){
				menu.getItem(i).setVisible(false);
			}
		}*/

	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId())
		{
			case R.id.threadlist_menu_newthread :
				handlePostThread(item);
				break;
			case R.id.threadlist_menu_item2 :
				/*int current = this.mViewPager.getCurrentItem();
				ActivityUtil.getInstance().noticeSaying(this);
				this.mViewPager.setAdapter(this.mTabsAdapter);
				this.mViewPager.setCurrentItem(current, true);*/
				this.refresh();
				break;
			case R.id.goto_bookmark_item:
				Intent intent_bookmark = new Intent(getActivity(), PhoneConfiguration.getInstance().topicActivityClass);
				intent_bookmark.putExtra("favor", 1);
				startActivity(intent_bookmark);
				break;
			case R.id.search:
				handleSearch();
				break;
			case R.id.threadlist_menu_item3 :
			default:
				//case android.R.id.home:
				Intent intent = new Intent(getActivity(), MainActivity.class);
	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivity(intent);
				break;
		}
		return true;
	}
	
	private boolean handlePostThread(MenuItem item){
		
		
		
		Intent intent = new Intent();
		//intent.putExtra("prefix",postPrefix.toString());
		intent.putExtra("fid", fid);
		intent.putExtra("action", "new");
		
		intent.setClass(getActivity(), PhoneConfiguration.getInstance().postActivityClass);
		startActivity(intent);
		if(PhoneConfiguration.getInstance().showAnimation)
		{
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

	private void handleSearch(){
		Bundle arg  = new Bundle();
		arg.putInt("id",fid);
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

        try{
        	df.show(ft, dialogTag);
		}catch(Exception e){
			Log.e(TopiclistContainer.class.getSimpleName(),Log.getStackTraceString(e));

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

	public void onCategoryChanged(int position){
		if(position != category)
		{
			category = position;
			refresh();
		}
	}

	@Override
	public void jsonfinishLoad(TopicListInfo result) {
        if(attacher != null)
            attacher.setRefreshComplete();

		if(result == null)
			return;
		if(result.get__SEARCHNORESULT())
			{
			JsonTopicListLoadTask task = new JsonTopicListLoadTask(getActivity(),this);
	        refresh_saying();
	        if(result.get__TABLE()>0){
	        	if(getActivity()!=null){
		        	Toast.makeText(getActivity(),
							"库"+String.valueOf(result.get__TABLE())+"中的结果已搜索完毕,正在搜索库"+String.valueOf(result.get__TABLE()-1)+"中的结果",
							Toast.LENGTH_SHORT).show();
	        	}
		        if(ActivityUtil.isGreaterThan_2_3_3())
		        	task.executeOnExecutor(JsonTopicListLoadTask.THREAD_POOL_EXECUTOR,getUrl(1,true,false));
		        else
		            task.execute(getUrl(1,true,false));
	        }else{
	        	if(getActivity()!=null){
		        	Toast.makeText(getActivity(),
							"所有数据库结果已搜索完毕",
							Toast.LENGTH_SHORT).show();
	        	}
	        }
			return;
			}
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

		adapter.clear();
		adapter.jsonfinishLoad(result);	
		listView.setAdapter(adapter);
		if(canDismiss)
			ActivityUtil.getInstance().dismiss();
		
	}

    @TargetApi(11)
    private void RunParallen(JsonTopicListLoadTask task)
    {
        task.executeOnExecutor(JsonTopicListLoadTask.THREAD_POOL_EXECUTOR,getUrl(adapter.getNextPage(),adapter.getIsEnd(),false));
    }
    @Override
    public void loadNextPage(OnTopListLoadFinishedListener callback) {
        JsonTopicListLoadTask task = new JsonTopicListLoadTask(getActivity(),callback);
        refresh_saying();
        if(ActivityUtil.isGreaterThan_2_3_3())
            RunParallen(task);
        else
            task.execute(getUrl(adapter.getNextPage(),adapter.getIsEnd(),false));
    }


    class ListRefreshListener implements PullToRefreshAttacher.OnRefreshListener{

        /*
		@Override
		public void onPullDownToRefresh(
				PullToRefreshBase<ListView> refreshView) {
			refresh();
			
		}

		@Override
		public void onPullUpToRefresh(
				PullToRefreshBase<ListView> refreshView) {
			JsonTopicListLoadTask task = new JsonTopicListLoadTask(getActivity(),
					new OnTopListLoadFinishedListener(){

						@Override
						public void jsonfinishLoad(
								TopicListInfo result) {
							mPullRefreshListView.onRefreshComplete();
							if(result == null)
								return;
							ActivityUtil.getInstance().dismiss();
							adapter.jsonfinishLoad(result);
							
						}
				
			} );
			ActivityUtil.getInstance().noticeSaying(getActivity());
			task.execute(getUrl(adapter.getNextPage()));
			
		}
		*/


        @Override
        public void onRefreshStarted(View view) {

            refresh();
        }
    }
}
