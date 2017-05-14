package sp.phone.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.util.NetUtil;
import sp.phone.bean.Attachment;
import sp.phone.bean.ThreadRowInfo;

/**
 * Created by liuboyu on 16/6/30.
 */
public class HtmlUtil {

    private static final String TAG = "HtmlUtil";

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
        if (row == null || (StringUtil.isEmpty(row.getSubject()) && !row.getISANONYMOUS()))
            return "";
        StringBuilder sb = new StringBuilder();
        sb.append("<h4 style='color:").append(fgColorStr).append("' >");
        if (!StringUtil.isEmpty(row.getSubject()))
            sb.append(row.getSubject());
        if (row.getISANONYMOUS())
            sb.append("<font style='color:#D00;font-weight: bold;'>").append("[匿名]").append("</font>");
        sb.append("</h4>");
        return sb.toString();
    }

    public static String convertToHtmlText(final ThreadRowInfo row,
                                           boolean showImage, int imageQuality, final String fgColorStr,
                                           final String bgcolorStr, Context context) {
        if (StringUtil.isEmpty(hide)) {
            if (context != null)
                initStaticStrings(context);
        }
        HashSet<String> imageURLSet = new HashSet<String>();
        String ngaHtml = StringUtil.decodeForumTag(row.getContent(), showImage,
                imageQuality, imageURLSet);
        if (row.get_isInBlackList()) {
            ngaHtml = "<HTML> <HEAD><META http-equiv=Content-Type content= \"text/html; charset=utf-8 \">"
                    + "<body bgcolor= '#"
                    + bgcolorStr
                    + "'>"
                    + "<font color='red' size='2'>["
                    + blacklistban
                    + "]</font>" + "</font></body>";
        } else {
            if (imageURLSet.size() == 0) {
                imageURLSet = null;
            }
            if (StringUtil.isEmpty(ngaHtml)) {
                ngaHtml = row.getAlterinfo();
            }
            if (StringUtil.isEmpty(ngaHtml)) {
                ngaHtml = "<font color='red'>[" + hide + "]</font>";
            }
            // 把链接替换为短字符
            //ngaHtml = replaceLinkText(ngaHtml);
            ngaHtml = ngaHtml
                    + buildComment(row, fgColorStr, showImage, imageQuality, context)
                    + buildAttachment(row, showImage, imageQuality, imageURLSet)
                    + buildSignature(row, showImage, imageQuality)
                    + buildVote(row);
            ngaHtml = "<HTML> <HEAD><META http-equiv=Content-Type content= \"text/html; charset=utf-8 \">"
                    + buildHeader(row, fgColorStr)
                    + "<body bgcolor= '#"
                    + bgcolorStr
                    + "'>"
                    + "<font color='#"
                    + fgColorStr
                    + "' size='2'>" + ngaHtml + "</font></body>";
        }
        return ngaHtml;
    }

    /**
     * 匹配超链接, 把链接文字缩短或替换
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
        if (theme.mode == theme.MODE_NIGHT) {
            ret.append("<table style='background:#000000;border:1px solid #b9986e;padding:10px;color:#6b2d25;font-size:2'>");
        } else {
            ret.append("<table style='background:#e1c8a7;border:1px solid #b9986e;padding:10px;color:#6b2d25;font-size:2'>");
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
            ret.append("<tr><td><a href='http://" + HttpUtil.NGA_ATTACHMENT_HOST + "/attachments/");
            ret.append(entry.getValue().getAttachurl());
            ret.append("'>");
            if (showImage) {
                String attachURL = "http://" + HttpUtil.NGA_ATTACHMENT_HOST
                        + "/attachments/" + entry.getValue().getAttachurl();
                if ("1".equals(entry.getValue().getThumb())) {
                    attachURL = attachURL + ".thumb.jpg";
                    // ret.append(entry.getValue().getExt());
                } else {
                    attachURL = StringUtil.buildOptimizedImageURL(attachURL, imageQuality);
                }
                ret.append("<img src='");
                ret.append(attachURL);
            } else {
                ret.append("<img src='file:///android_asset/ic_offline_image.png");
            }

            ret.append("' style= 'max-width:70%;'></a>");

            ret.append("</td></tr>");
            attachmentCount++;
        }
        ret.append("</tbody></table>");
        if (attachmentCount == 0)
            return "";
        else
            return ret.toString();
    }

    private static String buildComment(ThreadRowInfo row, String fgColor, boolean showImage, int imageQuality, Context context) {
        if (row == null || row.getComments() == null || row.getComments().size() == 0) {
            return "";
        }

        StringBuilder ret = new StringBuilder();
        ret.append("<br/></br>").append(comment).append("<hr/><br/>");
        ret.append("<table border='1px' cellspacing='0px' style='border-collapse:collapse;");
        ret.append("color:");
        ret.append(fgColor);
        ret.append("'>");

        ret.append("<tbody>");

        Iterator<ThreadRowInfo> it = row.getComments().iterator();
        final boolean downImg = NetUtil.getInstance().isInWifi() || PhoneConfiguration.getInstance().isDownAvatarNoWifi();
        while (it.hasNext()) {
            ThreadRowInfo comment = it.next();
            ret.append("<tr><td>");
            ret.append("<span style='font-weight:bold' >");
            ret.append(comment.getAuthor());
            ret.append("</span><br/>");
            ret.append("<img src='");
            String avatarUrl = FunctionUtil.parseAvatarUrl(comment.getJs_escap_avatar());
            String avatarPath = ImageUtil.newImage(avatarUrl, String.valueOf(comment.getAuthorid()));
            if (downImg) {
                if (StringUtil.isEmpty(avatarPath)) {
                    ret.append(avatarUrl);
                } else {
                    File f = new File(avatarPath);
                    if (f.exists()) {
                        ret.append("file://" + avatarPath);
                    } else {
                        ret.append(avatarUrl);
                    }
                }
            } else {
                if (StringUtil.isEmpty(avatarPath)) {
                    ret.append("file:///android_asset/default_avatar.png");
                } else {
                    File f = new File(avatarPath);
                    if (f.exists()) {
                        ret.append(avatarPath);
                    } else {
                        ret.append("file:///android_asset/default_avatar.png");
                    }
                }
            }
            ret.append("' style= 'max-width:32;'>");

            ret.append("</td><td>");
            ret.append(StringUtil.decodeForumTag(comment.getContent(), showImage, imageQuality, null));
            ret.append("</td></tr>");

        }
        ret.append("</tbody></table>");
        Log.i(TAG, ret.toString());
        return ret.toString();
    }

    private static String buildSignature(ThreadRowInfo row, boolean showImage,
                                         int imageQuality) {
        if (row == null || row.getSignature() == null
                || row.getSignature().length() == 0
                || !PhoneConfiguration.getInstance().showSignature) {
            return "";
        }
        return "<br/></br>"
                + sig
                + "<hr/><br/>"
                + StringUtil.decodeForumTag(row.getSignature(), showImage,
                imageQuality, null);
    }

    private static String buildVote(ThreadRowInfo row) {
        if (row == null || StringUtil.isEmpty(row.getVote())) {
            return "";
        }
        return "<br/><hr/>" + "本楼有投票/投注内容,长按本楼在菜单中点击投票/投注按钮";
    }
}
