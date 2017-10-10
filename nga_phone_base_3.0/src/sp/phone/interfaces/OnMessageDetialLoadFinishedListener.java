package sp.phone.interfaces;

import sp.phone.bean.MessageDetailInfo;

public interface OnMessageDetialLoadFinishedListener {
    //void finishLoad(RSSFeed feed);
    void finishLoad(MessageDetailInfo result);

}
