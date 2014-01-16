package sp.phone.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.commons.io.IOUtils;

import sp.phone.forumoperation.HttpPostClient;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.StringUtil;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;


//有Bug，且坑太大，干脆去掉了删除收藏的功能
public class DeleteBookmarkTask extends AsyncTask<String, Integer, String> {
	//String url = "http://bbs.ngacn.cc/nuke.php?func=topicfavor&action=del";
	//post tidarray:3092111
	private Context context;
	private final String url = "http://bbs.ngacn.cc/nuke.php?__lib=topic_favor&lite=js&noprefix&__act=topic_favor&action=del&tid=";
	
	
	
	public DeleteBookmarkTask(Context context) {
		super();
		this.context = context;
	}

	@Override
	protected String doInBackground(String... params) {

		/* 这个Tid是有问题的，真正的Tid应该是Tid_Pid才能准确删除收藏列表里的某一项，但是
		 * Pid的信息在Json Parse的部分就被扔掉了，要做的话要从网络部分一路改起，坑太大，慎重慎重！
		 */
		String tid = params[0];
		HttpPostClient c =  new HttpPostClient(url+tid);
		String cookie = PhoneConfiguration.getInstance().getCookie();
		c.setCookie(cookie);
		String body ="__lib=topic_favor&__act=topic_favor&lite=js&noprefix&action=del&tid="+tid;

		String ret = null;
		try {
			InputStream input = null;
			HttpURLConnection conn = c.post_body(body);
			if(conn!=null)
				input = conn.getInputStream();
			
			if(input != null)
			{
				String html = IOUtils.toString(input, "gbk");
				ret = html;//getPostResult(html);

			}
			
			}catch(IOException e){
				
			}
		return ret;
	}

	@Override
	protected void onPreExecute() {
		ActivityUtil.getInstance().noticeSaying(context);
	}

	@Override
	protected void onPostExecute(String result) {
		ActivityUtil.getInstance().dismiss();
		if(StringUtil.isEmpty(result))
			return;
		
		String msg = StringUtil.getStringBetween(result, 0, "{\"0\":\"", "\"},\"time\"").result;
		//android.R.drawable.ic_search_category_default
		if(!StringUtil.isEmpty(msg)){
			Toast.makeText(context, msg.trim(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onCancelled(String result) {
		this.onCancelled();
	}

	@Override
	protected void onCancelled() {
		ActivityUtil.getInstance().dismiss();
	}
	
	

}