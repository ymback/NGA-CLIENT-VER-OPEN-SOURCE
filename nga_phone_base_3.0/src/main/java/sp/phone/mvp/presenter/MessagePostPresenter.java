package sp.phone.mvp.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import java.io.IOException;
import java.io.InputStream;

import gov.anzong.androidnga.R;
import sp.phone.task.MessagePostTask;
import sp.phone.adapter.ExtensionEmotionAdapter;
import sp.phone.forumoperation.MessagePostAction;
import sp.phone.mvp.model.MessagePostModel;
import sp.phone.mvp.contract.MessagePostContract;
import sp.phone.task.MessagePostTask;
import sp.phone.util.ActivityUtils;
import sp.phone.util.FunctionUtils;

/**
 * Created by Justwen on 2017/5/28.
 */

public class MessagePostPresenter implements MessagePostContract.Presenter,MessagePostTask.CallBack {

    private MessagePostContract.View mView;

    private MessagePostContract.Model mModel;

    private final static Object COMMIT_LOCK = new Object();

    private boolean mLoading;

    private MessagePostAction mMessagePostAction;

    public MessagePostPresenter(MessagePostContract.View view) {
        mView = view;
        mView.setPresenter(this);
        mModel = new MessagePostModel(this);
    }

    @Override
    public void commit(String title, String to, String body) {
        synchronized (COMMIT_LOCK) {
            if (mLoading) {
                mView.showToast(R.string.avoidWindfury);
                return;
            }
            mLoading = true;
        }
        mMessagePostAction.setTo_(to);
        mMessagePostAction.setPost_subject_(title);
        if (body.length() > 0) {
            mMessagePostAction.setPost_content_(FunctionUtils.ColorTxtCheck(body));
            mModel.postMessage(mMessagePostAction,this);
        }
    }

    @Override
    public void setMessagePostAction(MessagePostAction messagePostAction) {
        mMessagePostAction = messagePostAction;
    }

    @Override
    public void onMessagePostFinished(boolean result, String resultInfo) {
        if (resultInfo != null){
            mView.showToast(resultInfo);
        }
        ActivityUtils.getInstance().dismiss();
        if (result) {
            if (!mMessagePostAction.getAction_().equals("new")) {
                mView.finish(123);
            } else {
                mView.finish(321);
            }
        }
        synchronized (COMMIT_LOCK) {
            mLoading = false;
        }
    }


    @Override
    public void setEmoticon(String emotion) {
        String urlTemp = emotion.replaceAll("\\n", "");
        if (urlTemp.indexOf("http") > 0) {
            urlTemp = urlTemp.substring(5, urlTemp.length() - 6);
            String sourceFile = ExtensionEmotionAdapter.getPathByURI(urlTemp);
            try(InputStream is = mView.getContext().getResources().getAssets().open(sourceFile)) {
                if (is != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    Drawable drawable = new BitmapDrawable(mView.getContext().getResources(),bitmap);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight());
                    SpannableString spanString = new SpannableString(emotion);
                    ImageSpan span = new ImageSpan(drawable,
                            ImageSpan.ALIGN_BASELINE);
                    spanString.setSpan(span, 0, emotion.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mView.insertBodyText(spanString);
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
                    try (InputStream is = mView.getContext().getResources().getAssets().open(sourceFile)){
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
                            mView.insertBodyText(spanString);
                        } else {
                            mView.insertBodyText(emotion);
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
    public Context getContext() {
        return null;
    }

    @Override
    public void setView(Object view) {

    }
}
