package gov.anzong.androidnga2.activity;

import gov.anzong.androidnga2.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import sp.phone.adapter.NearbyUsersAdapter;
import sp.phone.bean.NearbyUser;
import sp.phone.bean.PerferenceConstant;
import sp.phone.fragment.AlertDialogFragment;
import sp.phone.fragment.NearbyAlertDialogFragment;
import sp.phone.interfaces.OnNearbyLoadComplete;
import sp.phone.interfaces.PullToRefreshAttacherOnwer;
import sp.phone.task.NearbyUserTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

public class NearbyUserActivity extends SwipeBackAppCompatActivity
implements PerferenceConstant,OnNearbyLoadComplete,PullToRefreshAttacherOnwer{
	ListView lv;
	final private String ALERT_DIALOG_TAG = "alertdialog";
	NearbyUserTask task = null; 
	private Toast toast = null;
	private PullToRefreshAttacher mPullToRefreshAttacher;

	PullToRefreshAttacher attacher = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		//this.setContentView(R.layout.webview_layout);
		setTheme(R.style.AppTheme);
		lv = new ListView(this);
		this.setContentView(lv);
		getSupportActionBar().setTitle("Yoooo~");
		
		String alertString = this.getString(R.string.find_nearby_alert_string);
		AlertDialogFragment f = AlertDialogFragment.create(alertString);
		f.setOkListener(new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				initLocation();
				
			}
			
		});
		f.setCancleListener(new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
				
			}
			
		});
		f.show(getSupportFragmentManager(), ALERT_DIALOG_TAG);
		//initLocation();
		PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();
		options.refreshScrollDistance = 0.3f;
		options.refreshOnUp = true;
		mPullToRefreshAttacher = PullToRefreshAttacher.get(this, options);
		try {
			PullToRefreshAttacherOnwer attacherOnwer = (PullToRefreshAttacherOnwer) this;
			attacher = attacherOnwer.getAttacher();

		} catch (ClassCastException e) {
			Log.e("NEARBYUSERACTIVITY",
					"father activity should implement PullToRefreshAttacherOnwer");
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
		}else{
		}

		if (transformer == null){
			if(PhoneConfiguration.getInstance().fullscreen){ 
				refresh_saying();
			}else{
				ActivityUtil.getInstance().noticeSaying(this);
			}
			}
		else
			transformer.setRefreshingText(ActivityUtil.getSaying());
		if (attacher != null)
			attacher.setRefreshing(true);
	}
	
	void initLocation()
	{
		if(PhoneConfiguration.getInstance().location == null)
			ActivityUtil.reflushLocation(this);
		
	    Location location = PhoneConfiguration.getInstance().location;

	    SharedPreferences share = getSharedPreferences(
				PERFERENCE, MODE_PRIVATE);
		String userName = share.getString(USER_NAME, "");
		userName =StringUtil.encodeUrl(userName,"utf-8");
		if(location == null)
		{
			//Toast.makeText(this, R.string.fail_to_locate, Toast.LENGTH_SHORT).show();
		}else if(StringUtil.isEmpty(userName))
		{
			if (toast != null)
        	{
        		toast.setText(R.string.nearby_no_login);
        		toast.setDuration(Toast.LENGTH_SHORT);
        		toast.show();
        	} else
        	{
        		toast = Toast.makeText(lv.getContext(),  R.string.nearby_no_login, Toast.LENGTH_SHORT);
        		toast.show();
        	}
		}else
		{
	    	ActivityUtil.getInstance().noticeSaying(this);
	    	task = new NearbyUserTask(location.getLatitude(),location.getLongitude(),
					userName,PhoneConfiguration.getInstance().uid,this);
	    	task.execute();

	    }
	}
	


	@Override
	public void OnComplete(String result) {
		task = null;
		ActivityUtil.getInstance().dismiss();
		if(StringUtil.isEmpty(result))
			return;
		List<NearbyUser> list = null;
		try{
		list = JSON.parseArray(result, NearbyUser.class);
		}catch(Exception e){
			return ;
		}
		Location myloc = PhoneConfiguration.getInstance().location;
		for(int i=0;i<list.size();i++){
			list.get(i).setJuli(String.valueOf(ActivityUtil.distanceBetween(myloc, list.get(i).getLatitude(), list.get(i).getLongitude())));
		}
		attacher.setRefreshComplete();
		if(list != null && list.size() ==0){
			if (toast != null)
        	{
        		toast.setText(R.string.nearby_no_user);
        		toast.setDuration(Toast.LENGTH_SHORT);
        		toast.show();
        	} else
        	{
        		toast = Toast.makeText(lv.getContext(),  R.string.nearby_no_user, Toast.LENGTH_SHORT);
        		toast.show();
        	}
		}
		
		NearbyUsersAdapter adapter = new NearbyUsersAdapter(list);
		lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(final AdapterView<?> parent, View view,
					final int position, long id) {

				final NearbyUser u = (NearbyUser) parent.getItemAtPosition(position);
				String Name=u.getNickName();
				if(Name.indexOf("(")>0){
					Name=Name.substring(0, Name.indexOf("("));
				}
				final String Namea=Name;
				String text=null;
				try {
					text = URLDecoder.decode(Namea,"utf-8");
				} catch (UnsupportedEncodingException e) {
				}
				final String texta=text;
				NearbyAlertDialogFragment f = NearbyAlertDialogFragment.create(getString(R.string.seeingooglemaporprofile));
				f.setOkListener(new OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(StringUtil.isEmail(texta)){
							if (toast != null)
				        	{
				        		toast.setText("用户名为邮箱,无法通过邮箱获取论坛用户信息");
				        		toast.setDuration(Toast.LENGTH_SHORT);
				        		toast.show();
				        	} else
				        	{
				        		toast = Toast.makeText(lv.getContext(), "用户名为邮箱,无法通过邮箱获取论坛用户信息", Toast.LENGTH_SHORT);
				        		toast.show();
				        	}
						}else{
							Intent i = new Intent(Intent.ACTION_VIEW); 
							i.putExtra("mode", "username" );
				    		i.putExtra("username", texta);
				    		i.setClass(lv.getContext(), PhoneConfiguration.getInstance().profileActivityClass);
							if(PhoneConfiguration.getInstance().showAnimation)
								overridePendingTransition(R.anim.zoom_enter,
										R.anim.zoom_exit);
				    		startActivity(i);
						}
					}
					
				});
				f.setCancleListener(new OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {

				    	String loc = "https://maps.google.com.hk/?ie=UTF8&hl=zh-cn&q="
		    			+u.getLatitude() + "," + u.getLongitude()
		    			+"(" +Namea+")";
				    	Uri mapUri = Uri.parse(loc);  
		    			Intent i = new Intent(Intent.ACTION_VIEW); 
		    			i.setData(mapUri);  
		    			
		        		startActivity(i);
						
					}
					
				});
				f.show(getSupportFragmentManager(), ALERT_DIALOG_TAG);
				
				
				
			}
			
		});

		lv.setAdapter(adapter);
		
	}

	@Override
	public void onProgresUpdate(int progress, int total) {
		String saying = getString(R.string.fuck_gfw_prefix) +"("+progress+"/"
				+total+")";
		if(progress > total)
		{
			saying = this.getString(R.string.fail_to_cross_gfw);
		}
		ActivityUtil.getInstance().noticeError(saying,this);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final int flags = ThemeManager.ACTION_BAR_FLAG;
		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	protected void onResume() {
		if(PhoneConfiguration.getInstance().fullscreen){
		ActivityUtil.getInstance().setFullScreen(lv);
		}
		super.onResume();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		default:
			finish();
		}
		return true;
	}
	
	@Override
	protected void onStop() {
		if(task != null){
			task.cancel(true);
		}
		super.onStop();
	}

	@Override
	public PullToRefreshAttacher getAttacher() {
		// TODO Auto-generated method stub
		return mPullToRefreshAttacher;
	}

}
