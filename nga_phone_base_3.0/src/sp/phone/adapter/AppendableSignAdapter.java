package sp.phone.adapter;

import gov.anzong.androidnga.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sp.phone.bean.MissionDetialData;
import sp.phone.bean.SignData;
import sp.phone.bean.ThreadPageInfo;
import sp.phone.bean.TopicListInfo;
import sp.phone.interfaces.NextJsonTopicListLoader;
import sp.phone.task.JsonTopicListLoadTask;
import sp.phone.utils.ActivityUtil;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class AppendableSignAdapter extends SignPageAdapter {
	final private List<SignData> infoList;
    final private PullToRefreshAttacher attacher;
    private Toast toast = null;
    private final NextJsonTopicListLoader loader;
    private boolean isEndOfList = false;
    private boolean isPrompted = false;
	Set<Integer> tidSet;
	private int table;
	public AppendableSignAdapter(Context context,PullToRefreshAttacher attacher,NextJsonTopicListLoader loader ) {
		super(context);
		infoList = new ArrayList<SignData>();
		tidSet = new HashSet<Integer>();
        this.attacher = attacher;
        this.loader = loader;
	}

	@Override
	protected MissionDetialData getEntry(int position) {
		return null;
	}

	@Override
	public void jsonfinishLoad(SignData result) {
        if(attacher !=null)
            attacher.setRefreshComplete();
        if(result == null){
            return;
        }
        ActivityUtil.getInstance().dismiss();
		
	}
	
	public void clear(){
		count = 0;
		infoList.clear();
		tidSet.clear();
		setSelected(-1);
	}
	
	public int getNextPage(){
		if(!isLoadingTable){
			return infoList.size() + 1;
		}else{
			return infoList.size()-TableListPage+1;
		}
	}
	
	public boolean getIsEnd(){
		return isEndOfList;
	}

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View ret = super.getView(position, view, parent);
        return  ret;
    }
    private boolean isLoading = false;
    private int TableList__ROWS = 0;
    private boolean isLoadingTable = false;
    private int TableListPage=0;
    private int TableNum=6;
}
