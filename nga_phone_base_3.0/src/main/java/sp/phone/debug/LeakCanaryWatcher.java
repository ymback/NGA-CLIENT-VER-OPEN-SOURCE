package sp.phone.debug;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class LeakCanaryWatcher {

    private static RefWatcher sRefWatcher;

    private LeakCanaryWatcher() {
        throw new IllegalStateException("Utility class");
    }

    public static void initialize(Application application) {
        if (!LeakCanary.isInAnalyzerProcess(application)) {
            sRefWatcher = LeakCanary.install(application);
        }
    }

    public static void watch(Object obj) {
        if (sRefWatcher != null) {
            sRefWatcher.watch(obj);
        }
    }
}
