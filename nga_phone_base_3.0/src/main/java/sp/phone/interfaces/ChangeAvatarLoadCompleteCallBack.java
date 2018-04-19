package sp.phone.interfaces;

import android.graphics.Bitmap;

public interface ChangeAvatarLoadCompleteCallBack {
    void OnAvatarLoadStart(String url);

    void OnAvatarLoadComplete(String url, Bitmap result);
}
