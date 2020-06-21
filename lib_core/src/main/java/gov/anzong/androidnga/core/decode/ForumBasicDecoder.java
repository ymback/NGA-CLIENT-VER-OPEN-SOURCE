package gov.anzong.androidnga.core.decode;

import android.text.TextUtils;

import gov.anzong.androidnga.base.util.StringUtils;
import gov.anzong.androidnga.common.util.LogUtils;
import gov.anzong.androidnga.core.data.HtmlData;

/**
 * Created by Justwen on 2018/8/25.
 */
public class ForumBasicDecoder implements IForumDecoder {

    private static final String lesserNukeStyle = "<div style='border:1px solid #B63F32;margin:10px 10px 10px 10px;padding:10px' > <span style='color:#EE8A9E'>用户因此贴被暂时禁言，此效果不会累加</span><br/>";
    private static final String styleColor = "<span style='color:$1' >";
    private static final String endDiv = "</div>";

    private static final String STYLE_QUOTE = "<div class='quote' >";

    @Override
    public String decode(String content, HtmlData htmlData) {
        LogUtils.computeCost("ForumBasicDecoder");
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        // s = StringUtils.unEscapeHtml(s);

        String quoteStyle = STYLE_QUOTE;

        content = StringUtils.replaceAll(content, ignoreCaseTag + "&amp;", "&");

        // [l][/l] [r][/r]
        content = StringUtils.replaceAll(content, "\\[l\\](.*?)\\[/l\\]", "<div style='float:left'>$1</div>");
        content = StringUtils.replaceAll(content, "\\[r\\](.*?)\\[/r\\]", "<div style='float:right'>$1</div>");

        // [align=left(right,center)][/align]
        content = StringUtils.replaceAll(content, "\\[align=(.*?)\\](.*?)\\[/align\\]", "<div style='text-align:$1'>$2</div>");

        content = StringUtils.replaceAll(content,
                ignoreCaseTag
                        + "\\[b\\]Reply to \\[pid=(.+?),(.+?),(.+?)\\]Reply\\[/pid\\] (.+?)\\[/b\\]",
                "[quote]Reply to [b]<a href='" + htmlData.getNGAHost() + "read.php?searchpost=1&pid=$1' style='font-weight: bold;color:#3181f4'>[Reply]</a> $4[/b][/quote]");

        content = StringUtils.replaceAll(content,
                ignoreCaseTag + "\\[pid=(.+?),(.+?),(.+?)\\]Reply\\[/pid\\]",
                "<a href='" + htmlData.getNGAHost() + "read.php?searchpost=1&pid=$1' style='font-weight: bold;color:#3181f4'>[Reply]</a>");

        // 某些帖子会导致这个方法卡住, 暂时不清楚原因, 和这个方法的作用.... by elrond
        /*content = StringUtils.replaceAll(content, 
                ignoreCaseTag + "={3,}((^=){0,}(.*?){0,}(^=){0,})={3,}",
                "<h4 style='font-weight: bold;border-bottom: 1px solid #AAA;clear: both;margin-bottom: 0px;'>$1</h4>");*/

        // [quote][/quote]
        content = StringUtils.replaceAll(content, "\\[quote\\](.*?)\\[/quote\\]", "<div class='quote' >$1</div>");

        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[code\\]", quoteStyle + "Code:");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[code(.+?)\\]", quoteStyle);
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[/code\\]", endDiv);
        // reply
        // content = StringUtils.replaceAll(content, 
        // ignoreCaseTag +"\\[pid=\\d+\\]Reply\\[/pid\\]", "Reply");
        // content = StringUtils.replaceAll(content, 
        // ignoreCaseTag +"\\[pid=\\d+,\\d+,\\d\\]Reply\\[/pid\\]", "Reply");

