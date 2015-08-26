package sp.phone.bean;

public class ThreadPageInfo {

    private int tid;
    private int fid;
    private String tidarray;
    private int quote_from;
    private String topic_misc;
    //private String quote_to;
    //private String icon;
    private String titlefont;
    private String author;
    //private int authorid;
    private String subject;
    //private int ifmark;
    private int type;
    //private int type_2;
    //private String postdate;
    //private int lastpost;
    private String lastposter;
    private int replies;
    //private int locked;
    //private int digest;
    //private int ifupload;
    //private int lastmodify;
    private int recommend;
    //private int view_count;
    //private String fid_no_match;
    //private String subject_org;
    private String lastposter_org;
    //private String tpcurl;
    //private String ispage;
    private int pid;
    // private String adminbox;
    private String content;
    private String top_level;
    private String static_topic;

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTidarray() {
        return tidarray;
    }

    public void setTidarray(String tidarray) {
        this.tidarray = tidarray;
    }

    public int getReplies() {
        return replies;
    }

    public void setReplies(int replies) {
        this.replies = replies;
    }

    public int getQuote_from() {
        return quote_from;
    }

    public void setQuote_from(int quote_from) {
        this.quote_from = quote_from;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLastposter() {
        return lastposter;
    }

    public void setLastposter(String lastposter) {
        this.lastposter = lastposter;
    }

    public String getLastposter_org() {
        return lastposter_org;
    }

    public void setLastposter_org(String lastposter_org) {
        this.lastposter_org = lastposter_org;
    }

    public String getTitlefont() {
        return titlefont;
    }

    public void setTitlefont(String titlefont) {
        this.titlefont = titlefont;
    }

    public String getTopicMisc() {
        return topic_misc;
    }

    public void setTopicMisc(String topic_misc) {
        this.topic_misc = topic_misc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getStatic_topic() {
        return static_topic;
    }

    public void setStatic_topic(String static_topic) {
        this.static_topic = static_topic;
    }

    public String getTop_level() {
        return top_level;
    }

    public void setTop_level(String top_level) {
        this.top_level = top_level;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }
}
