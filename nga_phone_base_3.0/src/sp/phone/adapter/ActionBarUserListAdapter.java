package sp.phone.adapter;

import android.R;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by GDB437 on 9/3/13,nga_phone_base_3.0
 */
public class ActionBarUserListAdapter  extends SpinnerUserListAdapter {


    public ActionBarUserListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1,parent,false);
        }
        ((TextView)convertView).setText(userList.get(position).getNickName());

        return convertView;
    }


}
