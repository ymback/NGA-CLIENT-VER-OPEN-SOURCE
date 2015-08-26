package sp.phone.forumoperation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import sp.phone.utils.StringUtil;

public class ThreadPostAction {
    private int step_;
    private String pid_;
    private String action_;
    private int fid_;
    private String tid_;
    private String _ff_;
    private String attachments_;
    private String attachments_check_;
    private String force_topic_key_;
    private int filter_key_;
    private String post_subject_;
    private String post_content_;
    private String checkkey_;
    private String mention_;
    private String __ngaClientChecksum;
    private boolean __isanony;//&anony=1

    public ThreadPostAction(String tid, String subject, String content) {
        step_ = 2;
        pid_ = "";
        action_ = "new";
        fid_ = 0;
        tid_ = tid;
        _ff_ = "";
        attachments_ = "";
        attachments_check_ = "";
        force_topic_key_ = "";
        filter_key_ = 1;
        post_subject_ = subject;
        post_content_ = content;
        checkkey_ = "";
        mention_ = "";
        __isanony = false;


    }

    public int getFid_() {
        return fid_;
    }

    public void setFid_(int fid) {
        fid_ = fid;
    }

    public void set__isanony(boolean isanony) {
        __isanony = isanony;
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

    public void setPid_(String pid) {
        pid_ = pid;
    }

    public void setTid_(String tid) {
        tid_ = tid;
    }

    public String getAction_() {
        return action_;
    }

    public void setAction_(String action) {
        action_ = action;
    }

    public void set__ngaClientChecksum(String getngaClientChecksum) {
        // TODO Auto-generated method stub
        __ngaClientChecksum = getngaClientChecksum;
    }

    public void setMention_(String mention_) {
        this.mention_ = mention_;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("step=");
        sb.append(step_);
        sb.append("&pid=");
        sb.append(pid_);
        sb.append("&action=");
        sb.append(action_);

        if (!action_.equals("modify")) {
            sb.append("&fid=");
            sb.append(fid_);
        } else {
            sb.append("&fid=");
        }
        sb.append("&tid=");
        sb.append(tid_);
        if (__isanony) {
            sb.append("&anony=1");
        }
        sb.append("&_ff=");
        sb.append(_ff_);
        sb.append("&attachments=");
        sb.append(attachments_);
        sb.append("&attachments_check=");
        sb.append(attachments_check_);
        sb.append("&force_topic_key=");
        sb.append(force_topic_key_);
        sb.append("&filter_key=");
        sb.append(filter_key_);
        sb.append("&post_subject=");
        sb.append(StringUtil.encodeUrl(post_subject_, "GBK"));
        sb.append("&post_content=");
        sb.append(StringUtil.encodeUrl(post_content_, "GBK"));
        if (mention_.length() != 0) {
            sb.append("&mention=");
            sb.append(StringUtil.encodeUrl(mention_, "GBK"));

        } else {
            sb.append("&mention=");
            sb.append(StringUtil.encodeUrl("", "GBK"));
        }
        sb.append("&checkkey=");
        sb.append(checkkey_);
        sb.append("&__ngaClientChecksum=");
        sb.append(__ngaClientChecksum);
        return sb.toString();
    }

    public void appendAttachments_(String attachments_) {
        if (StringUtil.isEmpty(this.attachments_))
            this.attachments_ = attachments_;
        else {
            try {
                this.attachments_ = this.attachments_ + URLEncoder.encode("\t", "GBK") + attachments_;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public void appendAttachments_check_(String attachments_check_) {
        if (StringUtil.isEmpty(this.attachments_check_))
            this.attachments_check_ = attachments_check_;
        else {
            try {
                this.attachments_check_ = this.attachments_check_ + URLEncoder.encode("\t", "GBK") + attachments_check_;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


}

