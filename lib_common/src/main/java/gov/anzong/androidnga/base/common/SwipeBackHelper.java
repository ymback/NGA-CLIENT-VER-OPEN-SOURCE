package gov.anzong.androidnga.base.common;

import android.app.Activity;
import androidx.annotation.IdRes;
import android.view.View;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

public class SwipeBackHelper {

    private SwipeBackActivityHelper mHelper;

    public void onCreate(Activity activity) {
        mHelper = new SwipeBackActivityHelper(activity);
        mHelper.onActivityCreate();
        SwipeBackLayout swipeBackLayout = mHelper.getSwipeBackLayout();
        float density = activity.getResources().getDisplayMetrics().density;
        swipeBackLayout.setEdgeSize((int) (10 * density + 0.5f));
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_ALL);
    }

    public void onPostCreate() {
        mHelper.onPostCreate();
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return (T) mHelper.findViewById(id);
    }

}
