package sp.phone.mvp.model;

import sp.phone.common.ApplicationContextHolder;
import sp.phone.param.MessagePostParam;
import sp.phone.mvp.contract.MessagePostContract;
import sp.phone.task.MessagePostTask;

/**
 * Created by Justwen on 2017/6/25.
 */

public class MessagePostModel extends BaseModel implements MessagePostContract.Model {

    @Override
    public void postMessage(MessagePostParam action, MessagePostTask.CallBack callBack) {
        new MessagePostTask(ApplicationContextHolder.getContext(), callBack).execute(action.toString());
    }
}
