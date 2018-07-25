package gov.anzong.androidnga.gallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Arrays;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.BaseActivity;

/**
 * 显示图片
 * Created by Elrond on 2015/11/18.
 */
public class ImageZoomActivity extends BaseActivity {

    public static final String KEY_GALLERY_URLS = "keyGalleryUrl";

    public static final String KEY_GALLERY_RECT = "keyGalleryRect";

    public static final String KEY_GALLERY_CUR_URL = "keyGalleryCurUrl";

    private String[] mGalleryUrls;

    private int mPageIndex;

    private TextView mTxtView;

    private ProgressBar mProgressBar;

    private ViewPager mViewPager;

    private SaveImageTask mSaveImageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);
        receiveIntent();
        initBottomView();
        initGallery();
        initActionBar();

    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void initGallery() {
        mViewPager = (ViewPager) findViewById(R.id.gallery);
        GalleryAdapter adapter = new GalleryAdapter(this, mGalleryUrls);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mPageIndex);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mPageIndex = position;
                mTxtView.setText(String.valueOf(position + 1) + " / " + String.valueOf(mGalleryUrls.length));
            }
        });
    }

    private void receiveIntent() {
        Intent intent = getIntent();
        mGalleryUrls = intent.getStringArrayExtra(KEY_GALLERY_URLS);
        String curUrl = intent.getStringExtra(KEY_GALLERY_CUR_URL);
        mPageIndex = Arrays.asList(mGalleryUrls).indexOf(curUrl);
    }

    private void initBottomView() {
        mTxtView = (TextView) findViewById(R.id.reader_image_desc);
        mTxtView.setMovementMethod(new ScrollingMovementMethod());
        mTxtView.setText(String.valueOf(mPageIndex + 1) + " / " + String.valueOf(mGalleryUrls.length));
        ImageView download = (ImageView) findViewById(R.id.reader_image_download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBitmap(mGalleryUrls[mPageIndex]);
            }
        });
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void saveBitmap(String... urls) {
        if (mSaveImageTask == null) {
            mSaveImageTask = new SaveImageTask();
        }
        mSaveImageTask.execute(urls);
    }

    private String getPath() {
        String ret = mGalleryUrls[mPageIndex];
        //  ret = ret.replaceAll("img.nga.178.com", "img.ngacn.cc");
        return ret;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_zoom, menu);
        return true;
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
            case R.id.menu_download_all:
                showDownloadAllDialog();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showDownloadAllDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否要下载全部图片 ？")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveBitmap(mGalleryUrls);

                    }
                }).setNegativeButton(android.R.string.cancel, null).create().show();
    }

    public void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }
}
