package sp.phone.view.toolbar;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import gov.anzong.androidnga.R;
import sp.phone.mvp.contract.TopicPostContract.Presenter;
import sp.phone.view.KeyboardLayout;
import sp.phone.view.KeyboardLayout.KeyboardLayoutListener;

public class ToolbarContainer extends LinearLayout
        implements KeyboardLayoutListener, OnFocusChangeListener, OnTouchListener {

    private final Activity mActivity;

    private ViewGroup mActivePanel;

    private Runnable mAdjustImeRunnable;

    private CategoryControlPanel mCategoryPanel;

    private EmoticonControlPanel mEmoticonPanel;

    private View mFocusView;

    private FormattedControlPanel mFormattedPanel;

    private boolean mKeyboardActive;

    private int mKeyboardHeight;

    private Presenter mPresenter;

    public ToolbarContainer(Context context) {
        this(context, null);
    }

    public ToolbarContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mActivity = (Activity) context;
        mKeyboardHeight = getResources().getDimensionPixelSize(R.dimen.bottom_emoticon_min_height);
    }

    @Override
    protected void onFinishInflate() {
        ((KeyboardLayout) findViewById(R.id.keyboard_layout)).setListener(this);
        findViewById(R.id.btn_emoticon).setOnClickListener(v -> toggleEmoticonPanel());
        findViewById(R.id.btn_text).setOnClickListener(v -> toggleTextPanel());
        findViewById(R.id.btn_category).setOnClickListener(v -> toggleCategoryPanel());
        findViewById(R.id.btn_attachment).setOnClickListener(v -> mPresenter.showFilePicker());
        findViewById(R.id.btn_keyboard).setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) {
                return;
            }
            if (mKeyboardActive) {
                imm.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (mActivePanel == null || !mActivePanel.isShown()) {
                imm.showSoftInput(mFocusView, 0);
            } else {
                toggleInputMethod(mActivePanel);
            }
        });
        super.onFinishInflate();
    }

    public void setPresenter(Presenter presenter) {
        mPresenter = presenter;
    }

    private void toggleCategoryPanel() {
        if (mCategoryPanel == null) {
            ((ViewStub) findViewById(R.id.bottom_category_stub)).inflate();
            mCategoryPanel = findViewById(R.id.bottom_category_panel);
            mCategoryPanel.getLayoutParams().height = mKeyboardHeight;
            mCategoryPanel.setPresenter(mPresenter);
            mPresenter.loadTopicCategory(mCategoryPanel);
        }
        toggleControlPanel(mCategoryPanel);
    }

    private void toggleTextPanel() {
        if (mFormattedPanel == null) {
            ((ViewStub) findViewById(R.id.bottom_text_stub)).inflate();
            mFormattedPanel = findViewById(R.id.panel_super_text);
            mFormattedPanel.getLayoutParams().height = mKeyboardHeight;
            mFormattedPanel.setPresenter(mPresenter);
        }
        toggleControlPanel(mFormattedPanel);
    }

    private void toggleEmoticonPanel() {
        if (mEmoticonPanel == null) {
            ((ViewStub) findViewById(R.id.bottom_emoticon_stub)).inflate();
            mEmoticonPanel = findViewById(R.id.bottom_emoticon_panel);
            mEmoticonPanel.initialize(mKeyboardHeight);
        }
        toggleControlPanel(mEmoticonPanel);
    }

    private void toggleControlPanel(ViewGroup controlPanel) {
        if (mActivePanel == null || mActivePanel == controlPanel || !mActivePanel.isShown()) {
            mActivePanel = controlPanel;
            toggleInputMethod(controlPanel);
            return;
        }
        mActivePanel.setVisibility(GONE);
        mActivePanel = controlPanel;
        mActivePanel.setVisibility(VISIBLE);
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(mAdjustImeRunnable);
        super.onDetachedFromWindow();
    }

    private void toggleInputMethod(ViewGroup controlPanel) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        if (controlPanel.isShown()) {
            imm.showSoftInput(mFocusView, 0);
            adjustImeDelay();
        } else if (mKeyboardActive) {
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            controlPanel.setVisibility(VISIBLE);
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        } else {
            controlPanel.setVisibility(VISIBLE);
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        }
    }

    private void adjustImeDelay() {
        removeCallbacks(mAdjustImeRunnable);
        mAdjustImeRunnable = () -> {
            if (mActivePanel != null) {
                mActivePanel.setVisibility(GONE);
            }
            mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mAdjustImeRunnable = null;
        };
        postDelayed(mAdjustImeRunnable, 500);
    }

    @Override
    public void onKeyboardStateChanged(boolean isActive, int keyboardHeight) {
        mKeyboardActive = isActive;
        if (mKeyboardHeight < keyboardHeight) {
            mKeyboardHeight = keyboardHeight;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            mFocusView = v;
        }
    }

    public boolean onBackPressed() {
        if (mActivePanel == null || !mActivePanel.isShown()) {
            return false;
        }
        mActivePanel.setVisibility(GONE);
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mActivePanel == null || !mActivePanel.isShown()) {
                mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            } else {
                adjustImeDelay();
            }
        }
        return false;
    }
}