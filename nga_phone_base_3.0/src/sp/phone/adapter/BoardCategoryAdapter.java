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

import gov.anzong.androidnga.R;
import sp.phone.bean.BoardCategory;
import sp.phone.common.PhoneConfiguration;

public class BoardCategoryAdapter extends RecyclerView.Adapter<BoardCategoryAdapter.BoardViewHolder> {

    private BoardCategory mCategory;

    private Activity mActivity;

    private AdapterView.OnItemClickListener mItemClickListener;

    class BoardViewHolder extends RecyclerView.ViewHolder {

        ImageView img;

        TextView text;

        BoardViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.board_imgicon);
            text = (TextView) itemView.findViewById(R.id.board_name_view);
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
        return mCategory == null ? null : mCategory.get(position).getUrl();
    }

    @Override
    public BoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.board_icon,parent,false);
        return new BoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BoardViewHolder holder, final int position) {
        Drawable draw = getDrawable(position);
        holder.img.setImageDrawable(draw);
        holder.text.setText(mCategory.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(null,v,position,getItemId(position));
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(mCategory.get(position).getUrl());
    }

    @Override
    public int getItemCount() {
        return mCategory.size();
    }

    private Drawable getDrawable(int position) {
        Drawable drawable;
        String resName;
        if (PhoneConfiguration.getInstance().iconmode) {
            resName = "oldp"+ mCategory.get(position).getIconOld();
        } else {
            resName = "p"+ mCategory.get(position).getIcon();
        }

        int resId = mActivity.getResources().getIdentifier(resName,"drawable",mActivity.getPackageName());
        if (resId != 0) {
            drawable = ContextCompat.getDrawable(mActivity,resId);
        } else {
            if (PhoneConfiguration.getInstance().iconmode) {
                drawable = ContextCompat.getDrawable(mActivity, R.drawable.oldpdefault);
            } else {
                drawable = ContextCompat.getDrawable(mActivity, R.drawable.pdefault);
            }
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
