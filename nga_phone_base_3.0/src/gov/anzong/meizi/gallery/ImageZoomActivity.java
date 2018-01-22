package gov.anzong.meizi.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Arrays;

import gov.anzong.androidnga.R;

/**
 * 显示图片
 * Created by Elrond on 2015/11/18.
 */
public class ImageZoomActivity extends AppCompatActivity {
    public static final String KEY_GALLERY_URLS = "keyGalleryUrl";
    public static final String KEY_GALLERY_RECT = "keyGalleryRect";
    public static final String KEY_GALLERY_CUR_URL = "keyGalleryCurUrl";
    private final String PATH_IMAGES = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/";
    private String[] galleryUrls;
    private int mPageIndex;
    private int mInitPageIndex;
    private TextView mTxtView;
    private ProgressBar mProgressBar;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);
        receiveIntent();
        initBottomView();
        initGallery();
    }

    private void initGallery() {
        mViewPager = (ViewPager) findViewById(R.id.gallery);
        GalleryAdapter adapter = new GalleryAdapter(this, galleryUrls, mInitPageIndex);
        mViewPager.setAdapter(adapter);
    }

    private void receiveIntent() {
        Intent intent = getIntent();
        galleryUrls = intent.getStringArrayExtra(KEY_GALLERY_URLS);
        String curUrl = intent.getStringExtra(KEY_GALLERY_CUR_URL);
        mInitPageIndex = mPageIndex = Arrays.asList(galleryUrls).indexOf(curUrl);
    }

    private void initBottomView() {
        mTxtView = (TextView) findViewById(R.id.reader_image_desc);
        mTxtView.setMovementMethod(new ScrollingMovementMethod());
        mTxtView.setText(String.valueOf(mPageIndex + 1) + " / " + String.valueOf(galleryUrls.length));
        ImageView download = (ImageView) findViewById(R.id.reader_image_download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBitmap();
            }
        });
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void saveBitmap() {
        String path = PATH_IMAGES + System.currentTimeMillis() + ".png";
        SaveImageTask task = new SaveImageTask(this, path);
        task.execute(galleryUrls[mPageIndex]);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private String getPath() {
        String ret = getIntent().getStringExtra("path");
        ret = ret.replaceAll("img.nga.178.com", "img.ngacn.cc");
        return ret;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(getPath()));
                intent.setType("image/jpeg");
                String text = getResources().getString(R.string.share);
                startActivity(Intent.createChooser(intent, text));
                break;
            default:
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }
}
