package sp.phone.mvp.model.convert.builder;

import sp.phone.bean.ThreadRowInfo;
import sp.phone.util.StringUtils;

/**
 * Created by Justwen on 2018/8/28.
 */
public class HtmlHeaderBuilder {

    public static String build(ThreadRowInfo row, String fgColor) {
        if (StringUtils.isEmpty(row.getSubject()) && !row.getISANONYMOUS()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<h4 style='color:").append(fgColor).append("' >");
        if (!StringUtils.isEmpty(row.getSubject())) {
            sb.append(row.getSubject());
        }
        if (row.getISANONYMOUS()) {
            sb.append("<font style='color:#D00;font-weight: bold;'>")
                    .append("[匿名]")
                    .append("</font>");
        }
        sb.append("</h4>");
        return sb.toString();
    }
}
