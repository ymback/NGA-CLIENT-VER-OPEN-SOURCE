package sp.phone.utils;

import java.net.URLEncoder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class YouKuLoadFunction {
	@SuppressWarnings("static-access")
	public static String Load(String HTML) {
		if(StringUtil.isEmpty(HTML))
			return null;
		try {
			JSONObject JSON = null;
			String ep, dataep, ip, vid, sid, token;
			JSON = (JSONObject) JSON.parseObject(HTML);
			JSONArray dataarray = JSON.getJSONArray("data");
			JSONObject data = JSON.parseObject(dataarray.get(0).toString());
			dataep = data.getString("ep");
			ip = data.getString("ip");
			vid = data.getString("videoid");
			String list[] = yk_e("becaf9be", yk_na(dataep)).split("_");
			sid = list[0];
			token = list[1];
			ep = URLEncoder.encode(yk_d(yk_e("bf7e5f01", sid + "_" + vid + "_" + token)),"utf-8");
			String time = String.valueOf(System.currentTimeMillis());
			String tvaddr = "http://pl.youku.com/playlist/m3u8?type=mp4&vid="
					+ vid + "&keyframe=0&ts=" + time + "&ctype=12&ev=1&token="
					+ token + "&oip=" + ip + "&ep=" + ep + "&sid=" + sid;
			return tvaddr;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String yk_file_id(String fileId, String seed) {
		String mixed = yk_Mix_String(seed);
		String ids[] = fileId.split("\\*");
		String realId = "";
		for (int i = 0; i < ids.length; i++) {
			int idx = Integer.parseInt(ids[i]);
			realId += mixed.substring(idx, idx + 1);
		}
		return realId;
	}

	public static String yk_Mix_String(String seedstr) {
		long seed = Long.parseLong(seedstr);
		String string = "abcdefghijklmnopqrstuvwxyz"
				+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "/\\:._-1234567890";
		int count = string.length();
		int index = 0;
		String item;
		String mixed = "";
		for (int i = 0; i < count; i++) {
			seed = (seed * 211L + 30031L) % 65536;
			index = (int) ((seed * string.length() / 65536L));
			item = string.substring(index, index + 1);
			mixed += item;
			string = string.replace(item, "");
		}
		return mixed;
	}

	public static String yk_na(String a) {
		if (StringUtil.isEmpty(a)) {
			return "";
		}
		String SZ = "-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,62,-1,-1,-1,63,52,53,54,55,56,57,58,59,60,61,-1,-1,-1,-1,-1,-1,-1,0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,-1,-1,-1,-1,-1,-1,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,-1,-1,-1,-1,-1";
		String h[] = SZ.split(",");
		int i = a.length();
		int f = 0;
		String e = "";
		int b, c;
		for (; f < i;) {
			do {
				c = Integer.parseInt(h[Character.codePointAt(a, f++) & 255]);
			} while (f < i && c == -1);
			if (c == -1) {
				break;
			}
			do {
				b = Integer.parseInt(h[Character.codePointAt(a, f++) & 255]);
			} while (f < i && b == -1);
			if (b == -1) {
				break;
			}
			e += Character.toString((char) (c << 2 | (b & 48) >> 4));
			do {
				c = Character.codePointAt(a, f++) & 255;
				if (c == 61) {
					return e;
				}
				c = Integer.parseInt(h[c]);
			} while (f < i && -1 == c);
			if (-1 == c) {
				break;
			}
			e += Character.toString((char) ((b & 15) << 4 | (c & 60) >> 2));
			do {
				b = Character.codePointAt(a, f++) & 255;
				if (61 == b) {
					return e;
				}
				b = Integer.parseInt(h[b]);
			} while (f < i && -1 == b);
			if (-1 == b) {
				break;
			}
			e += Character.toString((char) ((c & 3) << 6 | b));
		}
		return e;
	}

	public static String yk_d(String a) {
		if (StringUtil.isEmpty(a)) {
			return "";
		}
		int f = a.length();
		int b = 0;
		int e;
		String c = "";
		String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
				+ "abcdefghijklmnopqrstuvwxyz" + "0123456789+/";
		for (; b < f;) {
			e = Character.codePointAt(a, b++) & 255;
			if (b == f) {
				c = String.valueOf(str.charAt(e >> 2));
				c += String.valueOf(str.charAt((e & 3) << 4));
				c += "==";
				break;
			}
			int g = Character.codePointAt(a, b++);
			if (b == f) {
				c += String.valueOf(str.charAt(e >> 2));
				c += String.valueOf(str.charAt((e & 3) << 4 | (g & 240) >> 4));
				c += String.valueOf(str.charAt((g & 15) << 2));
				c += "=";
				break;
			}
			int h = Character.codePointAt(a, b++);
			c += String.valueOf(str.charAt(e >> 2));
			c += String.valueOf(str.charAt((e & 3) << 4 | (g & 240) >> 4));
			c += String.valueOf(str.charAt((g & 15) << 2 | (h & 192) >> 6));
			c += String.valueOf(str.charAt(h & 63));
		}
		return c;
	}

	public static String yk_e(String a, String c) {
		String d = "";
		int h = 0;
		int b[] = new int[256];
		int f = 0;
		int i;
		for (; 256 > h; h++) {
			b[h] = h;
		}
		for (h = 0; 256 > h; h++) {
			f = (f + b[h] + Character.codePointAt(a, h % a.length())) % 256;
			i = b[h];
			b[h] = b[f];
			b[f] = i;
		}
		h = 0;
		f = 0;
		for (int p = 0; p < c.length(); p++) {
			h = (h + 1) % 256;
			f = (f + b[h]) % 256;
			i = b[h];
			b[h] = b[f];
			b[f] = i;
			d += Character
					.toString((char) (Character.codePointAt(c, p) ^ b[(b[h] + b[f]) % 256]));
		}
		return d;
	}
}
