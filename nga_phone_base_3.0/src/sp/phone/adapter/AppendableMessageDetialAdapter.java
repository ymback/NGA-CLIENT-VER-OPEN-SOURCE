package sp.phone.adapter;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.Media_Player;

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
import sp.phone.utils.MessageUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import sp.phone.utils.ThemeManager;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
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
	static Context context;
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
	
	public void notifyDataSetChangedWithModChange(){
		List<MessageDetialInfo> infoListTmp;
		for(int i=0;i<infoList.size();i++){
			for(int j=0;j<infoList.get(i).getMessageEntryList().size();j++){
				sethtmldata(infoList.get(i).getMessageEntryList().get(j),j);
			}
		}
		this.notifyDataSetChanged();
	}
	
	public void sethtmldata(MessageArticlePageInfo row,int i){
		fillFormated_html_data(row,i+1);
	}
	
	public void fillFormated_html_data(MessageArticlePageInfo row, int i) {

		ThemeManager theme = ThemeManager.getInstance();
		if (row.getContent() == null) {
			row.setContent(row.getSubject());
			row.setSubject(null);
		}
		int bgColor = context.getResources().getColor(
				theme.getBackgroundColor(i));
		int fgColor = context.getResources().getColor(
				theme.getForegroundColor());
		bgColor = bgColor & 0xffffff;
		final String bgcolorStr = String.format("%06x", bgColor);

		int htmlfgColor = fgColor & 0xffffff;
		final String fgColorStr = String.format("%06x", htmlfgColor);

		String formated_html_data = MessageDetialAdapter.convertToHtmlText(row,
				isShowImage(), showImageQuality(), fgColorStr, bgcolorStr);

		row.setFormated_html_data(formated_html_data);
	}

	public static int showImageQuality() {
		if (isInWifi()) {
			return 0;
		} else {
			return PhoneConfiguration.getInstance().imageQuality;
		}
	}
	private boolean isShowImage() {
		return PhoneConfiguration.getInstance().isDownImgNoWifi() || isInWifi();
	}
	public static boolean isInWifi() {
		ConnectivityManager conMan = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		return wifi == State.CONNECTED;
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
