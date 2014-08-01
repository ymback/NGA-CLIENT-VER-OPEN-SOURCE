package gov.anzong.mediaplayer;

import io.vov.vitamio.LibsChecker;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class ReceiveIntentActivity extends Activity {
	public String uri,title;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		Intent intent=getIntent();
		uri=intent.getStringExtra("uri");
		title=intent.getStringExtra("title");
		if(!isEmpty(uri)){
			if(isEmpty(title)){
				title="未知来源视频";
			}
			VideoActivity.openVideo(this, Uri.parse(uri), title);
		}else{
			Toast.makeText(this, "视频地址错误", Toast.LENGTH_SHORT).show();
		}
		this.finish();
	}

	/** 判断是否是 "" 或者 null */
	public static boolean isEmpty(String str) {
		if (str != null && !"".equals(str)) {
			return false;
		} else {
			return true;
		}
	}
}
