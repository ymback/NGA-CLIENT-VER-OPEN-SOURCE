package sp.phone.mvp.contract;

import android.net.Uri;

import java.util.List;

import sp.phone.forumoperation.PostParam;
import sp.phone.listener.OnHttpCallBack;
import sp.phone.task.TopicPostTask.CallBack;

public interface TopicPostContract {

    interface View {

        void hideUploadFileProgressBar();

        void insertBodyText(CharSequence text);

        void insertBodyText(CharSequence text, int position);

        void insertFile(String str, CharSequence text);

        void insertTitleText(CharSequence text);

        void showFilePicker();

        void showUploadFileProgressBar();
    }

    interface Model {

        void getPostInfo(PostParam postParam, OnHttpCallBack<PostParam> callBack);

        void loadTopicCategory(PostParam postParam, OnHttpCallBack<List<String>> callBack);

        void post(PostParam postParam, CallBack callBack);

        void uploadFile(Uri uri, PostParam postParam, OnHttpCallBack<String> callBack);
    }

    interface Presenter {

        void insertAtFormat();

        void insertBoldFormat();

        void insertDeleteLineFormat();

        void insertCollapseFormat();

        void insertFontColorFormat(String fontColor);

        void insertFontSizeFormat(String fontSize);

        void insertItalicFormat();

        void insertQuoteFormat();

        void insertTopicCategory(String str);

        void insertUnderLineFormat();

        void insertUrlFormat();

        void loadTopicCategory(OnHttpCallBack<List<String>> callBack);

        void post(String str, String str2, boolean z);

        void setEmoticon(String str);

        void setPostParam(PostParam postParam);

        void showFilePicker();

        void startUploadTask(Uri uri);
    }
}