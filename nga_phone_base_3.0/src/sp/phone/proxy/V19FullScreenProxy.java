package sp.phone.proxy;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;

/**
 * Created by Administrator on 13-11-27.
 */
public class V19FullScreenProxy implements FullScreenProxy {

    @Override
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setFullScreen(final View view) {
        final int flags = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        view.setSystemUiVisibility(flags);
        view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setSystemUiVisibility(flags);
                    }
                }, 5000);
            }
        });
    }

    @Override
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setNormalScreen(final View view) {
        final int flags = View.SYSTEM_UI_FLAG_VISIBLE;
        view.setSystemUiVisibility(flags);
        view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setSystemUiVisibility(flags);
                    }
                }, 5000);
            }
        });
    }
}
