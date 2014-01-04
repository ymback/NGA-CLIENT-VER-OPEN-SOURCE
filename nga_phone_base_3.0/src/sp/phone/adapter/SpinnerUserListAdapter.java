package sp.phone.adapter;

import java.util.ArrayList;
import java.util.List;

import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.User;
import sp.phone.utils.StringUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

public class SpinnerUserListAdapter extends BaseAdapter
implements PerferenceConstant{
	protected List<User> userList;
	protected Context context;
	public SpinnerUserListAdapter(Context context) {
		super();
		this.context = context;
		SharedPreferences share = context.getSharedPreferences(PERFERENCE,
				Context.MODE_PRIVATE);
		
		String userListString = share.getString(USER_LIST, "");
		

		//new ArrayList<User>();
		if(StringUtil.isEmpty(userListString)){
			userList = new ArrayList<User>();
		}else
		{
			userList = JSON.parseArray(userListString, User.class);
		}
		
	}
	

	@Override
	public int getCount() {
		if(userList == null)
			return 0;
		return userList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return userList.get(position);
	}

	@Override
	public long getItemId(int position) {
		final String uid = userList.get(position).getUserId();
		long ret = 0;
		try{
			ret = Long.valueOf(uid);
		}catch(RuntimeException e){
			e.printStackTrace();
		}
		return ret;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){
			convertView = new TextView(context);
		}
		((TextView)convertView).setText(userList.get(position).getNickName());
		((TextView)convertView).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
		return convertView;
	}


}
