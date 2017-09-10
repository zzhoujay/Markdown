package com.zzhoujay.markdowndemo;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.zzhoujay.markdown.MarkDown;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String test_str = "# MarkDown\n" +
            "\n" +
            "> Android平台的Markdown解析器\n" +
            "\n" +
            "##### 开发中。。。\n" +
            "\n" +
            "__by zzhoujay__\n";

    private static final String markdown_test = "image: ![image](http://image.tianjimedia.com/uploadImages/2015/129/56/J63MI042Z4P8.jpg)\n[link](https://github.com/zzhoujay/RichText/issues)";

    private static final String empty_string = "  \n   \n  \n# hello";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = (TextView) findViewById(R.id.textView);
        assert textView != null;
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        final InputStream stream = getResources().openRawResource(R.raw.hello);

        textView.post(new Runnable() {
            @Override
            public void run() {
                long time = System.nanoTime();
                Spanned spanned = MarkDown.fromMarkdown(stream, new Html.ImageGetter() {
                    public static final String TAG = "Markdown";

                    @Override
                    public Drawable getDrawable(String source) {
                        Log.d(TAG, "getDrawable() called with: source = [" + source + "]");
                        Drawable drawable = new ColorDrawable(Color.LTGRAY);
                        drawable.setBounds(0, 0, textView.getWidth() - textView.getPaddingLeft() - textView.getPaddingRight(), 400);
                        return drawable;
                    }
                }, textView);
                long useTime = System.nanoTime() - time;
                Toast.makeText(getApplicationContext(), "use time:" + useTime, Toast.LENGTH_LONG).show();
                textView.setText(spanned);
            }
        });

    }
}
