package gov.anzong.androidnga.activity;

import java.util.List;

import com.alibaba.fastjson.JSON;

import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.User;
import sp.phone.fragment.RecentReplyListFragment;
import sp.phone.fragment.ReplyListFragment;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import gov.anzong.androidnga.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class RecentReplyListActivity extends SwipeBackAppCompatActivity implements PerferenceConstant {
	FragmentManager fm;
	Fragment f;
	View v;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getSupportActionBar().setTitle("ÎÒµÄ±»Åç");
		v= LayoutInflater.from(this).inflate(R.layout.topiclist_activity,null,false);
		this.setContentView(v);
		fm  = this.getSupportFragmentManager();
		f = fm.findFragmentById(R.id.item_list);
		if( f == null)
		{
			f = new RecentReplyListFragment();
			fm.beginTransaction().add(R.id.item_list,f ).commit();
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.recent_reply_menu, menu);
		final int flags = ThemeManager.ACTION_BAR_FLAG;
		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		return super.onCreateOptionsMenu(menu);
	}

	public void removerecentlist(){
		PhoneConfiguration.getInstance().setReplyString("");
		PhoneConfiguration.getInstance().setReplyTotalNum(0);
		SharedPreferences share = getSharedPreferences(PERFERENCE,
				Context.MODE_PRIVATE);
		String userListString = share.getString(USER_LIST, "");
		List<User> userList = null;
		if (!StringUtil.isEmpty(userListString)) {
			userList = JSON.parseArray(userListString, User.class);
			for (User u : userList) {
				if (u.getUserId().equals(
						PhoneConfiguration.getInstance().uid)) {
					MyApp app = ((MyApp) getApplication());
					app.addToUserList(u.getUserId(), u.getCid(),
							u.getNickName(), "", 0);
					break;
				}
			}
		} else {
			Editor editor = share.edit();
			editor.putString(PENDING_REPLYS, "");
			editor.putString(REPLYTOTALNUM,
					"0");
			editor.commit();
		}
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.delectall :
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						removerecentlist();
						fm.beginTransaction().remove(f).commit();
						f = new RecentReplyListFragment();
						fm.beginTransaction().add(R.id.item_list,f).commit();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						// Do nothing
						break;
					}
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(this.getString(R.string.delete_recentreply_confirm_text))
					.setPositiveButton(R.string.confirm, dialogClickListener)
					.setNegativeButton(R.string.cancle, dialogClickListener);
			final AlertDialog dialog = builder.create();
			dialog.show();
			dialog.setOnDismissListener(new AlertDialog.OnDismissListener(){

				@Override
				public void onDismiss(DialogInterface arg0) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					if(PhoneConfiguration.getInstance().fullscreen){
					ActivityUtil.getInstance().setFullScreen(v);
					}
				}
				
			});
			break;
		default:
			finish();
		}
		return true;
	}

}
