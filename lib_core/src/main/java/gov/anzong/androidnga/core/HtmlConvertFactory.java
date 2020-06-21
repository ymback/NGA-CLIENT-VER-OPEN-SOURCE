package gov.anzong.androidnga.core;

import android.text.TextUtils;

import java.util.List;

import gov.anzong.androidnga.common.util.FileUtils;
import gov.anzong.androidnga.core.corebuild.HtmlBuilder;
import gov.anzong.androidnga.core.data.HtmlData;
import gov.anzong.androidnga.core.decode.ForumDecoder;

public class HtmlConvertFactory {

    private volatile static String sHtmlTemplate;

    static {
        sHtmlTemplate = FileUtils.readAssetToString("html/html_template.html");
    }

    public static String convert(HtmlData htmlData, List<String> images) {

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
            HtmlBuilder.build(builder, htmlData, images);
        }

        String html = builder.toString();
        String style = htmlData.isDarkMode() ? "style_dark.css" : "style_light.css";
        return String.format(sHtmlTemplate, style, html);
    }


}
