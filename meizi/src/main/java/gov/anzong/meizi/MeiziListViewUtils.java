package gov.anzong.meizi;

import android.os.Build.VERSION;

import com.huewu.pla.lib.MultiColumnListView;

public class MeiziListViewUtils {
    private MeiziListViewUtils() {

    }

    /**
     * 滚动列表到顶端
     *
     * @param listView
     */
    public static void smoothScrollListViewToTop(final MultiColumnListView listView) {
        if (listView == null) {
            return;
        }
        smoothScrollListView(listView, 0);
        listView.postDelayed(new Runnable() {

            @Override
            public void run() {
                listView.setSelection(0);
            }
        }, 200);
    }

    /**
     * 滚动列表到position
     *
     * @param listView
     * @param position
     * @param offset
     * @param duration
     */
    public static void smoothScrollListView(MultiColumnListView listView, int position) {
        if (VERSION.SDK_INT > 7) {
            listView.smoothScrollToPosition(0, 0);
        } else {
            listView.setSelection(position);
        }
    }
}
