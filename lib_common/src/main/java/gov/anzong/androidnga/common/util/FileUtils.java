package gov.anzong.androidnga.common.util;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import gov.anzong.androidnga.base.util.ContextUtils;

/**
 * @author yangyihang
 */
public class FileUtils {

    public static String readAssetToString(String path) {
        AssetManager assetManager = ContextUtils.getContext().getAssets();
        try (InputStream is = assetManager.open(path)) {
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
