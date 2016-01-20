package gov.anzong.androidnga.gallery;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;

import java.util.List;

import gov.anzong.androidnga.R;

public class CommonGalleryViewAdapter extends BaseAdapter {
    private List<String> list;
    private Context context;
    private RequestListener<String, GifDrawable> gifListener;
    private RequestListener<String, GlideDrawable> listener;

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
        imageView.setTag(R.id.tag_1, position);
        String url = list.get(position);
        loadImage(imageView, url, position);
        return imageView;
    }

    private void loadImage(ImageView imageView, String url, int index) {
        if (url.endsWith(".gif"))
            Glide.with(context).load(url).asGif().listener(gifListener).into(imageView);
        else
            Glide.with(context).load(url).listener(listener).into(imageView);
    }

    public void clear() {
        if (list != null && list.size() > 0) {
            list = null;
        }
    }

    public void setListener(RequestListener<String, GlideDrawable> listener, RequestListener<String, GifDrawable> gifListener) {
        this.listener = listener;
        this.gifListener = gifListener;
    }
}
