package sp.phone.presenter.contract;

import sp.phone.forumoperation.MessagePostAction;

/**
 * Created by Yang Yihang on 2017/5/28.
 */

public interface MessagePostContract {

    interface Presenter extends BaseContract.Presenter{

        void commit(String title,String to,String body);

        void setMessagePostAction(MessagePostAction messagePostAction);

        void setEmoticon(String emoticon);

    }

    interface View extends BaseContract.View<Presenter>{

        void finish(int resultCode);

        void insertBodyText(CharSequence text);
    }
}
