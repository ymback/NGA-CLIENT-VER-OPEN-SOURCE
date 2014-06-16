package sp.phone.adapter;

import gov.anzong.androidnga2.R;
import gov.anzong.androidnga2.activity.Media_Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sp.phone.bean.MessageArticlePageInfo;
import sp.phone.bean.MessageDetialInfo;
import sp.phone.bean.MessageListInfo;
import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.NextJsonMessageDetialLoader;
import sp.phone.interfaces.NextJsonMessageListLoader;
import sp.phone.interfaces.NextJsonTopicListLoader;
import sp.phone.task.JsonTopicListLoadTask;
import sp.phone.utils.ActivityUtil;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class AppendableMessageDetialAdapter extends MessageDetialAdapter {
	final private List<MessageDetialInfo> infoList;
    final private PullToRefreshAttacher attacher;
    private final NextJsonMessageDetialLoader loader;
    private int count=0;
    private boolean isEndOfList = false;
	Toast toast=null;
	Context context;
	public AppendableMessageDetialAdapter(Context context,PullToRefreshAttacher attacher,NextJsonMessageDetialLoader loader ) {
		super(context);
		this.context=context;
		infoList = new ArrayList<MessageDetialInfo>();
        this.attacher = attacher;
        this.loader = loader;
	}
	boolean isPrompted=false;

	@Override
	protected MessageArticlePageInfo getEntry(int position) {
		int i=(int)position/20;
		MessageArticlePageInfo tmp = infoList.get(i).getMessageEntryList().get(position%20);
		return tmp;
	}//FIXED FC BUG

	@Override
	public void finishLoad(MessageDetialInfo result) {
        isLoading = false;
        if(attacher !=null)
            attacher.setRefreshComplete();
        if(result == null){
            return;
        }
        ActivityUtil.getInstance().dismiss();

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
	
	
	public int getNextPage(){
		return infoList.size() + 1;
	}
	
	public boolean getIsEnd(){
		return isEndOfList;
	}
	
	@Override
	public int getCount() {
		return count;
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
            		toast.setText(context.getString(R.string.last_page_prompt_message_detail));
            		toast.setDuration(Toast.LENGTH_SHORT);
            		toast.show();
            	} else
            	{
            		toast = Toast.makeText(this.context, context.getString(R.string.last_page_prompt_message_detail), Toast.LENGTH_SHORT);
            		toast.show();
            	}
    			isPrompted=true;}
        	}
        }
        return  ret;
    }
    private boolean isLoading = false;
}
