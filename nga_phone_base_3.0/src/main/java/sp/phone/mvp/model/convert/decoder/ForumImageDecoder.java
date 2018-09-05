package sp.phone.mvp.model.convert.decoder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.anzong.androidnga.util.NetUtil;
import sp.phone.adapter.ExtensionEmotionAdapter;
import sp.phone.common.ApplicationContextHolder;
import sp.phone.common.PhoneConfiguration;
import sp.phone.util.DeviceUtils;
import sp.phone.util.HttpUtil;

/**
 * Created by Justwen on 2018/8/25.
 */
public class ForumImageDecoder implements IForumDecoder {

    private List<String> mImageUrls = new ArrayList<>();

    @Override
    public String decode(String content) {
        Pattern p = Pattern.compile("<img src='(http\\S+)' style= 'max-width:100%' >");
        Matcher m = p.matcher(content);
        mImageUrls.clear();
       boolean showImage = PhoneConfiguration.getInstance().isDownImgNoWifi()
                || DeviceUtils.isWifiConnected(ApplicationContextHolder.getContext());
        while (m.find()) {
            String s0 = m.group();
            String s1 = m.group(1);
            String path = ExtensionEmotionAdapter.getPathByURI(s1);
            if (path != null) {
                String newImgBlock = "<img src='"
                        + "file:///android_asset/" + path
                        + "' style= 'max-width:100%' >";
                content = content.replace(s0, newImgBlock);
            } else if (!showImage) {
                path = "ic_offline_image.png";
                String newImgBlock = "<img src='"
                        + "file:///android_asset/" + path
                        + "' style= 'max-width:100%' >";
                content = content.replace(s0, newImgBlock);
            } else {
                String newImgBlock = "<img src='"
                        + s1
                        + "' style= 'max-width:100%' >";
                content = content.replace(s0, newImgBlock);
                if (s1.contains(HttpUtil.NGA_ATTACHMENT_HOST)) {
                    mImageUrls.add(s1);
                }
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
