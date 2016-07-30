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
 * Markdown解析器
 */
public class MarkDown {

    /**
     * 解析markdown文本并返回spanned
     *
     * @param source      源文本
     * @param imageGetter 图片获取回调
     * @param textView    textView
     * @return spanned
     */
    public static Spanned fromMarkdown(String source, Html.ImageGetter imageGetter, TextView textView) {
        MarkDownParser parser = new MarkDownParser(source, new StyleBuilderImpl(textView, imageGetter));
        try {
            return parser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析markdown文本并返回spanned
     *
     * @param inputStream 输入流
     * @param imageGetter 图片获取回调
     * @param textView    textView
     * @return spanned
     */
    public static Spanned fromMarkdown(InputStream inputStream, Html.ImageGetter imageGetter, TextView textView) {
        MarkDownParser parser = new MarkDownParser(inputStream, new StyleBuilderImpl(textView, imageGetter));
        try {
            return parser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析markdown文本并返回spanned
     *
     * @param reader      BufferReader
     * @param imageGetter 图片获取回调
     * @param textView    textView
     * @return spanned
     */
    public static Spanned fromMarkdown(BufferedReader reader, Html.ImageGetter imageGetter, TextView textView) {
        MarkDownParser parser = new MarkDownParser(reader, new StyleBuilderImpl(textView, imageGetter));
        try {
            return parser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
