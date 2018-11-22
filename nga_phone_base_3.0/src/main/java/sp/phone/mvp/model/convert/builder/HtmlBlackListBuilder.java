package sp.phone.mvp.model.convert.builder;

/**
 * Created by Justwen on 2018/8/28.
 */
public class HtmlBlackListBuilder {

    public static String build() {
        return "<HTML> <HEAD><META http-equiv=Content-Type content= \"text/html; charset=utf-8 \">"
                + "<body "
                + "'>"
                + "<font color='red' size='2'>["
                + "屏蔽"
                + "]</font>" + "</font></body>";
    }
}
