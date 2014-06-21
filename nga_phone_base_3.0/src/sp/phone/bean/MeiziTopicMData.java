
package sp.phone.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MeiziTopicMData {

    public String doubanPosterUrl;

    public String title;

    public String doubanTopicUrl;

    public List<TopicContentItem> content;

    public Date date;

    public MeiziTopicMData() {
        date = new Date();
        content = new ArrayList<MeiziTopicMData.TopicContentItem>();
    }

    public static class TopicContentItem {
        public ContentItemType type;

        public String imgUrl;

        public String msg;
    }

    public enum ContentItemType {
        MSG,
        IMAGE
    }
}
