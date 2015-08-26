package sp.phone.forumoperation;

import sp.phone.utils.StringUtil;

public class NonameThreadPostAction {
    private String tid_;
    private String post_subject_;
    private String post_content_;

    public NonameThreadPostAction(String tid, String subject, String content) {
        tid_ = tid;
        post_subject_ = subject;
        post_content_ = content;

    }

    public String getPost_subject_() {
        return post_subject_;
    }

    public void setPost_subject_(String postSubject) {
        post_subject_ = postSubject;
    }

    public String getPost_content_() {
        return post_content_;
    }

    public void setPost_content_(String postContent) {
        post_content_ = postContent;
    }

    public void setTid_(String tid) {
        tid_ = tid;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("tid=");
        sb.append(tid_);
        sb.append("&title=");
        sb.append(StringUtil.encodeUrl(post_subject_, "UTF-8"));
        sb.append("&content=");
        sb.append(StringUtil.encodeUrl(post_content_, "UTF-8"));
        return sb.toString();
    }


}

