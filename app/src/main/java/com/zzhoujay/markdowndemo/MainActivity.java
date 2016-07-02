package com.zzhoujay.markdowndemo;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.TextView;

import com.zzhoujay.markdown.parser.MarkDownParser;
import com.zzhoujay.markdown.parser.StyleBuilderImpl;
import com.zzhoujay.markdown.spanneds.TestSpan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String test_str = "# MarkDown\n" +
            "\n" +
            "> Android平台的Markdown解析器\n" +
            "\n" +
            "##### 开发中。。。\n" +
            "\n" +
            "_by zzhoujay_\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = (TextView) findViewById(R.id.textView);
        assert textView != null;
        textView.setMovementMethod(LinkMovementMethod.getInstance());

//        textView.post(new Runnable() {
//            @Override
//            public void run() {
//                SpannableStringBuilder sb = new SpannableStringBuilder();
//                sb.append("hello world zzhoujay");
//                StyleSpan styleSpan = new StyleSpan(Typeface.ITALIC);
//                StyleSpan styleSpan1 = new StyleSpan(Typeface.BOLD);
//                sb.setSpan(styleSpan,0,sb.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                sb.setSpan(styleSpan1,4,10,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                textView.setText(sb);
//            }
//        });


        InputStream stream = getResources().openRawResource(R.raw.test);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        final StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        textView.post(new Runnable() {
            @Override
            public void run() {
                MarkDownParser markDownParser = new MarkDownParser(sb.toString(), new StyleBuilderImpl(textView));
                try {
                    textView.setText(markDownParser.parser());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

//
//        Pattern pattern = Pattern.compile("#\\s+(.*)");
//        String test = "# hello";
//        SpannableStringBuilder builder = new SpannableStringBuilder(test);
//        builder.setSpan(new ForegroundColorSpan(Color.RED), 1, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        Matcher matcher = pattern.matcher(builder);
//        if (matcher.find()) {
//            Log.i("find", matcher.group(1));
//            System.out.println(matcher.group(1));
//        } else {
//            Log.i("find", matcher.group(1));
//            System.out.println("gg");
//        }
    }
}
