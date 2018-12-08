package sp.phone.mvp.model.convert.decoder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sp.phone.adapter.ExtensionEmotionAdapter;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.common.PhoneConfiguration;
import sp.phone.util.DeviceUtils;

/**
 * Created by Justwen on 2018/8/25.
 */
public class ForumImageDecoder implements IForumDecoder {

    private List<String> mImageUrls = new ArrayList<>();

    private static final String HTML_EMOTICON = "<img src='file:///android_asset/%s' width='%s' >";

    private static final String HTML_IMG_DEFAULT = "<img src='file:///android_asset/ic_offline_image.png' style= 'max-width:100%' >";

    private static final String HTML_IMG = "<img src='%s' style= 'max-width:100%%' >";

    private static final String HTML_IMG_LINK = "<a href='%s'>";

    private static final String REGEX_IMG = "<img src='(http\\S+)' style= 'max-width:100%' >";

    @Override
    public String decode(String content) {
        Pattern p = Pattern.compile(REGEX_IMG);
        Matcher m = p.matcher(content);
        mImageUrls.clear();
        boolean showImage = PhoneConfiguration.getInstance().isDownImgNoWifi()
                || DeviceUtils.isWifiConnected(ApplicationContextHolder.getContext());
        while (m.find()) {
            String s0 = m.group();
            String s1 = m.group(1);
            String path = ExtensionEmotionAdapter.getPathByURI(s1);
            if (path != null) {
                int emoticonWidth = PhoneConfiguration.getInstance().getEmotionWidth();
                String newImgBlock = String.format(HTML_EMOTICON, path, emoticonWidth);
                content = content.replace(s0, newImgBlock)
                        // 移除可点击状态
                        .replace(String.format(HTML_IMG_LINK, s1), "");
            } else if (!showImage) {
                content = content.replace(s0, HTML_IMG_DEFAULT);
                mImageUrls.add(s1);
            } else {
                String newImgBlock = String.format(HTML_IMG, s1);
                content = content.replace(s0, newImgBlock);
                mImageUrls.add(s1);
            }
        }
        return convertGifImage(content);
    }

    private String convertGifImage(String content) {
        Pattern pattern = Pattern.compile("(http\\S+).gif.(.*?).jpg");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String s = matcher.group(0);
            content = content.replaceAll(s, s.substring(0, s.indexOf(".gif") + 4));
        }
        return content;
    }

    @Override
    public List<String> getImageUrls() {
        return mImageUrls;
    }
}
