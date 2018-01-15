package sp.phone.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import sp.phone.common.PhoneConfiguration;
import sp.phone.common.ThemeManager;
import sp.phone.utils.ActivityUtils;

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


        if (PhoneConfiguration.getInstance().isMaterialMode() && ActivityUtils.supportMaterialMode(context)){
            convertView.setBackgroundColor(ThemeManager.getInstance().getPrimaryColor(context));
            ((TextView) convertView).setTextColor(ContextCompat.getColor(context,R.color.toolbar_text_color));
        }
        return convertView;
    }


}
