package sp.phone.util;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gov.anzong.androidnga.R;
import sp.phone.bean.Attachment;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.mvp.model.convert.decoder.ForumDecoder;
import sp.phone.theme.ThemeManager;

/**
 * Created by liuboyu on 16/6/30.
 */
public class HtmlUtils {

    private static final String TAG_IGNORE_CASE = "(?i)";

    private static final String TAG_END_DIV = "</div>";

    public static String hide = null;
    static String blacklistban = null;
    static String legend = null;
    static String attachment = null;
    static String comment = null;
    static String sig = null;

    public static void initStaticStrings(Context activity) {
        hide = activity.getString(R.string.hide);
        blacklistban = activity.getString(R.string.blacklistban);
        legend = activity.getString(R.string.legend);
        attachment = activity.getString(R.string.attachment);
        comment = activity.getString(R.string.comment);
        sig = activity.getString(R.string.sig);
    }

    public static String convertWebColor(@ColorInt int color) {
        return String.format("#%06x", color & 0xFFFFFF);
    }

    private static String buildHeader(ThreadRowInfo row, String fgColorStr) {
        if (row == null || (StringUtils.isEmpty(row.getSubject()) && !row.getISANONYMOUS()))
            return "";
        StringBuilder sb = new StringBuilder();
        sb.append("<h4 style='color:").append(fgColorStr).append("' >");
        if (!StringUtils.isEmpty(row.getSubject()))
            sb.append(row.getSubject());
        if (row.getISANONYMOUS())
            sb.append("<font style='color:#D00;font-weight: bold;'>").append("[匿名]").append("</font>");
        sb.append("</h4>");
        return sb.toString();
    }

    public static String convertToHtmlText(final ThreadRowInfo row,
                                           boolean showImage, int imageQuality, final String fgColorStr,
                                           Context context) {
        if (StringUtils.isEmpty(hide)) {
            if (context != null)
                initStaticStrings(context);
        }

        List<String> imageUrls = new ArrayList<>();
        String ngaHtml = new ForumDecoder(true).decode(row.getContent(), imageUrls);
        if (row.get_isInBlackList()) {
            ngaHtml = "<HTML> <HEAD><META http-equiv=Content-Type content= \"text/html; charset=utf-8 \">"
                    + "<body "
                    + "'>"
                    + "<font color='red' size='2'>["
                    + blacklistban
                    + "]</font>" + "</font></body>";
        } else {
            if (StringUtils.isEmpty(ngaHtml)) {
                ngaHtml = row.getAlterinfo();
            }
            if (StringUtils.isEmpty(ngaHtml)) {
                ngaHtml = "<font color='red'>[" + hide + "]</font>";
            }
            ngaHtml = ngaHtml
                    + buildComment(row, fgColorStr, showImage, imageQuality)
                    + buildAttachment(row, showImage, imageQuality, imageUrls)
                    + buildSignature(row, showImage, imageQuality)
                    + buildVote(row);
            ngaHtml = "<HTML> <HEAD><META http-equiv=Content-Type content= \"text/html; charset=utf-8 \">"
                    + buildHeader(row, fgColorStr)
                    + "<body style=word-break:break-all; "
                    + "'>"
                    + "<font color='#"
                    + fgColorStr
                    + "' size='2'>" + ngaHtml + "</font>"
                    + "<script type=\"text/javascript\" src=\"file:///android_asset/html/script.js\"></script>"
                    + "</body>";
        }
        row.getImageUrls().addAll(imageUrls);
        return ngaHtml;
    }

    private static String buildAttachment(ThreadRowInfo row, boolean showImage, int imageQuality, List<String> imageUrls) {
        if (row == null || row.getAttachs() == null
                || row.getAttachs().size() == 0) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        ThemeManager theme = ThemeManager.getInstance();
        ret.append("<br/><br/>").append(attachment).append("<hr/><br/>");
        // ret.append("<table style='background:#e1c8a7;border:1px solid #b9986e;margin:0px 0px 10px 30px;padding:10px;color:#6b2d25;max-width:100%;'>");
        if (theme.isNightMode()) {
            ret.append("<table style='border:1px solid #b9986e;padding:10px;color:#6b2d25;font-size:10'>");
        } else {
            ret.append("<table style='border:1px solid #b9986e;padding:10px;color:#6b2d25;font-size:10'>");
        }
        ret.append("<tbody>");
        Iterator<Map.Entry<String, Attachment>> it = row.getAttachs().entrySet()
                .iterator();
        int attachmentCount = 0;
        int imageAttachmentCount = 0;
        while (it.hasNext()) {
            Map.Entry<String, Attachment> entry = it.next();
            // String url = "http://img.nga.178.com/attachments/" +
            // entry.getValue().getAttachurl();
            String attachUrl = entry.getValue().getAttachurl();
            if (attachUrl.contains("mp3")) {
                ret = buildAudioAttachment(ret, entry.getValue());
            } else if (attachUrl.contains("mp4")) {
                ret = buildVideoAttachment(ret, entry.getValue());
            } else {
                imageAttachmentCount++;
                buildImageAttachment(ret, entry.getValue(), imageAttachmentCount, imageUrls);
            }
            attachmentCount++;
        }
        if (imageAttachmentCount > 0) {
            ret.append("<script> function displayImg(a,b){ document.getElementById('img'+a).src=b; document.getElementById('show' + a).style.display='none'; } </script>");
        }
        ret.append("</tbody></table>");
        if (attachmentCount == 0)
            return "";
        else
            return ret.toString();
    }

