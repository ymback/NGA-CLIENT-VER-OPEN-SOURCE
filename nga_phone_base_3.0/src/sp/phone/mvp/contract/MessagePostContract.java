package sp.phone.mvp.contract;

import sp.phone.forumoperation.MessagePostAction;
import sp.phone.presenter.contract.*;
import sp.phone.presenter.contract.BaseContract;
import sp.phone.task.MessagePostTask;

/**
 * Created by Yang Yihang on 2017/5/28.
 */

public interface MessagePostContract {

    interface Presenter extends sp.phone.presenter.contract.BaseContract.Presenter {

        void commit(String title,String to,String body);

        void setMessagePostAction(MessagePostAction messagePostAction);

        void setEmoticon(String emoticon);

    }

    interface View extends sp.phone.presenter.contract.BaseContract.View<Presenter> {

        void finish(int resultCode);

        void insertBodyText(CharSequence text);
    }

    interface Model extends BaseContract.Model {

        void postMessage(MessagePostAction action,MessagePostTask.CallBack callBack);
    }
}
