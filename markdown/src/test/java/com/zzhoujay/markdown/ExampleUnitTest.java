package com.zzhoujay.markdown;

import org.junit.Test;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    private static final String testString = "# MarkDown\n" +
            "\n" +
            "> Android平台的Markdown解析器\n" +
            "\n" +
            "\n" +
            "```````````````````````\n" +
            "ASDFSDAFASDF\n" +
            "```````````````````````\n" +
            "\n" +
            "1. 111111\n" +
            "2. 222222\n" +
            "3. 333333\n" +
            "\n" +
            "* asdfasdf\n" +
            "* dfsgsdfa\n" +
            "\n" +
            "hello`gg`world\n" +
            "\n" +
            "__by zzhoujay__\n" +
            "\n";

    @Test
    public void addition_isCorrect() throws Exception {

    }

    @Test
    public void test() throws IOException {
        Pattern pattern = Pattern.compile("[( {4})\\t](.*)");
        String test = "# hello";
        Matcher matcher = pattern.matcher(test);
        if (matcher.find()){

        }
    }
}