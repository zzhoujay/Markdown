package com.zzhoujay.markdowndemo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Pattern pattern = Pattern.compile("#\\s+(.*)");
        String test = "# hello";
        SpannableStringBuilder builder = new SpannableStringBuilder(test);
        builder.setSpan(new ForegroundColorSpan(Color.RED), 1, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Matcher matcher = pattern.matcher(builder);
        if (matcher.find()) {
            Log.i("find", matcher.group(1));
            System.out.println(matcher.group(1));
        } else {
            Log.i("find", matcher.group(1));
            System.out.println("gg");
        }
    }
}
