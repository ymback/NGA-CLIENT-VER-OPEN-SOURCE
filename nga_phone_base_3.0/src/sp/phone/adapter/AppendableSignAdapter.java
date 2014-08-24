package sp.phone.adapter;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import sp.phone.bean.MissionDetialData;
import sp.phone.bean.SignData;
import sp.phone.interfaces.NextJsonTopicListLoader;
import sp.phone.utils.ActivityUtil;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class AppendableSignAdapter extends SignPageAdapter {
	final private List<SignData> infoList;
    final private PullToRefreshAttacher attacher;
    @SuppressWarnings("unused")
	private final NextJsonTopicListLoader loader;
    private boolean isEndOfList = false;
	Set<Integer> tidSet;
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
    private boolean isLoadingTable = false;
    private int TableListPage=0;
}
