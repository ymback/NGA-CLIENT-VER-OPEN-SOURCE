package sp.phone.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import sp.phone.common.User;
import sp.phone.util.ImageUtils;


public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.UserViewHolder> {

    private Context mContext;

    private List<User> mUserList;

    private View.OnClickListener mOnClickListener;

    private Bitmap mDefaultAvatar;

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_name)
        TextView userNameView;

        @BindView(R.id.avatar)
        ImageView avatarView;

        public UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public BlackListAdapter(Context context, List<User> userList) {
        mContext = context;
        mUserList = userList;
        mDefaultAvatar = ImageUtils.loadDefaultAvatar();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_settings_black_list_item, parent, false);
        UserViewHolder holder = new UserViewHolder(convertView);
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.avatarView.setImageBitmap(mDefaultAvatar);
        return holder;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.userNameView.setText(mUserList.get(position).getNickName());
        holder.itemView.setTag(mUserList.get(position));
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

}
