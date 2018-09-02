package sp.phone.mvp.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import gov.anzong.androidnga.R;
import sp.phone.adapter.ExtensionEmotionAdapter;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.forumoperation.PostParam;
import sp.phone.fragment.TopicPostFragment;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.mvp.contract.TopicPostContract;
import sp.phone.mvp.model.TopicPostModel;
import sp.phone.task.TopicPostTask;
import sp.phone.util.ActivityUtils;
import sp.phone.util.DeviceUtils;
import sp.phone.util.FunctionUtils;
import sp.phone.util.PermissionUtils;
import sp.phone.util.StringUtils;

public class TopicPostPresenter extends BasePresenter<TopicPostFragment, TopicPostModel>
        implements TopicPostContract.Presenter, TopicPostTask.CallBack {

    private boolean mLoading;

    private PostParam mPostParam;

    @Override
    public void setEmoticon(String emotion) {
        String urlTemp = emotion.replaceAll("\\n", "");
        if (urlTemp.contains("http")) {
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
                e.printStackTrace();
            }

        } else {
            int[] emotions = {1, 2, 3, 24, 25, 26, 27, 28, 29, 30, 32, 33, 34,
                    35, 36, 37, 38, 39, 4, 40, 41, 42, 43, 5, 6, 7, 8};
            for (int i = 0; i < 27; i++) {
                if (emotion.indexOf("[s:" + emotions[i] + "]") == 0) {
                    String sourceFile = "a" + emotions[i] + ".gif";
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
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }//

    public void setPostParam(PostParam postParam) {
        mPostParam = postParam;
        mBaseModel.getPostInfo(mPostParam, new OnHttpCallBack<PostParam>() {
            public void onError(String text) {
                if (mBaseView != null) {
                    ActivityUtils.showToast(text);
                }
            }

            public void onSuccess(PostParam data) {
                mPostParam = data;
            }
        });
    }

    @Override
    public void onViewCreated() {
        if (!TextUtils.isEmpty(mPostParam.getPostSubject())) {
            mBaseView.insertTitleText(mPostParam.getPostSubject());
        }
        if (!TextUtils.isEmpty(mPostParam.getPostContent())) {
            mBaseView.insertBodyText(mPostParam.getPostContent());
        }
        super.onViewCreated();
    }

    public void post(String title, String body, boolean isAnony) {
        if (mLoading) {
            mBaseView.showToast(R.string.avoidWindfury);
            return;
        }
        mLoading = true;
        mPostParam.setAnonymous(isAnony);
        mPostParam.setPostSubject(title);
        if (!body.isEmpty()) {
            mPostParam.setPostContent(FunctionUtils.ColorTxtCheck(body));
            mBaseModel.post(mPostParam, this);
        }
    }

    public void showFilePicker() {
        if (!DeviceUtils.isGreaterEqual_6_0()) {
            mBaseView.showFilePicker();
        } else if (PermissionUtils.hasStoragePermission(mBaseView.getContext())) {
            mBaseView.showFilePicker();
        } else {
            PermissionUtils.requestStoragePermission(mBaseView);
        }
    }

    public void startUploadTask(final Uri uri) {
        mBaseView.showUploadFileProgressBar();
        mBaseModel.uploadFile(uri, mPostParam, new OnHttpCallBack<String>() {
            public void onError(String text) {
                if (mBaseView != null) {
                    mBaseView.hideUploadFileProgressBar();
                    ActivityUtils.showToast(text);
                }
            }

            public void onSuccess(String data) {
                if (mBaseView != null) {
                    mBaseView.hideUploadFileProgressBar();
                    ActivityUtils.showToast("上传成功");
                    finishUpload(data, uri);
                }
            }
        });
    }

    public void insertAtFormat() {
        mBaseView.insertBodyText("[@]", 2);
    }

    public void insertQuoteFormat() {
        mBaseView.insertBodyText("[quote][/quote]", "[quote]".length());
    }

    public void insertUrlFormat() {
        mBaseView.insertBodyText("[url][/url]", "[url]".length());
    }

    public void insertBoldFormat() {
        mBaseView.insertBodyText("[b][/b]", "[b]".length());
    }

    public void insertItalicFormat() {
        mBaseView.insertBodyText("[i][/i]", "[i]".length());
    }

    public void insertUnderLineFormat() {
        mBaseView.insertBodyText("[u][/u]", "[u]".length());
    }

    public void insertDeleteLineFormat() {
        mBaseView.insertBodyText("[del][/del]", "[del]".length());
    }

    public void insertFontColorFormat(String fontColor) {
        mBaseView.insertBodyText(fontColor, fontColor.length() - "[/color]".length());
    }

    public void insertFontSizeFormat(String fontSize) {
        mBaseView.insertBodyText(fontSize, "[size=100%]".length());
    }

    public void insertTopicCategory(String category) {
        mBaseView.insertTitleText(category);
    }

    public void loadTopicCategory(OnHttpCallBack<List<String>> callBack) {
        mBaseModel.loadTopicCategory(mPostParam, callBack);
    }

    public void onArticlePostFinished(boolean isSuccess, String result) {
        ActivityUtils.getInstance().dismiss();
        if (mBaseView != null) {
            if (!StringUtils.isEmpty(result)) {
                mBaseView.showToast(result);
            }
            if (isSuccess) {
                mBaseView.setResult(-1);
                mBaseView.finish();
            }
        }
        mLoading = false;
    }

    private void finishUpload(String picUrl, Uri uri) {
        String selectedImagePath2 = FunctionUtils.getPath(mBaseView.getContext(), uri);
        String spanStr = "[img]./" + picUrl + "[/img]";
        if (!StringUtils.isEmpty(selectedImagePath2)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(selectedImagePath2, options);
            DisplayMetrics dm = ApplicationContextHolder.getResources().getDisplayMetrics();

            int screenWidth = (int) (dm.widthPixels * 0.75);
            int screenHeight = (int) (dm.heightPixels * 0.75);
            int width = options.outWidth;
            int height = options.outHeight;
            float scaleWidth = ((float) screenWidth) / width;
            float scaleHeight = ((float) screenHeight) / height;
            if (scaleWidth < scaleHeight && scaleWidth < 1f) {// 不能放大啊,然后主要是哪个小缩放到哪个就行了
                options.inSampleSize = (int) (1 / scaleWidth);
            } else if (scaleWidth >= scaleHeight && scaleHeight < 1f) {
                options.inSampleSize = (int) (1 / scaleHeight);
            } else {
                options.inSampleSize = 1;
            }
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath2, options);
            BitmapDrawable bd = new BitmapDrawable(bitmap);
            bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());
            SpannableString spanStringS = new SpannableString(spanStr);
            ImageSpan span = new ImageSpan(bd, ImageSpan.ALIGN_BASELINE);
            spanStringS.setSpan(span, 0, spanStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mBaseView.insertFile(selectedImagePath2, spanStringS);
        } else {
            mBaseView.insertFile(selectedImagePath2, picUrl);
        }
    }

    protected TopicPostModel onCreateModel() {
        return new TopicPostModel();
    }
}