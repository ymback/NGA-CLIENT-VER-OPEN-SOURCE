package gov.anzong.androidnga.core.corebuild;

import android.text.TextUtils;

import gov.anzong.androidnga.core.data.HtmlData;

/**
 * Created by Justwen on 2018/8/28.
 */
public class HtmlVoteBuilder implements IHtmlBuild {

    @Override
    public String build(HtmlData htmlData) {
        return TextUtils.isEmpty(htmlData.getVote()) ? "" : "<br/><hr/>本楼有投票/投注内容,长按本楼在菜单中点击投票/投注按钮";
    }
}
