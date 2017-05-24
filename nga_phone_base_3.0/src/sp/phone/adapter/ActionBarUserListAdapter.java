package sp.phone.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ThemeManager;

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
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        ((TextView) convertView).setText(userList.get(position).getNickName());


        if (PhoneConfiguration.getInstance().isMaterialMode() && ActivityUtil.supportMaterialMode(context)){
            if (ThemeManager.getInstance().isNightMode()){
                convertView.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));
            } else {
                convertView.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary));
            }
            ((TextView) convertView).setTextColor(ContextCompat.getColor(context,R.color.text_color_dark));
        }
        return convertView;
    }


}
