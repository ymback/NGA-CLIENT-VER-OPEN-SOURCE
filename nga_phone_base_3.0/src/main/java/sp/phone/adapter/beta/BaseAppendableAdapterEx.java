package sp.phone.adapter.beta;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import sp.phone.view.RecyclerViewEx;

/**
 * Created by Justwen on 2018/6/10.
 */
public abstract class BaseAppendableAdapterEx<E, T extends RecyclerView.ViewHolder> extends BaseAdapterEx<E, T> implements RecyclerViewEx.IAppendableAdapter {

    private boolean mHasNextPage = true;

    private int mTotalPage;

    public BaseAppendableAdapterEx(Context context) {
        super(context);
    }

    @Override
    public int getNextPage() {
        return mTotalPage + 1;
    }

    @Override
    public boolean hasNextPage() {
        return mHasNextPage;
    }

    @Override
    public void setData(List<E> dataList) {
        mTotalPage = 0;
        mHasNextPage = true;
        super.setData(dataList);
    }

    public void appendData(List<E> dataList) {
        for (E e : dataList) {
            if (!mDataList.contains(e)) {
                mDataList.add(e);
            }
        }
        mTotalPage++;
        notifyDataSetChanged();

    }

    public void setNextPageEnabled(boolean enabled) {
        mHasNextPage = enabled;
    }
}
