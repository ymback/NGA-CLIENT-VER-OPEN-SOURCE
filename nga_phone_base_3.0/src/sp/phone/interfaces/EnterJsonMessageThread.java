package sp.phone.interfaces;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import gov.anzong.androidnga.R;
import sp.phone.adapter.MessageListAdapter;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;

public class EnterJsonMessageThread implements OnItemClickListener {

    private final Activity activity;

    public EnterJsonMessageThread(Activity activity) {
        super();
        this.activity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        String guid = (String) parent.getItemAtPosition(position);
        if (StringUtil.isEmpty(guid))
            return;

        guid = guid.trim();

        int mid = StringUtil.getUrlParameter(guid, "mid");

        Intent intent = new Intent();
        intent.putExtra("mid", mid);
        ListView listview = (ListView) parent;
        Object a = parent.getAdapter();
        MessageListAdapter adapter = null;
        if (a instanceof MessageListAdapter) {
            adapter = (MessageListAdapter) a;
        } else if (a instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter ha = (HeaderViewListAdapter) a;
            adapter = (MessageListAdapter) ha.getWrappedAdapter();
            position -= ha.getHeadersCount();
        }
        adapter.setSelected(position);
        listview.setItemChecked(position, true);

        intent.setClass(activity, PhoneConfiguration.getInstance().messageDetialActivity);
        activity.startActivity(intent);
        if (PhoneConfiguration.getInstance().showAnimation)
            activity.overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);


    }

}
