package gov.anzong.androidnga.gallery;

import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

public class LinearClock {
	private static final int FPS = 100;
	private static final int FRAME_TIME = 1000 / FPS;
	private int mDuration;
	private boolean mRunning;
	private long mBase;
	private boolean isLastFrame = false;
	private Handler mHandler;
	private ClockRunnable mTick;
	private ClockCallback mCallback;
	private Interpolator mInterpolator;

	public LinearClock(int duration, ClockCallback callback) {
		mDuration = duration+2*FRAME_TIME;
		mCallback = callback;
		mInterpolator = new AccelerateInterpolator();
		mHandler = new Handler();
	}

	public void start() {
		if (!mRunning) {
			mRunning = true;
			isLastFrame = false;
			mBase = SystemClock.uptimeMillis();
			mCallback.onTweenStarted();
			long next = SystemClock.uptimeMillis() + FRAME_TIME;
			if (mTick == null) {
				mTick = new ClockRunnable();
			}
			mTick.reset();
			mHandler.postAtTime(mTick, next);
		}
	}

	public void stop() {
		if (mTick != null) {
			mTick.stop();
			mHandler.removeCallbacks(mTick);
		}
		mRunning = false;
	}

	public boolean isRunning() {
		return mRunning;
	}

	class ClockRunnable implements Runnable {
		private boolean isFinish;
		private boolean isSuspend;

		public void reset() {
			isFinish = false;
			isSuspend = false;
		}

		public void stop() {
			isSuspend = true;
		}

		public void run() {
			if (!isSuspend) {
				if (!isFinish) {
					long base = mBase;
					long now = SystemClock.uptimeMillis();
					long diff = now - base;
					int duration = mDuration;
					float val = diff / (float) duration;
					val = mInterpolator.getInterpolation(val);
					val = Math.max(Math.min(val, 1.0f), 0.0f);
					int frame = (int) (diff / FRAME_TIME);
					long next = base + ((frame + 1) * FRAME_TIME);
					if (!isLastFrame) {
						mCallback.onTweenValueChanged(val);
					} else {
						mCallback.onTweenValueChanged(val);
						this.isFinish = true;
					}
					if (diff > duration) {
						if (!isLastFrame) {
							isLastFrame = true;
						}
					}
					mHandler.postAtTime(this, next);
				} else {
					mCallback.onTweenFinished();
					mRunning = false;
				}
			}
		}
	}
}
