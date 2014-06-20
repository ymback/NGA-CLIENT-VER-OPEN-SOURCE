package sp.phone.task;

import gov.anzong.androidnga.R;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

import sp.phone.utils.ActivityUtil;
import sp.phone.utils.HttpUtil;
import sp.phone.utils.ImageUtil;
import sp.phone.utils.StringUtil;
import sp.phone.utils.UploadCookieCollector;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class NonameFileUploadTask extends AsyncTask<String, Integer, String> {
	private static final String TAG = NonameFileUploadTask.class
			.getSimpleName();
	private static final String BOUNDARY = "-----------------------------7db1c5232222b";
	private static final String ATTACHMENT_SERVER = "http://ngac.sinaapp.com/nganoname/attach.php?";
	private static final String LOG_TAG = NonameFileUploadTask.class
			.getSimpleName();

	/* private InputStream is; */
	private long filesize;
	private Context context;
	private onFileUploaded notifier;

	private String filename;
	private String utfFilename;
	private String contentType;
	final private Uri uri;

	private String errorStr = "";

	/*
	 * public FileUploadTask(InputStream is, long filesize, Context context,
	 * onFileUploaded notifier, String contentType) { super(); this.is = is;
	 * this.filesize = filesize; this.context = context; this.notifier =
	 * notifier; this.contentType = contentType; this.filename =
	 * contentType.replace('/', '.'); this.utfFilename = filename.substring(1);
	 * }
	 */

	public NonameFileUploadTask(Context context, onFileUploaded notifier,
			Uri uri) {
		this.context = context;
		this.notifier = notifier;
		this.uri = uri;
	}

	@Override
	protected void onPreExecute() {
		ActivityUtil.getInstance().noticeSaying(context);
		super.onPreExecute();
	}

	@Override
	protected void onCancelled() {
		ActivityUtil.getInstance().dismiss();
		super.onCancelled();
	}

	@Override
	protected void onCancelled(String result) {
		ActivityUtil.getInstance().dismiss();
		super.onCancelled();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		if (values[0] == 50) {
			Toast.makeText(context, R.string.image_to_big, Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	protected void onPostExecute(String result) {
		if(StringUtil.isEmpty(result)){
			Toast.makeText(context, errorStr, Toast.LENGTH_SHORT)
			.show();
		}else{
			Gson gson = new Gson();
			NonameUploadResponse response = gson.fromJson(result,
					NonameUploadResponse.class);
			if (response.error == true) {
				Toast.makeText(context, response.errorinfo, Toast.LENGTH_SHORT)
						.show();
			} else {
				notifier.finishUpload(response.data, uri);
			}
		}
		ActivityUtil.getInstance().dismiss();
		super.onPostExecute(result);
	}

	@Override
	protected String doInBackground(String... params) {

		ContentResolver cr = context.getContentResolver();

		InputStream is = null;
		try {
			ParcelFileDescriptor pfd = cr.openFileDescriptor(uri, "r");
			contentType = cr.getType(uri);
			if (StringUtil.isEmpty(contentType)) {
				errorStr = context.getResources().getString(
						R.string.invalid_img_selected);
				return null;
			}
			filesize = pfd.getStatSize();
			if (filesize >= 1024 * 1024) {
				this.publishProgress(50);
				byte[] img = ImageUtil.fitImageToUpload(
						cr.openInputStream(uri), cr.openInputStream(uri));
				contentType = "image/png";
				filesize = img.length;
				is = new ByteArrayInputStream(img);

			}

			Log.d(LOG_TAG, "file size =" + filesize);
			pfd.close();
			if (is == null)
				is = cr.openInputStream(uri);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			errorStr = context.getResources().getString(
					R.string.img_cannot_read);
			return null;
		}

		this.filename = contentType.replace('/', '.');
		this.utfFilename = filename.substring(1);

		final byte header[] = buildHeader().getBytes();
		final byte tail[] = buildTail().getBytes();

		String html = null;
		URL url;
		try {
			url = new URL(ATTACHMENT_SERVER);
			// Log.d(LOG_TAG, "cookie:" + cookie);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);
			conn.setRequestProperty("Content-Length",
					String.valueOf(header.length + filesize + tail.length));
			conn.setRequestProperty("Accept-Charset", "GBK");
			conn.setDoOutput(true);

			OutputStream out = conn.getOutputStream();

			byte[] buf = new byte[1024];
			int len;
			out.write(header);
			while ((len = is.read(buf)) != -1)
				out.write(buf, 0, len);

			out.write(tail);

			is.close();
			InputStream httpInputStream = conn.getInputStream();
			for (int i = 1; (conn.getHeaderFieldKey(i)) != null; i++) {
				// Log.d(LOG_TAG, conn.getHeaderFieldKey(i) + ":"
				// + conn.getHeaderField(i));

			}
			html = IOUtils.toString(httpInputStream, "gbk");
			// Log.d(LOG_TAG, "get response" + html);
			out.close();

		} catch (Exception e) {
			errorStr = context.getResources().getString(
					R.string.net_work_error);
			Log.e(LOG_TAG, Log.getStackTraceString(e));
			return null;
		}

		return html;
	}

	private String buildHeader() {
		StringBuilder sb = new StringBuilder();
		final String keys[] = { "v2", "attachment_file1_watermark",
				"attachment_file1_dscp", "attachment_file1_url_utf8_name",
				"fid", "func", "attachment_file1_img", "origin_domain", "lite" };
		final String values[] = { "1", "", "", filename, "-7", "upload", "1",
				"nga.178.com", "js" };

		for (int i = 0; i < keys.length; ++i) {
			sb = sb.append("--");
			sb = sb.append(BOUNDARY);
			sb = sb.append("\r\n");
			sb = sb.append("Content-Disposition: form-data; name=\"" + keys[i]
					+ "\"\r\n\r\n");
			sb = sb.append(values[i]);
			sb = sb.append("\r\n");
		}

		sb.append("--" + BOUNDARY + "\r\n");
		// sb.append("Content-Disposition: form-data; name=\"attachment_file1\"");
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
		/*
		 * sb.append("\r\n"); sb.append("--" + BOUNDARY + "\r\n");
		 * sb.append("Content-Disposition: form-data;");
		 * sb.append(" name=\"attachment_file1_watermark\"\r\n\r\n\r\n");
		 * 
		 * sb.append("--" + BOUNDARY + "\r\n");
		 * sb.append("Content-Disposition: form-data;");
		 * sb.append(" name=\"attachment_file1_dscp\"\r\n\r\n\r\n");
		 * sb.append("--" + BOUNDARY + "\r\n");
		 * sb.append("Content-Disposition: form-data;");
		 * sb.append(" name=\"attachment_file1_url_utf8_name\"\r\n\r\n");
		 * sb.append(utfFilename + "\r\n"); sb.append("--" + BOUNDARY + "\r\n");
		 * sb.append("Content-Disposition: form-data;");
		 * sb.append(" name=\"func\"\r\n\r\nupload\r\n"); sb.append("--" +
		 * BOUNDARY + "\r\n"); sb.append("Content-Disposition: form-data;");
		 * sb.append(" name=\"fid\"\r\n\r\n-7\r\n");
		 */

		sb.append("\r\n--" + BOUNDARY + "--\r\n");

		return sb.toString();
	}

	public interface onFileUploaded {
		int finishUpload(String picUrl, Uri uri);
	}

	private static class NonameUploadResponse {
		public boolean error;
		public String errorinfo;
		public String data;
	}

}