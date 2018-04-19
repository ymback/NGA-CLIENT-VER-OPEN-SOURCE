package sp.phone.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Justwen on 2017/10/9.
 * <p>
 * 支持emptyView 和 上滑加载更多
 */
public class RecyclerViewEx extends RecyclerView {

    private View mEmptyView;

    private OnNextPageLoadListener mNextPageLoadListener;

    private IAppendableAdapter mAppendAbleAdapter;

    private int mLastVisibleItemPosition;

    public interface IAppendableAdapter {

        int getNextPage();

        boolean hasNextPage();
    }

    public interface OnNextPageLoadListener {

        void loadNextPage();
    }

    private AdapterDataObserver mAdapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };

    public RecyclerViewEx(Context context) {
        super(context);
    }

    public RecyclerViewEx(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewEx(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        checkIfEmpty();
    }

    private void checkIfEmpty() {
        if (mEmptyView != null) {
            boolean isEmpty = getAdapter() == null || getAdapter().getItemCount() == 0;
            mEmptyView.setVisibility(isEmpty ? VISIBLE : GONE);
            setVisibility(isEmpty ? GONE : VISIBLE);
        }
    }

    public void setOnNextPageLoadListener(OnNextPageLoadListener loadListener) {
        mNextPageLoadListener = loadListener;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof IAppendableAdapter) {
            mAppendAbleAdapter = (IAppendableAdapter) adapter;
        }
        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(mAdapterDataObserver);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mAdapterDataObserver);
        }
        checkIfEmpty();
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        int totalCount = getAdapter().getItemCount();
        if (mLastVisibleItemPosition + 1 == totalCount
                && mAppendAbleAdapter != null
                && mNextPageLoadListener != null
                && mAppendAbleAdapter.hasNextPage()) {
            mNextPageLoadListener.loadNextPage();
        }
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        LayoutManager lm = getLayoutManager();
        if (lm instanceof LinearLayoutManager) {
            mLastVisibleItemPosition = ((LinearLayoutManager) lm).findLastCompletelyVisibleItemPosition();
        }
    }
}
