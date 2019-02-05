package sp.phone.mvp.model.convert.builder;

import android.text.TextUtils;

import java.util.List;

import sp.phone.bean.ThreadRowInfo;
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
        String fgColorStr = getForegroundColorStr();
        StringBuilder retBuilder = new StringBuilder();
        retBuilder.append("<HTML> <HEAD><META http-equiv=Content-Type content= \"text/html; charset=utf-8 \">")
                .append(HtmlHeaderBuilder.build(row, fgColorStr))
                .append("<body style=word-break:break-all; ")
                .append("'>")
                .append("<font color='#")
                .append(fgColorStr)
                .append("' size='2'>");

        if (TextUtils.isEmpty(ngaHtml)) {
            ngaHtml = row.getAlterinfo();
        }

        if (TextUtils.isEmpty(ngaHtml)) {
            ngaHtml = HtmlHideBuilder.build();
        }

        retBuilder.append(ngaHtml)
                .append(HtmlCommentBuilder.build(row, fgColorStr))
                .append(HtmlAttachmentBuilder.build(row, imageUrls))
                .append(HtmlSignatureBuilder.build(row))
                .append(HtmlVoteBuilder.build(row))
                .append("</font>");
        if (decodeResult.hasCollapseTag()) {
            retBuilder.append("<script type=\"text/javascript\" src=\"file:///android_asset/html/script.js\"></script>");
        }
        retBuilder.append("</body>");
        return retBuilder.toString();
    }
}
