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

    private static final String HTML_TEXT_HIDE = "[隱藏]";

    private static final String HTML_TEXT_SHIELD = "[屏蔽]";

    private static String getForegroundColorStr() {
        int webTextColor = ThemeManager.getInstance().getWebTextColor();
        return String.format("%06x", webTextColor & 0xffffff);
    }

    /**
     * 1 字体大小
     * 2 字体颜色
     * 3 标题
     * 4 内容
     * 5 状态 （屏蔽/隐藏）
     *
     * @return 格式化HTML串
     */
    private static String getHtmlTemplate() {
        if (sHtmlTemplate == null) {
            sHtmlTemplate = StringUtils.getStringFromAssets("html/html_template.html");
        }
        return sHtmlTemplate;
    }

    public static String build(ThreadRowInfo row, String ngaHtml, List<String> imageUrls, ForumDecodeRecord decodeResult) {
        String status = "";
        if (row.get_isInBlackList()) {
            status = HTML_TEXT_SHIELD;
            ngaHtml = "";
        } else if (TextUtils.isEmpty(ngaHtml) && !TextUtils.isEmpty(row.getAlterinfo())) {
            ngaHtml = row.getAlterinfo();
        } else if (TextUtils.isEmpty(ngaHtml)) {
            status = HTML_TEXT_HIDE;
        }

        String fgColorStr = getForegroundColorStr();
        StringBuilder builder = new StringBuilder();
        builder.append(ngaHtml)
                .append(HtmlCommentBuilder.build(row))
                .append(HtmlAttachmentBuilder.build(row, imageUrls))
                .append(HtmlSignatureBuilder.build(row))
                .append(HtmlVoteBuilder.build(row));
        if (builder.length() == row.getContent().length()
                && row.getContent().equals(ngaHtml)
                && TextUtils.isEmpty(row.getSubject())
                && TextUtils.isEmpty(status)) {
            row.setContent(row.getContent().replaceAll("<br/>", "\n"));
            return null;
        } else {
            int webTextSize = PhoneConfiguration.getInstance().getTopicContentSize();
            String template = getHtmlTemplate();
            return String.format(template, webTextSize, fgColorStr, row.getSubject(), builder.toString(), status);
        }
    }
}
