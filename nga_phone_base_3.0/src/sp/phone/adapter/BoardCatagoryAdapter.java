package sp.phone.adapter;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import sp.phone.bean.BoardCategory;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.ThemeManager;

public class BoardCatagoryAdapter extends BaseAdapter {


    //private Map<String,Drawable> iconMap;
    Resources resources;
    LayoutInflater layoutInflater;
    BoardCategory category;
    //private BoardHolder boardInfo;


    public BoardCatagoryAdapter(Resources resources,
                                LayoutInflater layoutInflater, BoardCategory category) {
        super();
        this.resources = resources;
        this.layoutInflater = layoutInflater;
        //iconMap = new HashMap<String,Drawable>();
        this.category = category;
    }

    public int getCount() {
        return category == null ? 0 : category.size();
    }

    public Object getItem(int position) {
        return category == null ? null : category.get(position).getUrl();//boardInfo.get(category,position).getUrl();//urls[position];
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
            //iconView.setGravity(Gravity.CENTER_HORIZONTAL);
            ReflectionUtil.view_setGravity(convertView, Gravity.CENTER_HORIZONTAL);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        if (ThemeManager.getInstance().getMode() == ThemeManager.MODE_NIGHT) {
            holder.text.setTextColor(getResources().getColor(
                    R.color.night_fore_color));
        } else {
            holder.text.setTextColor(getResources().getColor(
                    R.color.black));
        }
 
        Drawable draw = getDrable(convertView, position);
        holder.img.setImageDrawable(draw);
        holder.text.setText(category.get(position).getName());
        return convertView;

    }

    ;

    private Drawable getDrable(View convertView, int position) {
        Drawable d = null;
        int resId = category.get(position).getIcon();
        if (resId != 0) {// default board
            d = getResources().getDrawable(resId);
        } else {// optional board
            d = getResources().getDrawable(R.drawable.pdefault);
        }

        return d;
    }

    public Resources getResources() {
        return resources;
    }

    public LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    class ViewHolder {
        ImageView img;
        TextView text;
    }


}
