package gov.anzong.androidnga.core.decode;

/**
 * Created by Justwen on 2018/8/25.
 */
public class ForumCollapseDecoder implements IForumDecoder {

    @Override
    public String decode(String content) {
        content = content.replaceAll("\\[collapse=(.*?)](.*?)\\[/collapse]", "<div><button onclick='toggleCollapse(this,\"$1\")'>点击显示内容 : $1</button><div name='collapse' style='border:1px solid #888;padding:5px;margin:5px 0px 0px 0px;display:none' >$2</div></div>");
        content = content.replaceAll("\\[collapse](.*?)\\[/collapse]", "<div><button onclick='toggleCollapse(this)'>点击显示内容</button><div name='collapse' style='border:1px solid #888;padding:5px;margin:5px 0px 0px 0px;display:none' >$1</div></div>");
        return content;
    }
}
