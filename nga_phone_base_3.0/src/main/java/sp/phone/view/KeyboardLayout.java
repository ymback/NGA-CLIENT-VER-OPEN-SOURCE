package sp.phone.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by Justwen on 2018/6/8.
 */
public class KeyboardLayout extends FrameLayout {

    private int mKeyboardHeight;

    private boolean mKeyboardActive;

    private KeyboardLayoutListener mListener;

    public interface KeyboardLayoutListener {
        /**
         * @param isActive       输入法是否激活
         * @param keyboardHeight 输入法面板高度
         */
        void onKeyboardStateChanged(boolean isActive, int keyboardHeight);
    }

    private class KeyboardOnGlobalChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {

        int mScreenHeight = 0;

        private int getScreenHeight() {
            if (mScreenHeight > 0) {
                return mScreenHeight;
            }
            mScreenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
            return mScreenHeight;
        }

        @Override
        public void onGlobalLayout() {
            Rect rect = new Rect();
            // 获取当前页面窗口的显示范围
            ((Activity) getContext()).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            int screenHeight = getScreenHeight();
            int keyboardHeight = screenHeight - rect.bottom; // 输入法的高度
            boolean isActive = false;
            if (Math.abs(keyboardHeight) > screenHeight / 5) {
                isActive = true; // 超过屏幕五分之一则表示弹出了输入法
                mKeyboardHeight = keyboardHeight;
            }
            mKeyboardActive = isActive;
            if (mListener != null) {
                mListener.onKeyboardStateChanged(isActive, keyboardHeight);
            }
        }
    }

    public KeyboardLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardOnGlobalChangeListener());
    }

    public void setListener(KeyboardLayoutListener listener) {
        mListener = listener;
    }
}
