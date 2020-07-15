package gov.anzong.androidnga.core;

import org.junit.Test;

import gov.anzong.androidnga.core.data.HtmlData;
import gov.anzong.androidnga.core.decode.ForumBasicDecoder;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testQuote() {
        String testString = "[quote]123[quote]456[/quote]789[/quote]";
        ForumBasicDecoder decoder = new ForumBasicDecoder();
        String result = decoder.decode(testString, createHtmlData());
        result = decoder.decode(result, createHtmlData());
        System.out.println(result);
    }

    private HtmlData createHtmlData() {
        return new HtmlData("");
    }
}