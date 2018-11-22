package sp.phone.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import gov.anzong.androidnga.R;
import gov.anzong.androidnga.util.GlideApp;
import sp.phone.bean.BoardCategory;
import sp.phone.common.ApiConstants;

/**
 * 版块Grid Adapter
 */
public class BoardCategoryAdapter extends RecyclerView.Adapter<BoardCategoryAdapter.BoardViewHolder> {

    private BoardCategory mCategory;

    private Activity mActivity;

    private AdapterView.OnItemClickListener mItemClickListener;

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
    public BoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.list_board_icon, parent, false);
        return new BoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BoardViewHolder holder, int position) {
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
        holder.name.setText(mCategory.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(null, v, position, getItemId(position));
                }
            }
        });
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
        } else if (mCategory.getCategoryIndex() > 0){
            drawable = ContextCompat.getDrawable(mActivity, R.drawable.default_board_icon);
        }

        return drawable;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public LayoutInflater getLayoutInflater() {
        return mActivity.getLayoutInflater();
    }
}
