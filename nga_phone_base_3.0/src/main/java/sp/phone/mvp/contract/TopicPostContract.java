package sp.phone.mvp.contract;

import android.net.Uri;

import sp.phone.forumoperation.PostParam;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.task.TopicPostTask;

/**
 * Created by Justwen on 2017/6/6.
 */

public interface TopicPostContract {

    interface Presenter {

        void setEmoticon(String emoticon);

        void setTopicPostAction(PostParam postAction);

        void post(String title, String body, boolean isAnony);

        void showFilePicker();

        void startUploadTask(Uri uri);

        void getPostInfo();

    }

    interface View {

        void insertBodyText(CharSequence text);

        void finish();

        void insertFile(String path, CharSequence file);

        void showFilePicker();

        void showUploadFileProgressBar();

        void hideUploadFileProgressBar();

        void setResult(int result);

    }

    interface Model {

        void post(PostParam postParam, TopicPostTask.CallBack callBack);

        void uploadFile(Uri uri, PostParam postParam, OnHttpCallBack<String> callBack);

        // 获取发布信息
        void getPostInfo(PostParam postParam, OnHttpCallBack<PostParam> callBack);
    }
}
