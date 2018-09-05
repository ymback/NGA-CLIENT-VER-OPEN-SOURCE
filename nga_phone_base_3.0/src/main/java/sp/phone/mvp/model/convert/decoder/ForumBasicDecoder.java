package sp.phone.mvp.model.convert.decoder;

import gov.anzong.androidnga.Utils;
import sp.phone.theme.ThemeManager;
import sp.phone.util.HtmlUtils;
import sp.phone.util.HttpUtil;
import sp.phone.util.StringUtils;

/**
 * Created by Justwen on 2018/8/25.
 */
public class ForumBasicDecoder implements IForumDecoder {

    private static final String lesserNukeStyle = "<div style='border:1px solid #B63F32;margin:10px 10px 10px 10px;padding:10px' > <span style='color:#EE8A9E'>用户因此贴被暂时禁言，此效果不会累加</span><br/>";
    private static final String styleAlignRight = "<div style='text-align:right' >";
    private static final String styleAlignLeft = "<div style='text-align:left' >";
    private static final String styleAlignCenter = "<div style='text-align:center' >";
    private static final String styleColor = "<span style='color:$1' >";
    private static final String ignoreCaseTag = "(?i)";
    private static final String endDiv = "</div>";

    private static final String STYLE_QUOTE = "<div style='background:%s;padding:5px;border:1px solid #888' >";

