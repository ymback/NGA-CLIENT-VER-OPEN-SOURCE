package com.noname.interfaces;

import com.noname.gson.parse.NonameThreadResponse;


public interface OnNonameTopListLoadFinishedListener {
    //void finishLoad(RSSFeed feed);
    void jsonfinishLoad(NonameThreadResponse result);

}
