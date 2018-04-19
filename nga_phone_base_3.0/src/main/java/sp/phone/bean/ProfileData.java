package sp.phone.bean;

import java.util.List;

public class ProfileData {
    public String iplog;
    List<ReputationData> ReputationEntryList;
    List<adminForumsData> adminForumsEntryList;
    private String uid;
    private String username;
    private String fame;
    private boolean hasemail = false, hastel = false;
    private String email, tel;
    private String group;
    private String posts;
    private String money;
    private String title;
    private String verified;
    private String muteTime;
    private String regdate, lastpost;
    private String sign;
    private String avatar;
    private int ReputationEntryListrows;
    private int adminForumsEntryListrows;

    public String get_uid() {
        return uid;
    }

    public void set_uid(String uid) {
        this.uid = uid;
    }

    public String get_username() {
        return username;
    }

    public void set_username(String username) {
        this.username = username;
    }

    public String get_iplog() {
        return iplog;
    }

    public void set_iplog(String iplog) {
        this.iplog = iplog;
    }

    public String get_fame() {
        return fame;
    }

    public void set_fame(String fame) {
        this.fame = fame;
    }

    public void set_hasemail(boolean hasemail, String email) {
        this.hasemail = hasemail;
        this.email = email;
    }

    public void set_hastel(boolean hastel, String tel) {
        this.hastel = hastel;
        this.tel = tel;
    }

    public boolean get_hasemail() {
        return hasemail;
    }

    public String get_email() {
        return email;
    }

    public boolean get_hastel() {
        return hastel;
    }

    public String get_tel() {
        return tel;
    }

    public String get_group() {
        return group;
    }

    public void set_group(String group) {
        this.group = group;
    }

    public String get_posts() {
        return posts;
    }

    public void set_posts(String posts) {
        this.posts = posts;
    }

    public String get_money() {
        return money;
    }

    public void set_money(String money) {
        this.money = money;
    }

    public String get_title() {
        return title;
    }

    public void set_title(String title) {
        this.title = title;
    }

    public String get_verified() {
        return verified;
    }

    public void set_verified(String verified) {
        this.verified = verified;
    }

    public String get_muteTime() {
        return muteTime;
    }

    public void set_muteTime(String muteTime) {
        this.muteTime = muteTime;
    }

    public String get_regdate() {
        return regdate;
    }

    public void set_regdate(String regdate) {
        this.regdate = regdate;
    }

    public String get_lastpost() {
        return lastpost;
    }

    public void set_lastpost(String lastpost) {
        this.lastpost = lastpost;
    }

    public String get_sign() {
        return sign;
    }

    public void set_sign(String sign) {
        this.sign = sign;
    }

    public String get_avatar() {
        return avatar;
    }

    public void set_avatar(String avatar) {
        this.avatar = avatar;
    }

    public List<ReputationData> get_ReputationEntryList() {
        return ReputationEntryList;
    }

    public void set_ReputationEntryList(List<ReputationData> ReputationEntryList) {
        this.ReputationEntryList = ReputationEntryList;
    }

    public int get_ReputationEntryListrows() {
        return ReputationEntryListrows;
    }

    public void set_ReputationEntryListrows(int ReputationEntryListrows) {
        this.ReputationEntryListrows = ReputationEntryListrows;
    }

    public List<adminForumsData> get_adminForumsEntryList() {
        return adminForumsEntryList;
    }

    public void set_adminForumsEntryList(List<adminForumsData> adminForumsEntryList) {
        this.adminForumsEntryList = adminForumsEntryList;
    }

    public int get_adminForumsEntryListrows() {
        return adminForumsEntryListrows;
    }

    public void set_adminForumsEntryListrows(int adminForumsEntryListrows) {
        this.adminForumsEntryListrows = adminForumsEntryListrows;
    }

}
