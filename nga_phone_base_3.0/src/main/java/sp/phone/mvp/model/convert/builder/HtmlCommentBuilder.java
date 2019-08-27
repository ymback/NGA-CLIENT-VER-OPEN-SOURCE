package sp.phone.mvp.model.convert.builder;

import android.text.TextUtils;

import sp.phone.http.bean.ThreadRowInfo;
import sp.phone.mvp.model.convert.decoder.ForumDecoder;
import sp.phone.util.FunctionUtils;
import sp.phone.util.StringUtils;

/**
 * Created by Justwen on 2018/8/28.
 */
public class HtmlCommentBuilder {

    private static String sFormattedHtml;


    private static String getFormattedHtml() {
        if (sFormattedHtml == null) {
            sFormattedHtml = StringUtils.getStringFromAssets("html/html_comment_template.html");
        }
        return sFormattedHtml;
    }

    public static String build(ThreadRowInfo row) {

        if (row.getComments() == null || row.getComments().isEmpty()) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
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
            ret.append(String.format("<tr><td width='10%%'> <img class='circle' src='%s' />  <span style='font-weight:bold'>%s %s</span>%s</td></tr>",
                    avatarUrl, author, time, content));

        }

        String htmlText = getFormattedHtml();
        return String.format(htmlText, ret.toString());
    }
}
