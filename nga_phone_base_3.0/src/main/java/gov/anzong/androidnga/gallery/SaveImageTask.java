package gov.anzong.androidnga.gallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import gov.anzong.androidnga.R;
import sp.phone.util.ActivityUtils;

/**
 */
public class SaveImageTask extends AsyncTask<String, Void, File> {

    private final Context mContext;

    private String mTargetPath;

    public SaveImageTask(Context context, String savePath) {
        mContext = context.getApplicationContext();
        mTargetPath = savePath;
    }

    @Override
    protected File doInBackground(String... params) {
        String url = params[0]; // should be easy to extend to share multiple images at once
        try {
            return Glide
                    .with(mContext)
                    .load(url)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get(); // needs to be called on background thread
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(File result) {
        if (result == null) {
            return;
        }

        File file = new File(mTargetPath);

        try {
            FileUtils.copyFile(result, file);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String toast = mContext.getString(R.string.file_saved) + mTargetPath;
        ActivityUtils.showToast(toast);
        Uri uri = Uri.fromFile(file);
        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }

}
