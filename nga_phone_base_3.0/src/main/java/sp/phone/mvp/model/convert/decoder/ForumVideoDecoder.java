package sp.phone.mvp.model.convert.decoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Justwen on 2018/8/25.
 */
public class ForumVideoDecoder implements IForumDecoder {

    @Override
    public String decode(String content) {
        String regex = "\\[flash=video](.*?)\\[/flash]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String url = matcher.group(1).substring(1);
            url = "<video src=\"http://img.ngacn.cc/attachments" + url + "\" controls=\"controls\"></video>";
            content = matcher.replaceFirst(url);
            matcher = pattern.matcher(content);
        }
        return content;
    }
}
