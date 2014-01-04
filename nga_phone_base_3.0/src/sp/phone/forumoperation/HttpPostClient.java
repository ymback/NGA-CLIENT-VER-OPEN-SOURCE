package sp.phone.forumoperation;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import sp.phone.utils.HttpUtil;





import android.util.Log;

public class HttpPostClient {
	public HttpPostClient(String urlString) 
	{
		this.urlString = urlString;
		cookie = null;
	}

	private static final String LOG_TAG = HttpPostClient.class
			.getSimpleName();
	private String urlString;
	private String cookie;
	
	/**
	 * @return the cookie
	 */
	public String getCookie() {
		return cookie;
	}

	/**
	 * @param cookie the cookie to set
	 */
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public HttpURLConnection post_body(String body)
	{
		HttpURLConnection conn;
		try
		{
			
		    
			URL url = new URL(this.urlString);
			conn = (HttpURLConnection) url.openConnection();
			if(cookie != null)
				conn.setRequestProperty("Cookie", cookie);
			conn.setInstanceFollowRedirects(false);
			
			conn.setRequestProperty("User-Agent", HttpUtil.USER_AGENT);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
			conn.setRequestProperty("Content-Length", String.valueOf(body.length())); 
			conn.setRequestProperty("Accept-Charset", "GBK");
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
	
			conn.connect();
	
			OutputStreamWriter out = new OutputStreamWriter(conn
					.getOutputStream());
			out.write(body);
			out.flush();
			out.close();

	
			
			Log.i(LOG_TAG, conn.getResponseMessage());

		}catch(Exception e) {
			//sb.append(e.toString());
			conn = null;
			Log.e(LOG_TAG, Log.getStackTraceString(e));
		}
		return conn;
	}

}
