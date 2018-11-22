package com.noname.interfaces;

import com.noname.gson.parse.NonameReadResponse;

public interface OnNonameThreadPageLoadFinishedListener {
    void finishLoad(NonameReadResponse data);

}
