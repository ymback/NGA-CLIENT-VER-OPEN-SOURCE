package sp.phone.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import sp.phone.bean.AvatarTag;
import sp.phone.interfaces.AvatarLoadCompleteCallBack;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.ImageUtil;

public class AvatarLoadTask extends AsyncTask<String, Integer, Bitmap> {
    static final String TAG = AvatarLoadTask.class.getSimpleName();
    final ImageView view;
    final ZipFile zipFile;
    final boolean downImg;
    final int floor;
    final AvatarLoadCompleteCallBack callBack;
    String uri = null;

    public AvatarLoadTask(ImageView view, ZipFile zipFile, boolean downImg, int floor, AvatarLoadCompleteCallBack callBack) {
        super();
        this.view = view;
        this.zipFile = zipFile;
        this.downImg = downImg;
        this.floor = floor;
        this.callBack = callBack;
    }


    @SuppressWarnings("resource")
    @Override
    protected Bitmap doInBackground(String... params) {

        final String avatarUrl = params[0];
        final String avatarLocalPath = params[1];
        uri = avatarUrl;
        callBack.OnAvatarLoadStart(uri);

        Bitmap bitmap = null;
        InputStream is = null;

        try {
            is = new FileInputStream(avatarLocalPath);
        } catch (FileNotFoundException e) {
            //Log.d(TAG,
            //		"avatar:" + avatarLocalPath + " is not cached" );
        }


        if (is == null && downImg) {
            HttpUtil.downImage(avatarUrl, avatarLocalPath);
            try {
                is = new FileInputStream(avatarLocalPath);
                //Log.d(TAG,
                //		"download avatar from " + avatarUrl);

            } catch (FileNotFoundException e) {
                Log.d(TAG,
                        "avatar " + avatarUrl + " is failed to download");
            }
        }

        if (is != null) {
            //Log.d(TAG,"load avatar from file: " + avatarLocalPath);
            bitmap = ImageUtil.loadAvatarFromSdcard(avatarLocalPath);
        }


        return bitmap;
    }


    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            AvatarTag tag = (AvatarTag) view.getTag();
            int floor = tag.lou;
            if (floor == this.floor)
                view.setImageBitmap(result);
            else
                result.recycle();
        }
        callBack.OnAvatarLoadComplete(uri);
    }


    @Override
    protected void onCancelled(Bitmap result) {

        onCancelled();
    }


    @Override
    protected void onCancelled() {
        callBack.OnAvatarLoadComplete(uri);
    }


}
