package sp.phone.bean;

import java.util.List;
import java.util.Map;

/**
 * 每一行的内容
 */
public class ThreadRowInfo {

    private int tid;
    private int fid;
    private String author;//user name
    private int authorid;
    private String subject;
    private String vote;
    private String postdate;
    private int pid;
    private boolean isanonymous = false;
    private String alterinfo;// something like "edited by ..."
    private String content;
    private int lou;
    private Map<String, Attachment> attachs;
    private String level;
    private String yz; //negative integer if user is nuked
    private String js_escap_avatar;//avatar url
    private String muteTime;
    private int aurvrc;//prestige
    private String signature;
    private List<ThreadRowInfo> comments;
    public List<String> hotReplies; //热门回复

    private boolean isInBlackList;
    private String formattedHtmlData;
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

    public String getMuteTime() {
        return muteTime;
    }

    public void setMuteTime(String muteTime) {
        this.muteTime = muteTime;
    }

    public String getFormattedHtmlData() {
        return formattedHtmlData;
    }

    public void setFormattedHtmlData(String formattedHtmlData) {
        this.formattedHtmlData = formattedHtmlData;
    }


}
