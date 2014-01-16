package gov.anzong.androidnga.activity;

import gov.anzong.androidnga.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import sp.phone.adapter.NearbyUsersAdapter;
import sp.phone.bean.NearbyUser;
import sp.phone.bean.PerferenceConstant;
import sp.phone.fragment.AlertDialogFragment;
import sp.phone.interfaces.OnNearbyLoadComplete;
import sp.phone.task.NearbyUserTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

public class NearbyUserActivity extends SwipeBackAppCompatActivity
implements PerferenceConstant,OnNearbyLoadComplete{
	ListView lv;
	final private String ALERT_DIALOG_TAG = "alertdialog";
	NearbyUserTask task = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		//this.setContentView(R.layout.webview_layout);
		setTheme(R.style.AppTheme);
		lv = new ListView(this);
		this.setContentView(lv);
		
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
	}
	
	void initLocation()
	{
		if(PhoneConfiguration.getInstance().location == null)
			ActivityUtil.reflushLocation(this);
		
	    Location location = PhoneConfiguration.getInstance().location;

	    SharedPreferences share = getSharedPreferences(
				PERFERENCE, MODE_PRIVATE);
		String userName = share.getString(USER_NAME, "");
		try {
			userName = URLEncoder.encode(userName,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(location == null)
		{
			//Toast.makeText(this, R.string.fail_to_locate, Toast.LENGTH_SHORT).show();
		}else if(StringUtil.isEmpty(userName))
		{
			Toast.makeText(this, R.string.nearby_no_login, Toast.LENGTH_SHORT).show();
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
		if(list != null && list.size() ==0){
			Toast.makeText(this, R.string.nearby_no_user, Toast.LENGTH_SHORT).show();
		}
		
		NearbyUsersAdapter adapter = new NearbyUsersAdapter(list);
		lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				NearbyUser u = (NearbyUser) parent.getItemAtPosition(position);
		    	String loc = "https://maps.google.com.hk/?ie=UTF8&hl=zh-cn&q="
    			+u.getLatitude() + "," + u.getLongitude()
    			+"(" +u.getNickName()+")";
		    	Uri mapUri = Uri.parse(loc);  
    			Intent i = new Intent(Intent.ACTION_VIEW); 
    			i.setData(mapUri);  
       
        		startActivity(i);
				
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
	protected void onStop() {
		if(task != null){
			task.cancel(true);
		}
		super.onStop();
	}

}
