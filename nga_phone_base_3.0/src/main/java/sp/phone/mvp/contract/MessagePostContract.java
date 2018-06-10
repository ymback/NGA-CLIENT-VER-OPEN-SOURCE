package sp.phone.mvp.contract;

import sp.phone.mvp.contract.tmp.BaseContract;
import sp.phone.task.MessagePostTask;
import sp.phone.forumoperation.MessagePostAction;
import sp.phone.mvp.contract.tmp.BaseContract;
import sp.phone.task.MessagePostTask;

/**
 * Created by Justwen on 2017/5/28.
 */

public interface MessagePostContract {

    interface Presenter extends sp.phone.mvp.contract.tmp.BaseContract.Presenter {

        void commit(String title,String to,String body);

        void setMessagePostAction(MessagePostAction messagePostAction);

        void setEmoticon(String emoticon);

    }

    interface View {

        void finish(int resultCode);

        void insertBodyText(CharSequence text);
    }

    interface Model extends BaseContract.Model {

        void postMessage(MessagePostAction action,MessagePostTask.CallBack callBack);
    }
}
