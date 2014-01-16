package sp.phone.interfaces;

import gov.anzong.androidnga.R;
import sp.phone.adapter.TopicListAdapter;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

public class EnterJsonArticle implements OnItemClickListener {

	private final Activity activity;
	public EnterJsonArticle(Activity activity) {
		super();
		this.activity = activity;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String guid = (String) parent.getItemAtPosition(position);
		if(StringUtil.isEmpty(guid))
			return;
		
		guid = guid.trim();

		int pid = StringUtil.getUrlParameter(guid, "pid");
		int tid = StringUtil.getUrlParameter(guid, "tid");
		int authorid = StringUtil.getUrlParameter(guid, "authorid");
		
		Intent intent = new Intent();
		intent.putExtra("tab", "1");
		intent.putExtra("tid",tid );
		intent.putExtra("pid",pid );
		intent.putExtra("authorid",authorid );
		ListView listview = (ListView)parent;
		Object a = parent.getAdapter();
		TopicListAdapter adapter = null;
		if(a instanceof TopicListAdapter ){
			adapter = (TopicListAdapter)a;
		}else if(a instanceof HeaderViewListAdapter){
			HeaderViewListAdapter ha = (HeaderViewListAdapter)a;
			adapter = (TopicListAdapter) ha.getWrappedAdapter();
			position -= ha.getHeadersCount();
		}
		adapter.setSelected(position);
		listview.setItemChecked(position, true);
		
		intent.setClass(activity, PhoneConfiguration.getInstance().articleActivityClass);
		activity.startActivity(intent);
		if(PhoneConfiguration.getInstance().showAnimation)
			activity.overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
	
		
	}

}
