package sp.phone.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gov.anzong.androidnga.R;
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
            convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        ((TextView) convertView).setText(mUserList.get(position).getNickName());

        // TODO: 2018/1/16  need check this condition later
        if (ActivityUtils.supportNewUi(mContext) || mContext.getClass().getSimpleName().equals(ContextThemeWrapper.class.getSimpleName())){
            convertView.setBackgroundColor(ThemeManager.getInstance().getPrimaryColor(mContext));
            ((TextView) convertView).setTextColor(ContextCompat.getColor(mContext,R.color.toolbar_text_color));
        }
        return convertView;
    }


}
