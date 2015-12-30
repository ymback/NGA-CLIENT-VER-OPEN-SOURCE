package gov.anzong.androidnga.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;

import java.util.LinkedList;

import gov.anzong.androidnga.util.UiUtil;

public class CommonGestureControlGalleryView extends ViewGroup implements ClockCallback {

    private CommonGalleryViewAdapter mAdapter;
    private Scroller mScroller;
    private GalleryData mGalleryData;
    private int mInitPageIndex;

    private int mCurrentPage = 0;

    private int mScrollX = 0;
    private float mLastMotionX;
    private float mLastMotionY;

    private int mTouchSlop = 0;
    private VelocityTracker mVelocityTracker;
    private static final int SNAP_VELOCITY = 1000;

    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;
    private final static int TOUCH_STATE_SCROLLING_CHILD = 2;
    private final static int TOUCH_STATE_SINGLE_NOROTATION = 3;
    private final static int TOUCH_STATE_DOUBLE_POINT = 4;
    private int mTouchState = TOUCH_STATE_REST;

    private int mWidth;
    private int mHeight;
    private LinkedList<View> mCacheViews;
    private OnGalleryPageChangeListener mPageChangeListener;
    private OnScaleModeChangeListener mOnScaleModeChangeListener;

    private final static float FIT_SCREEN_SCALE = 1.0f;
    private final static float FIT_SCREEN_TRANSLATEX = 0;
    private final static float FIT_SCREEN_TRANSLATEY = 0;
    private final static float FIT_SCREEN_ROTATION = 0;
    private final static int ALPHA_TRANSPARENT = 0;
    private final static int ALPHA_BLACK = 255;

    private float mOriginalScale;
    private float mOriginalTranslateX;
    private float mOriginalTranslateY;

    private float mRotation = FIT_SCREEN_ROTATION;
    private float mScale = FIT_SCREEN_SCALE;
    private float mTranslateX = FIT_SCREEN_TRANSLATEX;
    private float mTranslateY = FIT_SCREEN_TRANSLATEY;

    private float mTempTranslateX;
    private float mTempTranslateY;
    private float mTempScale;
    private float mTempRotation;

    private float mDistTranslateX;
    private float mDistTranslateY;
    private float mDistScale;
    private float mDistRotation;

    private Matrix mMatrix = new Matrix();
    PointF mMidPoint = new PointF();
    PointF mCurrentMidPoint = new PointF();
    private LinearClock mLinearClock;
    private boolean mIsFistResume = true;
    private boolean mIsExit = false;
    private int mAlpha = ALPHA_TRANSPARENT;

    public static final int MODE_ORIGINAL = 0;
    public static final int MODE_FIT_WIDTH = 1;
    public static final int MODE_BIG_SCALE = 2;
    private int mMode = MODE_ORIGINAL;
    private static final float DEFAULT_SCALE_DISTANCE = 1.0f;
    private float mFirstDist = DEFAULT_SCALE_DISTANCE;
    private float mChangeDist = DEFAULT_SCALE_DISTANCE;
    private float mFirstRotation = FIT_SCREEN_ROTATION;
    private float mBaseScale = FIT_SCREEN_SCALE;
    private float mBaseTranslateY = FIT_SCREEN_TRANSLATEY;
    private float mBaseTranslateX = FIT_SCREEN_TRANSLATEX;
    private int EXTRA_SCROLL_DIS;

    public CommonGestureControlGalleryView(Context context) {
        super(context);
        init(context);
    }

