package gov.anzong.androidnga.core.corebuild;

import java.util.List;

import gov.anzong.androidnga.core.data.HtmlData;

public interface IHtmlBuild {

    default CharSequence build(HtmlData htmlData) {
        return "";
    }

    default CharSequence build(HtmlData htmlData, List<String> images) {
        return build(htmlData);
    }
}
