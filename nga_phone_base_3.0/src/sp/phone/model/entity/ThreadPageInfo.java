package sp.phone.model.entity;

public class ThreadPageInfo {

    private int mTid;

    private String mAuthor;

    private int mFid;

    private int mAuthorId;

    private String mLastPoster;

    private int mReplies;

    private String mSubject;

    private String mTitleFont;

    private int mType;

    private String mTopicMisc;

    private int mPage;

    private int mPid;

    private int mPosition;

    public int getTid() {
        return mTid;
    }

    public void setTid(int tid) {
        mTid = tid;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public int getFid() {
        return mFid;
    }

    public void setFid(int fid) {
        mFid = fid;
    }

    public int getAuthorId() {
        return mAuthorId;
    }

    public void setAuthorId(int authorId) {
        mAuthorId = authorId;
    }

    public String getLastPoster() {
        return mLastPoster;
    }

    public void setLastPoster(String lastPoster) {
        mLastPoster = lastPoster;
    }

    public int getReplies() {
        return mReplies;
    }

    public void setReplies(int replies) {
        mReplies = replies;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        mSubject = subject;
    }

    public String getTitleFont() {
        return mTitleFont;
    }

    public void setTitleFont(String titleFont) {
        mTitleFont = titleFont;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getTopicMisc() {
        return mTopicMisc;
    }

    public void setTopicMisc(String topicMisc) {
        mTopicMisc = topicMisc;
    }

    public int getPage() {
        return mPage;
    }

    public void setPage(int page) {
        mPage = page;
    }

    public int getPid() {
        return mPid;
    }

    public void setPid(int pid) {
        mPid = pid;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ThreadPageInfo && mTid == ((ThreadPageInfo) obj).getTid();
    }
}
