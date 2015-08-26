package gov.anzong.meizi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import gov.anzong.androidnga.R;

public class MeiziLoadingFooterTask {
    protected View mLoadingFooter;

    protected TextView mLoadingText;

    protected State mState = State.Idle;

    private ProgressBar mProgress;

    private Button mRetryButton;

    @SuppressWarnings("unused")
    private long mAnimationDuration;

    private ReloadListener mReloadListener;

    public MeiziLoadingFooterTask(Context context, ReloadListener listener) {
        mLoadingFooter = LayoutInflater.from(context).inflate(R.layout.loading_footer, null);
        mLoadingFooter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 屏蔽点击
            }
        });
        mProgress = (ProgressBar) mLoadingFooter.findViewById(R.id.progressBar);
        mLoadingText = (TextView) mLoadingFooter.findViewById(R.id.textView);
        mRetryButton = (Button) mLoadingFooter.findViewById(R.id.retryBtn);
        registerReloadListener(listener);
        mAnimationDuration = context.getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        setState(State.Idle);
    }

    public View getView() {
        return mLoadingFooter;
    }

    public State getState() {
        return mState;
    }

    public void setState(State status) {
        if (mState == status) {
            return;
        }
        mState = status;

        mLoadingFooter.setVisibility(View.VISIBLE);

        switch (status) {
            case Loading:
                mLoadingText.setVisibility(View.GONE);
                mProgress.setVisibility(View.VISIBLE);
                mRetryButton.setVisibility(View.GONE);
                break;
            case TheEnd:
                mLoadingText.setVisibility(View.VISIBLE);
                // mLoadingText.animate().withLayer().alpha(1).setDuration(mAnimationDuration);
                mProgress.setVisibility(View.GONE);
                mRetryButton.setVisibility(View.GONE);
                break;
            case Error:
                mRetryButton.setVisibility(View.VISIBLE);
                mLoadingText.setVisibility(View.GONE);
                mProgress.setVisibility(View.GONE);
                break;
            default:
                mLoadingFooter.setVisibility(View.GONE);
                break;
        }
    }

    public void setState(final State state, long delay) {
        mLoadingFooter.postDelayed(new Runnable() {

            @Override
            public void run() {
                setState(state);
            }
        }, delay);
    }

    public void registerReloadListener(ReloadListener listener) {
        mReloadListener = listener;
        mRetryButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mReloadListener != null) {
                    mReloadListener.onReload();
                }
            }
        });
    }

    public static enum State {
        Idle, TheEnd, Loading, Error
    }

    public static interface ReloadListener {

        public void onReload();
    }
}
