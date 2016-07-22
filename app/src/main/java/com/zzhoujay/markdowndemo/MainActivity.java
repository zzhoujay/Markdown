package com.zzhoujay.markdowndemo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.zzhoujay.markdown.MarkDown;

import java.io.InputStream;

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


        final InputStream stream = getResources().openRawResource(R.raw.tt);
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
//        final StringBuilder sb = new StringBuilder();
//        String line;
//        try {
//            while ((line = bufferedReader.readLine()) != null) {
//                sb.append(line).append('\n');
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        textView.post(new Runnable() {
            @Override
            public void run() {
                Spanned spanned = MarkDown.fromMarkdown(stream, new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
                        drawable.setBounds(0, 0, 400, 400);
                        return drawable;
                    }
                }, textView);
                textView.setText(spanned);
//                MarkDownParser markDownParser = new MarkDownParser(stream, new StyleBuilderImpl(textView, new Html.ImageGetter() {
//                    @Override
//                    public Drawable getDrawable(String source) {
//                        Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
//                        drawable.setBounds(0,0,400,400);
//                        return drawable;
//                    }
//                }));
//                try {
//                    textView.setText(markDownParser.parser());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

            }
        });

//
//        Pattern pattern = Pattern.compile("#\\s+(.*)");
//        String test = "# hello";
//        SpannableStringBuilder builder = new SpannableStringBuilder(test);
//        style.setSpan(new ForegroundColorSpan(Color.RED), 1, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        Matcher matcher = pattern.matcher(style);
//        if (matcher.find()) {
//            Log.i("find", matcher.group(1));
//            System.out.println(matcher.group(1));
//        } else {
//            Log.i("find", matcher.group(1));
//            System.out.println("gg");
//        }
    }
}
