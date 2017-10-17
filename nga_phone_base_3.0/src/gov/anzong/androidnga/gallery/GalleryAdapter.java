package gov.anzong.androidnga.gallery;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * 浏览
 * Created by elrond on 2017/10/13.
 */

public class GalleryAdapter extends PagerAdapter {

    private Context mContext;
    private String[] mGalleryUrls;

    public GalleryAdapter(Context context, String[] galleryUrls, int index) {
        mContext = context;
        mGalleryUrls = galleryUrls;
    }

    @Override
    public int getCount() {
        return mGalleryUrls.length;
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(container.getContext());
        String url = mGalleryUrls[position];
        if (url.endsWith(".gif"))
            Glide.with(mContext).load(url).asGif().listener(gifListener).into(photoView);
        else
            Glide.with(mContext).load(url).listener(listener).into(photoView);

        // Now just add PhotoView to ViewPager and return it
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private RequestListener<String, GlideDrawable> listener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String s, Target<GlideDrawable> target, boolean b) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b, boolean b1) {
            callbackActivity();
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
            callbackActivity();
            return false;
        }
    };

    private void callbackActivity() {
        ((ImageZoomActivity) mContext).hideLoading();
    }
}
