package sp.phone.mvp.model;

import sp.phone.forumoperation.MessagePostAction;
import sp.phone.mvp.contract.MessagePostContract;
import sp.phone.task.MessagePostTask;

/**
 * Created by Yang Yihang on 2017/6/25.
 */

public class MessagePostModel implements MessagePostContract.Model {


    private MessagePostContract.Presenter mPresenter;


    public MessagePostModel(MessagePostContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void postMessage(MessagePostAction action,MessagePostTask.CallBack callBack) {
        new MessagePostTask(mPresenter.getContext(),callBack).execute(action.toString());
    }
}
