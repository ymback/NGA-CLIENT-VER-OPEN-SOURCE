package sp.phone.interfaces;

import sp.phone.bean.MessageListInfo;

public interface OnMessageListLoadFinishedListener {
    //void finishLoad(RSSFeed feed);
    void jsonfinishLoad(MessageListInfo result);

}
