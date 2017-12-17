package sp.phone.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import sp.phone.bean.User;
import sp.phone.common.UserManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.utils.ImageUtil;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {

    private Context mContext;

    private List<User> mUserList;

    private View.OnClickListener mOnClickListener;

    private UserManager mUserManager;

    private Bitmap mDefaultAvatar;

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

    public UserListAdapter(Context context) {
        mContext = context;
        mUserManager = UserManagerImpl.getInstance();
        mUserList = mUserManager.getUserList();
        mDefaultAvatar = ImageUtil.loadDefaultAvatar();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_user_list_item, parent, false);
        UserViewHolder holder = new UserViewHolder(convertView);
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.checkView.setOnClickListener(mOnClickListener);
        holder.avatarView.setImageBitmap(mDefaultAvatar);
        return holder;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.userNameView.setText(mUserList.get(position).getNickName());
        holder.checkView.setChecked(mUserManager.getActiveUserIndex() == position);
        holder.itemView.setTag(position);
        holder.checkView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }


}
