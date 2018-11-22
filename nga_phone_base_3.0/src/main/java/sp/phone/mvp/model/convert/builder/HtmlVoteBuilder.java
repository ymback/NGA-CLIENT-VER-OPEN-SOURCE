package sp.phone.mvp.model.convert.builder;

import sp.phone.bean.ThreadRowInfo;
import sp.phone.util.StringUtils;

/**
 * Created by Justwen on 2018/8/28.
 */
public class HtmlVoteBuilder {

    public static String build(ThreadRowInfo row) {
        if (StringUtils.isEmpty(row.getVote())) {
            return "";
        } else {
            return "<br/><hr/>" + "本楼有投票/投注内容,长按本楼在菜单中点击投票/投注按钮";
        }
    }
}
