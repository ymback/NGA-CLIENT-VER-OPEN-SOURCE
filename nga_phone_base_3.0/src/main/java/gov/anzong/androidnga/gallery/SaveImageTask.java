package gov.anzong.androidnga.gallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.util.Pair;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import org.apache.commons.io.FileUtils;
import org.reactivestreams.Subscription;

import java.io.File;

import gov.anzong.androidnga.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.listener.OnSimpleHttpCallBack;
import sp.phone.rxjava.BaseSubscriber;
import sp.phone.util.ActivityUtils;

public class SaveImageTask {

    private Context mContext;

    private int mDownloadCount;

    private static final String PATH_IMAGES = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/nga_open_source/";

    private Subscription mSubscription;

    public SaveImageTask() {
        mContext = ApplicationContextHolder.getContext();
    }

    public static class DownloadResult {

        File file;

        String url;

        public DownloadResult(File file, String url) {
            this.file = file;
            this.url = url;
        }
    }

    public void execute(OnSimpleHttpCallBack<DownloadResult> callBack, String... urls) {

        if (isRunning()) {
            ActivityUtils.showToast("图片正在下载，防止风怒！！");
            return;
        }

        mDownloadCount = 0;
        Observable.fromArray(urls)
                .observeOn(Schedulers.io())
                .map(url -> {
                    File file = Glide
                            .with(mContext)
                            .load(url)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                    return new DownloadResult(file, url);
                })
                .map(result -> {
                    String url = result.url;
                    String suffix = url.substring(url.lastIndexOf('.'));
                    String path = PATH_IMAGES + System.currentTimeMillis() + suffix;
                    File target = new File(path);
                    FileUtils.copyFile(result.file, target);
                    return new DownloadResult(target, url);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<DownloadResult>() {
                    @Override
                    public void onNext(DownloadResult result) {
                        Uri uri = Uri.fromFile(result.file);
                        ApplicationContextHolder.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                        mDownloadCount++;
                        if (mDownloadCount == urls.length) {
                            if (urls.length > 1) {
                                ActivityUtils.showToast("所有图片已保存");
                            } else {
                                ActivityUtils.showToast(mContext.getString(R.string.file_saved) + result.file.getAbsolutePath());
                            }
                        }
                        callBack.onResult(result);
                    }

                    @Override
                    public void onComplete() {
                        mSubscription = null;
                    }

                    @Override
                    public void onSubscribe(Subscription subscription) {
                        super.onSubscribe(subscription);
                        mSubscription = subscription;
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mSubscription = null;
                        ActivityUtils.showToast("下载失败");
                    }

                });
    }

    private boolean isRunning() {
        return mSubscription != null;
    }


}
