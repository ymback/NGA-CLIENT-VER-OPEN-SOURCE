package gov.anzong.meizi;


import java.util.List;


public interface OnMeiziCategoryLoadFinishedListener {

    void datafinishLoad(List<MeiziUrlData> result);

    void datafinishLoad(List<MeiziUrlData> result, int sid);


}
