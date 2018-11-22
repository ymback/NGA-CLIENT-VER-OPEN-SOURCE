package sp.phone.mvp.model.convert.builder;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sp.phone.bean.Attachment;
import sp.phone.bean.ThreadRowInfo;
import sp.phone.theme.ThemeManager;
import sp.phone.util.HttpUtil;

/**
 * Created by Justwen on 2018/8/28.
 */
public class HtmlAttachmentBuilder {

    public static String build(ThreadRowInfo row, List<String> imageUrls) {
        if (row == null || row.getAttachs() == null
                || row.getAttachs().size() == 0) {
            return "";
        }
        StringBuilder ret = new StringBuilder();
        ThemeManager theme = ThemeManager.getInstance();
        ret.append("<br/><br/>附件<hr/><br/>");
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
}
