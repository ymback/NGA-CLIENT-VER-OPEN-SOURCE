package sp.phone.interfaces;

import android.graphics.Bitmap;

public interface OnAuthcodeLoadFinishedListener {
    //void finishLoad(RSSFeed feed);
    void authcodefinishLoad(Bitmap authimg, String authcode);

    void authcodefinishLoadError();

}
