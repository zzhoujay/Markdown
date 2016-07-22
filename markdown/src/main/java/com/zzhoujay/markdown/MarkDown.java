package com.zzhoujay.markdown;

import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import com.zzhoujay.markdown.parser.StyleBuilderImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhou on 16-6-25.
 */
public class MarkDown {

    public static Spanned fromMarkdown(String source, Html.ImageGetter imageGetter, TextView textView) {
        MarkDownParser parser = new MarkDownParser(source, new StyleBuilderImpl(textView, imageGetter));
        try {
            return parser.parser();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Spanned fromMarkdown(InputStream inputStream, Html.ImageGetter imageGetter, TextView textView) {
        MarkDownParser parser = new MarkDownParser(inputStream, new StyleBuilderImpl(textView, imageGetter));
        try {
            return parser.parser();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Spanned fromMarkdown(BufferedReader reader, Html.ImageGetter imageGetter, TextView textView) {
        MarkDownParser parser = new MarkDownParser(reader, new StyleBuilderImpl(textView, imageGetter));
        try {
            return parser.parser();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
