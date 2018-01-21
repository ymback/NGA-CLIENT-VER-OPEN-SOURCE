package sp.phone.mvp.contract;

import android.net.Uri;

import sp.phone.forumoperation.TopicPostAction;
import sp.phone.mvp.contract.tmp.BaseContract;

/**
 * Created by Justwen on 2017/6/6.
 */

public interface TopicPostContract {

    interface Presenter extends BaseContract.Presenter {

        void prepare();

        void setEmoticon(String emoticon);

        void setTopicPostAction(TopicPostAction postAction);

        TopicPostAction getTopicPostAction();

        void post(String title,String body,boolean isAnony);

        void prepareUploadFile();

        void startUploadTask(Uri uri);

    }

    interface View extends BaseContract.View<Presenter> {

        void insertBodyText(CharSequence text);

        void finish();

        void insertFile(String path,CharSequence file);

        void showFilePicker();

        void setResult(int result);

    }

    interface Model extends BaseContract.Model {

        void preparePost();

        void post();

        void uploadFile(Uri uri);
    }
}
