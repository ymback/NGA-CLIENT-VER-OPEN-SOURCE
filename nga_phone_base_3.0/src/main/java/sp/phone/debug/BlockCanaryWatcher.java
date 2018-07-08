package sp.phone.debug;

import android.content.Context;
import android.support.annotation.MainThread;

import com.github.moduth.blockcanary.BlockCanary;
import com.github.moduth.blockcanary.BlockCanaryContext;

/**
 * Created by Justwen on 2018/7/2.
 */
public class BlockCanaryWatcher {

    @MainThread
    public static void startWatching(Context context) {
        BlockCanary.install(context, new BlockCanaryCustom()).start();
    }
}
