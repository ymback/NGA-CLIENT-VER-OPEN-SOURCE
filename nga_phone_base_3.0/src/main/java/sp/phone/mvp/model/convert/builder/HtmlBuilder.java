package sp.phone.mvp.model.convert.builder;

import android.text.TextUtils;

import java.util.List;

import sp.phone.bean.ThreadRowInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.mvp.model.convert.decoder.ForumDecodeRecord;
import sp.phone.theme.ThemeManager;
import sp.phone.util.StringUtils;

/**
 * Created by Justwen on 2018/8/28.
 */
public class HtmlBuilder {

    private static String sHtmlTemplate;

    private static String sDarkHtmlTemplate;

    private static String getForegroundColorStr() {
        int webTextColor = ThemeManager.getInstance().getWebTextColor();
        return String.format("%06x", webTextColor & 0xffffff);
    }

    private static String getHtmlTemplate() {
        if (ThemeManager.getInstance().isNightMode()) {
            if (sDarkHtmlTemplate == null) {
                sDarkHtmlTemplate = StringUtils.getStringFromAssets("html/html_template_dark.html");
            }
            return sDarkHtmlTemplate;
        } else {
            if (sHtmlTemplate == null) {
                sHtmlTemplate = StringUtils.getStringFromAssets("html/html_template.html");
            }
            return sHtmlTemplate;
        }
    }

    public static String build(ThreadRowInfo row, String ngaHtml, List<String> imageUrls, ForumDecodeRecord decodeResult) {

        StringBuilder builder = new StringBuilder();
        if (row.get_isInBlackList()) {
            builder.append("<h5>[屏蔽]</h5>");
        } else if (TextUtils.isEmpty(ngaHtml) && TextUtils.isEmpty(row.getAlterinfo())) {
            builder.append("<h5>[隐藏]</h5>");
        } else {
            if (!TextUtils.isEmpty(row.getSubject())) {
                builder.append("<div class='title'>").append(row.getSubject()).append("</div><br>");
            }
            if (TextUtils.isEmpty(ngaHtml)) {
                ngaHtml = row.getAlterinfo();
            }
            builder.append(ngaHtml)
                    .append(HtmlCommentBuilder.build(row))
                    .append(HtmlAttachmentBuilder.build(row, imageUrls))
                    .append(HtmlSignatureBuilder.build(row))
                    .append(HtmlVoteBuilder.build(row));
            if (!PhoneConfiguration.getInstance().useOldWebCore()
                    && builder.length() == row.getContent().length()
                    && row.getContent().equals(ngaHtml)) {
                row.setContent(row.getContent().replaceAll("<br/>", "\n"));
                return null;
            }
        }
        String template = getHtmlTemplate();
        int webTextSize = PhoneConfiguration.getInstance().getTopicContentSize();
        int emoticonSize = PhoneConfiguration.getInstance().getEmoticonSize();

        return String.format(template, webTextSize, emoticonSize, builder.toString());
    }
}
