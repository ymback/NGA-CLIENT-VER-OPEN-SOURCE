package sp.phone.adapter;

import gov.anzong.androidnga.R;
import sp.phone.bean.User;
import sp.phone.utils.PhoneConfiguration;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

public class UserListAdapter extends SpinnerUserListAdapter
implements  OnClickListener{


	public UserListAdapter(Context context){
		super(context);
		this.context = context;
	
		
	}
	


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.user_list,parent,false);
		}

		tv = (TextView) convertView.findViewById(R.id.user_name);
		ImageButton b = (ImageButton) convertView.findViewById(R.id.delete_user);
		b.setTag(position);
		b.setOnClickListener(this);
		
		tv.setText(userList.get(position).getNickName());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		return convertView;
	}

	@Override
	public void onClick(View v) {
		ImageButton b = (ImageButton)v;
		int position = (Integer)b.getTag();
		userList.remove(position);
		
		SharedPreferences share = context.getSharedPreferences(PERFERENCE,
				Context.MODE_PRIVATE);
		
		String userListString = JSON.toJSONString(userList);
		share.edit().putString(USER_LIST, userListString).commit();
		
		if(position == 0){
			if(userList.size() == 0){
				PhoneConfiguration.getInstance().setUid("");
				PhoneConfiguration.getInstance().setNickname("");
				PhoneConfiguration.getInstance().setCid("");			
				PhoneConfiguration.getInstance().setReplyString("");
				PhoneConfiguration.getInstance().setReplyTotalNum(0);
				Editor editor = share.edit();
				editor.putString(UID, "");
				editor.putString(CID, "");
				editor.putString(USER_NAME, "" );
				editor.putString(PENDING_REPLYS, "");
				editor.putString(REPLYTOTALNUM, "0");
				editor.commit();
			}else{
				User u = userList.get(0);
				PhoneConfiguration.getInstance().setUid(u.getUserId());
				PhoneConfiguration.getInstance().setNickname(u.getNickName());
				PhoneConfiguration.getInstance().setCid(u.getCid());
				PhoneConfiguration.getInstance().setReplyString(u.getReplyString());
				PhoneConfiguration.getInstance().setReplyTotalNum(u.getReplyTotalNum());
			}
		}
		
		this.notifyDataSetInvalidated();
		
		
	}

}
