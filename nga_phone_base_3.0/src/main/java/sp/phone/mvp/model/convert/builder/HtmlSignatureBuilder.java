package sp.phone.mvp.model.convert.builder;

import android.text.TextUtils;

import sp.phone.bean.ThreadRowInfo;
import sp.phone.common.PhoneConfiguration;
import sp.phone.mvp.model.convert.decoder.ForumDecoder;

/**
 * Created by Justwen on 2018/8/28.
 */
public class HtmlSignatureBuilder {

    private static final String HTML_VOTE = "<br/></br>签名<hr/><br/>%s";

    public static String build(ThreadRowInfo row) {
        if (TextUtils.isEmpty(row.getSignature())
                || !PhoneConfiguration.getInstance().isShowSignature()) {
            return "";
        } else {
            return String.format(HTML_VOTE, new ForumDecoder(true).decode(row.getSignature()));
        }
    }
}
