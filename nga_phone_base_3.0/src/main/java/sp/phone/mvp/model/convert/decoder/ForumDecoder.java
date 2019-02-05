package sp.phone.mvp.model.convert.decoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justwen on 2018/8/25.
 */
public class ForumDecoder {

    private static List<IForumDecoder> sDecoderPool = new ArrayList<>();

    static {
        sDecoderPool.add(new ForumBasicDecoder());
        sDecoderPool.add(new ForumAlbumDecoder());
        sDecoderPool.add(new ForumCollapseDecoder());
        sDecoderPool.add(new ForumAudioDecoder());
        sDecoderPool.add(new ForumEmoticonDecoder());
        sDecoderPool.add(new ForumImageDecoder());
        sDecoderPool.add(new ForumVideoDecoder());
    }

    private List<IForumDecoder> mForumDecoders = new ArrayList<>();

    public ForumDecoder() {
        this(false);
    }

    public ForumDecoder(boolean allDecoders) {
        if (allDecoders) {
            mForumDecoders.addAll(sDecoderPool);
        } else {
            mForumDecoders.add(new ForumBasicDecoder());
        }
    }

    public String decode(String content, List<String> urls) {
        return decode(content, urls, null);
    }

    public String decode(String content, List<String> urls, ForumDecodeRecord decodeResult) {
        for (IForumDecoder decoder : mForumDecoders) {
            content = decoder.decode(content, decodeResult);
            if (urls != null && decoder.getImageUrls() != null && !decoder.getImageUrls().isEmpty()) {
                urls.addAll(decoder.getImageUrls());
            }
        }
        return content;
    }

    public String decode(String content) {
        return decode(content, null);
    }
}
