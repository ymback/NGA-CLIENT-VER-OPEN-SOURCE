package sp.phone.mvp.model.convert;

import sp.phone.bean.ThreadData;
import sp.phone.util.ArticleUtil;
import sp.phone.bean.ThreadData;
import sp.phone.util.ArticleUtil;
import sp.phone.common.ApplicationContextHolder;

/**
 * Created by Justwen on 2017/12/3.
 */

public class ArticleConvertFactory {

    public static ThreadData getArticleInfo(String js) {
        if (js.isEmpty()) {
            return null;
        }
        if (js.indexOf("/*error fill content") > 0)
            js = js.substring(0, js.indexOf("/*error fill content"));
        js = js.replaceAll("/\\*\\$js\\$\\*/", "");
        ThreadData result = new ArticleUtil(ApplicationContextHolder.getContext()).parseJsonThreadPage(js);
        return result;
    }
}
