package gov.anzong.androidnga.core.corebuild;

import android.text.TextUtils;

import gov.anzong.androidnga.common.util.FileUtils;
import gov.anzong.androidnga.core.data.CommentData;
import gov.anzong.androidnga.core.data.HtmlData;
import gov.anzong.androidnga.core.decode.ForumDecoder;

/**
 * Created by Justwen on 2018/8/28.
 */
public class HtmlCommentBuilder implements IHtmlBuild {

    private volatile static String sFormattedHtml;


    private static String getFormattedHtml() {
        if (sFormattedHtml == null) {
            sFormattedHtml = FileUtils.readAssetToString("html/html_comment_template.html");
        }
        return sFormattedHtml;
    }

    @Override
    public String build(HtmlData htmlData) {

        if (htmlData.getCommentList() == null || htmlData.getCommentList().isEmpty()) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        for (CommentData comment : htmlData.getCommentList()) {
            String author = comment.getAuthor();
            String avatarUrl = comment.getAvatarUrl(); //FunctionUtils.parseAvatarUrl(comment.getJs_escap_avatar());
            if (TextUtils.isEmpty(avatarUrl)) {
                avatarUrl = "file:///android_asset/default_avatar.png";
            }
            String content = comment.getContent();
            int end = content.indexOf("[/b]");
            String time = '(' + comment.getPostTime() + ')';
            content = content.substring(end + 4);
            ret.append(String.format("<tr><td width='10%%'> <img class='circle' src='%s' />  <span style='font-weight:bold'>%s %s</span>%s</td></tr>",
                    avatarUrl, author, time, content));

        }

        String htmlText = getFormattedHtml();
        return String.format(htmlText, ret.toString());
    }
}
