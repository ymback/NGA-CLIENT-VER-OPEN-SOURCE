package sp.phone.fragment;
import gov.anzong.androidnga.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sp.phone.adapter.TopicListAdapter;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.EnterJsonArticle;
import sp.phone.interfaces.OnTopListLoadFinishedListener;
import sp.phone.task.JsonTopicListLoadTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TopicListFragment extends Fragment
	implements OnTopListLoadFinishedListener{
	static final String TAG = TopicListFragment.class.getSimpleName();
	ListView listview=null;
	TopicListAdapter adapter=null;
	JsonTopicListLoadTask task=null;
	String key = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		if(adapter== null)
			adapter =new TopicListAdapter(this.getActivity());
		Log.d(TAG,"onCreate" + (1+getArguments().getInt("page")) );


		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG,"onCreateView" + (1+getArguments().getInt("page")) );
		listview = new ListView(getActivity());
		try{
			OnItemClickListener father = (OnItemClickListener)getActivity();
			listview.setOnItemClickListener(father);
		}catch(ClassCastException e){
			Log.i(TAG,  "father activity didn't implement OnItemClickListener, open a new activity");
			listview.setOnItemClickListener(new EnterJsonArticle(getActivity()));
		}
		
		
		if(PhoneConfiguration.getInstance().showAnimation)
		{
			LayoutAnimationController anim = AnimationUtils.loadLayoutAnimation
					(getActivity(), R.anim.topic_list_anim);
			listview.setLayoutAnimation(anim);
		}
		listview.setDivider(null);
		listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		//listview.setTextFilterEnabled(true);
		//listview.setFocusableInTouchMode(true);

		return listview;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG,"onActivityCreated" + (1+getArguments().getInt("page")) );
		listview.setAdapter(adapter);
	}
	
	

	@Override
	public void onStart() {
		Log.d(TAG,"onStart" + (1+getArguments().getInt("page")) );
		this.loadPage();
		super.onStart();
	}
	
	

	@Override
	public void onResume() {
		//this.setHasOptionsMenu(true);
		Log.d(TAG,"onResume" + (1+getArguments().getInt("page")) );
		
		super.onResume();
	}
	
	private void loadPage(){
		if(task == null)
		{
			int index = getArguments().getInt("page");
			Activity activity = getActivity();
			final String page = String.valueOf(1 + index );
			String fidString = String.valueOf(getArguments().getInt("id",0));
			String authoridString = String.valueOf(getArguments().getInt("authorid",0));
			int searchpost = getArguments().getInt("searchpost",0);
			int favor = getArguments().getInt("favor", 0);
			key = getArguments().getString("key");
			
			String jsonUri = HttpUtil.Server + "/thread.php?";
			if(!fidString.equals("0"))
				jsonUri +="fid=" + fidString + "&";
			if(!authoridString.equals("0"))
				jsonUri +="authorid=" + authoridString + "&";
			if(searchpost !=0)
				jsonUri +="searchpost=" + searchpost + "&";
			if(favor !=0)
				jsonUri +="favor=" + favor + "&";
			if(!StringUtil.isEmpty(key)){
				jsonUri += "key=" +StringUtil.encodeUrl(key, "GBK") + "&";
			}
			
			jsonUri += "page="+ page + "&lite=js&noprefix";
			
			task = new JsonTopicListLoadTask(activity,this);
			task.execute(jsonUri);
		}else{
			ActivityUtil.getInstance().dismiss();
		}
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		task.cancel(false);
		super.onSaveInstanceState(outState);
		outState.putInt("page", this.getArguments().getInt("page"));
	}

	
	


	@Override
	public void jsonfinishLoad(TopicListInfo result) {
		if(result == null){
			return;
		}
		
		adapter.jsonfinishLoad(result);
		//listview.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		
		if(getArguments().getInt("searchpost",0) != 0){
			int page = 1 + getArguments().getInt("page",0);
			result.set__ROWS(page);
		}
		OnTopListLoadFinishedListener father = null;
		try{
			father = (OnTopListLoadFinishedListener)getActivity();
			if(father != null)
				father.jsonfinishLoad(result);
		}catch(ClassCastException e){
			Log.e(TAG, "father activity should implements " + OnTopListLoadFinishedListener.class.getCanonicalName());
		}
	}



	
	
	
	
	
}
