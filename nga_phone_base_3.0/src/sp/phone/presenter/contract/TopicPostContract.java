package sp.phone.presenter.contract;

import android.net.Uri;

import sp.phone.forumoperation.TopicPostAction;

/**
 * Created by Yang Yihang on 2017/6/6.
 */

public interface TopicPostContract {

    interface Presenter extends BaseContract.Presenter{

        void prepare();

        void setEmoticon(String emoticon);

        void setTopicPostAction(TopicPostAction postAction);

        TopicPostAction getTopicPostAction();

        void post(String title,String body,boolean isAnony);

        void prepareUploadFile();

        void startUploadTask(Uri uri);

    }

    interface View extends BaseContract.View<Presenter>{

        void insertBodyText(CharSequence text);

        void finish();

        void insertFile(String path,CharSequence file);

        void showFilePicker();

    }

    interface Model extends BaseContract.Model {

        void preparePost();

        void post();

        void uploadFile(Uri uri);
    }
}
