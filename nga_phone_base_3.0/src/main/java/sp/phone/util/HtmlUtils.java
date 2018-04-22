package sp.phone.util;

import android.content.Context;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.anzong.androidnga.R;
import sp.phone.bean.Attachment;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.theme.ThemeManager;

/**
 * Created by liuboyu on 16/6/30.
 */
public class HtmlUtils {

    private static final String TAG = "HtmlUtils";

    public static String userDistance = null;
    static String meter = null;
    static String kiloMeter = null;
    static String hide = null;
    static String blacklistban = null;
    static String legend = null;
    static String attachment = null;
    static String comment = null;
    static String sig = null;

    static Pattern linkPattern = null;

    public static void initStaticStrings(Context activity) {
        userDistance = activity.getString(R.string.user_distance);
        meter = activity.getString(R.string.meter);
        kiloMeter = activity.getString(R.string.kilo_meter);
        hide = activity.getString(R.string.hide);
        blacklistban = activity.getString(R.string.blacklistban);
        legend = activity.getString(R.string.legend);
        attachment = activity.getString(R.string.attachment);
        comment = activity.getString(R.string.comment);
        sig = activity.getString(R.string.sig);

        linkPattern = Pattern.compile("<a(?: [^>]*)+href=([^ >]*)(?: [^>]*)*>");
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
        HashSet<String> imageURLSet = new HashSet<String>();
        String ngaHtml = StringUtils.decodeForumTag(row.getContent(), showImage,
                imageQuality, imageURLSet);
        if (row.get_isInBlackList()) {
            ngaHtml = "<HTML> <HEAD><META http-equiv=Content-Type content= \"text/html; charset=utf-8 \">"
                    + "<body "
                    + "'>"
                    + "<font color='red' size='2'>["
                    + blacklistban
                    + "]</font>" + "</font></body>";
        } else {
            if (imageURLSet.size() == 0) {
                imageURLSet = null;
            }
            if (StringUtils.isEmpty(ngaHtml)) {
                ngaHtml = row.getAlterinfo();
            }
            if (StringUtils.isEmpty(ngaHtml)) {
                ngaHtml = "<font color='red'>[" + hide + "]</font>";
            }
            // 把链接替换为短字符
            //ngaHtml = replaceLinkText(ngaHtml);
            ngaHtml = ngaHtml
                    + buildComment(row, fgColorStr, showImage, imageQuality)
                    + buildAttachment(row, showImage, imageQuality, imageURLSet)
                    + buildSignature(row, showImage, imageQuality)
                    + buildVote(row);
            ngaHtml = "<HTML> <HEAD><META http-equiv=Content-Type content= \"text/html; charset=utf-8 \">"
                    + buildHeader(row, fgColorStr)
                    + "<body style=word-break:break-all; "
                    + "'>"
                    + "<font color='#"
                    + fgColorStr
                    + "' size='2'>" + ngaHtml + "</font></body>";
        }
        return ngaHtml;
    }

    /**
     * 匹配超链接, 把链接文字缩短或替换
     *
     * @param input
     * @return
     */
    private static String replaceLinkText(String input) {
        String ret = "";
        Matcher matcher = linkPattern.matcher(input);
        while (matcher.find()) {
            String pre = matcher.group();
            ret = pre + "链接</a>";
        }
        return ret;
    }


    @SuppressWarnings("static-access")
    private static String buildAttachment(ThreadRowInfo row, boolean showImage, int imageQuality, HashSet<String> imageURLSet) {
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
        while (it.hasNext()) {
            Map.Entry<String, Attachment> entry = it.next();
            if (imageURLSet != null && imageURLSet.size() > 0 && imageURLSet.contains(entry.getValue().getAttachurl())) {
                continue;
            }
            // String url = "http://img.nga.178.com/attachments/" +
            // entry.getValue().getAttachurl();
            String attachUrl = entry.getValue().getAttachurl();
            if (attachUrl.contains("mp3")) {
                ret = buildAudioAttachment(ret, entry.getValue());
            } else if (attachUrl.contains("mp4")) {
                ret = buildVideoAttachment(ret, entry.getValue());
            } else {
                ret = buildImageAttachment(ret, entry.getValue(), showImage, imageQuality);
            }
            attachmentCount++;
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

    private static StringBuilder buildImageAttachment(StringBuilder ret, Attachment attachment, boolean showImage, int imageQuality) {
        String url = attachment.getAttachurl();
        ret.append("<tr><td><a href='http://")
                .append(HttpUtil.NGA_ATTACHMENT_HOST)
                .append("/attachments/")
                .append(url)
                .append("'>");

        if (showImage) {
            String attachURL = "http://" + HttpUtil.NGA_ATTACHMENT_HOST + "/attachments/" + url;
            if ("1".equals(attachment.getThumb())) {
                attachURL = attachURL + ".thumb.jpg";
            } else {
                attachURL = StringUtils.buildOptimizedImageURL(attachURL, imageQuality);
            }
            ret.append("<img src='")
                    .append(attachURL);
        } else {
            ret.append("<img src='file:///android_asset/ic_offline_image.png");
        }

        ret.append("' style= 'max-width:70%;'></a>").append("</td></tr>");
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
            // TODO: 2018/4/15  should not use magic code, need refactor it later
            int start = content.indexOf("[/uid]") + 7;
            int end = content.indexOf("[/b]");
            String time = content.substring(start, end);
            content = content.substring(end + 4);
            content = StringUtils.decodeForumTag(content, showImage, imageQuality, null);
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
