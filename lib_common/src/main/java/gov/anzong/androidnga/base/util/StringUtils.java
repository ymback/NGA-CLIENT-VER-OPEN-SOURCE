package gov.anzong.androidnga.base.util;

import com.google.common.base.Strings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.RequestBody;
import okio.Buffer;

/**
 * @author Justwen
 */
public class StringUtils {

    private static Map<String, Pattern> sPatternMap = new HashMap<>();

    public static String replaceAll(String content, String regex, String replacement) {
        return getPattern(regex).matcher(content).replaceAll(replacement);
    }

    public static Pattern getPattern(String regex) {
        Pattern pattern = sPatternMap.get(regex);
        if (pattern == null) {
            pattern = Pattern.compile(regex);
            sPatternMap.put(regex, pattern);
        }
        return pattern;
    }
    public static String requestBody2String(final RequestBody request) {
        try {
            final Buffer buffer = new Buffer();
            if (request != null) {
                request.writeTo(buffer);
                return buffer.readUtf8();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static boolean isEmpty(String content) {
        return Strings.isNullOrEmpty(content);
    }

}
