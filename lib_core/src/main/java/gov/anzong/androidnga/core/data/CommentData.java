package gov.anzong.androidnga.core.data;

public class CommentData {

    private String mAuthor;

    private String mContent;

    private String mAvatarUrl;

    private String mPostTime;

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        mAvatarUrl = avatarUrl;
    }

    public String getPostTime() {
        return mPostTime;
    }

    public void setPostTime(String postTime) {
        mPostTime = postTime;
    }
}
