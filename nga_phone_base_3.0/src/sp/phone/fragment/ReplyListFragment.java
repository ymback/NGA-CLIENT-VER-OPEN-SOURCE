package sp.phone.fragment;

import java.util.List;

import sp.phone.adapter.PendingReplyAdapter;
import sp.phone.bean.NotificationObject;
import sp.phone.bean.PerferenceConstant;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;

public class ReplyListFragment extends Fragment
implements PerferenceConstant{

	private ListView lv;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		lv = new ListView(getActivity());
		return lv;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		SharedPreferences  share = 
			getActivity().getSharedPreferences(PERFERENCE, Context.MODE_PRIVATE);
		String str = share.getString(PENDING_REPLYS,"");
		if(!StringUtil.isEmpty(str)){
			List<NotificationObject> list = JSON.parseArray(str, NotificationObject.class);
			if( list != null && list.size() != 0){
				lv.setAdapter(new PendingReplyAdapter(list));
				
			}
		}
		//lv.setAdapter(adapter);
		lv.setOnItemClickListener( new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				NotificationObject no = (NotificationObject) parent.getItemAtPosition(position);
				if(no == null){
					return;
				}
				
				Intent intent = new Intent();
				intent.putExtra("tab", "1");
				intent.putExtra("tid",no.getTid() );
				intent.putExtra("pid",no.getPid() );
				intent.putExtra("authorid",0 );
				
				intent.setClass(getActivity(), PhoneConfiguration.getInstance().articleActivityClass);
				getActivity().startActivity(intent);
				
			}
			
		});
	}

}
