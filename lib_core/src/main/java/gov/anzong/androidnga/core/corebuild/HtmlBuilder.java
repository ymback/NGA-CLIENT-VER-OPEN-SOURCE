package gov.anzong.androidnga.core.corebuild;

import java.util.ArrayList;
import java.util.List;

import gov.anzong.androidnga.core.data.HtmlData;

/**
 * Created by Justwen on 2018/8/28.
 */
public class HtmlBuilder {

    private static List<IHtmlBuild> sHtmlBuilders = new ArrayList<>();

    static {
        sHtmlBuilders.add(new HtmlCommentBuilder());
        sHtmlBuilders.add(new HtmlAttachmentBuilder());
        sHtmlBuilders.add(new HtmlSignatureBuilder());
        sHtmlBuilders.add(new HtmlVoteBuilder());
    }

    public static void build(StringBuilder builder, HtmlData htmlData,  List<String> images) {
        for (IHtmlBuild build : sHtmlBuilders) {
            builder.append(build.build(htmlData, images));
        }
    }

}
