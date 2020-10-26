package sp.phone.mvp.model;

import gov.anzong.androidnga.base.util.ContextUtils;;
import sp.phone.param.MessagePostParam;
import sp.phone.mvp.contract.MessagePostContract;
import sp.phone.task.MessagePostTask;

/**
 * Created by Justwen on 2017/6/25.
 */

public class MessagePostModel extends BaseModel implements MessagePostContract.Model {

    @Override
    public void postMessage(MessagePostParam action, MessagePostTask.CallBack callBack) {
        new MessagePostTask(ContextUtils.getContext(), callBack).execute(action.toString());
    }
}
