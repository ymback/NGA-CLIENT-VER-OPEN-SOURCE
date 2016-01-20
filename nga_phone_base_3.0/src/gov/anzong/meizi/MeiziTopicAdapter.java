package gov.anzong.meizi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.gallery.ImageZoomActivity;
import gov.anzong.meizi.MeiziTopicMData.TopicContentItem;
import sp.phone.utils.ThemeManager;

public class MeiziTopicAdapter extends BaseAdapter {

    private List<TopicContentItem> mData;
    private Activity mactivity;

    private LayoutInflater mLayoutInflater;

    public MeiziTopicAdapter(Activity activity) {
        mactivity = activity;
        mLayoutInflater = activity.getLayoutInflater();
        mData = new ArrayList<MeiziTopicMData.TopicContentItem>();
    }

    public void setData(List<TopicContentItem> data) {
        mData.clear();
        mData.addAll(data);
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
    public View getView(int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        final Holder holder;
        if (view != null && view.getTag() != null) {
            holder = (Holder) convertView.getTag();
        } else {
            view = mLayoutInflater.inflate(R.layout.listitem_topic, null);
            holder = new Holder(view);
            view.setTag(holder);
        }

        final TopicContentItem item = mData.get(position);
        switch (item.type) {
            case IMAGE:
                holder.image.setVisibility(View.VISIBLE);
                holder.text.setVisibility(View.GONE);
                Glide.with(mactivity).load(item.imgUrl).into(holder.image);
                holder.image.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra(ImageZoomActivity.KEY_GALLERY_URLS, new String[]{item.imgUrl});
                        Rect rect = new Rect();
                        view.getGlobalVisibleRect(rect);
                        intent.putExtra(ImageZoomActivity.KEY_GALLERY_RECT, rect);
                        intent.putExtra(ImageZoomActivity.KEY_GALLERY_CUR_URL, item.imgUrl);
                        intent.setClass(parent.getContext(), ImageZoomActivity.class);
                        parent.getContext().startActivity(intent);
                    }

                });
                break;
            case MSG:
                holder.image.setVisibility(View.GONE);
                holder.text.setVisibility(View.VISIBLE);
                holder.text.setText(item.msg);
                if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
                    holder.text.setTextColor(parent.getContext().getResources().getColor(R.color.night_fore_color));
                }
                break;

            default:
                break;
        }

        return view;
    }

    private class Holder {
        public ImageView image;

        public TextView text;

        public View header;

        public Holder(View view) {
            image = (ImageView) view.findViewById(R.id.image);
            text = (TextView) view.findViewById(R.id.text);
            header = view.findViewById(R.id.header);
            header.setVisibility(View.GONE);
        }
    }
}
