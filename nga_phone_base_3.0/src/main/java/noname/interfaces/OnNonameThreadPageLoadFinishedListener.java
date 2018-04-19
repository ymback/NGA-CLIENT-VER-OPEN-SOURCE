package noname.interfaces;

import noname.gson.parse.NonameReadResponse;

public interface OnNonameThreadPageLoadFinishedListener {
    void finishLoad(NonameReadResponse data);

}