        // topic
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[tid=\\d+\\]Topic\\[/pid\\]",
                "Topic");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[tid=?(\\d{0,50})\\]Topic\\[/tid\\]",
                "<a href='" + htmlData.getNGAHost() + "read.php?tid=$1' style='font-weight: bold;color:#3181f4'>[Topic]</a>");
        // reply
        // s =
        // s.replaceAll("\\[b\\]Reply to \\[pid=\\d+\\]Reply\\[/pid\\] (Post by .+ \\(\\d{4,4}-\\d\\d-\\d\\d \\d\\d:\\d\\d\\))\\[/b\\]"
        // , "Reply to Reply <b>$1</b>");
        // 转换 tag

        content = StringUtils.replaceAll(content, "\\[b\\](.*?)\\[/b\\]", "<b>$1</b>");
        content = StringUtils.replaceAll(content, "\\[item\\](.*?)\\[/item\\]", "<b>$1</b>");
        content = StringUtils.replaceAll(content, "\\[u\\](.*?)\\[/u\\]", "<u>$1</u>");

        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[s:(\\d+)\\]",
                "<img src='file:///android_asset/a$1.gif'>");
        content = content.replace(ignoreCaseTag + "<br/><br/>", "<br/>");
        // [url][/url]
        content = StringUtils.replaceAll(content,
                ignoreCaseTag + "\\[url\\]/([^\\[|\\]]+)\\[/url\\]",
                "<a href=\"" + htmlData.getNGAHost() + "$1\" style='color:#3181f4'>" + htmlData.getNGAHost() + "$1</a>");
        content = StringUtils.replaceAll(content,
                ignoreCaseTag + "\\[url\\]([^\\[|\\]]+)\\[/url\\]",
                "<a href=\"$1\" style='color:#3181f4'>$1</a>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[url=/([^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/url\\]",
                "<a href=\"" + htmlData.getNGAHost() + "$1\" style='color:#3181f4'>$2</a>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[url=([^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/url\\]",
                "<a href=\"$1\">$2</a>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                + "\\[uid=?(\\d{0,50})\\](.+?)\\[\\/uid\\]", "$2");
        content = StringUtils.replaceAll(content,
                ignoreCaseTag + "Post by\\s{0,}([^\\[\\s]{1,})\\s{0,}\\(",
                "Post by <a href='" + htmlData.getNGAHost() + "nuke.php?func=ucp&username=$1' style='font-weight: bold;color:#3181f4'>[$1]</a> (");
        content = StringUtils.replaceAll(content,
                ignoreCaseTag + "\\[@(.{2,20}?)\\]",
                "<a href='" + htmlData.getNGAHost() + "nuke.php?func=ucp&username=$1' style='font-weight: bold;color:#3181f4'>[@$1]</a>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                + "\\[uid=-?(\\d{0,50})\\](.+?)\\[\\/uid\\]", "$2");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[hip\\](.+?)\\[\\/hip\\]",
                "$1");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[tid=?(\\d{0,50})\\](.+?)\\[/tid\\]",
                "<a href='" + htmlData.getNGAHost() + "read.php?tid=$1' style='font-weight: bold;color:#3181f4'>[$2]</a>");
        content = StringUtils.replaceAll(content,
                ignoreCaseTag
                        + "\\[pid=(.+?)\\]\\[/pid\\]",
                "<a href='" + htmlData.getNGAHost() + "read.php?pid=$1' style='font-weight: bold;color:#3181f4'>[Reply]</a>");
        content = StringUtils.replaceAll(content,
                ignoreCaseTag
                        + "\\[pid=(.+?)\\](.+?)\\[/pid\\]",
                "<a href='" + htmlData.getNGAHost() + "read.php?pid=$1' style='font-weight: bold;color:#3181f4'>[$2]</a>");
        // flash
        content = StringUtils.replaceAll(content,
                ignoreCaseTag + "\\[flash\\](http[^\\[|\\]]+)\\[/flash\\]",
                "<a href=\"$1\"><img src='file:///android_asset/flash.png' style= 'max-width:100%;' ></a>");
        // color

        // content = StringUtils.replaceAll(content, "\\[color=([^\\[|\\]]+)\\]\\s*(.+?)\\s*\\[/color\\]"
        // ,"<b style=\"color:$1\">$2</b>");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[color=([^\\[|\\]]+)\\]",
                styleColor);
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[/color\\]", "</span>");

        // lessernuke
        content = StringUtils.replaceAll(content, "\\[lessernuke\\]", lesserNukeStyle);
        content = StringUtils.replaceAll(content, "\\[/lessernuke\\]", endDiv);

        // [table][/table]
        content = StringUtils.replaceAll(content, "\\[table](.*?)\\[/table]", "<div><table cellspacing='0px' class='default'><tbody>$1</tbody></table></div>");

        // [tr][/tr]
        content = StringUtils.replaceAll(content, "\\[tr](.*?)\\[/tr]", "<tr>$1</tr>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td[ ]*(\\d+)\\]",
                "<td style='border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\scolspan(\\d+)\\swidth(\\d+)\\]",
                "<td colspan='$1' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\scolspan(\\d+)\\]",
                "<td colspan='$2' style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");

        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\srowspan(\\d+)\\]",
                "<td rowspan='$2' style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\srowspan(\\d+)\\swidth(\\d+)\\]",
                "<td rowspan='$1' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");

        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\scolspan(\\d+)\\srowspan(\\d+)\\swidth(\\d+)\\]",
                "<td colspan='$1' rowspan='$2' style='width:$3%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\scolspan(\\d+)\\swidth(\\d+)\\srowspan(\\d+)\\]",
                "<td colspan='$1' rowspan='$3' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\srowspan(\\d+)\\scolspan(\\d+)\\swidth(\\d+)\\]",
                "<td rowspan='$1' colspan='$2' style='width:$3%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\srowspan(\\d+)\\swidth(\\d+)\\scolspan(\\d+)\\]",
                "<td rowspan='$1' colspan='$3' style='width:$2%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\scolspan(\\d+)\\srowspan(\\d+)\\]",
                "<td rowspan='$3' colspan='$2' style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\swidth(\\d+)\\srowspan(\\d+)\\scolspan(\\d+)\\]",
                "<td rowspan='$2' colspan='$3'  style='width:$1%;border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");


        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\scolspan=(\\d+)\\]",
                "<td colspan='$1' style='border-left:1px solid #aaa;border-bottom:1px solid #aaa'>");
        content = StringUtils.replaceAll(content, ignoreCaseTag
                        + "\\[td\\srowspan=(\\d+)\\]",
                "<td rowspan='$1' style='border-left:1px solid #aaa;border-bottom:1px solid #aaa;'>");
        content = StringUtils.replaceAll(content, "\\[td\\]", "<td style='border-left:1px solid #aaa;border-bottom:1px solid #aaa;'>");
        content = StringUtils.replaceAll(content, "\\[/td\\]", "</td>");
        // 处理表格外面的额外空行
        content = StringUtils.replaceAll(content, "<([/]?(table|tbody|tr|td))><br/>", "<$1>");

        // [i][/i]
        content = StringUtils.replaceAll(content, "\\[i\\](.*?)\\[/i\\]", "<i style='font-style:italic'>$1</i>");

        // [del][/del]
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[del\\]", "<del class=\"gray\">");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[/del\\]", "</del>");

        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[font=([^\\[|\\]]+)\\]",
                "<span style=\"font-family:$1\">");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[/font\\]", "</span>");

        // size
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[size=(\\d+)%\\]",
                "<span style=\"font-size:$1%;line-height:$1%\">");
        content = StringUtils.replaceAll(content, ignoreCaseTag + "\\[/size\\]", "</span>");

        // [list][/list]
        // TODO: 2018/9/18  部分页面里和 collapse 标签有冲突 http://bbs.nga.cn/read.php?tid=14949699
        content = StringUtils.replaceAll(content, IGNORE_CASE_TAG + "\\[list\\](.+?)\\[/list\\]", "<ul>$1</ul>");
        content = StringUtils.replaceAll(content, IGNORE_CASE_TAG + "\\[list\\]", "");
        content = StringUtils.replaceAll(content, IGNORE_CASE_TAG + "\\[/list\\]", "");
        content = StringUtils.replaceAll(content, IGNORE_CASE_TAG + "\\[\\*\\](.+?)<br/>", "<li>$1</li>");

        // [h][/h]
        content = StringUtils.replaceAll(content, IGNORE_CASE_TAG + "\\[h](.*?)\\[/h]", "<b>$1</b>");

        // [collapse][/collapse]
        content = StringUtils.replaceAll(content, "\\[collapse=(.*?)](.*?)\\[/collapse]", "<div><button onclick='toggleCollapse(this,\"$1\")'>点击显示内容 : $1</button><div name='collapse' class='collapse' style='display:none'>$2</div></div>");
        content = StringUtils.replaceAll(content, "\\[collapse](.*?)\\[/collapse]", "<div><button onclick='toggleCollapse(this)'>点击显示内容</button><div name='collapse' class='collapse'style='display:none' >$1</div></div>");

        // [flash=video]/flash]
        content = StringUtils.replaceAll(content, "\\[flash=video].(.*?)\\[/flash]", "<video src='http://img.ngacn.cc/attachments$1' controls='controls'></video>");

        // [flash=audio][/flash]"
        content = StringUtils.replaceAll(content, "\\[flash=audio].(.*?)\\[/flash]", "<audio src='http://img.ngacn.cc/attachments$1&filename=nga_audio.mp3' controls='controls'></audio>");

        LogUtils.computeCost("ForumBasicDecoder");
        return content;
    }
}