    private static StringBuilder buildAudioAttachment(StringBuilder ret, Attachment attachment) {
        String url = attachment.getAttachurl();
        ret.append("<tr><td><a href='http://")
                .append(HttpUtil.NGA_ATTACHMENT_HOST)
                .append("/attachments/")
                .append(url)
                .append("'>")
                .append("nga_audio.mp3</a>")
                .append("</td></tr>");
        return ret;
    }

    private static StringBuilder buildVideoAttachment(StringBuilder ret, Attachment attachment) {
        String url = attachment.getAttachurl();
        ret.append("<tr><td><a href='http://")
                .append(HttpUtil.NGA_ATTACHMENT_HOST)
                .append("/attachments/")
                .append(url)
                .append("'>")
                .append("nga_video.mp4</a>")
                .append("</td></tr>");
        return ret;
    }

    private static StringBuilder buildImageAttachment(StringBuilder ret, Attachment attachment, int index, List<String> imageUrls) {

        String attachUrl = "http://" + HttpUtil.NGA_ATTACHMENT_HOST + "/attachments/" + attachment.getAttachurl();
        String attachUrlThumb = attachUrl;
        String indexStr = String.valueOf(index);
        if ("1".equals(attachment.getThumb())) {
            attachUrlThumb = attachUrlThumb + ".thumb.jpg";
        }
        ret.append("<tr><td>")
                .append(String.format("<button id='show%s' type='button' onclick='displayImg(%s,\"%s\")'>点击显示附件</button>", indexStr, indexStr, attachUrlThumb))
                .append(String.format("<a href=%s>", attachUrl))
                .append(String.format("<img style='max-width:100%%'; id='img%s'/>", indexStr))
                .append("</a></td></tr>");
        if (!imageUrls.contains(attachUrl)) {
            imageUrls.add(attachUrl);
        }

        return ret;
    }

    private static StringBuilder buildComment(ThreadRowInfo row, String fgColor, boolean showImage, int imageQuality) {
        StringBuilder ret = new StringBuilder();
        if (row == null || row.getComments() == null || row.getComments().isEmpty()) {
            return ret;
        }
        ret.append(String.format("<br/><br/>%s<hr/><br/><table border='1px' cellpadding='10px' style='table-layout:fixed;word-break:break-all;border-collapse:collapse; color:%s'>", comment, fgColor));

        for (ThreadRowInfo comment : row.getComments()) {
            String author = comment.getAuthor();
            String avatarUrl = FunctionUtils.parseAvatarUrl(comment.getJs_escap_avatar());
            if (!showImage || TextUtils.isEmpty(avatarUrl)) {
                avatarUrl = "file:///android_asset/default_avatar.png";
            }
            String content = comment.getContent();
            int end = content.indexOf("[/b]");
            String time = '(' + comment.getPostdate() + ')';
            content = content.substring(end + 4);
            content = new ForumDecoder(true).decode(content, null);
            ret.append(String.format("<tr><td width='10%%'> <img src='%s' align='absmiddle' style='max-width:32;' />  <span style='font-weight:bold'>%s %s</span>%s</td></tr>",
                    avatarUrl, author, time, content));

        }
        ret.append("</table>");
        return ret;
    }

    private static String buildSignature(ThreadRowInfo row, boolean showImage,
                                         int imageQuality) {
        if (row == null || row.getSignature() == null
                || row.getSignature().length() == 0
                || !PhoneConfiguration.getInstance().isShowSignature()) {
            return "";
        }
        return "<br/></br>"
                + sig
                + "<hr/><br/>"
                + StringUtils.decodeForumTag(row.getSignature(), showImage,
                imageQuality, null);
    }

    private static String buildVote(ThreadRowInfo row) {
        if (row == null || StringUtils.isEmpty(row.getVote())) {
            return "";
        }
        return "<br/><hr/>" + "本楼有投票/投注内容,长按本楼在菜单中点击投票/投注按钮";
    }
}
