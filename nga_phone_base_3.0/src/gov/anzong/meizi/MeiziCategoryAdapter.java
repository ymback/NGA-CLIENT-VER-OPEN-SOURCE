package gov.anzong.meizi;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.huewu.pla.lib.MultiColumnListView;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.R;
import sp.phone.utils.ThemeManager;

public class MeiziCategoryAdapter extends BaseAdapter {

    Activity mactivity;
    private List<MeiziUrlData> mData;
    private LayoutInflater mLayoutInflater;
    private MultiColumnListView mListView;

    public MeiziCategoryAdapter(Activity activity, MultiColumnListView listView) {
        mLayoutInflater = activity.getLayoutInflater();
        mData = new ArrayList<MeiziUrlData>();
        mListView = listView;
        mactivity = activity;
    }

    public void addData(List<MeiziUrlData> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void clearData() {
        mData.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder holder;
        if (view != null && view.getTag() != null) {
            holder = (Holder) convertView.getTag();
        } else {
            if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
                view = mLayoutInflater.inflate(
                        R.layout.listitem_category_night, null);
            } else {
                view = mLayoutInflater
                        .inflate(R.layout.listitem_category, null);
            }
            holder = new Holder(view);
            view.setTag(holder);
        }

        view.setEnabled(!mListView.isItemChecked(position
                + mListView.getHeaderViewsCount()));

        MeiziUrlData meiziM = mData.get(position);
        Glide.with(mactivity).load(meiziM.smallPicUrl).into(holder.image);
        return view;
    }

    private class Holder {
        public ImageView image;

        public Holder(View view) {
            image = (ImageView) view.findViewById(R.id.image);
        }
    }
}
