package sp.phone.mvp.model.convert.decoder;

public class ForumDecodeRecord {

    private boolean mHasCollapseTag;

    private boolean mHasEmoticonTag;

    private boolean mHasImageTag;

    private boolean mHasAudioTag;

    private boolean mHasVideoTag;

    public boolean hasCollapseTag() {
        return mHasCollapseTag;
    }

    public void setHasCollapseTag(boolean hasCollapseTag) {
        mHasCollapseTag = hasCollapseTag;
    }

    public boolean hasEmoticonTag() {
        return mHasEmoticonTag;
    }

    public void setHasEmoticonTag(boolean hasEmoticonTag) {
        mHasEmoticonTag = hasEmoticonTag;
    }

    public boolean hasImageTag() {
        return mHasImageTag;
    }

    public void setHasImageTag(boolean hasImageTag) {
        mHasImageTag = hasImageTag;
    }

    public boolean hasAudioTag() {
        return mHasAudioTag;
    }

    public void setHasAudioTag(boolean hasAudioTag) {
        mHasAudioTag = hasAudioTag;
    }

    public boolean hasVideoTag() {
        return mHasVideoTag;
    }

    public void setHasVideoTag(boolean hasVideoTag) {
        mHasVideoTag = hasVideoTag;
    }

    public boolean hasTag() {
        return mHasAudioTag || mHasCollapseTag || mHasEmoticonTag || mHasImageTag || mHasVideoTag;
    }
}
