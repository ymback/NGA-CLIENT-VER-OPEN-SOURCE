package sp.phone.task;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import sp.phone.utils.ActivityUtils;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.NLog;
import sp.phone.utils.StringUtils;
import sp.phone.utils.UploadCookieCollector;

public class FileUploadTask extends
        AsyncTask<String, Integer, String> {
    private static final String TAG = FileUploadTask.class.getSimpleName();
    private static final String BOUNDARY =
            "-----------------------------7db1c5232222b";
    private static final String ATTACHMENT_SERVER = "http://" + HttpUtil.NGA_ATTACHMENT_HOST + ":8080/attach.php?";
    private static final String LOG_TAG = FileUploadTask.class.getSimpleName();
    static final private String attachmentsStartFlag = "attachments:'";
    static final private String attachmentsEndFlag = "'";
    static final private String attachmentsCheckStartFlag = "attachments_check:'";
    static final private String attachmentsCheckEndFlag = "'";
    static final private String picUrlStartTag = "url:'";
    static final private String picUrlEndTag = "'";
    final private Uri uri;
    /*private InputStream is;*/
    private long filesize;
    private Context context;
    private onFileUploaded notifier;
    private String filename;
    @SuppressWarnings("unused")
    private String utfFilename;
    private String contentType;
    private String errorStr = null;
    private String auth;

	/*public FileUploadTask(InputStream is, long filesize, Context context, onFileUploaded notifier, String contentType) {
		super();
		this.is = is;
		this.filesize = filesize;
		this.context = context;
		this.notifier = notifier;
		this.contentType = contentType;
		this.filename = contentType.replace('/', '.');
		this.utfFilename = filename.substring(1);
	}*/

    public FileUploadTask(Context context, onFileUploaded notifier, Uri uri) {
        this.context = context;
        this.notifier = notifier;
        this.uri = uri;
    }

    public FileUploadTask(Context context, onFileUploaded notifier, Uri uri,String auth) {
        this.context = context;
        this.notifier = notifier;
        this.uri = uri;
        this.auth = auth;
    }

    @Override
    protected void onPreExecute() {
        ActivityUtils.getInstance().noticeSayingWithProgressBar(context);
        super.onPreExecute();
    }


    @Override
    protected void onCancelled() {
        ActivityUtils.getInstance().dismiss();
        super.onCancelled();
    }

    @Override
    protected void onCancelled(String result) {
        ActivityUtils.getInstance().dismiss();
        super.onCancelled();
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        if (values[0] == -101) {
            Toast.makeText(context, R.string.image_to_big, Toast.LENGTH_SHORT)
                    .show();
        } else {
            if (values[0] < 0 || values[0] > 100) {
                values[0] = 99;
            }
            ActivityUtils.getInstance().noticebarsetprogress(values[0]);
        }
    }


    @Override
    protected void onPostExecute(String result) {
        do {
            if (StringUtils.isEmpty(result))
                break;
            NLog.i(TAG, result);
            int start = result.indexOf(attachmentsStartFlag);
            if (start == -1)
                break;
            start = start + attachmentsStartFlag.length();
            int end = result.indexOf(attachmentsEndFlag, start);
            if (end == -1)
                break;
            String attachments = result.substring(start, end);
            attachments = StringUtils.encodeUrl(attachments, "utf-8");

            start = result.indexOf(attachmentsCheckStartFlag, start);
            if (start == -1)
                break;
            start = start + attachmentsCheckStartFlag.length();
            end = result.indexOf(attachmentsCheckEndFlag, start);
            if (end == -1)
                break;
            String attachmentsCheck = result.substring(start, end);
            attachmentsCheck = StringUtils.encodeUrl(attachmentsCheck, "utf-8");

            start = result.indexOf(picUrlStartTag, start);
            if (start == -1)
                break;
            start = start + picUrlStartTag.length();
            end = result.indexOf(picUrlEndTag, start);
            if (end == -1)
                break;
            String picUrl = result.substring(start, end);
            notifier.finishUpload(attachments, attachmentsCheck, picUrl, uri);
        } while (false);
        if (result == null && errorStr != null) {
            Toast.makeText(context, errorStr, Toast.LENGTH_SHORT).show();
        }
        ActivityUtils.getInstance().dismiss();
        super.onPostExecute(result);
    }

    @Override
    protected String doInBackground(String... params) {


        ContentResolver cr = context.getContentResolver();

        InputStream is = null;
        try {
            ParcelFileDescriptor pfd = cr.openFileDescriptor(uri, "r");
            contentType = cr.getType(uri);
            if (StringUtils.isEmpty(contentType)) {
                errorStr = context.getResources().getString(R.string.invalid_img_selected);
                return null;
            }
            filesize = pfd.getStatSize();
            if (filesize >= 1024 * 1024) {
                publishProgress(-101);
                byte[] img = ImageUtil.fitImageToUpload(cr.openInputStream(uri), cr.openInputStream(uri));
                contentType = "image/png";
                filesize = img.length;
                is = new ByteArrayInputStream(img);

            }


            NLog.d(LOG_TAG, "file size =" + filesize);
            pfd.close();
            if (is == null)
                is = cr.openInputStream(uri);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return null;
        }

        this.filename = contentType.replace('/', '.');
        this.utfFilename = filename.substring(1);

        final byte header[] = buildHeader().getBytes();
        final byte tail[] = buildTail().getBytes();

        final String cookie = new UploadCookieCollector().StartCollect().toString();
        String html = null;
        URL url;
        try {
            url = new URL(ATTACHMENT_SERVER);
            //NLog.d(LOG_TAG, "cookie:" + cookie);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + BOUNDARY);
            conn.setRequestProperty("Content-Length",
                    String.valueOf(header.length + filesize + tail.length));
            conn.setRequestProperty("Accept-Charset", "GBK");
            conn.setRequestProperty("Cookie", cookie);
            conn.setDoOutput(true);

            OutputStream out = conn.getOutputStream();

            byte[] buf = new byte[1024];
            int len;
            out.write(header);
            int ilen = 0, progress = 0;
            while ((len = is.read(buf)) != -1) {
                ilen += len;
                progress = (int) ((ilen / (float) filesize) * 100);
                if (progress == 100) {
                    progress = 99;
                }
                publishProgress(progress);
                out.write(buf, 0, len);
            }
            out.write(tail);

            is.close();
            InputStream httpInputStream = conn.getInputStream();
            html = IOUtils.toString(httpInputStream, "gbk");
            out.close();
            publishProgress(100);

        } catch (Exception e) {
            NLog.e(LOG_TAG, NLog.getStackTraceString(e));
        }

        return html;
    }

    private String buildHeader() {
        StringBuilder sb = new StringBuilder();
        final String keys[] = {"v2",
                "attachment_file1_watermark",
                "attachment_file1_dscp",
                "attachment_file1_url_utf8_name",
                "fid",
                "func",
                "attachment_file1_img", "origin_domain",
                "lite",
                "auth"};
        final String values[] = {"1", "", "", filename, "-7", "upload",
                "1", Utils.getNGADomain(), "js",auth
        };

        for (int i = 0; i < keys.length; ++i) {
            sb = sb.append("--");
            sb = sb.append(BOUNDARY);
            sb = sb.append("\r\n");
            sb = sb.append("Content-Disposition: form-data; name=\"" + keys[i] + "\"\r\n\r\n");
            sb = sb.append(values[i]);
            sb = sb.append("\r\n");
        }

        sb.append("--" + BOUNDARY + "\r\n");
        //sb.append("Content-Disposition: form-data; name=\"attachment_file1\"");
        sb.append("Content-Disposition: form-data; name=\"attachment_file1\"; ");
        sb.append("filename=\"");
        sb.append(filename);
        sb.append("\"");
        sb.append("\r\n");

        sb.append("Content-Type: ");
        sb.append(contentType);
        sb.append("\r\n\r\n");

        return sb.toString();

    }

    private String buildTail() {
        StringBuilder sb = new StringBuilder();
		/*sb.append("\r\n");
		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data;");
		sb.append(" name=\"attachment_file1_watermark\"\r\n\r\n\r\n");

		
		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data;");
		sb.append(" name=\"attachment_file1_dscp\"\r\n\r\n\r\n");
		
		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data;");
		sb.append(" name=\"attachment_file1_url_utf8_name\"\r\n\r\n");
		sb.append(utfFilename + "\r\n");
		
		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data;");
		sb.append(" name=\"func\"\r\n\r\nupload\r\n");
		
		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data;");
		sb.append(" name=\"fid\"\r\n\r\n-7\r\n");*/

        sb.append("\r\n--" + BOUNDARY + "--\r\n");

        return sb.toString();
    }

    public interface onFileUploaded {
        int finishUpload(String attachments, String attachmentsCheck, String picUrl, Uri uri);
    }

}
