package sp.phone.mvp.model.convert.decoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Justwen on 2018/8/25.
 */
public class ForumAudioDecoder implements IForumDecoder {

    @Override
    public String decode(String content) {
        String regex = "\\[flash=audio](.*?)\\[/flash]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String audioUrl = matcher.group(1).substring(1);
            audioUrl = "<audio src=\"http://img.ngacn.cc/attachments" + audioUrl + "&filename=nga_audio.mp3\" controls=\"controls\"></audio>";
            content = matcher.replaceFirst(audioUrl);
            matcher = pattern.matcher(content);
        }
        return content;
    }
}
