package sp.phone.bean;

public class MessageArticlePageInfo {

    private String subject;
    private String time;
    private String content;
    private String from;
    private int lou;
    private String js_escap_avatar;//avatar url
    private String author;//user name
    private String yz; //negative integer if user is nuked
    private String mute_time;
    private String signature;
    private String formated_html_data;

    public int getLou() {
        return lou;
    }

    public void setLou(int lou) {
        this.lou = lou;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getJs_escap_avatar() {
        return js_escap_avatar;
    }

    public void setJs_escap_avatar(String js_escap_avatar) {
        this.js_escap_avatar = js_escap_avatar;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getFormated_html_data() {
        return formated_html_data;
    }

    public void setFormated_html_data(String formated_html_data) {
        this.formated_html_data = formated_html_data;
    }

}
