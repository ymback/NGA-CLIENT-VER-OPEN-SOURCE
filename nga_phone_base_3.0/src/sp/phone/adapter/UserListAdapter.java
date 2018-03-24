package sp.phone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.util.GlideApp;
import sp.phone.common.User;
import sp.phone.common.UserManager;
import sp.phone.common.UserManagerImpl;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {

    private Context mContext;

    private List<User> mUserList;

    private View.OnClickListener mOnClickListener;

    private UserManager mUserManager;

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_name)
        TextView userNameView;

        @BindView(R.id.avatar)
        ImageView avatarView;

        @BindView(R.id.check)
        Switch checkView;

        public UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public UserListAdapter(Context context, List<User> userList) {
        mContext = context;
        mUserManager = UserManagerImpl.getInstance();
        mUserList = userList;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.list_user_manager_item, parent, false);
        UserViewHolder holder = new UserViewHolder(convertView);
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.checkView.setOnClickListener(mOnClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User user = mUserList.get(position);
        holder.userNameView.setText(user.getNickName());
        holder.checkView.setChecked(mUserManager.getActiveUserIndex() == position);
        holder.itemView.setTag(position);
        holder.checkView.setTag(position);
        String avatarUrl = user.getAvatarUrl();
        GlideApp.with(mContext)
                .load(avatarUrl)
                .placeholder(R.drawable.default_avatar)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(holder.avatarView);
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }


}
