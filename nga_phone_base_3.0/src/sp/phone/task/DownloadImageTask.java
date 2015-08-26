package sp.phone.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

import gov.anzong.androidnga.R;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.StringUtil;

import static android.media.MediaScannerConnection.scanFile;

public class DownloadImageTask extends AsyncTask<String, Integer, String> {

    private final Context context;
    private String fullPath;

    public DownloadImageTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

        ActivityUtil.getInstance().noticeSaying(context);
    }

    @Override
    protected void onPostExecute(String result) {
        ActivityUtil.getInstance().dismiss();
        String description = context.getResources().getString(R.string.image_saved)
                + HttpUtil.PATH_IMAGES;
        if (result != null)
            description = result;
        else
            scanFile(context, new String[]{fullPath}, null, null);
        Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
        super.onPostExecute(result);
    }

    @Override
    protected String doInBackground(String... params) {

        final String invalidURI = context.getResources().getString(R.string.invalid_url);
        final String networkError = context.getResources().getString(R.string.network_error);
        final String mkdirFailed = context.getResources().getString(R.string.mkdir_fail);
        if (params.length == 0)
            return invalidURI;
        final String uri = params[0];
        if (StringUtil.isEmpty(uri))
            return invalidURI;

        String path = HttpUtil.PATH_IMAGES;
        File f = new File(path);

        if (!f.exists()) {
            if (!f.mkdir())
                return mkdirFailed;

        }
        String name = ImageUtil.getImageName(uri);
        if (StringUtil.isEmpty(name))
            return invalidURI;
        int i = 0;
        fullPath = path + "/" + name;
        String extension = FilenameUtils.getExtension(name);
        String baseName = FilenameUtils.getBaseName(name);
        do {
            f = new File(fullPath);
            if (!f.exists())
                break;
            ++i;

            fullPath = path + "/" + baseName + i + "." + extension;
        } while (true);

        HttpUtil.downImage(uri, fullPath);

        f = new File(fullPath);
        if (!f.exists())
            return networkError;


        return null;
    }

}
