package gov.anzong.androidnga.gallery;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.Arrays;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.BaseActivity;
import sp.phone.listener.OnSimpleHttpCallBack;
import sp.phone.util.DeviceUtils;

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

    private SaveImageTask.DownloadResult[] mDownloadResults;

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
                setTitle((position + 1) + " / " + mGalleryUrls.length);
                //  mTxtView.setText(String.valueOf(position + 1) + " / " + String.valueOf(mGalleryUrls.length));
            }
        });
    }

    private void receiveIntent() {
        Intent intent = getIntent();
        mGalleryUrls = intent.getStringArrayExtra(KEY_GALLERY_URLS);
        String curUrl = intent.getStringExtra(KEY_GALLERY_CUR_URL);
        if (mGalleryUrls == null) {
            mGalleryUrls = new String[1];
            mGalleryUrls[0] = curUrl;
        }
        mPageIndex = Arrays.asList(mGalleryUrls).indexOf(curUrl);
        mDownloadResults = new SaveImageTask.DownloadResult[mGalleryUrls.length];
    }

    private void initBottomView() {
//        mTxtView = (TextView) findViewById(R.id.reader_image_desc);
//        mTxtView.setMovementMethod(new ScrollingMovementMethod());11
//        mTxtView.setText(String.valueOf(mPageIndex + 1) + " / " + String.valueOf(mGalleryUrls.length));
//        ImageView download = (ImageView) findViewById(R.id.reader_image_download);
//        download.setOnClickListener(v -> saveBitmap(mGalleryUrls[mPageIndex >= 0 ? mPageIndex : 0]));
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mProgressBar.setVisibility(View.VISIBLE);
        setTitle((mPageIndex + 1) + " / " + mGalleryUrls.length);
    }

    private void saveBitmap(OnSimpleHttpCallBack<SaveImageTask.DownloadResult> callBack, String... urls) {
        new RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        if (mSaveImageTask == null) {
                            mSaveImageTask = new SaveImageTask();
                        }
                        mSaveImageTask.execute(callBack, urls);
                    } else {
                        // Oups permission denied
                    }
                });
    }

    private void saveBitmap(String... urls) {
        saveBitmap(data -> {
            for (int i = 0; i < mGalleryUrls.length; i++) {
                if (mGalleryUrls[i].equals(data.url)) {
                    mDownloadResults[i] = data;
                    break;
                }
            }
        }, urls);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_zoom, menu);
        return true;
    }

    private void share(File file) {
        if (DeviceUtils.isGreaterEqual_7_0()) {
            Uri contentUri = FileProvider.getUriForFile(this,
                    "gov.anzong.androidnga", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, contentUri);
            intent.setType("image/jpeg");
            String text = getResources().getString(R.string.share);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, text));
        } else {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/jpeg");
            String text = getResources().getString(R.string.share);
            startActivity(Intent.createChooser(intent, text));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                if (mDownloadResults[mPageIndex] != null) {
                    share(mDownloadResults[mPageIndex].file);
                } else {
                    saveBitmap(data -> {
                        for (int i = 0; i < mGalleryUrls.length; i++) {
                            if (mGalleryUrls[i].equals(data.url)) {
                                mDownloadResults[i] = data;
                                break;
                            }
                        }
                        share(data.file);
                    }, mGalleryUrls[mPageIndex]);
                }
                break;
            case R.id.menu_download_all:
                showDownloadAllDialog();
                break;
            case R.id.menu_download:
                saveBitmap(mGalleryUrls[mPageIndex]);
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
