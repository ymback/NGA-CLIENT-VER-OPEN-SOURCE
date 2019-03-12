package sp.phone.util;

import android.content.Context;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;

/**
 * Created by Justwen on 2019/3/12.
 */
public class ARouterUtils {

    private ARouterUtils() {

    }

    public static Postcard build(String path) {
        return ARouter.getInstance().build(path);
    }

    public static void navigation(String path) {
        navigation(null, path);
    }

    public static void navigation(Context context, String path) {
        ARouter.getInstance().build(path).navigation(context);
    }

}
