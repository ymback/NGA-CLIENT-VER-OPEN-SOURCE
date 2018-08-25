package sp.phone.mvp.model.convert.decoder;

import sp.phone.theme.ThemeManager;

/**
 * Created by Justwen on 2018/8/25.
 */
public class ForumAlbumDecoder implements IForumDecoder {

    @Override
    public String decode(String content) {
        String quoteStyle = "<div style='background:#E8E8E8;padding:5px;border:1px solid #888' >";
        if (ThemeManager.getInstance().isNightMode())
            quoteStyle = "<div style='background:#000000;padding:5px;border:1px solid #888' >";
        int startpos = content.indexOf("[album=");
        int endpos = content.indexOf("[/album]") + 8;
        String sup = "", sdown = "", salbum = "", stemp = "", stitle = "";
        while (startpos < endpos && startpos >= 0) {
            sup = content.substring(0, startpos);
            if (endpos >= 0)
                sdown = content.substring(endpos, content.length());
            salbum = content.substring(startpos, endpos);
            stitle = salbum.replaceAll("(?i)" + "\\[album=(.*?)\\](.*?)\\[/album\\]", "$1");
            stemp = salbum.replaceAll("(?i)" + "\\[album=(.*?)\\](.*?)\\[/album\\]", "$2");
            if (stemp.startsWith("<br/>")) {
                stemp = "[img]" + stemp.substring(5) + "[/img]";
            }
            stitle = "相册列表:" + stitle + "<br/>";
            stemp = stemp.replaceAll("<br/>", "[/img]<br/><br/>[img]");
            stemp = "<br/>" + quoteStyle + stitle + "<br/>" + stemp + "</div>";
            content = sup + stemp + sdown;
            startpos = content.indexOf("[album=");
            endpos = content.indexOf("[/album]") + 8;
        }
        return content;
    }
}
