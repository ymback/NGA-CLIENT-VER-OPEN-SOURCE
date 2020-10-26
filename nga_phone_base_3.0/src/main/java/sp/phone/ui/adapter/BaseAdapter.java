package sp.phone.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by Justwen on 2018/3/23.
 */

public abstract class BaseAdapter<E, T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    protected List<E> mDataList;

    protected View.OnClickListener mOnClickListener;

    protected View.OnLongClickListener mOnLongClickListener;

    protected Context mContext;

    protected LayoutInflater mLayoutInflater;

    public BaseAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public E getItem(int position) {
        return mDataList.get(position);
    }

    public void setData(List<E> dataList) {
        mDataList = dataList;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mDataList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItem(E e) {
        int index = mDataList.indexOf(e);
        if (index != -1) {
            removeItem(index);
        }
    }

    public void clear() {
        setData(null);
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
