package sp.phone.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Justwen on 2017/6/3.
 */

public class ScrollableViewPager extends ViewPager {


    private boolean mEnableScroll = false;

    public ScrollableViewPager(Context context) {
        super(context);
    }

    public ScrollableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setEnableScroll(boolean enable) {
        mEnableScroll = enable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        return mEnableScroll && super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
        return mEnableScroll && super.onInterceptTouchEvent(arg0);

    }

}
