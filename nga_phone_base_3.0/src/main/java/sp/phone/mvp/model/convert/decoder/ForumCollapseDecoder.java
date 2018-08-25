package sp.phone.mvp.model.convert.decoder;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Justwen on 2018/8/25.
 */
public class ForumCollapseDecoder implements IForumDecoder {

    private static final String REGEX_COLLAPSE = "\\[collapse(.*?)](.*?)\\[/collapse]";

    private static final String HTML_COLLAPSE = "<div id=collapse%s style='border:1px solid #888;padding:5px;margin:5px 0px 0px 0px;display:none' >%s</div>";

    private static final String HTML_COLLAPSE_BUTTON = "<button id=collapseBtn%s onclick='toggleCollapse(%s)'>点击显示内容</button>%s";

    private static final String HTML_COLLAPSE_BUTTON_TITLE = "<button id=collapseBtn%s onclick='toggleCollapse(%s,\"%s\")'>点击显示内容 : %s</button>%s";

    @Override
    public String decode(String content) {
        Pattern pattern = Pattern.compile(REGEX_COLLAPSE);
        Matcher matcher = pattern.matcher(content);
        int index = 0;
        while (matcher.find()) {
            String title = matcher.group(1);
            content = matcher.group(2);
            content = String.format(HTML_COLLAPSE, index, content);
            if (TextUtils.isEmpty(title)) {
                content = String.format(HTML_COLLAPSE_BUTTON, index, index, content);
            } else {
                title = title.substring(1);
                content = String.format(HTML_COLLAPSE_BUTTON_TITLE, index, index, title, title, content);
            }
            content = matcher.replaceFirst(content);
            matcher = pattern.matcher(content);
            index++;
        }
        return content;
    }
}
