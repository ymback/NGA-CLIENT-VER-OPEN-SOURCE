package sp.phone.interfaces;

import sp.phone.bean.MessageDetialInfo;
import sp.phone.bean.MessageListInfo;

public interface OnMessageDetialLoadFinishedListener {
	//void finishLoad(RSSFeed feed);
	void finishLoad(MessageDetialInfo result);

}
