package sp.phone.util;

import android.content.Context;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;

/**
 * Created by Justwen on 2019/3/12.
 */
public class ARouterUtils {

    private static ARouter sARouter = ARouter.getInstance();

    private ARouterUtils() {

    }

    public static Postcard build(String path) {
        return sARouter.build(path);
    }

    public static void navigation(String path) {
        navigation(null, path);
    }

    public static void navigation(Context context, String path) {
        sARouter.build(path).navigation(context);
    }
}
