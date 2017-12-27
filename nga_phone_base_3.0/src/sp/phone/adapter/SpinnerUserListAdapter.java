package sp.phone.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import sp.phone.common.PreferenceKey;
import sp.phone.common.User;
import sp.phone.common.UserManager;
import sp.phone.common.UserManagerImpl;

public class SpinnerUserListAdapter extends BaseAdapter
        implements PreferenceKey {
    protected List<User> userList;
    protected Context context;

    public SpinnerUserListAdapter(Context context) {
        super();
        this.context = context;
        UserManager um = UserManagerImpl.getInstance();
        userList = um.getUserList();
    }


    @Override
    public int getCount() {
        if (userList == null)
            return 0;
        return userList.size();
    }

    @Override
    public Object getItem(int position) {

        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        final String uid = userList.get(position).getUserId();
        long ret = 0;
        try {
            ret = Long.valueOf(uid);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return ret;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = new TextView(context);
        }
        ((TextView) convertView).setText(userList.get(position).getNickName());
        ((TextView) convertView).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        return convertView;
    }


}
