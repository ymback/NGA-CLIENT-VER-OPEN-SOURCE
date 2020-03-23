package gov.anzong.androidnga.core.corebuild;

import java.util.List;

import gov.anzong.androidnga.core.data.HtmlData;

public interface IHtmlBuild {

    default String build(HtmlData htmlData) {
        return "";
    }

    default String build(HtmlData htmlData, List<String> images) {
        return build(htmlData);
    }
}
