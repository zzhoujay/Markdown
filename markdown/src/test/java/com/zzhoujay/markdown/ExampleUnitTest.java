package com.zzhoujay.markdown;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.zzhoujay.markdown.parser.CharQueue;
import com.zzhoujay.markdown.parser.MarkDownParser;
import com.zzhoujay.markdown.parser.Token;
import com.zzhoujay.markdown.parser.Word;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

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
        Pattern pattern = Pattern.compile("#\\s+(.*)");
        String test = "# hello";
        SpannableStringBuilder builder = new SpannableStringBuilder(test);
        builder.setSpan(new ForegroundColorSpan(Color.RED), 1, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Matcher matcher = pattern.matcher(builder);
        if (matcher.find()) {
            System.out.println(matcher.group(1));
        }
    }
}