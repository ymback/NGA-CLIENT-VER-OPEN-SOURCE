package sp.phone.mvp.contract;

import android.net.Uri;

import sp.phone.forumoperation.PostParam;
import sp.phone.listener.OnHttpCallBack;

/**
 * Created by Justwen on 2017/6/6.
 */

public interface TopicPostContract {

    interface Presenter  {

        void setEmoticon(String emoticon);

        void setTopicPostAction(PostParam postAction);

        PostParam getTopicPostAction();

        void post(String title, String body, boolean isAnony);

        void prepareUploadFile();

        void startUploadTask(Uri uri);

        void getPostInfo();

    }

    interface View  {

        void insertBodyText(CharSequence text);

        void finish();

        void insertFile(String path, CharSequence file);

        void showFilePicker();

        void setResult(int result);

    }

    interface Model  {

        void post();

        void uploadFile(Uri uri);

        // 获取发布信息
        void getPostInfo(PostParam postParam, OnHttpCallBack<PostParam> callBack);
    }
}
