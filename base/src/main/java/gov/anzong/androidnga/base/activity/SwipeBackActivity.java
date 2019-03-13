package gov.anzong.androidnga.base.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import gov.anzong.androidnga.base.common.PreferenceKey;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

public abstract class SwipeBackActivity extends AppCompatActivity {

    private SwipeBackActivityHelper mHelper;

    private int mSwipeBackState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configSwipeBack();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mHelper != null) {
            mHelper.onPostCreate();
        }
    }

    @Override
    public <T extends View> T findViewById(int id) {
        T view = super.findViewById(id);
        if (view == null && mHelper != null) {
            view = (T) mHelper.findViewById(id);
        }
        return view;
    }

    public void setSwipeBackEnable(boolean enable) {
        mSwipeBackState = enable ? 1 : -1;
    }

    private void configSwipeBack() {
        if (getSharedPreferences(PreferenceKey.PREFERENCE_SETTINGS, Context.MODE_PRIVATE).getBoolean(PreferenceKey.KEY_SWIPE_BACK, true)
                && mSwipeBackState != -1) {
            mHelper = new SwipeBackActivityHelper(this);
            mHelper.onActivityCreate();
            SwipeBackLayout swipeBackLayout = mHelper.getSwipeBackLayout();
            float density = getResources().getDisplayMetrics().density;
            swipeBackLayout.setEdgeSize((int) (10 * density + 0.5f));
            swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT | SwipeBackLayout.EDGE_RIGHT);
        }
    }

}
