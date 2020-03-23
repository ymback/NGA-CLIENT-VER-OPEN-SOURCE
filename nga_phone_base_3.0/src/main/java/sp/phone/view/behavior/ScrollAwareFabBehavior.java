package sp.phone.view.behavior;

import android.content.Context;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

/**
 * Created by Justwen on 2017/6/3.
 */

public class ScrollAwareFabBehavior extends FloatingActionButton.Behavior {

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    private boolean mIsAnimatingOut = false;

    private boolean mIsAnimationIn = false;

    private boolean mIsShown = true;

    private static final int SCROLL_AXIS_VERTICAL = 10;

    public ScrollAwareFabBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
                                       View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }


    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dx, int dy, int[] consumed) {
        if (dy > SCROLL_AXIS_VERTICAL && !mIsAnimatingOut && mIsShown) {
            // User scrolled down and the FAB is currently visible -> hide the FAB
            animateOut(child);
        } else if (dy < -SCROLL_AXIS_VERTICAL && !mIsAnimationIn && !mIsShown) {
            // User scrolled up and the FAB is currently not visible -> show the FAB
            animateIn(child);
        }
    }

    // Same animation that FloatingActionButton.Behavior uses to hide the FAB when the AppBarLayout exits
    private void animateOut(final FloatingActionButton button) {
        ViewCompat.animate(button).translationY(button.getHeight() + getMarginBottom(button)).setInterpolator(INTERPOLATOR).withLayer()
                .setListener(new ViewPropertyAnimatorListener() {
                    public void onAnimationStart(View view) {
                        mIsAnimatingOut = true;
                    }

                    public void onAnimationCancel(View view) {
                        mIsAnimatingOut = false;
                    }

                    public void onAnimationEnd(View view) {
                        mIsAnimatingOut = false;
                        mIsShown = false;
                    }
                }).start();
    }

    // Same animation that FloatingActionButton.Behavior uses to show the FAB when the AppBarLayout enters
    private void animateIn(FloatingActionButton button) {
        button.setVisibility(View.VISIBLE);
        ViewCompat.animate(button).translationY(0)
                .setInterpolator(INTERPOLATOR).withLayer().setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
                mIsAnimationIn = true;
            }

            @Override
            public void onAnimationEnd(View view) {
                mIsShown = true;
                mIsAnimationIn = false;
            }

            @Override
            public void onAnimationCancel(View view) {
                mIsAnimationIn = false;
            }
        }).start();
    }

    private int getMarginBottom(View v) {
        int marginBottom = 0;
        final ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
        }
        return marginBottom;
    }
}
