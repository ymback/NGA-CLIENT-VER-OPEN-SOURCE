package sp.phone.view;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Justwen on 2017/6/3.
 */

public class ScrollableViewPager extends ViewPager {

    private int mHeight;

    private boolean mEnableScroll = true;

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

    public void setHeight(int height) {
        mHeight = height;
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
//        setMeasuredDimension(widthMeasureSpec, newHeightMeasureSpec);
//    }
}
