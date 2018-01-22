package gov.anzong.meizi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import gov.anzong.meizi.MeiziCategory.MeiziCategoryItem;

public class MeiziDrawerAdapter extends BaseAdapter {

    private Context mContext;

    private ListView mListView;

    public MeiziDrawerAdapter(Context context, ListView listView) {
        mContext = context;
        mListView = listView;
    }

    @Override
    public int getCount() {
        return MeiziCategory.ITEMS.length;
    }

    @Override
    public MeiziCategoryItem getItem(int position) {
        if (position >= 0 && position < MeiziCategory.ITEMS.length) {
            return MeiziCategory.ITEMS[position];
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.meizi_listitem_drawer, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        textView.setText(getItem(position).getName());
        textView.setSelected(mListView.isItemChecked(position));
        return convertView;
    }
}
