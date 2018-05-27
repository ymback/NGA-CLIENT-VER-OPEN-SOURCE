package sp.phone.mvp.presenter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;

import java.io.IOException;
import java.io.InputStream;

import gov.anzong.androidnga.R;
import sp.phone.adapter.ExtensionEmotionAdapter;
import sp.phone.forumoperation.PostParam;
import sp.phone.fragment.TopicPostFragment;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.contract.TopicPostContract;
import sp.phone.mvp.model.TopicPostModel;
import sp.phone.task.FileUploadTask;
import sp.phone.task.TopicPostTask;
import sp.phone.util.ActivityUtils;
import sp.phone.util.DeviceUtils;
import sp.phone.util.FunctionUtils;
import sp.phone.util.PermissionUtils;
import sp.phone.util.StringUtils;

/**
 * Created by Justwen on 2017/6/6.
 */

public class TopicPostPresenter extends BasePresenter<TopicPostFragment, TopicPostModel> implements TopicPostContract.Presenter, TopicPostTask.CallBack, FileUploadTask.onFileUploaded {


    private PostParam mPostParam;

    private final static Object COMMIT_LOCK = new Object();

    private boolean mLoading;

    public TopicPostPresenter() {
    }

    @Override
    public void setEmoticon(String emotion) {
        String urlTemp = emotion.replaceAll("\\n", "");
        if (urlTemp.indexOf("http") > 0) {
            urlTemp = urlTemp.substring(5, urlTemp.length() - 6);
            String sourceFile = ExtensionEmotionAdapter.getPathByURI(urlTemp);
            try (InputStream is = mBaseView.getContext().getResources().getAssets().open(sourceFile)) {
                if (is != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    Drawable drawable = new BitmapDrawable(mBaseView.getContext().getResources(), bitmap);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight());
                    SpannableString spanString = new SpannableString(emotion);
                    ImageSpan span = new ImageSpan(drawable,
                            ImageSpan.ALIGN_BASELINE);
                    spanString.setSpan(span, 0, emotion.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mBaseView.insertBodyText(spanString);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            int[] emotions = {1, 2, 3, 24, 25, 26, 27, 28, 29, 30, 32, 33, 34,
                    35, 36, 37, 38, 39, 4, 40, 41, 42, 43, 5, 6, 7, 8};
            for (int i = 0; i < 27; i++) {
                if (emotion.indexOf("[s:" + String.valueOf(emotions[i]) + "]") == 0) {
                    String sourceFile = "a" + String.valueOf(emotions[i])
                            + ".gif";
                    try (InputStream is = mBaseView.getContext().getResources().getAssets().open(sourceFile)) {
                        if (is != null) {
                            Bitmap bitmap = BitmapFactory.decodeStream(is);
                            Drawable drawable = new BitmapDrawable(bitmap);
                            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                                    drawable.getIntrinsicHeight());
                            SpannableString spanString = new SpannableString(
                                    emotion);
                            ImageSpan span = new ImageSpan(drawable,
                                    ImageSpan.ALIGN_BASELINE);
                            spanString.setSpan(span, 0, emotion.length(),
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            mBaseView.insertBodyText(spanString);
                        } else {
                            mBaseView.insertBodyText(emotion);
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }//

    @Override
    public void setTopicPostAction(PostParam postAction) {
        mPostParam = postAction;
    }

    @Override
    public PostParam getTopicPostAction() {
        return mPostParam;
    }

    @Override
    public void post(String title, String body, boolean isAnony) {
        synchronized (COMMIT_LOCK) {
            if (mLoading) {
                mBaseView.showToast(R.string.avoidWindfury);
                return;
            }
            mLoading = true;
        }

        mPostParam.set__isanony(isAnony);
        mPostParam.setPost_subject_(title);
        if (body.length() > 0) {
            mPostParam.setPost_content_(FunctionUtils.ColorTxtCheck(body));
            mBaseModel.post();
        }
    }

    @Override
    public void prepareUploadFile() {
        if (DeviceUtils.isGreaterEqual_6_0()) {
            if (PermissionUtils.hasStoragePermission(mBaseView.getContext())) {
                mBaseView.showFilePicker();
            } else {
                PermissionUtils.requestStoragePermission(mBaseView);
            }
        } else {
            mBaseView.showFilePicker();
        }
    }

    @Override
    public void startUploadTask(Uri uri) {
        mBaseModel.uploadFile(uri);
    }

    @Override
    public void getPostInfo() {

        mBaseModel.getPostInfo(mPostParam, new OnHttpCallBack<PostParam>() {
            @Override
            public void onError(String text) {

            }

            @Override
            public void onSuccess(PostParam data) {
                mPostParam = data;
            }

        });
    }

    @Override
    public void onArticlePostFinished(boolean isSuccess, String result) {
        ActivityUtils.getInstance().dismiss();
        if (mBaseView != null) {
            if (!StringUtils.isEmpty(result)) {
                mBaseView.showToast(result);
            }
            if (isSuccess) {
                mBaseView.setResult(Activity.RESULT_OK);
                mBaseView.finish();
            }
        }
        synchronized (COMMIT_LOCK) {
            mLoading = false;
        }

    }

    @Override
    public int finishUpload(String attachments, String attachmentsCheck, String picUrl, Uri uri) {
        String selectedImagePath2 = FunctionUtils.getPath(mBaseView.getContext(), uri);
        mPostParam.appendAttachments_(attachments);
        mPostParam.appendAttachments_check_(attachmentsCheck);
        String spantmp = "[img]./" + picUrl + "[/img]";
        if (!StringUtils.isEmpty(selectedImagePath2)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inJustDecodeBounds = false;
            DisplayMetrics dm = new DisplayMetrics();
            ((Activity) mBaseView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
            int screenwidth = (int) (dm.widthPixels * 0.75);
            int screenheigth = (int) (dm.heightPixels * 0.75);
            int width = options.outWidth;
            int height = options.outHeight;
            float scaleWidth = ((float) screenwidth) / width;
            float scaleHeight = ((float) screenheigth) / height;
            if (scaleWidth < scaleHeight && scaleWidth < 1f) {// 不能放大啊,然后主要是哪个小缩放到哪个就行了
                options.inSampleSize = (int) (1 / scaleWidth);
            } else if (scaleWidth >= scaleHeight && scaleHeight < 1f) {
                options.inSampleSize = (int) (1 / scaleHeight);
            } else {
                options.inSampleSize = 1;
            }
            Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath2, options);
            BitmapDrawable bd = new BitmapDrawable(bitmap);
            bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
            SpannableString spanStringS = new SpannableString(spantmp);
            ImageSpan span = new ImageSpan(bd, ImageSpan.ALIGN_BASELINE);
            spanStringS.setSpan(span, 0, spantmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mBaseView.insertFile(selectedImagePath2, spanStringS);

        } else {
            mBaseView.insertFile(selectedImagePath2, picUrl);
        }
        return 1;
    }

    @Override
    protected TopicPostModel onCreateModel() {
        return new TopicPostModel(this);
    }
}
