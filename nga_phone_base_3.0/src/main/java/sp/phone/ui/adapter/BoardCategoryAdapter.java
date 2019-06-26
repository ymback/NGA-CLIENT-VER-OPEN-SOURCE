package sp.phone.ui.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.util.GlideApp;
import sp.phone.common.ApiConstants;
import sp.phone.mvp.model.entity.BoardCategory;
import sp.phone.rxjava.RxBus;
import sp.phone.rxjava.RxEvent;
import sp.phone.rxjava.RxUtils;

/**
 * 版块Grid Adapter
 */
public class BoardCategoryAdapter extends RecyclerView.Adapter<BoardCategoryAdapter.BoardViewHolder> {

    private BoardCategory mCategory;

    private Activity mActivity;

    class BoardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon_board_img)
        public ImageView icon;
        @BindView(R.id.text_board_name)
        public TextView name;

        BoardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public BoardCategoryAdapter(Activity activity, BoardCategory category) {
        mActivity = activity;
        mCategory = category;
    }

    public int getCount() {
        return mCategory == null ? 0 : mCategory.size();
    }

    public Object getItem(int position) {
        return mCategory == null ? null : mCategory.get(position).getFid();
    }

    @Override
    public BoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.list_board_icon, parent, false);
        return new BoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BoardViewHolder holder, int position) {
        Drawable draw = getDrawable(position);
        if (draw == null) {
            long resId = getItemId(position);
            GlideApp.with(mActivity)
                    .load(String.format(ApiConstants.URL_BOARD_ICON, resId))
                    .placeholder(R.drawable.default_board_icon)
                    .dontAnimate()
                    .into(holder.icon);
        } else {
            holder.icon.setImageDrawable(draw);
        }
        holder.itemView.setTag(mCategory.get(position));
        holder.name.setText(mCategory.get(position).getName());
        RxUtils.clicks(holder.itemView, v -> RxBus.getInstance().post(new RxEvent(RxEvent.EVENT_SHOW_TOPIC_LIST, mCategory.get(holder.getAdapterPosition()))));
    }

    @Override
    public long getItemId(int position) {
        return mCategory.get(position).getFid();
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    private int getResId(int position) {
        int fid = mCategory.get(position).getFid();
        String resName = fid > 0 ? "p" + fid : "p_" + Math.abs(fid);
        return mActivity.getResources().getIdentifier(resName, "drawable", mActivity.getPackageName());
    }

    private Drawable getDrawable(int position) {
        Drawable drawable = null;
        int resId = getResId(position);
        if (resId != 0) {
            drawable = ContextCompat.getDrawable(mActivity, resId);
        } else if (!mCategory.isBookmarkCategory()) {
            drawable = ContextCompat.getDrawable(mActivity, R.drawable.default_board_icon);
        }

        return drawable;
    }

    public LayoutInflater getLayoutInflater() {
        return mActivity.getLayoutInflater();
    }
}
