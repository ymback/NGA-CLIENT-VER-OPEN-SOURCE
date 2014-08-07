package gov.anzong.androidnga.activity;

import gov.anzong.androidnga.R;
import sp.phone.task.DownloadImageTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.ThemeManager;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.support.v7.app.ActionBarActivity;

public class ImageViewerActivity extends  SwipeBackAppCompatActivity {
	private WebView wv;
	//private final String IPHONE_UA = "Mozilla/5.0 (iPad; CPU OS 5_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B176 Safari/7534.48.3";
	@Override
	protected void onCreate(Bundle arg0) {
		if(ActivityUtil.isGreaterThan_2_3_3())
			requestWindowFeature(Window.FEATURE_PROGRESS);
		super.onCreate(arg0); 
		View view = LayoutInflater.from(this).inflate(R.layout.webview_layout,null,false);
		this.setContentView(view);
		wv = (WebView) findViewById(R.id.webview);
		wv.getSettings().setUserAgentString(getString(R.string.clientua)+((MyApp) getApplication()).version);
		if(ActivityUtil.isGreaterThan_2_3_3())
		 wv.setWebChromeClient(new WebChromeClient() {  
             public void onProgressChanged(WebView view, int progress) {  

            	 ImageViewerActivity.this.setProgress(progress * 100);  
             }  
		 });
		
		 
	}

	@Override
	protected void onResume() {
		load();
		if(PhoneConfiguration.getInstance().fullscreen){
		ActivityUtil.getInstance().setFullScreen(wv);
		}
		super.onResume();
	}
	
	private String getPath(){
		return getIntent().getStringExtra("path");
	}
	
	@TargetApi(8)
	private void load(){
		final String uri = getPath();
		final WebSettings  settings = wv.getSettings(); 

		
		if(uri.endsWith(".swf")
				&& ActivityUtil.isGreaterThan_2_1() )//android 2.2
		{
			wv.setWebChromeClient(new WebChromeClient());
			//settings.setPluginState(PluginState.ON);
			wv.loadUrl(uri);

			getSupportActionBar().setTitle("查看视频");
		}else{//images

			settings.setSupportZoom(true);
			settings.setBuiltInZoomControls(true);	
			settings.setUseWideViewPort(true); 
			if(ActivityUtil.isGreaterThan_2_1())
				settings.setLoadWithOverviewMode(true);
			//settings.setUserAgentString(IPHONE_UA);
			wv.setWebViewClient(new WebViewClient(){

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					// TODO Auto-generated method stub
					return false;
				}
				
			});
			wv.loadUrl(uri);
			getSupportActionBar().setTitle("查看图片");
		}
		
	}
	
	

	@Override
	protected void onPause() {
		wv.stopLoading();
		wv.loadUrl("about:blank");
		super.onPause();
	}
	
	

	@Override
	protected void onStop() {
		wv.stopLoading();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.imageview_option_menu, menu);
		final int flags = ThemeManager.ACTION_BAR_FLAG;
		ReflectionUtil.actionBar_setDisplayOption(this, flags);
		return super.onCreateOptionsMenu(menu);
	}
	@TargetApi(11)
	private void runOnExecutor(DownloadImageTask task,String path){
		task.executeOnExecutor(DownloadImageTask.THREAD_POOL_EXECUTOR, path);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_refresh :
			load();
			break;
		case R.id.save_image:
			final String path = getPath();
			DownloadImageTask task = new DownloadImageTask(this);
			if(ActivityUtil.isGreaterThan_2_3_3()){
				runOnExecutor(task,path);
			}else{
				task.execute(path);
			}
			break;
		case R.id.item_share:
			Intent intent= new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_STREAM,Uri.parse(getPath()) );
			intent.setType("image/jpeg"); 
			String text = getResources().getString(R.string.share);
			startActivity(Intent.createChooser(intent, text));
			break;
		default:
			/*Intent MyIntent = new Intent(Intent.ACTION_MAIN);
			MyIntent.setClass(this, ArticleListActivity.class);
			MyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(MyIntent);*/
			this.finish();
				
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		//super.onSaveInstanceState(outState);
	}
	
	

	
}
