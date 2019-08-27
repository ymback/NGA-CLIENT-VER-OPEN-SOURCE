package sp.phone.mvp.model.convert.decoder;

import java.util.List;

/**
 * Created by Justwen on 2018/8/25.
 */
public interface IForumDecoder {

    String ignoreCaseTag = "(?i)";

    String IGNORE_CASE_TAG = "(?i)";

    String decode(String content);

    default String decode(String content, ForumDecodeRecord result) {
        return decode(content);
    }

    default List<String> getImageUrls() {
        return null;
    }

}
