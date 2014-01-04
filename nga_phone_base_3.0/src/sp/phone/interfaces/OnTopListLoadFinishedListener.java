package sp.phone.interfaces;

import sp.phone.bean.TopicListInfo;

public interface OnTopListLoadFinishedListener {
	//void finishLoad(RSSFeed feed);
	void jsonfinishLoad(TopicListInfo result);

}
