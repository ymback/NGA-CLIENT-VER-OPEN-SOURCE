package sp.phone.adapter;

import gov.anzong.androidnga.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sp.phone.bean.MessageListInfo;
import sp.phone.bean.MessageThreadPageInfo;
import sp.phone.interfaces.NextJsonMessageListLoader;
import sp.phone.utils.ActivityUtil;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class AppendableMessageAdapter extends MessageListAdapter {
	final private List<MessageListInfo> infoList;
    final private PullToRefreshAttacher attacher;
    private final NextJsonMessageListLoader loader;
    private boolean isEndOfList = false;
	Set<Integer> midSet;
	Toast toast=null;
	public AppendableMessageAdapter(Context context,PullToRefreshAttacher attacher,NextJsonMessageListLoader loader ) {
		super(context);
		infoList = new ArrayList<MessageListInfo>();
		midSet = new HashSet<Integer>();
        this.attacher = attacher;
        this.loader = loader;
	}
	boolean isPrompted=false;

	@Override
	protected MessageThreadPageInfo getEntry(int position) {
		for(int i=0; i< infoList.size(); i++){
			if(position < (infoList.get(i).get__currentPage()*infoList.get(i).get__rowsPerPage())){
				return infoList.get(i).getMessageEntryList().get(position);
			}
			position -= infoList.get(i).get__rowsPerPage();
		}
		return null;
	}

	@Override
	public void jsonfinishLoad(MessageListInfo result) {
        isLoading = false;
        if(attacher !=null)
            attacher.setRefreshComplete();
        if(result == null){
            return;
        }
        ActivityUtil.getInstance().dismiss();

		if (count != 0) {
			List<MessageThreadPageInfo> threadList = new ArrayList<MessageThreadPageInfo>();
			for (int i = 0; i < result.getMessageEntryList().size(); i++) {
				MessageThreadPageInfo info = result.getMessageEntryList().get(i);
				if(info == null){
					continue;
				}
				int mid = info.getMid();
				if (!midSet.contains(mid)) {
					threadList.add(info);
					midSet.add(mid);
				}
			}
			result.setMessageEntryList(threadList);
		}else{
			for (int i = 0; i < result.getMessageEntryList().size(); i++) {
				MessageThreadPageInfo info = result.getMessageEntryList().get(i);
				if(info == null){
					continue;
				}
				int mid = info.getMid();
				midSet.add(mid);
			}
			
		}

		infoList.add(result);
		count += result.getMessageEntryList().size();
		if (result.get__nextPage()>0)
		{
			isEndOfList = false;
		}else{
			isEndOfList = true;
		}

			this.notifyDataSetChanged();
		
	}
	
	public void clear(){
		count = 0;
		infoList.clear();
		midSet.clear();
		setSelected(-1);
		isPrompted=false;
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
        	if (isEndOfList == false)
        	{
            isLoading = true;
            loader.loadNextPage(this);
        	}else{
        		if(isPrompted==false){
    			if (toast != null)
            	{
            		toast.setText(context.getString(R.string.last_page_prompt_message));
            		toast.setDuration(Toast.LENGTH_SHORT);
            		toast.show();
            	} else
            	{
            		toast = Toast.makeText(this.context, context.getString(R.string.last_page_prompt_message), Toast.LENGTH_SHORT);
            		toast.show();
            	}
    			isPrompted=true;}
        	}
        }
        return  ret;
    }
    private boolean isLoading = false;
}
