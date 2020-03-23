package sp.phone.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sp.phone.view.RecyclerViewEx;

/**
 * Created by Justwen on 2018/3/23.
 */

public abstract class BaseAppendableAdapter<E, T extends RecyclerView.ViewHolder> extends BaseAdapter<E, T> implements RecyclerViewEx.IAppendableAdapter {

    private boolean mHaveNextPage = true;

    private int mTotalPage;

    public BaseAppendableAdapter(Context context) {
        super(context);
    }

    @Override
    public int getNextPage() {
        return mTotalPage + 1;
    }

    @Override
    public boolean hasNextPage() {
        return mHaveNextPage;
    }

    @Override
    public void setData(List<E> dataList) {
        mTotalPage = 0;
        mHaveNextPage = true;
        super.setData(dataList);
    }

    public void appendData(List<E> dataList) {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }

        for (E e : dataList) {
            if (!mDataList.contains(e)) {
                mDataList.add(e);
            }
        }
        mTotalPage++;
        notifyDataSetChanged();

    }

    public void setNextPageEnabled(boolean enabled) {
        mHaveNextPage = enabled;
    }
}
