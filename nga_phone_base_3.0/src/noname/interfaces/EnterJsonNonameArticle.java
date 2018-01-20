package noname.interfaces;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import noname.gson.parse.NonameThreadBody;
import noname.adapter.NonameTopicListAdapter;
import sp.phone.common.PhoneConfiguration;
import sp.phone.utils.StringUtils;

public class EnterJsonNonameArticle implements OnItemClickListener {

    private final Activity activity;

    public EnterJsonNonameArticle(Activity activity) {
        super();
        this.activity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        int stid = 0;
        String guid = "";
        if (parent.getItemAtPosition(position) instanceof NonameThreadBody) {
            stid = ((NonameThreadBody) parent.getItemAtPosition(position)).tid;
            if (stid == 0) {
                guid = (String) parent.getItemAtPosition(position);
                if (StringUtils.isEmpty(guid))
                    return;
            } else {
                guid = "tid=" + String.valueOf(stid);
            }
        } else {
            guid = (String) parent.getItemAtPosition(position);
            if (StringUtils.isEmpty(guid))
                return;
        }

        int tid = StringUtils.getUrlParameter(guid, "tid");

        Intent intent = new Intent();
        intent.putExtra("tab", "1");
        intent.putExtra("tid", tid);
        ListView listview = (ListView) parent;
        Object a = parent.getAdapter();
        NonameTopicListAdapter adapter = null;
        if (a instanceof NonameTopicListAdapter) {
            adapter = (NonameTopicListAdapter) a;
        } else if (a instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter ha = (HeaderViewListAdapter) a;
            adapter = (NonameTopicListAdapter) ha.getWrappedAdapter();
            position -= ha.getHeadersCount();
        }
        adapter.setSelected(position);
        listview.setItemChecked(position, true);

        intent.setClass(activity, PhoneConfiguration.getInstance().nonameArticleActivityClass);
        activity.startActivity(intent);


    }

}
