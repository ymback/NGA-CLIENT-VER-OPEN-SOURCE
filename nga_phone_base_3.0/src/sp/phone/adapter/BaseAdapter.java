package sp.phone.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Created by Justwen on 2018/3/23.
 */

public abstract class BaseAdapter<E, T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    protected List<E> mDataList;

    protected View.OnClickListener mOnClickListener;

    protected View.OnLongClickListener mOnLongClickListener;

    public E getItem(int position) {
        return mDataList.get(position);
    }

    public void setData(List<E> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mDataList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        mOnLongClickListener = onLongClickListener;
    }
}
