package gov.anzong.androidnga.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

public class CommonGalleryViewAdapter extends BaseAdapter {
    private List<String> list;
    private Context context;
    private SimpleImageLoadingListener imageLoadingListener;

    public CommonGalleryViewAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = null;
        if (convertView == null) {
            imageView = new ImageView(context);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setTag(position);
        String url = list.get(position);
        loadImage(imageView, url, position);
        return imageView;
    }

    private void loadImage(ImageView imageView, String url, int index) {
        DisplayImageOptions imageOptions = (new com.nostra13.universalimageloader.core.DisplayImageOptions.Builder())
                .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY).build();
        ImageLoader.getInstance().displayImage(url, imageView, imageOptions, imageLoadingListener);
    }

    public void clear() {
        if (list != null && list.size() > 0) {
            list = null;
        }
    }

    public void setImageLoadingListener(SimpleImageLoadingListener imageLoadingListener) {
        this.imageLoadingListener = imageLoadingListener;
    }
}
