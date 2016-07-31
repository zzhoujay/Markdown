package com.zzhoujay.markdowndemo;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.zzhoujay.markdown.MarkDown;
import com.zzhoujay.markdown.style.QuotaBulletSpan;

import java.io.InputStream;

import android.widget.*;

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

        final InputStream stream = getResources().openRawResource(R.raw.tt);

        textView.post(new Runnable() {
            @Override
            public void run() {
                long time = System.nanoTime();
                Spanned spanned = MarkDown.fromMarkdown(stream, null, textView);
                long useTime = System.nanoTime() - time;
                Toast.makeText(getApplicationContext(), "use time:" + useTime, Toast.LENGTH_LONG).show();
                textView.setText(spanned);
            }
        });

//        SpannableString spannableString = new SpannableString("this a test text");
//        QuotaBulletSpan quotaBulletSpan = new QuotaBulletSpan(2, 0, Color.LTGRAY, Color.DKGRAY, 1, textView);
//        spannableString.setSpan(quotaBulletSpan,0,spannableString.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        textView.setText(spannableString);
    }
}
