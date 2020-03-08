package gov.anzong.androidnga.core.decode;

import android.support.annotation.Nullable;

import java.util.List;

import gov.anzong.androidnga.core.data.HtmlData;

/**
 * Created by Justwen on 2018/8/25.
 */
public interface IForumDecoder {

    String ignoreCaseTag = "(?i)";

    String IGNORE_CASE_TAG = "(?i)";

    default String decode(String content) {
        return "";
    }

    default String decode(String content, @Nullable HtmlData htmlData) {
        return decode(content);
    }

    default List<String> getImageUrls() {
        return null;
    }

}
