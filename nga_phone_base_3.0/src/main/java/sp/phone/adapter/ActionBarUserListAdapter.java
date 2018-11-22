package sp.phone.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import sp.phone.theme.ThemeManager;

/**
 * Created by GDB437 on 9/3/13,nga_phone_base_3.0
 */
public class ActionBarUserListAdapter extends SpinnerUserListAdapter {


    public ActionBarUserListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        ((TextView) convertView).setText(mUserList.get(position).getNickName());

        convertView.setBackgroundColor(ThemeManager.getInstance().getPrimaryColor(mContext));
        ((TextView) convertView).setTextColor(ContextCompat.getColor(mContext,R.color.toolbar_text_color));
        return convertView;
    }


}
