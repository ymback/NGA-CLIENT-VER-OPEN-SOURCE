/*package sp.phone.task;

import sp.phone.adapter.ArticleListAdapter;
import sp.phone.bean.ThreadRowInfo;
import android.os.AsyncTask;
import android.webkit.WebView;

public class ForumTagDecodTask extends AsyncTask<WebView, Integer, String> {

	final ThreadRowInfo row;
	final boolean showImage;
	final String fgColorStr;
	final String bgcolorStr;

	
	WebView webview;
	
	public ForumTagDecodTask(ThreadRowInfo row, boolean showImage,
			String fgColorStr, String bgcolorStr) {
		super();
		this.row = row;
		this.showImage = showImage;
		this.fgColorStr = fgColorStr;
		this.bgcolorStr = bgcolorStr;

	}

	@Override
	protected String doInBackground(WebView ... params) {
		this.webview = params[0];
		return ArticleListAdapter.convertToHtmlText(row, showImage, fgColorStr, bgcolorStr);
	}

	@Override
	protected void onPostExecute(String result) {
		if(result == null)
		{
			return;
		}
		int tag = (Integer) webview.getTag();
		//final int lou = row.getLou();
		if(tag == row.getLou()){
			final String htmldata = result;
			webview.loadDataWithBaseURL(null,htmldata, "text/html", "utf-8",null);
			//Log.d("loadDataWithBaseURL", "load content for "+ lou);
			
		}
		super.onPostExecute(result);
	}
	

}
*/