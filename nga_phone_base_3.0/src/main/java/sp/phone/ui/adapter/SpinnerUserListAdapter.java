package sp.phone.ui.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import gov.anzong.androidnga.common.PreferenceKey;
import sp.phone.common.User;
import sp.phone.common.UserManagerImpl;

public class SpinnerUserListAdapter extends BaseAdapter implements PreferenceKey {

    protected List<User> mUserList;

    protected Context mContext;

    public SpinnerUserListAdapter(Context context) {
        mContext = context;
        mUserList = UserManagerImpl.getInstance().getUserList();
    }

    @Override
    public int getCount() {
        return mUserList == null ? 0 : mUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return mUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        final String uid = mUserList.get(position).getUserId();
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
            convertView = new TextView(mContext);
        }
        ((TextView) convertView).setText(mUserList.get(position).getNickName());
        ((TextView) convertView).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
        return convertView;
    }


}
