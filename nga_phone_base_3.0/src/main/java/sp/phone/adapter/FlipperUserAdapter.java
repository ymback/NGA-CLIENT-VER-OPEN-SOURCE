package sp.phone.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import gov.anzong.androidnga.R;
import sp.phone.common.User;
import sp.phone.common.UserManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.mvp.contract.BoardContract;
import sp.phone.util.ImageUtils;

public class FlipperUserAdapter extends RecyclerView.Adapter<FlipperUserAdapter.UserViewHolder> {

    private UserManager mUserManager = UserManagerImpl.getInstance();

    private BoardContract.Presenter mPresenter;

    public FlipperUserAdapter(BoardContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.nav_header_view_login_user, viewGroup, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder viewHolder, int i) {
        int size = mUserManager.getUserSize();
        if (size == 0) {
            viewHolder.loginState.setText("未登录");
            viewHolder.loginId.setText("点击下面的登录账号登录");
            viewHolder.nextImage.setVisibility(View.GONE);
            viewHolder.itemView.setOnClickListener(v -> mPresenter.startLogin());
        } else {
            if (size <= 1) {
                viewHolder.nextImage.setVisibility(View.GONE);
            }
            if (size == 1) {
                viewHolder.loginState.setText("已登录1个账户");
            } else {
                viewHolder.loginState.setText(String.format("已登录%s", String.valueOf(size + "个账户,点击切换")));
            }
            User user = mUserManager.getUserList().get(i);
            viewHolder.loginId.setText(String.format("当前:%s(%s)", user.getNickName(), user.getUserId()));
            handleUserAvatar(viewHolder.avatarImage, user.getAvatarUrl());
            viewHolder.itemView.setOnClickListener(v -> mPresenter.startUserProfile(user.getNickName()));

            viewHolder.nextImage.setOnClickListener(v -> mPresenter.toggleUser(mUserManager.getUserList()));
        }
    }

    private void handleUserAvatar(ImageView avatarIV, String url) {
        avatarIV.setImageTintList(null);
        ImageUtils.loadRoundCornerAvatar(avatarIV, url);
    }

    @Override
    public int getItemCount() {
        int size = mUserManager.getUserSize();
        return size == 0 ? 1 : size;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView loginId;

        TextView loginState;

        ImageView avatarImage;

        ImageButton nextImage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            loginState = itemView.findViewById(R.id.loginstate);
            loginId = itemView.findViewById(R.id.loginnameandid);
            avatarImage = itemView.findViewById(R.id.avatarImage);
            nextImage = itemView.findViewById(R.id.nextImage);
        }
    }
}