    @Override
    public String decode(String content) {
        if (StringUtils.isEmpty(content))
            return "";
        // s = StringUtils.unEscapeHtml(s);

        int quoteColor = ThemeManager.getInstance().getWebQuoteBackgroundColor();
        String quoteColorStr = HtmlUtils.convertWebColor(quoteColor);
        String quoteStyle = String.format(STYLE_QUOTE,quoteColorStr);

//        String quoteStyle = "<div style='background:#E8E8E8;padding:5px;border:1px solid #888' >";
//        if (ThemeManager.getInstance().isNightMode())
//            quoteStyle = "<div style='background:#000000;padding:5px;border:1px solid #888' >";

        final String styleLeft = "<div style='float:left' >";
        final String styleRight = "<div style='float:right' >";
        content = content.replaceAll(ignoreCaseTag + "&amp;", "&");
        content = content.replaceAll(ignoreCaseTag + "\\[l\\]", styleLeft);
        content = content.replaceAll(ignoreCaseTag + "\\[/l\\]", endDiv);
        // content = content.replaceAll("\\[L\\]", styleLeft);
        // content = content.replaceAll("\\[/L\\]", endDiv);

        content = content.replaceAll(ignoreCaseTag + "\\[r\\]", styleRight);
        content = content.replaceAll(ignoreCaseTag + "\\[/r\\]", endDiv);
        // content = content.replaceAll("\\[R\\]", styleRight);
        // content = content.replaceAll("\\[/R\\]", endDiv);

        content = content.replaceAll(ignoreCaseTag + "\\[align=right\\]", styleAlignRight);
        content = content.replaceAll(ignoreCaseTag + "\\[align=left\\]", styleAlignLeft);
        content = content.replaceAll(ignoreCaseTag + "\\[align=center\\]", styleAlignCenter);
        content = content.replaceAll(ignoreCaseTag + "\\[/align\\]", endDiv);

        content = content.replaceAll(
                ignoreCaseTag
                        + "\\[b\\]Reply to \\[pid=(.+?),(.+?),(.+?)\\]Reply\\[/pid\\] (.+?)\\[/b\\]",
                "[quote]Reply to [b]<a href='" + Utils.getNGAHost() + "read.php?searchpost=1&pid=$1' style='font-weight: bold;'>[Reply]</a> $4[/b][/quote]");

        content = content.replaceAll(
                ignoreCaseTag + "\\[pid=(.+?),(.+?),(.+?)\\]Reply\\[/pid\\]",
                "<a href='" + Utils.getNGAHost() + "read.php?searchpost=1&pid=$1' style='font-weight: bold;'>[Reply]</a>");

        // 某些帖子会导致这个方法卡住, 暂时不清楚原因, 和这个方法的作用.... by elrond
        /*content = content.replaceAll(
                ignoreCaseTag + "={3,}((^=){0,}(.*?){0,}(^=){0,})={3,}",
                "<h4 style='font-weight: bold;border-bottom: 1px solid #AAA;clear: both;margin-bottom: 0px;'>$1</h4>");*/

        content = content.replaceAll(ignoreCaseTag + "\\[quote\\]", quoteStyle);
        content = content.replaceAll(ignoreCaseTag + "\\[/quote\\]", endDiv);

        content = content.replaceAll(ignoreCaseTag + "\\[code\\]", quoteStyle + "Code:");
        content = content.replaceAll(ignoreCaseTag + "\\[code(.+?)\\]", quoteStyle);
        content = content.replaceAll(ignoreCaseTag + "\\[/code\\]", endDiv);
        // reply
        // content = content.replaceAll(
        // ignoreCaseTag +"\\[pid=\\d+\\]Reply\\[/pid\\]", "Reply");
        // content = content.replaceAll(
        // ignoreCaseTag +"\\[pid=\\d+,\\d+,\\d\\]Reply\\[/pid\\]", "Reply");

        // topic
        content = content.replaceAll(ignoreCaseTag + "\\[tid=\\d+\\]Topic\\[/pid\\]",
                "Topic");
        content = content.replaceAll(ignoreCaseTag + "\\[tid=?(\\d{0,50})\\]Topic\\[/tid\\]",
                "<a href='" + Utils.getNGAHost() + "read.php?tid=$1' style='font-weight: bold;'>[Topic]</a>");
        // reply
        // s =
        // s.replaceAll("\\[b\\]Reply to \\[pid=\\d+\\]Reply\\[/pid\\] (Post by .+ \\(\\d{4,4}-\\d\\d-\\d\\d \\d\\d:\\d\\d\\))\\[/b\\]"
        // , "Reply to Reply <b>$1</b>");
        // 转换 tag
        // [b]
        content = content.replaceAll(ignoreCaseTag + "\\[b\\]", "<b>");
        content = content.replaceAll(ignoreCaseTag + "\\[/b\\]", "</b>"/* "</font>" */);

        // item
        content = content.replaceAll(ignoreCaseTag + "\\[item\\]", "<b>");
        content = content.replaceAll(ignoreCaseTag + "\\[/item\\]", "</b>");

        content = content.replaceAll(ignoreCaseTag + "\\[u\\]", "<u>");
        content = content.replaceAll(ignoreCaseTag + "\\[/u\\]", "</u>");

        content = content.replaceAll(ignoreCaseTag + "\\[s:(\\d+)\\]",
                "<img src='file:///android_asset/a$1.gif'>");
        content = content.replace(ignoreCaseTag + "<br/><br/>", "<br/>");
        // [url][/url]
        content = content.replaceAll(
                ignoreCaseTag + "\\[url\\]/([^\\[|\\]]+)\\[/url\\]",
                "<a href=\"" + Utils.getNGAHost() + "$1\">" + Utils.getNGAHost() + "$1</a>");
        content = content.replaceAll(
                ignoreCaseTag + "\\[url\\]([^\\[|\\]]+)\\[/url\\]",
                "<a href=\"$1\">$1</a>");
        content = content.replaceAll(ignoreCaseTag
                        + "\\[url=/([^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/url\\]",
                "<a href=\"" + Utils.getNGAHost() + "$1\">$2</a>");
        content = content.replaceAll(ignoreCaseTag
                        + "\\[url=([^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/url\\]",
                "<a href=\"$1\">$2</a>");
        content = content.replaceAll(ignoreCaseTag
                + "\\[uid=?(\\d{0,50})\\](.+?)\\[\\/uid\\]", "$2");
        content = content.replaceAll(
                ignoreCaseTag + "Post by\\s{0,}([^\\[\\s]{1,})\\s{0,}\\(",
                "Post by <a href='" + Utils.getNGAHost() + "nuke.php?func=ucp&username=$1' style='font-weight: bold;'>[$1]</a> (");
        content = content.replaceAll(
                ignoreCaseTag + "\\[@(.{2,20}?)\\]",
                "<a href='" + Utils.getNGAHost() + "nuke.php?func=ucp&username=$1' style='font-weight: bold;'>[@$1]</a>");
        content = content.replaceAll(ignoreCaseTag
                + "\\[uid=-?(\\d{0,50})\\](.+?)\\[\\/uid\\]", "$2");
        content = content.replaceAll(ignoreCaseTag
                        + "\\[hip\\](.+?)\\[\\/hip\\]",
                "$1");
        content = content.replaceAll(ignoreCaseTag + "\\[tid=?(\\d{0,50})\\](.+?)\\[/tid\\]",
                "<a href='" + Utils.getNGAHost() + "read.php?tid=$1' style='font-weight: bold;'>[$2]</a>");
        content = content.replaceAll(
                ignoreCaseTag
                        + "\\[pid=(.+?)\\]\\[/pid\\]",
                "<a href='" + Utils.getNGAHost() + "read.php?pid=$1' style='font-weight: bold;'>[Reply]</a>");
        content = content.replaceAll(
                ignoreCaseTag
                        + "\\[pid=(.+?)\\](.+?)\\[/pid\\]",
                "<a href='" + Utils.getNGAHost() + "read.php?pid=$1' style='font-weight: bold;'>[$2]</a>");
        // flash
        content = content.replaceAll(
                ignoreCaseTag + "\\[flash\\](http[^\\[|\\]]+)\\[/flash\\]",
                "<a href=\"$1\"><img src='file:///android_asset/flash.png' style= 'max-width:100%;' ></a>");
        // color

        // content = content.replaceAll("\\[color=([^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/color\\]"
        // ,"<b style=\"color:$1\">$2</b>");
        content = content.replaceAll(ignoreCaseTag + "\\[color=([^\\[|\\]]+)\\]",
                styleColor);
        content = content.replaceAll(ignoreCaseTag + "\\[/color\\]", "</span>");

        // lessernuke
        content = content.replaceAll("\\[lessernuke\\]", lesserNukeStyle);
        content = content.replaceAll("\\[/lessernuke\\]", endDiv);

        content = content.replaceAll(
                "\\[table\\]",
                "<div><table cellspacing='0px' style='border:1px solid #aaa;width:99.9%;'><tbody>");
        content = content.replaceAll("\\[/table\\]", "</tbody></table></div>");
        content = content.replaceAll("\\[tr\\]", "<tr>");
        content = content.replaceAll("\\[/tr\\]", "<tr>");
        content = content.replaceAll(ignoreCaseTag
                        + "\\[td(\\d+)\\]",
                "<td style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = content.replaceAll(ignoreCaseTag
                        + "\\[td\\scolspan(\\d+)\\swidth(\\d+)\\]",
                "<td colspan='$1' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = content.replaceAll(ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\scolspan(\\d+)\\]",
                "<td colspan='$2' style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");

        content = content.replaceAll(ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\srowspan(\\d+)\\]",
                "<td rowspan='$2' style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = content.replaceAll(ignoreCaseTag
                        + "\\[td\\srowspan(\\d+)\\swidth(\\d+)\\]",
                "<td rowspan='$1' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");

