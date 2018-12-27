package sp.phone.mvp.model.convert.builder;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

import sp.phone.common.ApplicationContextHolder;

/**
 * Created by Justwen on 2018/8/28.
 */
public class HtmlBlackListBuilder {

    private static String sBlackListHtml;

    public static String build() {
        if (sBlackListHtml == null) {
            AssetManager assetManager = ApplicationContextHolder.getContext().getAssets();
            try (InputStream is = assetManager.open("html/black_list.html")) {
                int length = is.available();
                byte[] buffer = new byte[length];
                is.read(buffer);
                sBlackListHtml = new String(buffer, "utf-8");
            } catch (IOException e) {
                sBlackListHtml = getBlackListHtml();
            }
        }
        return sBlackListHtml;
    }

    private static String getBlackListHtml() {
        return "<HTML> <HEAD><META http-equiv=Content-Type content= \"text/html; charset=utf-8 \">"
                + "<body "
                + "'>"
                + "<font color='red' size='2'>["
                + "屏蔽"
                + "]</font>" + "</font></body>";
    }


}
