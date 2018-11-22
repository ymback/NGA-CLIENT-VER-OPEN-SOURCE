package sp.phone.mvp.model.convert.builder;

import android.text.TextUtils;

import sp.phone.bean.ThreadRowInfo;
import sp.phone.mvp.model.convert.decoder.ForumDecoder;
import sp.phone.theme.ThemeManager;
import sp.phone.util.FunctionUtils;

/**
 * Created by Justwen on 2018/8/28.
 */
public class HtmlCommentBuilder {

    public static String build(ThreadRowInfo row, String fgColor) {
        StringBuilder ret = new StringBuilder();
        if (row.getComments() == null || row.getComments().isEmpty()) {
            return "";
        }
        ret.append(String.format("<br/><br/>评论<hr/><br/><table border='1px' cellpadding='10px' style='table-layout:fixed;word-break:break-all;border-collapse:collapse; color:%s'>", fgColor));

        ForumDecoder forumDecoder = new ForumDecoder(true);
        for (ThreadRowInfo comment : row.getComments()) {
            String author = comment.getAuthor();
            String avatarUrl = FunctionUtils.parseAvatarUrl(comment.getJs_escap_avatar());
            if (TextUtils.isEmpty(avatarUrl)) {
                avatarUrl = "file:///android_asset/default_avatar.png";
            }
            String content = comment.getContent();
            int end = content.indexOf("[/b]");
            String time = '(' + comment.getPostdate() + ')';
            content = content.substring(end + 4);
            content = forumDecoder.decode(content, null);
            ret.append(String.format("<tr><td width='10%%'> <img src='%s' align='absmiddle' style='max-width:32;' />  <span style='font-weight:bold'>%s %s</span>%s</td></tr>",
                    avatarUrl, author, time, content));

        }
        ret.append("</table>");
        return ret.toString();
    }

    private static String getForegroundColorStr() {
        int webTextColor = ThemeManager.getInstance().getWebTextColor();
        return String.format("%06x", webTextColor & 0xffffff);
    }
}
