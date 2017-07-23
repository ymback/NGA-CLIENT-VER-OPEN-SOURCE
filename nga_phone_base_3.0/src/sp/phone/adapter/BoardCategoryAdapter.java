package sp.phone.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import sp.phone.bean.BoardCategory;

public class BoardCategoryAdapter extends BaseAdapter {

    private BoardCategory mCategory;

    private Activity mActivity;


    public BoardCategoryAdapter(Activity activity, BoardCategory category) {
        super();
        mActivity = activity;
        mCategory = category;
    }

    public int getCount() {
        return mCategory == null ? 0 : mCategory.size();
    }

    public Object getItem(int position) {
        return mCategory == null ? null : mCategory.get(position).getUrl();
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView,
                        ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = getLayoutInflater().inflate(R.layout.board_icon,
                    null);

            ImageView iconView = (ImageView) convertView
                    .findViewById(R.id.board_imgicon);
            TextView tv = (TextView) convertView
                    .findViewById(R.id.board_name_view);
            holder.img = iconView;
            holder.text = tv;
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        Drawable draw = getDrawable(position);
        holder.img.setImageDrawable(draw);
        holder.text.setText(mCategory.get(position).getName());
        return convertView;

    }

    ;

    private Drawable getDrawable(int position) {
        Drawable drawable;
        int resId = mCategory.get(position).getIcon();
        if (resId != 0) {// default board
            drawable = ContextCompat.getDrawable(mActivity,resId);
        } else {// optional board
            drawable = ContextCompat.getDrawable(mActivity,R.drawable.pdefault);
        }
        return drawable;
    }

    public Resources getResources() {
        return mActivity.getResources();
    }

    public LayoutInflater getLayoutInflater() {
        return mActivity.getLayoutInflater();
    }

    class ViewHolder {
        ImageView img;
        TextView text;
    }


}
