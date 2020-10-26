package gov.anzong.androidnga.util;

import android.app.ActivityManager;
import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

/**
 * @author Justwen
 */
@GlideModule
public class CustomGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(Context context, @NonNull GlideBuilder builder) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            int memoryCacheSize = 1024 * 1024 * am.getMemoryClass() / 3;
            builder.setMemoryCache(new LruResourceCache(memoryCacheSize));
        }
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.format(DecodeFormat.PREFER_ARGB_8888);
        builder.setDefaultRequestOptions(requestOptions);
        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context));
    }

}
