package sp.phone.task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sp.phone.utils.HttpUtil;
import sp.phone.utils.StringUtil;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

public class YoukuVideoLoader extends AsyncTask<String, Integer, String> {
	static final String TAG = YoukuVideoLoader.class.getSimpleName();
	
	final private Activity act;
	
	String format=null;
	public YoukuVideoLoader(Activity act) {
		super();
		this.act = act;
	}

	@Override
	protected String doInBackground(String... params) {
		if(params.length ==0)
			return null;
		final String swfUri = params[0];
		final String startStr = "http://v.youku.com/v_show/id_";
		final String endStr = ".html";
		final String videoId = StringUtil.getStringBetween(swfUri, 0, startStr, endStr).result;
		final String jsonUri = "http://v.youku.com/player/getPlayList/VideoIDS/"
				+ videoId;
		String html = HttpUtil.getHtml(jsonUri, null);
		
		try{
		JSONObject jsobj = new JSONObject(html);
		JSONArray jsonarr = jsobj.getJSONArray("data");
		JSONObject obj1 = jsonarr.getJSONObject(0);
		double seed = obj1.getDouble("seed");
		JSONObject obj2 = obj1.getJSONObject("streamfileids");
		String mp4id = null;
		String flvid = null;

		try
		{
			mp4id = obj2.getString("mp4");
			format = "mp4";
		} catch (JSONException e)
		{
			System.out.println("没有MP4格式");
			try
			{
				flvid = obj2.getString("flv");
				format = "flv";
			} catch (JSONException e1)
			{
				System.out.println("没有FLV格式");
				return null;
			}
		}
 
		String realfileid = null;
		if (format.equals("mp4"))
		{
			realfileid = getFileID(mp4id, seed);
		} else
		{
			realfileid = getFileID(flvid, seed);
		}
		String idLeft = realfileid.substring(0, 8);
		String idRight = realfileid.substring(10);
 
		String sid = genSid();
		JSONObject obj3 = obj1.getJSONObject("segs");
		JSONArray mp4arr = obj3.getJSONArray(format);
		String url = null;
		for (int i = 0; i < mp4arr.length();)
		{
			JSONObject o = mp4arr.getJSONObject(i);
			String k = o.getString("k");
			url = "http://f.youku.com/player/getFlvPath/sid/" + sid + "_" + String.format("%1$02X", i) + "/st/" + format
					+ "/fileid/" + idLeft + String.format("%1$02X", i) + idRight + "?K=" + k;
			System.out.println(url);
			break;
		}
			return url;
		}catch(JSONException e){
			return null;
		}
 

	}

	@Override
	protected void onPostExecute(String result) {
		if(result != null){
			String mime = "video/"+ format;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse(result), mime );
			act.startActivity(intent);
		}
		super.onPostExecute(result);
	}


	private static String getFileID(String fileid, double seed)
	{
		String mixed = getFileIDMixString(seed);
		String[] ids = fileid.split("\\*");
		StringBuilder realId = new StringBuilder();
		int idx;
		for (int i = 0; i < ids.length; i++)
		{
			idx = Integer.parseInt(ids[i]);
			realId.append(mixed.charAt(idx));
		}
		return realId.toString();
	}
 
	public static String genSid()
	{
		int i1 = (int) (1000 + Math.floor(Math.random() * 999));
		int i2 = (int) (1000 + Math.floor(Math.random() * 9000));
		return System.currentTimeMillis() + "" + i1 + "" + i2;
	}
 
	private static String getFileIDMixString(double seed)
	{
		StringBuilder mixed = new StringBuilder();
		StringBuilder source = new StringBuilder("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ/\\:._-1234567890");
		int index, len = source.length();
		for (int i = 0; i < len; ++i)
		{
			seed = (seed * 211 + 30031) % 65536;
			index = (int) Math.floor(seed / 65536 * source.length());
			mixed.append(source.charAt(index));
			source.deleteCharAt(index);
		}
		return mixed.toString();
	}

}
