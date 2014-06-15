package sp.phone.adapter;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.Media_Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import noname.gson.parse.NonameThreadBody;
import noname.gson.parse.NonameThreadResponse;
import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.NextJsonNonameTopicListLoader;
import sp.phone.interfaces.NextJsonTopicListLoader;
import sp.phone.task.JsonTopicListLoadTask;
import sp.phone.utils.ActivityUtil;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class AppendableNonameTopicAdapter extends NonameTopicListAdapter {
	final private List<NonameThreadResponse> infoList;
    final private PullToRefreshAttacher attacher;
    private Toast toast = null;
    private final NextJsonNonameTopicListLoader loader;
    private boolean isEndOfList = false;
    private boolean isPrompted = false;
	Set<Integer> tidSet;
	private int table;
	public AppendableNonameTopicAdapter(Context context,PullToRefreshAttacher attacher,NextJsonNonameTopicListLoader loader ) {
		super(context);
		infoList = new ArrayList<NonameThreadResponse>();
		tidSet = new HashSet<Integer>();
        this.attacher = attacher;
        this.loader = loader;
	}

	@Override
	protected NonameThreadBody getEntry(int position) {
		int i=(int) position/20;
		NonameThreadBody tmp = infoList.get(i).data.threads[position%20];
		if(tmp!=null)
			return tmp;
		
		return null;
	}

	@Override
	public void jsonfinishLoad(NonameThreadResponse result) {
        isLoading = false;
        if(attacher !=null)
            attacher.setRefreshComplete();
        if(result == null){
            return;
        }
        ActivityUtil.getInstance().dismiss();

//		if (count != 0) {
//			List<NonameThreadBody> threadList = new ArrayList<NonameThreadBody>();
//			for (int i = 0; i < result.get_data().get_threads().length; i++) {
//				NonameThreadBody info = result.get_data().get_threads()[i];
//				if(info == null){
//					continue;
//				}
//				int tid = info.get_tid();
//				if (!tidSet.contains(tid)) {
//					threadList.add(info);
//					tidSet.add(tid);
//				}
//			}
//			int size = threadList.size();
//			NonameThreadBody[] arr = (NonameThreadBody[]) threadList.toArray(new NonameThreadBody[size]);
//			result.get_data().set_threads(arr);
//		}else{
        Log.i("TAG", String.valueOf(result.data));
			for (int i = 0; i < result.data.threads.length; i++) {
				NonameThreadBody info = result.data.threads[i];
				if(info == null){
					continue;
				}
				int tid = info.tid;
				tidSet.add(tid);
			}
			
//		}

		infoList.add(result);
		count += result.data.threads.length;
		if (result.data.page==result.data.totalpage)
		{
			isEndOfList = true;
		}else{
			isEndOfList = false;
		}
		this.notifyDataSetChanged();
		
	}
	
	public void clear(){
		count = 0;
		infoList.clear();
		tidSet.clear();
		setSelected(-1);
	}
	
	public int getNextPage(){
			return infoList.size() + 1;
	}
	
	public boolean getIsEnd(){
		return isEndOfList;
	}

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View ret = super.getView(position, view, parent);
        if( position +1 == this.getCount() && !isLoading){
        	if (isEndOfList == true)
        	{
        		if (isPrompted == false) {
					if (toast != null)
	            	{
	            		toast.setText(context.getString(R.string.last_page_prompt));
	            		toast.setDuration(Toast.LENGTH_SHORT);
	            		toast.show();
	            	} else
	            	{
	            		toast = Toast.makeText(this.context, context.getString(R.string.last_page_prompt), Toast.LENGTH_SHORT);
	            		toast.show();
	            	}
					isPrompted = true;
				}
        	}
        	else {
            isLoading = true;
            loader.loadNextPage(this);
        	}
        }
        return  ret;
    }
    private boolean isLoading = false;
	

}
