package sp.phone.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Yang Yihang on 2017/6/24.
 */

public class PermissionUtil {

    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 0;


    public static boolean hasStoragePermission(Context context){
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestStoragePermission(Activity activity){
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
    }
}
