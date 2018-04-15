package gov.anzong.androidnga.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;
import sp.phone.common.PreferenceKey;

/**
 * Created by Administrator on 13-9-29.
 */
public abstract class SwipeBackAppCompatActivity extends BaseActivity implements SwipeBackActivityBase {
    public static final int MY_EDGE_SIZE = 10;
    private SwipeBackActivityHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBack();
    }

    private void setSwipeBack() {
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        if (getSharedPreferences(PreferenceKey.PREFERENCE_SETTINGS, Context.MODE_PRIVATE).getBoolean(PreferenceKey.KEY_SWIPE_BACK, true)) {
            final float density = getResources().getDisplayMetrics().density;// 获取屏幕密度PPI
            getSwipeBackLayout().setEdgeSize((int) (MY_EDGE_SIZE * density + 0.5f));// 10dp
            int pos = SwipeBackLayout.EDGE_LEFT | SwipeBackLayout.EDGE_RIGHT;
            getSwipeBackLayout().setEdgeTrackingEnabled(pos);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v != null)
            return v;
        return mHelper.findViewById(id);
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        getSwipeBackLayout().scrollToFinishActivity();
    }
}
