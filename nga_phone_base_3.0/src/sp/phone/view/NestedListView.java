package sp.phone.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.util.AttributeSet;
import android.widget.ListView;

import sp.phone.utils.DeviceUtils;

/**
 * Created by Yang Yihang on 2017/6/3.
 */

public class NestedListView extends ListView  {

    private int mDyConsumed;


    public NestedListView(Context context) {
        this(context,null);
    }

    public NestedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (DeviceUtils.isGreaterEqual_5_0()) {
            setNestedScrollingEnabled(true);
        }
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable @Size(value = 2) int[] offsetInWindow) {
        mDyConsumed = dyConsumed;
        return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable @Size(value = 2) int[] consumed, @Nullable @Size(value = 2) int[] offsetInWindow) {
        if (mDyConsumed < 0 && dy > 0){
            mDyConsumed = 0;
            super.dispatchNestedScroll(dx,dy,dx,dy,offsetInWindow);
        }
        return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }
}
