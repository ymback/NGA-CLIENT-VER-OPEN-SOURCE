package sp.phone.bean;

import java.util.List;
import java.util.Map;

/**
 * 每一行的内容
 */
public class ThreadRowInfo {

    private int tid;
    private int fid;
    // private int quote_from;
    //private String quote_to;
    // private String icon;
    // private String titlefont;
    private String author;//user name
    private int authorid;
    private String subject;
    private String vote;
    // private int type;
    // private int type_2;
    private String postdate;
    // private int lastpost;
    // private String lastposter;
    //private int replies;
    // private int locked;
    // private int digest;
    // private int ifupload;
    // private int lastmodify;
    // private int recommend;
    private int pid;
    private boolean isanonymous = false;
    private String alterinfo;// something like "edited by ..."
    private String content;
    private int lou;
    //private int postdatetimestamp;
    //private int content_length;
    private Map<String, Attachment> attachs;
    //private int credit;
    //private String reputation;
    //private int groupid;
    //private String lpic;
    private String level;
    //private int gp_lesser;
    private String yz; //negative integer if user is nuked
    //private String js_escap_site;
    //private String js_escap_honor;
    private String js_escap_avatar;//avatar url
    //private int regdate;
    //private String mute_time;
    private String mute_time;
    //private int postnum; //may be empty string
    private int aurvrc;//prestige
    //private int money;
    //private int thisvisit;
    private String signature;
    //private String nickname;
    private List<ThreadRowInfo> comments;

    private boolean isInBlackList;
    private String formated_html_data;
    private String from_client;
    private String from_client_model;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore_2() {
        return score_2;
    }

    public void setScore_2(int score_2) {
        this.score_2 = score_2;
    }

    private int score;
    private int score_2;

    public void set_IsInBlackList(boolean isin) {
        this.isInBlackList = isin;
    }

    public boolean get_isInBlackList() {
        return isInBlackList;
    }

    public Map<String, Attachment> getAttachs() {
        return attachs;
    }

    public void setAttachs(Map<String, Attachment> attachs) {
        this.attachs = attachs;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getJs_escap_avatar() {
        return js_escap_avatar;
    }

    public void setJs_escap_avatar(String js_escap_avatar) {
        this.js_escap_avatar = js_escap_avatar;
    }

    public String getAlterinfo() {
        return alterinfo;
    }

    public void setAlterinfo(String alterinfo) {
        this.alterinfo = alterinfo;
    }

    public boolean getISANONYMOUS() {
        return isanonymous;
    }

    public void setISANONYMOUS(boolean isanonymous) {
        this.isanonymous = isanonymous;
    }

    public int getLou() {
        return lou;
    }

    public void setLou(int lou) {
        this.lou = lou;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getAuthorid() {
        return authorid;
    }

    public void setAuthorid(int authorid) {
        this.authorid = authorid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getFid() {
        return fid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public String getPostdate() {
        return postdate;
    }

    public void setPostdate(String postdate) {
        this.postdate = postdate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ThreadRowInfo> getComments() {
        return comments;
    }

    public void setComments(List<ThreadRowInfo> comments) {
        this.comments = comments;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getAurvrc() {
        return aurvrc;
    }

    public void setAurvrc(int aurvrc) {
        this.aurvrc = aurvrc;
    }

    public String getFromClient() {
        return from_client;
    }

    public void setFromClient(String from_client) {
        this.from_client = from_client;
    }

    public String getFromClientModel() {
        return from_client_model;
    }

    public void setFromClientModel(String from_client_model) {
        this.from_client_model = from_client_model;
    }


    public String getYz() {
        return yz;
    }

    public void setYz(String yz) {
        this.yz = yz;
    }

    public String getMute_time() {
        return mute_time;
    }

    public void setMute_time(String mute_time) {
        this.mute_time = mute_time;
    }

    public String getFormated_html_data() {
        return formated_html_data;
    }

    public void setFormated_html_data(String formated_html_data) {
        this.formated_html_data = formated_html_data;
    }


}
