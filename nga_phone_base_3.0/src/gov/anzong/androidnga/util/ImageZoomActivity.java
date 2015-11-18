package gov.anzong.androidnga.util;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.SwipeBackAppCompatActivity;
import sp.phone.task.DownloadImageTask;
import sp.phone.utils.ActivityUtil;
import sp.phone.utils.PhoneConfiguration;
import sp.phone.utils.ReflectionUtil;
import sp.phone.utils.ThemeManager;

/**
 * Created by Elrond on 2015/11/18.
 */
public class ImageZoomActivity extends SwipeBackAppCompatActivity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle arg0) {
        if (ActivityUtil.isGreaterThan_2_3_3())
            requestWindowFeature(Window.FEATURE_PROGRESS);
        super.onCreate(arg0);
        setContentView(R.layout.activity_image_zoom);
        mImageView = (ImageView) findViewById(R.id.zoom_img);
    }

    @Override
    protected void onResume() {
        load();
        if (PhoneConfiguration.getInstance().fullscreen) {
            ActivityUtil.getInstance().setFullScreen(mImageView);
        }
        super.onResume();
    }

    private String getPath() {
        String ret = getIntent().getStringExtra("path");
        ret = ret.replaceAll("img.nga.178.com", "img.ngacn.cc");
        return ret;
    }

    @TargetApi(8)
    private void load() {
        final String uri = getPath();
        if (uri.endsWith(".swf") && ActivityUtil.isGreaterThan_2_1())//android 2.2
        {
            /*wv.setWebChromeClient(new WebChromeClient());
            wv.loadUrl(uri);
            getSupportActionBar().setTitle("查看视频");*/
        } else {//images
            DisplayImageOptions options = new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.EXACTLY).build();
            ImageLoader.getInstance().displayImage(uri, mImageView, options);
            getSupportActionBar().setTitle("查看图片");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
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
    private void runOnExecutor(DownloadImageTask task, String path) {
        task.executeOnExecutor(DownloadImageTask.THREAD_POOL_EXECUTOR, path);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_refresh:
                load();
                break;
            case R.id.save_image:
                final String path = getPath();
                DownloadImageTask task = new DownloadImageTask(this);
                if (ActivityUtil.isGreaterThan_2_3_3()) {
                    runOnExecutor(task, path);
                } else {
                    task.execute(path);
                }
                break;
            case R.id.item_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(getPath()));
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
}
