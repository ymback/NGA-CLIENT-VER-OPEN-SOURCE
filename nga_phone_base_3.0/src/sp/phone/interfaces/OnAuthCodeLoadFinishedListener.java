package sp.phone.interfaces;

import android.graphics.Bitmap;

public interface OnAuthCodeLoadFinishedListener {
    //void finishLoad(RSSFeed feed);
    void authCodeFinishLoad(Bitmap authImg, String authCode);

    void authCodeFinishLoadError();

}
