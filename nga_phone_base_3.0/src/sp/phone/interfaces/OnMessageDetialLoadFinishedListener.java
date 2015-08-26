package sp.phone.interfaces;

import sp.phone.bean.MessageDetialInfo;

public interface OnMessageDetialLoadFinishedListener {
    //void finishLoad(RSSFeed feed);
    void finishLoad(MessageDetialInfo result);

}
