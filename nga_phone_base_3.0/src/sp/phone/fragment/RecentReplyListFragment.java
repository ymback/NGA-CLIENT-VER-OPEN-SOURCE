package sp.phone.fragment;

import gov.anzong.androidnga2.R;
import gov.anzong.androidnga2.activity.MyApp;
import gov.anzong.androidnga2.activity.ReplyListActivity;

import java.util.List;

import sp.phone.utils.ActivityUtil;
import sp.phone.adapter.RecentReplyAdapter;
import sp.phone.bean.NotificationObject;
import sp.phone.bean.PerferenceConstant;
import sp.phone.bean.User;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import sp.phone.proxy.SlideCutListView;
import sp.phone.proxy.SlideCutListView.RemoveDirection;
import sp.phone.proxy.SlideCutListView.RemoveListener;

import com.alibaba.fastjson.JSON;

public class RecentReplyListFragment extends Fragment
implements RemoveListener{

	private SlideCutListView lv;
	private RecentReplyAdapter adapter;
	private Context mcontext;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		lv = new SlideCutListView(getActivity());
		lv.setCacheColorHint(0x00000000);
		mcontext = container.getContext();
		return lv;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		String str = PhoneConfiguration.getInstance().getReplyString();
		lv.setRemoveListener(this);
		if(!StringUtil.isEmpty(str)){
					List<NotificationObject> list = JSON.parseArray(str, NotificationObject.class);
					if( list != null && list.size() != 0){
						adapter =new RecentReplyAdapter(list,mcontext);
						lv.setAdapter(adapter);
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
				intent.putExtra("fromreplyactivity",1 );
				
				intent.setClass(getActivity(), PhoneConfiguration.getInstance().articleActivityClass);
				getActivity().startActivity(intent);
				
			}
			
		});
	}
	
	@Override
    public void onResume() {
		if(PhoneConfiguration.getInstance().fullscreen){
        ActivityUtil.getInstance().setFullScreen(lv);
        }
		super.onResume();
    }
	
	@Override
	public void removeItem(RemoveDirection direction, int position) {
		// TODO Auto-generated method stub
		adapter.remove(position);
	}
}