    public CommonGestureControlGalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mCacheViews = new LinkedList<View>();
        EXTRA_SCROLL_DIS = UiUtil.dip2px(getContext(), 80);
    }

    public void setGalleryData(GalleryData galleryData, int initPageIndex) {
        this.mGalleryData = galleryData;
        this.mInitPageIndex = initPageIndex;
        Rect imageRect = mGalleryData.getImageRect();
        setGallerySize(imageRect.right - imageRect.left, imageRect.bottom - imageRect.top);
    }

    public void setGallerySize(int width, int height) {
        mWidth = width;
        mHeight = height;
        mMidPoint.x = mWidth / 2;
        mMidPoint.y = mHeight / 2;
        mCurrentMidPoint.x = mMidPoint.x;
        mCurrentMidPoint.y = mMidPoint.y;
    }

    public void setAdapter(CommonGalleryViewAdapter adapter) {
        if (this.mAdapter != null) {
            this.mAdapter.clear();
        }
        this.mAdapter = adapter;
        requestLayout();
    }

    public CommonGalleryViewAdapter getAdapter() {
        return mAdapter;
    }

    public void setGalleryPageChangeListener(
            OnGalleryPageChangeListener pageChangeListener) {
        this.mPageChangeListener = pageChangeListener;
    }

    public void setOnScaleModeChangeListener(
            OnScaleModeChangeListener onScaleModeChangeListener) {
        this.mOnScaleModeChangeListener = onScaleModeChangeListener;
    }

    private View getCachedView() {
        if (mCacheViews == null || mCacheViews.size() == 0) {
            return null;
        }
        return mCacheViews.removeFirst();
    }

    public int getCurrentPageIndex() {
        return mCurrentPage;
    }

    public boolean isFirstResume() {
        return mIsFistResume;
    }

    public void startFirstResume() {
        if (mIsFistResume) {
            mIsFistResume = false;
            calculateResumeParmas();
            startLinearClock();
        }
    }

    public void startExit() {
        // 临时解决方案
        if (mCurrentPage != mInitPageIndex) {
            ((Activity) getContext()).finish();
            return;
        }
        if (mScroller.isFinished()) {
            mScrollX = getScrollX();
            int page = Math.abs(mScrollX / mWidth);
            if (page == mCurrentPage) {
                if (mMode == MODE_BIG_SCALE) {
                    setMode(MODE_FIT_WIDTH);
                    calculateChangeParmas();
                    startLinearClock();
                } else {
                    if (!mIsExit) {
                        mIsExit = true;
                        setMode(MODE_ORIGINAL);
                        mMidPoint.x = mWidth / 2;
                        mMidPoint.y = mHeight / 2;
                        calculateChangeParmas();
                        startLinearClock();
                    }
                }
            }
        }
    }

    private void calculateResumeParmas() {
        Rect imageRect = mGalleryData.getImageRect();
        mScale = mOriginalScale = ((float) imageRect.width()) / mWidth;
        mTranslateX = mOriginalTranslateX = ((float) (imageRect.left
                + imageRect.right - mWidth)) / 2;
        mTranslateY = mOriginalTranslateY = ((float) (imageRect.top
                + imageRect.bottom - mHeight)) / 2;
        mRotation = FIT_SCREEN_ROTATION;

        setMode(MODE_FIT_WIDTH);
        calculateChangeParmas();

    }

    private void calculateAlphaValue() {
        if (mScale < mOriginalScale) {
            mAlpha = ALPHA_TRANSPARENT;
        } else if (mScale >= FIT_SCREEN_SCALE) {
            mAlpha = ALPHA_BLACK;
        } else {
            mAlpha = (int) (ALPHA_BLACK * ((mScale - mOriginalScale) / (FIT_SCREEN_SCALE - mOriginalScale)));
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            canvas.drawARGB(mAlpha, 0, 0, 0);
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                int index = (Integer) child.getTag();
                canvas.save();
                mMatrix.reset();
                if (index == mCurrentPage) {
                    mMatrix.postScale(mScale, mScale, mWidth / 2, mHeight / 2);
                    mMatrix.postTranslate(mTranslateX, mTranslateY);
                    mMatrix.postRotate(mRotation, mCurrentMidPoint.x,
                            mCurrentMidPoint.y);
                }
                canvas.setMatrix(mMatrix);
                drawChild(canvas, child, getDrawingTime());
                canvas.restore();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE && mTouchState != TOUCH_STATE_REST)
                || mTouchState == TOUCH_STATE_DOUBLE_POINT || mIsExit) {
            return true;
        }
        float x = ev.getX();
        float y = ev.getY();

        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                requestLayout();
                mLastMotionX = x;
                mLastMotionY = y;
                mBaseTranslateY = mTranslateY;
                mBaseTranslateX = mTranslateX;
                mTouchState = TOUCH_STATE_REST;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (mTouchState == TOUCH_STATE_REST) {
                    mTouchState = TOUCH_STATE_DOUBLE_POINT;
                    mBaseScale = mScale;
                    mBaseTranslateY = mTranslateY;
                    mBaseTranslateX = mTranslateX;
                    mFirstDist = spacing(ev);
                    mFirstRotation = rotation(ev);
                    setMidPoint(mMidPoint, ev);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                final int xDiff = (int) Math.abs(x - mLastMotionX);
                boolean xMoved = xDiff > mTouchSlop;
                int yDiff = (int) Math.abs(y - mLastMotionY);
                boolean yMoved = yDiff > mTouchSlop;
                if (!isRunningAnimation()) {
                    if (xMoved && mMode != MODE_BIG_SCALE) {
                        mTouchState = TOUCH_STATE_SCROLLING;
                    } else if (yMoved && mMode != MODE_BIG_SCALE) {
                        mTouchState = TOUCH_STATE_SINGLE_NOROTATION;
                    } else if ((xMoved || yMoved) && mMode == MODE_BIG_SCALE) {
                        mTouchState = TOUCH_STATE_SCROLLING_CHILD;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                break;
        }
        if (mTouchState != TOUCH_STATE_REST) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsExit) {
            return true;
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                requestLayout();
                mLastMotionX = x;
                mBaseTranslateY = mTranslateY;
                mBaseTranslateX = mTranslateX;
                mTouchState = TOUCH_STATE_REST;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (mTouchState != TOUCH_STATE_SCROLLING
                        && mTouchState != TOUCH_STATE_SCROLLING_CHILD) {
                    if (event.getPointerCount() == 2) {
                        if (!isRunningAnimation()) {
                            mFirstDist = spacing(event);
                        }
                        if (mTouchState != TOUCH_STATE_DOUBLE_POINT) {
                            mTouchState = TOUCH_STATE_DOUBLE_POINT;
                            mBaseScale = mScale;
                            mBaseTranslateY = mTranslateY;
                            mBaseTranslateX = mTranslateX;
                            setMidPoint(mMidPoint, event);
                            mFirstRotation = rotation(event);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                final int deltaX = (int) (mLastMotionX - x);
                boolean xMoved = Math.abs(deltaX) > mTouchSlop;
                int deltaY = (int) (mLastMotionY - y);
                boolean yMoved = Math.abs(deltaY) > mTouchSlop;
                if (mTouchState == TOUCH_STATE_REST && (xMoved || yMoved)) {
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                    if (mTouchState != TOUCH_STATE_DOUBLE_POINT
                            && !isRunningAnimation()) {
                        if (mMode == MODE_BIG_SCALE) {
                            mTouchState = TOUCH_STATE_SCROLLING_CHILD;
                        } else {
                            if (xMoved) {
                                mTouchState = TOUCH_STATE_SCROLLING;
                            } else if (yMoved) {
                                mTouchState = TOUCH_STATE_SINGLE_NOROTATION;
                                scrollTo(mCurrentPage * mWidth, 0);
                                requestLayout();
                            }
                        }
                    }
                }
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    mLastMotionX = x;
                    if (deltaX < 0) {
                        if (mScrollX > -EXTRA_SCROLL_DIS) {
                            scrollBy(
                                    Math.max(-EXTRA_SCROLL_DIS - mScrollX, deltaX),
                                    0);
                            requestLayout();
                        }
                    } else if (deltaX > 0) {
                        final int availableToScroll = (mAdapter.getCount() - 1)
                                * mWidth - mScrollX + EXTRA_SCROLL_DIS;
                        if (availableToScroll > 0) {
                            scrollBy(Math.min(availableToScroll, deltaX), 0);
                            requestLayout();
                        }
                    }
                    mScrollX = this.getScrollX();
                    return true;
                } else if (mTouchState == TOUCH_STATE_SINGLE_NOROTATION) {
                    float delta = Math.abs(deltaY);
                    mScale = FIT_SCREEN_SCALE - delta / (mHeight / 2);
                    mScale = Math.max(mOriginalScale * 0.5f, mScale);
                    mTranslateY = -deltaY;
                    calculateAlphaValue();
                    invalidate();
                    return true;
                } else if (mTouchState == TOUCH_STATE_SCROLLING_CHILD) {
                    mTranslateX = mBaseTranslateX - deltaX;
                    mTranslateY = mBaseTranslateY - deltaY;
                    invalidate();
                    return true;
                } else if (mTouchState == TOUCH_STATE_DOUBLE_POINT
                        && event.getPointerCount() == 2) {
                    boolean runningAnimation = isRunningAnimation();
                    if (runningAnimation) {
                        mLinearClock.stop();
                    }
                    mRotation = rotation(event) - mFirstRotation;
                    mChangeDist = spacing(event);
                    float scale = mChangeDist / mFirstDist == 0 ? 1.0f
                            : mChangeDist / mFirstDist;
                    mScale = mBaseScale * scale;
                    mScale = Math.max(mScale, 0.1f);
                    calculateAlphaValue();
                    setMidPoint(mCurrentMidPoint, event);
                    if (mMode == MODE_BIG_SCALE) {
                        mTranslateX = mBaseTranslateX
                                + (mCurrentMidPoint.x - mMidPoint.x);
                        mTranslateY = mBaseTranslateY
                                + (mCurrentMidPoint.y - mMidPoint.y);
                    } else {
                        mTranslateX = mCurrentMidPoint.x - mMidPoint.x;
                        mTranslateY = mCurrentMidPoint.y - mMidPoint.y;
                    }
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000);
                    int velocityX = (int) velocityTracker.getXVelocity();

                    if (velocityX > SNAP_VELOCITY && mCurrentPage - 1 >= 0) {
                        snapToPage(mCurrentPage - 1);
                    } else if (velocityX < -SNAP_VELOCITY
                            && mCurrentPage + 1 < mAdapter.getCount()) {
                        snapToPage(mCurrentPage + 1);
                    } else {
                        snapToDestination();
                    }
                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                    mScrollX = this.getScrollX();
                    mTouchState = TOUCH_STATE_REST;
                    return true;
                } else if (mTouchState == TOUCH_STATE_SCROLLING_CHILD) {
                    calculateChangeParmas();
                    startLinearClock();
                    return true;
                } else if (mTouchState == TOUCH_STATE_DOUBLE_POINT
                        || mTouchState == TOUCH_STATE_SINGLE_NOROTATION) {
                    if (isChange()) {
                        calculateChildMode();
                        calculateChangeParmas();
                        startLinearClock();
                    } else {
                        mTouchState = TOUCH_STATE_REST;
                    }
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean isChange() {
        if (mScale != FIT_SCREEN_SCALE || mTranslateX != FIT_SCREEN_TRANSLATEX
                || mTempTranslateY != FIT_SCREEN_TRANSLATEY
                || mRotation != FIT_SCREEN_ROTATION) {
            return true;
        }
        return false;
    }

    private void setMode(int mode) {
        mMode = mode;
        if (mOnScaleModeChangeListener != null) {
            mOnScaleModeChangeListener.onScaleModeChange(mMode);
        }
    }

    private void calculateChildMode() {
        if (mScale >= mOriginalScale && mScale <= FIT_SCREEN_SCALE) {
            setMode(MODE_FIT_WIDTH);
        } else if (mScale < mOriginalScale) {
            setMode(MODE_ORIGINAL);
        } else {
            setMode(mMode == MODE_ORIGINAL ? MODE_FIT_WIDTH : MODE_BIG_SCALE);
        }

    }

    private void calculateChangeParmas() {
        switch (mMode) {
            case MODE_ORIGINAL:
                mDistRotation = FIT_SCREEN_ROTATION;
                mDistScale = mOriginalScale;
                mDistTranslateX = mOriginalTranslateX;
                mDistTranslateY = mOriginalTranslateY;
                break;

            case MODE_FIT_WIDTH:
                mDistRotation = FIT_SCREEN_ROTATION;
                mDistScale = FIT_SCREEN_SCALE;
                mDistTranslateX = FIT_SCREEN_TRANSLATEX;
                mDistTranslateY = FIT_SCREEN_TRANSLATEY;
                break;
            case MODE_BIG_SCALE:
                mDistRotation = FIT_SCREEN_ROTATION;
                mDistScale = mScale;
                calculateTranslateX();
                calculateDisTranslateY();
                break;

        }
    }

    private void calculateTranslateX() {
        float leftScale = mWidth / 2 * (mScale - 1);
        if (mTranslateX > leftScale) {
            mDistTranslateX = leftScale;
        } else if (-mTranslateX > leftScale) {
            mDistTranslateX = -leftScale;
        } else {
            mDistTranslateX = mTranslateX;
        }
    }

    private void calculateDisTranslateY() {
        float scaleWidth = (float) mWidth / mGalleryData.getImageRect().width();
        float height = mGalleryData.getImageRect().height() * scaleWidth;
        float rectHeightScale = height * mScale;
        if (rectHeightScale <= mHeight) {
            mDistTranslateY = FIT_SCREEN_TRANSLATEY;
        } else {
            float canTranslateY = (rectHeightScale - mHeight) / 2;
            if (mTranslateY > canTranslateY) {
                mDistTranslateY = canTranslateY;
            } else if (-mTranslateY > canTranslateY) {
                mDistTranslateY = -canTranslateY;
            } else {
                mDistTranslateY = mTranslateY;
            }
        }
    }

    private boolean isRunningAnimation() {
        return mLinearClock != null && mLinearClock.isRunning();
    }

    private void snapToDestination() {
        int PageWidth = mWidth;
        int whichPage = (mScrollX + (PageWidth / 2)) / PageWidth;
        snapToPage(whichPage);
    }

    public void snapToPage(int whichPage) {
        int count = mAdapter.getCount();
        whichPage = whichPage < 0 ? 0 : whichPage;
        whichPage = whichPage >= count ? count : whichPage;
        boolean isChange = mCurrentPage != whichPage;
        mCurrentPage = whichPage;
        if (isChange && mPageChangeListener != null) {
            mPageChangeListener.onPageChange(mGalleryData, mCurrentPage);
        }
        final int newX = whichPage * mWidth;
        final int delta = newX - mScrollX;
        mScroller.startScroll(mScrollX, 0, delta, 0, Math.abs(delta) / 2);
        invalidate();
    }

    public void scrollToPage(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < mAdapter.getCount()
                && pageIndex != mCurrentPage) {
            mCurrentPage = pageIndex;
            scrollTo(mCurrentPage * mWidth, 0);
            if (mPageChangeListener != null) {
                mPageChangeListener.onPageChange(mGalleryData, mCurrentPage);
            }
            requestLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mAdapter != null && mAdapter.getCount() > 0) {
            mScrollX = getScrollX();
            removeOtherChild();
            addGalleryChild();
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() != View.GONE) {
                    int tag = (Integer) child.getTag();
                    child.layout(-mScrollX + mWidth * tag, 0, -mScrollX
                            + mWidth * tag + mWidth, mHeight);
                }
            }
        }
    }

    private void addGalleryChild() {
        int page = Math.abs(mScrollX / mWidth);
        addCurrentChild(page);
        addPreChild(page - 1);
        addNextChild(page + 1);
    }

    private void addCurrentChild(int page) {
        addGalleryChildView(page);
    }

    private void addPreChild(int page) {
        if (page >= 0) {
            addGalleryChildView(page);
        }
    }

    private void addNextChild(int page) {
        if (page < mAdapter.getCount()) {
            addGalleryChildView(page);
        }
    }

    private void addGalleryChildView(int page) {
        ImageView child = getchildByTag(page);
        if (child == null) {
            ImageView view = (ImageView) mAdapter.getView(page,
                    getCachedView(), this);
            addViewInLayout(view, 0, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            view.measure(MeasureSpec.EXACTLY | mWidth,
                    MeasureSpec.EXACTLY | mHeight);
        }
    }

    private ImageView getchildByTag(int Page) {
        ImageView imageView = null;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView child = (ImageView) getChildAt(i);
            if (child != null) {
                int tag = (Integer) child.getTag();
                if (tag == Page) {
                    imageView = child;
                    break;
                }
            }
        }
        return imageView;
    }

    private void removeOtherChild() {
        int page = Math.abs(mScrollX / mWidth);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) getChildAt(i);
            if (imageView != null) {
                int position = (Integer) imageView.getTag();
                if (position != page && position != page - 1
                        && position != page + 1) {
                    removeViewInLayout(imageView);
                    mCacheViews.add(imageView);
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0, height = 0;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
                width = mWidth;
                break;
            default:
                width = MeasureSpec.getSize(widthMeasureSpec);
        }
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
                height = mHeight;
                break;
            default:
                height = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(MeasureSpec.EXACTLY | mWidth,
                    MeasureSpec.EXACTLY | mHeight);
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mScrollX = mScroller.getCurrX();
            scrollTo(mScrollX, 0);
            requestLayout();
            postInvalidate();
        }
    }

    public void reset() {
        mAdapter.clear();
        mCacheViews.clear();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child != null) {
                removeViewInLayout(child);
            }
        }
    }

    public interface OnGalleryPageChangeListener {
        public void onPageChange(GalleryData galleryData, int pageIndex);
    }

    private void startLinearClock() {
        if (mLinearClock == null) {
            mLinearClock = new LinearClock(300, this);
        }
        mLinearClock.stop();
        mLinearClock.start();
    }

    private float spacing(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        if (pointerCount >= 2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        }
        return 0;
    }

    private float rotation(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        if (pointerCount >= 2) {
            double delta_x = (event.getX(0) - event.getX(1));
            double delta_y = (event.getY(0) - event.getY(1));
            double radians = Math.atan2(delta_y, delta_x);
            return (float) Math.toDegrees(radians);
        } else
            return 0;
    }

    private void setMidPoint(PointF point, MotionEvent event) {
        int pointerCount = event.getPointerCount();
        if (pointerCount >= 2) {
            float x = event.getX(0) + event.getX(1);
            float y = event.getY(0) + event.getY(1);
            point.set(x / 2, y / 2);
        }
    }

    @Override
    public void onTweenValueChanged(float timeRatio) {
        float ratio = 1 - timeRatio;
        mTranslateX = mDistTranslateX + (mTempTranslateX - mDistTranslateX)
                * ratio;
        mTranslateY = mDistTranslateY + (mTempTranslateY - mDistTranslateY)
                * ratio;
        mCurrentMidPoint.x = mTranslateX + mMidPoint.x;
        mCurrentMidPoint.y = mTranslateY + mMidPoint.y;
        mRotation = mDistRotation + (mTempRotation - mDistRotation) * ratio;
        mScale = mDistScale + (mTempScale - mDistScale) * ratio;
        calculateAlphaValue();
        postInvalidate();
    }

    @Override
    public void onTweenStarted() {
        mTempScale = mScale;
        mTempRotation = mRotation % 360;
        mTempTranslateX = mTranslateX;
        mTempTranslateY = mTranslateY;
        if (mTempRotation < -180) {
            mTempRotation = mTempRotation + 360;
        }
    }

    @Override
    public void onTweenFinished() {
        mDistTranslateX = FIT_SCREEN_TRANSLATEX;
        mDistTranslateY = FIT_SCREEN_TRANSLATEY;
        mDistRotation = FIT_SCREEN_ROTATION;
        mDistScale = FIT_SCREEN_SCALE;
        mTouchState = TOUCH_STATE_REST;
        if (mIsExit || mMode == MODE_ORIGINAL) {
            ((Activity) getContext()).finish();
        }
    }

    public interface OnScaleModeChangeListener {
        public void onScaleModeChange(int mode);
    }

    private void sendBroadcast(Intent intent) {
        if (intent == null)
            return;
        intent.setPackage(getContext().getPackageName());
        getContext().sendBroadcast(intent);
    }
}
