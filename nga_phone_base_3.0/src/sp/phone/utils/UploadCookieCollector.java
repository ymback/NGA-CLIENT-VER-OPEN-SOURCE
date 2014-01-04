package sp.phone.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class UploadCookieCollector {
	private static final String LOG_TAG = UploadCookieCollector.class.getSimpleName();
	private Map<String,String> ConcernCookies ;
	//static final String collectURL = "http://nga.178.com/nuke.php";
	static final String collectURL = "http://bbs.ngacn.cc/nuke.php";
	public UploadCookieCollector(){
		ConcernCookies = new HashMap<String,String>();
		ConcernCookies.put("ngacn0comUserInfo=", null);
		ConcernCookies.put("ngacn0comUserInfoCheck=", null);
		ConcernCookies.put("ngacn0comInfoCheckTime=", null);
		ConcernCookies.put("ngaPassportUid=", null);
		ConcernCookies.put("ngaPassportUrlencodedUname=", null);
		ConcernCookies.put("ngaPassportCid=", null);
		ConcernCookies.put("_i=", null);

		
	}
	
	public String toString(){
		ConcernCookies.put("ngaPassportUid=", PhoneConfiguration.getInstance().getUid());
		String ret = "";
		for(Map.Entry<String, String> entry: ConcernCookies.entrySet())
		{
			ret = ret + entry.getKey() + entry.getValue() + "; ";
		}
		return ret;
	}
	
	public UploadCookieCollector StartCollect(){
		final PhoneConfiguration config = PhoneConfiguration.getInstance();
		/*String data = "func=login&uid=" + config .getUid()+" &cid=" + config.getCid()
				+"&expires=31536000&do_not_multi_login=1";
		HttpPostClient c =  new HttpPostClient(collectURL);
		String cookie = config.getCookie();
		c.setCookie(cookie);
		
		HttpURLConnection conn  = c.post_body(data);*/
		final String urlString  = collectURL + "?func=login&uid=" + config.getUid() 
				+ "&cid=" + config.getCid() + "&expires=31536000" ;
		//URL=http://bbs.ngacn.cc/nuke.php?func=login&uid=553736&cid=ca583128fd6a500fcee2ff9d5f6c656fffade423&expires=31536000
		
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.setRequestProperty("User-Agent", HttpUtil.USER_AGENT);

			conn.connect();

			//OutputStreamWriter out = new OutputStreamWriter(conn
			//		.getOutputStream());
			conn.getInputStream();
			
			UpdateCookie(conn);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return this;
	}
	
	public boolean UpdateCookie(HttpURLConnection conn)
	{
		if (conn == null)
			return false;
		String key = null;
		String cookieVal = null;
		for (int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++) {
			
			if (key.equalsIgnoreCase("set-cookie")) {
				Log.d(LOG_TAG, conn.getHeaderFieldKey(i) + ":"
						+ conn.getHeaderField(i));
				cookieVal = conn.getHeaderField(i);
				UpdateCookie(cookieVal);


			}


		}
		
		return true;
	}
	
	private boolean UpdateCookie(String cookieVal){
		for(Map.Entry<String, String> entry: ConcernCookies.entrySet())
		{
			int posStart = cookieVal.indexOf(entry.getKey());
			if( posStart != -1)
			{
				int posEnd = cookieVal.indexOf(';', posStart);
				if(posEnd == -1){
					posEnd = cookieVal.length();
				}
				final String newValue = cookieVal.substring(posStart + entry.getKey().length() , posEnd);
				ConcernCookies.put(entry.getKey(), newValue);
				
			}
		}
		return true;
	}

}
