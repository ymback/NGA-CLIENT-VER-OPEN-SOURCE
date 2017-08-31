package gov.anzong.androidnga.gallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.util.UiUtil;

/**
 */
public class SaveImageTask extends AsyncTask<String, Void, File> {
    private final Context context;
    private String savePath;

    public SaveImageTask(Context context, String savePath) {
        this.context = context;
        this.savePath = savePath;
    }

    @Override
    protected File doInBackground(String... params) {
        String url = params[0]; // should be easy to extend to share multiple images at once
        try {
            return Glide
                    .with(context)
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
        try {
            copyFile(result, new File(savePath), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String toast = context.getString(R.string.file_saved) + savePath;
        UiUtil.showToast(context, toast);
        Uri uri = Uri.fromFile(new File(savePath));
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }

    public static void copyFile(File sourceFile, File desFile, boolean isDeleteSourceFile) throws IOException {
        if (sourceFile.isFile() && sourceFile.exists()) {
            byte[] buffer = new byte[1024];
            InputStream fis = new FileInputStream(sourceFile);
            File dir = desFile.getParentFile();
            if (!dir.exists())
                dir.mkdirs();
            OutputStream fos = new FileOutputStream(desFile);
            int b = fis.read(buffer);
            while (b != -1) {
                fos.write(buffer, 0, b);
                b = fis.read(buffer);
            }
            fis.close();
            fos.flush();
            fos.close();
            if (isDeleteSourceFile) {
                sourceFile.delete();
            }
        }
    }
}