        content = content.replaceAll(ignoreCaseTag
                        + "\\[td\\scolspan(\\d+)\\srowspan(\\d+)\\swidth(\\d+)\\]",
                "<td colspan='$1' rowspan='$2' style='width:$3%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = content.replaceAll(ignoreCaseTag
                        + "\\[td\\scolspan(\\d+)\\swidth(\\d+)\\srowspan(\\d+)\\]",
                "<td colspan='$1' rowspan='$3' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = content.replaceAll(ignoreCaseTag
                        + "\\[td\\srowspan(\\d+)\\scolspan(\\d+)\\swidth(\\d+)\\]",
                "<td rowspan='$1' colspan='$2' style='width:$3%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = content.replaceAll(ignoreCaseTag
                        + "\\[td\\srowspan(\\d+)\\swidth(\\d+)\\scolspan(\\d+)\\]",
                "<td rowspan='$1' colspan='$3' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = content.replaceAll(ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\scolspan(\\d+)\\srowspan(\\d+)\\]",
                "<td rowspan='$3' colspan='$2' style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = content.replaceAll(ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\srowspan(\\d+)\\scolspan(\\d+)\\]",
                "<td rowspan='$2' colspan='$3'  style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");


        content = content.replaceAll(ignoreCaseTag
                        + "\\[td\\scolspan=(\\d+)\\]",
                "<td colspan='$1' style='border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = content.replaceAll(ignoreCaseTag
                        + "\\[td\\srowspan=(\\d+)\\]",
                "<td rowspan='$1' style='border-left:1px solid #aaa;border-bottom:1px solid #aaa;'>");
        content = content.replaceAll("\\[td\\]", "<td style='border-left:1px solid #aaa;border-bottom:1px solid #aaa;'>");
        content = content.replaceAll("\\[/td\\]", "<td>");
        // [i][/i]
        content = content.replaceAll(ignoreCaseTag + "\\[i\\]",
                "<i style=\"font-style:italic\">");
        content = content.replaceAll(ignoreCaseTag + "\\[/i\\]", "</i>");
        // [del][/del]
        content = content.replaceAll(ignoreCaseTag + "\\[del\\]", "<del class=\"gray\">");
        content = content.replaceAll(ignoreCaseTag + "\\[/del\\]", "</del>");

        content = content.replaceAll(ignoreCaseTag + "\\[font=([^\\[|\\]]+)\\]",
                "<span style=\"font-family:$1\">");
        content = content.replaceAll(ignoreCaseTag + "\\[/font\\]", "</span>");

        // size
        content = content.replaceAll(ignoreCaseTag + "\\[size=(\\d+)%\\]",
                "<span style=\"font-size:$1%;line-height:$1%\">");
        content = content.replaceAll(ignoreCaseTag + "\\[/size\\]", "</span>");

        // [img]./ddd.jpg[/img]
        // if(showImage){
        content = content.replaceAll(ignoreCaseTag
                        + "\\[img\\]\\s*\\.(/[^\\[|\\]]+)\\s*\\[/img\\]",
                "<a href='http://" + HttpUtil.NGA_ATTACHMENT_HOST
                        + "/attachments$1'><img src='http://"
                        + HttpUtil.NGA_ATTACHMENT_HOST
                        + "/attachments$1' style= 'max-width:100%' ></a>");
        content = content.replaceAll(ignoreCaseTag
                        + "\\[img\\]\\s*(http[^\\[|\\]]+)\\s*\\[/img\\]",
                "<a href='$1'><img src='$1' style= 'max-width:100%' ></a>");

        content = content.replaceAll(ignoreCaseTag
                        + "\\[list\\](.+?)\\[/list\\]",
                "<ul>$1</ul>");
        content = content.replaceAll(ignoreCaseTag
                        + "\\[\\*\\](.+?)<br/>",
                "<li>$1</li>");

        return content;
    }
}
