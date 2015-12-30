package gov.anzong.androidnga.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.Utils;
import gov.anzong.androidnga.activity.SwipeBackAppCompatActivity;
import gov.anzong.androidnga.util.UiUtil;

/**
 * Created by Elrond on 2015/11/18.
 */
public class ImageZoomActivity extends SwipeBackAppCompatActivity {
    public static final String KEY_GALLERY_URLS = "keyGalleryUrl";
    public static final String KEY_GALLERY_RECT = "keyGalleryRect";
    public static final String KEY_GALLERY_CUR_URL = "keyGalleryCurUrl";
    private final String PATH_IMAGES = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/Pictures/";
    private CommonGestureControlGalleryView mGalleryView;
    private String[] galleryUrls;
    private Rect mRect;
    private Bitmap mCurBitmap;
    private int mPageIndex;
    private int mInitPageIndex;
    private ViewGroup mBottomLayout;
    private TextView mTxtView;
    private ProgressBar mProgressBar;

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

    private void receiveIntent() {
        Intent intent = getIntent();
        galleryUrls = intent.getStringArrayExtra(KEY_GALLERY_URLS);
        mRect = intent.getParcelableExtra(KEY_GALLERY_RECT);
        if (mRect == null) {
            DisplayMetrics dm = new DisplayMetrics();
            Display d = getWindowManager().getDefaultDisplay();
            d.getMetrics(dm);
            mRect = new Rect((int) (0.25 * dm.widthPixels), (int) (0.25 * dm.heightPixels), (int) (0.75 * dm.widthPixels)
                    , (int) (0.75 * dm.heightPixels));
        }
        String curUrl = intent.getStringExtra(KEY_GALLERY_CUR_URL);
        mInitPageIndex = mPageIndex = Arrays.asList(galleryUrls).indexOf(curUrl);
    }

    private void initBottomView() {
        mTxtView = (TextView) findViewById(R.id.reader_image_desc);
        mTxtView.setMovementMethod(new ScrollingMovementMethod());
        mTxtView.setText(String.valueOf(mPageIndex + 1) + " / " + String.valueOf(galleryUrls.length));
        mBottomLayout = (ViewGroup) findViewById(R.id.reader_image_bottom_layout);
        ImageView download = (ImageView) findViewById(R.id.reader_image_download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = PATH_IMAGES + File.separator + System.currentTimeMillis() + ".png";
                saveBitmap(path);
                String toast = getString(R.string.file_saved) + path;
                Utils.updateSystemGallery(ImageZoomActivity.this, new File(path));
                UiUtil.showToast(ImageZoomActivity.this, toast);
            }
        });
    }

    private void initGallery() {
        GalleryData galleryData = new GalleryData();
        galleryData.setImageRect(mRect);
        galleryData.setFiles(galleryUrls);

        Paint rectPaint = new Paint();
        rectPaint.setColor(getResources().getColor(R.color.white));
        mGalleryView = (CommonGestureControlGalleryView) findViewById(R.id.gallery);
        mGalleryView.setGalleryData(galleryData, mInitPageIndex);
        DisplayMetrics dm = new DisplayMetrics();
        Display d = getWindowManager().getDefaultDisplay();
        d.getMetrics(dm);
        mGalleryView.setGallerySize(dm.widthPixels, dm.heightPixels);
        CommonGalleryViewAdapter adapter = new CommonGalleryViewAdapter(this, Arrays.asList(galleryUrls));
        mGalleryView.setAdapter(adapter);
        mGalleryView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mGalleryView.startExit();
            }
        });
        mGalleryView.setOnScaleModeChangeListener(mOnScaleModeChangeListener);
        mGalleryView.setGalleryPageChangeListener(mOnGalleryPageChangeListener);
        adapter.setImageLoadingListener(mImageLoadingListener);
        mGalleryView.scrollToPage(mPageIndex);

        mProgressBar = (ProgressBar) findViewById(R.id.progress);
    }

    private void saveBitmap(String filePath) {
        if (mCurBitmap == null) {
            return;
        }
        File f = new File(filePath);
        FileOutputStream fOut = null;
        try {
            if (f.exists()) {
                f.delete();
            }
            File dir = f.getParentFile();
            if (!dir.exists())
                dir.mkdirs();
            f.createNewFile();
            fOut = new FileOutputStream(f);
            mCurBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGalleryView.isFirstResume()) {
            mGalleryView.startFirstResume();
        }
    }

    private String getPath() {
        String ret = getIntent().getStringExtra("path");
        ret = ret.replaceAll("img.nga.178.com", "img.ngacn.cc");
        return ret;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_share:
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

    private CommonGestureControlGalleryView.OnScaleModeChangeListener mOnScaleModeChangeListener = new CommonGestureControlGalleryView.OnScaleModeChangeListener() {
        @Override
        public void onScaleModeChange(int mode) {
            if (mode == CommonGestureControlGalleryView.MODE_FIT_WIDTH) {
                mBottomLayout.setVisibility(ViewGroup.VISIBLE);
            } else {
                mBottomLayout.setVisibility(ViewGroup.INVISIBLE);
            }
        }
    };

    private CommonGestureControlGalleryView.OnGalleryPageChangeListener mOnGalleryPageChangeListener = new CommonGestureControlGalleryView.OnGalleryPageChangeListener() {
        public void onPageChange(GalleryData galleryData, int pageIndex) {
            mPageIndex = pageIndex;
            mTxtView.setText(String.valueOf(pageIndex + 1) + " / " + String.valueOf(galleryUrls.length));
        }
    };

    private SimpleImageLoadingListener mImageLoadingListener = new SimpleImageLoadingListener() {
        @Override
        public void onLoadingStarted(String s, View view) {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoadingFailed(String s, View view, FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
            mProgressBar.setVisibility(View.GONE);
            mCurBitmap = bitmap;
        }

        @Override
        public void onLoadingCancelled(String s, View view) {

        }
    };
}
