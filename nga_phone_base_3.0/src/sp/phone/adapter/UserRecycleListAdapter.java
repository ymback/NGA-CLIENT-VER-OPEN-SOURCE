package sp.phone.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.R;
import sp.phone.bean.PreferenceConstant;
import sp.phone.bean.User;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;


public class UserRecycleListAdapter extends RecyclerView.Adapter<UserRecycleListAdapter.UserViewHolder> {

    private Context mContext;

    private List<User> mUserList;

    private View.OnClickListener mOnClickListener;

    public static class UserViewHolder extends RecyclerView.ViewHolder{

        TextView userNameView;

        public UserViewHolder(View itemView) {
            super(itemView);
            userNameView = (TextView) itemView.findViewById(R.id.user_name);
            userNameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            itemView.findViewById(R.id.delete_user).setVisibility(View.GONE);
        }
    }

    public UserRecycleListAdapter(Context context, View.OnClickListener onClickListener,RecyclerView listView) {
        mContext = context;
        mOnClickListener = onClickListener;
        SharedPreferences share = context.getSharedPreferences(PreferenceConstant.PERFERENCE,
                Context.MODE_PRIVATE);

        String userListString = share.getString(PreferenceConstant.USER_LIST, "");
        if (StringUtil.isEmpty(userListString)) {
            mUserList = new ArrayList<>();
        } else {
            mUserList = JSON.parseArray(userListString, User.class);
        }

        if (listView != null){
            ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {

                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    remove(viewHolder.getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
            //将recycleView和ItemTouchHelper绑定
            touchHelper.attachToRecyclerView(listView);
        }
    }

    public UserRecycleListAdapter(Context context, View.OnClickListener onClickListener) {
        this(context,onClickListener,null);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.user_list, parent, false);
        return new UserViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.userNameView.setText(mUserList.get(position).getNickName());
        holder.userNameView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    private void remove(int position){
        mUserList.remove(position);

        SharedPreferences share = mContext.getSharedPreferences(PreferenceConstant.PERFERENCE,
                Context.MODE_PRIVATE);

        String userListString = JSON.toJSONString(mUserList);
        share.edit().putString(PreferenceConstant.USER_LIST, userListString).apply();

        if (position == 0) {
            PhoneConfiguration configuration = PhoneConfiguration.getInstance();
            if (mUserList.size() == 0) {
                configuration.setUid("");
                configuration.setNickname("");
                configuration.setCid("");
                configuration.setReplyString("");
                configuration.setReplyTotalNum(0);
                configuration.blacklist = StringUtil.blackliststringtolisttohashset("");
                SharedPreferences.Editor editor = share.edit();
                editor.putString(PreferenceConstant.UID, "")
                .putString(PreferenceConstant.CID, "")
                .putString(PreferenceConstant.USER_NAME, "")
                .putString(PreferenceConstant.PENDING_REPLYS, "")
                .putString(PreferenceConstant.REPLYTOTALNUM, "0")
                .putString(PreferenceConstant.BLACK_LIST, "")
                .apply();
            } else {
                User user = mUserList.get(0);
                configuration.setUid(user.getUserId());
                configuration.setNickname(user.getNickName());
                configuration.setCid(user.getCid());
                configuration.setReplyString(user.getReplyString());
                configuration.setReplyTotalNum(user.getReplyTotalNum());
                configuration.blacklist = StringUtil.blackliststringtolisttohashset(user.getBlackList());
            }
        }

    }

}
