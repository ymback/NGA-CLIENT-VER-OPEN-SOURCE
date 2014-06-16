package gov.anzong.androidnga2.activity;

import java.util.List;

import sp.phone.bean.Bookmark;
import sp.phone.bean.PerferenceConstant;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import gov.anzong.androidnga2.R;

import com.alibaba.fastjson.JSON;

public class BookmarkActivity extends SwipeBackAppCompatActivity 
	implements OnItemClickListener, PerferenceConstant{
	List<Bookmark> bookmarks;//reference
	ListView view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int orentation = ThemeManager.getInstance().screenOrentation;
		if(orentation ==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE||
				orentation ==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		{
			setRequestedOrientation(orentation);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
		ThemeManager.SetContextTheme(this);
		//this.setContentView(R.layout.bookmarks);
		// view = (ListView)findViewById(R.id.bookmark_listview);
		view = new ListView(this);
		this.setContentView(view);
		bookmarks = 
				PhoneConfiguration.getInstance().getBookmarks();

		
        view.setAdapter(new BookmarkAdapter());
        view.setOnItemClickListener(this);
        //this.getListView();
        this.registerForContextMenu(view);
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		int flags = 15;
		/*ActionBar.DISPLAY_SHOW_HOME;
		flags |= ActionBar.DISPLAY_USE_LOGO;
		flags |= ActionBar.DISPLAY_SHOW_TITLE;
		flags |= ActionBar.DISPLAY_HOME_AS_UP;
		flags |= ActionBar.DISPLAY_SHOW_CUSTOM;*/
		//final ActionBar bar = getActionBar();
		//bar.setDisplayOptions(flags);
		//ReflectionUtil.actionBar_setDisplayOption(this, flags);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId())
		{
			default:
			//case android.R.id.home:
				this.finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		menu.add(0,0,0,getString(R.string.delete));
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

		switch(item.getItemId())
		{
			case 0:

				SharedPreferences  share = 
						getSharedPreferences(PERFERENCE, MODE_PRIVATE);

					Editor editor = share.edit();
					bookmarks.remove(info.position);
					String jsonString = "";
					if(bookmarks.size()>0)
						jsonString =JSON.toJSONString(bookmarks.getClass());
					editor.putString(BOOKMARKS,jsonString  );
					editor.commit();
					
					BookmarkAdapter ad = (BookmarkAdapter) view.getAdapter();
					ad.notifyDataSetChanged();
				break;
			default:
		}
		return super.onContextItemSelected(item);
	}
	
	private class BookmarkAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return bookmarks.size();
		}

		@Override
		public Object getItem(int position) {
			
			return bookmarks.get(position).getTitle();
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView ==null){
				convertView = new TextView(BookmarkActivity.this);
			}
			
			((TextView) convertView).setText(
					bookmarks.get(position).getTitle());
			Drawable draw = getResources().getDrawable(android.R.drawable.btn_star_big_on);
			((TextView) convertView).setCompoundDrawablesWithIntrinsicBounds(draw,null,null,null);
			return convertView;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String url = bookmarks.get(position).getUrl();
		String tidString = getTid(url);
		int tid = Integer.valueOf(tidString);
		Intent intent = new Intent();
		intent.putExtra("tab", "1");
		intent.putExtra("tid",tid);
		intent.setClass(this, PhoneConfiguration.getInstance().articleActivityClass);
		startActivity(intent);
		if(PhoneConfiguration.getInstance().showAnimation)
			overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
	
		
		
		
	}
	
	private String getTid(String url){
		String tid = "";
		tid = url.substring(url.indexOf("tid=")+4);
		int end = tid.indexOf("&");
		if(end == -1)
			end = tid.length();
		tid = tid.substring(0,end);
		return tid;
	}
	
	
	

}
