package gov.anzong.androidnga.core;

import android.text.TextUtils;

import java.util.List;

import gov.anzong.androidnga.common.util.StringUtils;
import gov.anzong.androidnga.core.corebuild.HtmlBuilder;
import gov.anzong.androidnga.core.data.HtmlData;
import gov.anzong.androidnga.core.decode.ForumDecoder;

public class HtmlConvertFactory {

    private volatile static String sHtmlTemplate;

    private volatile static String sCssTemplate;

    private volatile static String sDarkCssTemplate;

    public static String convert(HtmlData htmlData, List<String> images) {

        init(htmlData.isDarkMode());

        StringBuilder builder = new StringBuilder();

        if (htmlData.isInBackList()) {
            builder.append("<h5>[屏蔽]</h5>");
        } else if (TextUtils.isEmpty(htmlData.getAlertInfo()) && TextUtils.isEmpty(htmlData.getRawData())) {
            builder.append("<h5>[隐藏]</h5>");
        } else {
            if (!TextUtils.isEmpty(htmlData.getSubject())) {
                builder.append(String.format("<div class='title'>%s</div><br>", htmlData.getSubject()));
            }
            String ngaHtml = ForumDecoder.decode(htmlData.getRawData(), htmlData, images);
            if (TextUtils.isEmpty(ngaHtml)) {
                ngaHtml = htmlData.getAlertInfo();
            }
            builder.append(ngaHtml);
            HtmlBuilder.build(builder,htmlData, images);
        }

        String html = builder.toString();
        String cssStr = htmlData.isDarkMode() ? sDarkCssTemplate : sCssTemplate;
        String style = String.format(cssStr, htmlData.getTextSize(), htmlData.getTableTextSize(), htmlData.getEmotionSize());
        return String.format(sHtmlTemplate, style, html);
    }

    private static void init(boolean darkMode) {
        if (sHtmlTemplate == null) {
            sHtmlTemplate = StringUtils.getStringFromAssets("html/html_template.html");
        }
        if (darkMode && sDarkCssTemplate == null) {
            sDarkCssTemplate = StringUtils.getStringFromAssets("html/style_dark.css");
            sCssTemplate = null;
        } else if (!darkMode && sCssTemplate == null) {
            sCssTemplate= StringUtils.getStringFromAssets("html/style.css");
            sDarkCssTemplate = null;
        }
    }

}
