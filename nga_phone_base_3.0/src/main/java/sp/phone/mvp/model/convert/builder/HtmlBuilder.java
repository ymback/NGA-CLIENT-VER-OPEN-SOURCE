package sp.phone.mvp.model.convert.builder;

import android.text.TextUtils;

import java.util.List;
import java.util.Locale;

import sp.phone.bean.ThreadRowInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.mvp.model.convert.decoder.ForumDecodeRecord;
import sp.phone.theme.ThemeManager;

/**
 * Created by Justwen on 2018/8/28.
 */
public class HtmlBuilder {

    private static String getForegroundColorStr() {
        int webTextColor = ThemeManager.getInstance().getWebTextColor();
        return String.format("%06x", webTextColor & 0xffffff);
    }

    public static String build(ThreadRowInfo row, String ngaHtml, List<String> imageUrls, ForumDecodeRecord decodeResult) {
        if (row.get_isInBlackList()) {
            return HtmlBlackListBuilder.build();
        }

        if (TextUtils.isEmpty(ngaHtml)) {
            ngaHtml = row.getAlterinfo();
        }

        if (TextUtils.isEmpty(ngaHtml)) {
            ngaHtml = HtmlHideBuilder.build();
        }

        String fgColorStr = getForegroundColorStr();
        StringBuilder builder = new StringBuilder();
        builder.append(ngaHtml)
                .append(HtmlCommentBuilder.build(row, fgColorStr))
                .append(HtmlAttachmentBuilder.build(row, imageUrls))
                .append(HtmlSignatureBuilder.build(row))
                .append(HtmlVoteBuilder.build(row));
        if (builder.length() == row.getContent().length() && row.getContent().equals(ngaHtml) && TextUtils.isEmpty(row.getSubject())) {
            row.setContent(row.getContent().replaceAll("<br/>","\n"));
            return null;
        } else {
            int webTextSize = PhoneConfiguration.getInstance().getTopicContentSize();
            StringBuilder retBuilder = new StringBuilder();
            retBuilder.append("<!DOCTYPE html><HTML><HEAD><META http-equiv=Content-Type content= \"text/html; charset=utf-8 \">")
                    .append(HtmlHeaderBuilder.build(row, fgColorStr))
                    .append(String.format(Locale.getDefault(), "<style>body {font-size:%dpx;word-break:break-all;color:#%s }</style>", webTextSize, fgColorStr))
                    .append("<body>")
                    .append(builder);

            if (decodeResult.hasCollapseTag()) {
                retBuilder.append("<script type=\"text/javascript\" src=\"file:///android_asset/html/script.js\"></script>");
            }
            retBuilder.append("</body>");
            return retBuilder.toString();
        }


    }
}
