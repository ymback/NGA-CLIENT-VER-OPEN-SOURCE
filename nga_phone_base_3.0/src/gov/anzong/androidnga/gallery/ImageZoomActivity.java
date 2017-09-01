package gov.anzong.androidnga.gallery;

import android.content.Intent;
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

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.Arrays;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.SwipeBackAppCompatActivity;

/**
 * Created by Elrond on 2015/11/18.
 */
public class ImageZoomActivity extends SwipeBackAppCompatActivity {
    public static final String KEY_GALLERY_URLS = "keyGalleryUrl";
    public static final String KEY_GALLERY_RECT = "keyGalleryRect";
    public static final String KEY_GALLERY_CUR_URL = "keyGalleryCurUrl";
    private final String PATH_IMAGES = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/";
    private CommonGestureControlGalleryView mGalleryView;
    private String[] galleryUrls;
    private Rect mRect;
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
            mRect = new Rect(0, 0, dm.widthPixels, dm.heightPixels);
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
                saveBitmap();
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
        adapter.setListener(listener, gifListener);
        mGalleryView.scrollToPage(mPageIndex);

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
            if (mode == CommonGestureControlGalleryView.MODE_FIT_WIDTH || mode == CommonGestureControlGalleryView.MODE_ORIGINAL) {
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
            mProgressBar.setVisibility(View.VISIBLE);
        }
    };

    private RequestListener<String, GlideDrawable> listener = new RequestListener<String, GlideDrawable>() {

        @Override
        public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
            mProgressBar.setVisibility(View.GONE);
            return false;
        }
    };

    private RequestListener<String, GifDrawable> gifListener = new RequestListener<String, GifDrawable>() {
        @Override
        public boolean onException(Exception e, String s, Target<GifDrawable> target, boolean b) {
            return false;
        }

        @Override
        public boolean onResourceReady(GifDrawable gifDrawable, String s, Target<GifDrawable> target, boolean b, boolean b1) {
            mProgressBar.setVisibility(View.GONE);
            return false;
        }
    };
}
