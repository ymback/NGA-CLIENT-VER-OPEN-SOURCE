package noname.interfaces;

import noname.gson.parse.NonameThreadResponse;


public interface OnNonameTopListLoadFinishedListener {
    //void finishLoad(RSSFeed feed);
    void jsonfinishLoad(NonameThreadResponse result);

}
