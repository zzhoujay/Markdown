package com.zzhoujay.markdown.parser;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.QuoteSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zzhoujay.markdown.R;
import com.zzhoujay.markdown.style.CodeBlockSpan;
import com.zzhoujay.markdown.style.CodeSpan;
import com.zzhoujay.markdown.style.EmailSpan;
import com.zzhoujay.markdown.style.FontSpan;
import com.zzhoujay.markdown.style.LinkSpan;
import com.zzhoujay.markdown.style.LongPressClickableSpan;
import com.zzhoujay.markdown.style.MarkDownBulletSpan;
import com.zzhoujay.markdown.style.MarkDownQuoteSpan;
import com.zzhoujay.markdown.style.QuotaBulletSpan;
import com.zzhoujay.markdown.style.UnderLineSpan;

import java.lang.ref.WeakReference;

/**
 * Created by zhou on 16-6-28.
 * StyleBuilderImpl
 */
public class StyleBuilderImpl implements StyleBuilder {

    private static final float scale_h1 = 2.25f;
    private static final float scale_h2 = 1.75f;
    private static final float scale_h3 = 1.5f;
    private static final float scale_h4 = 1.25f;
    private static final float scale_h5 = 1, scale_h6 = 1;
    private static final float scale_normal = 1;

    private final int h1_text_color;
    private final int h6_text_color;
    private final int quota_color;
    private final int quota_text_color;
    private final int code_text_color;
    private final int code_background_color;
    private final int link_color;
    private final int h_under_line_color;

    private WeakReference<TextView> textViewWeakReference;
    private Html.ImageGetter imageGetter;

    public StyleBuilderImpl(TextView textView, Html.ImageGetter imageGetter) {
        this.textViewWeakReference = new WeakReference<>(textView);
        this.imageGetter = imageGetter;


        Context context = textView.getContext();
        TypedArray a = context.obtainStyledAttributes(null, R.styleable.MarkdownTheme, R.attr.markdownStyle, 0);
        final boolean failed = !a.hasValue(0);
        if (failed) {
            Log.w("Markdown", "Missing markdownStyle in your theme, using hardcoded color.");

            h1_text_color = 0xdf000000;
            h6_text_color = 0x8a000000;
            quota_color = 0x4037474f;
            quota_text_color = 0x61000000;
            code_text_color = 0xd8000000;
            code_background_color = 0x0c37474f;
            link_color = 0xdc3e7bc9;
            h_under_line_color = 0x1837474f;
        } else {
            h1_text_color = a.getColor(R.styleable.MarkdownTheme_h1TextColor, 0);
            h6_text_color = a.getColor(R.styleable.MarkdownTheme_h6TextColor, 0);
            quota_color = a.getColor(R.styleable.MarkdownTheme_quotaColor, 0);
            quota_text_color = a.getColor(R.styleable.MarkdownTheme_quotaTextColor, 0);
            code_text_color = a.getColor(R.styleable.MarkdownTheme_codeTextColor, 0);
            code_background_color = a.getColor(R.styleable.MarkdownTheme_codeBackgroundColor, 0);
            link_color = a.getColor(R.styleable.MarkdownTheme_linkColor, 0);
            h_under_line_color = a.getColor(R.styleable.MarkdownTheme_underlineColor, 0);
        }

        a.recycle();
    }

    @Override
    public SpannableStringBuilder em(CharSequence charSequence) {
        SpannableStringBuilder builder = SpannableStringBuilder.valueOf(charSequence);
        FontSpan fontSpan = new FontSpan(scale_normal, Typeface.BOLD, h1_text_color);
        builder.setSpan(fontSpan, 0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public SpannableStringBuilder italic(CharSequence charSequence) {
        SpannableStringBuilder builder = SpannableStringBuilder.valueOf(charSequence);
        FontSpan fontSpan = new FontSpan(scale_normal, Typeface.ITALIC, h1_text_color);
        builder.setSpan(fontSpan, 0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public SpannableStringBuilder emItalic(CharSequence charSequence) {
        SpannableStringBuilder builder = SpannableStringBuilder.valueOf(charSequence);
        FontSpan fontSpan = new FontSpan(scale_normal, Typeface.BOLD_ITALIC, h1_text_color);
        builder.setSpan(fontSpan, 0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public SpannableStringBuilder delete(CharSequence charSequence) {
        SpannableStringBuilder builder = SpannableStringBuilder.valueOf(charSequence);
        StrikethroughSpan span = new StrikethroughSpan();
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(h1_text_color);
        builder.setSpan(colorSpan, 0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(span, 0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public SpannableStringBuilder email(CharSequence charSequence) {
        SpannableStringBuilder builder = SpannableStringBuilder.valueOf(charSequence);
        EmailSpan emailSpan = new EmailSpan(charSequence.toString(), link_color);
        builder.setSpan(emailSpan, 0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public SpannableStringBuilder code(CharSequence charSequence) {
        SpannableStringBuilder builder = SpannableStringBuilder.valueOf(charSequence);
        CodeSpan span = new CodeSpan(code_background_color, code_text_color);
        builder.setSpan(span, 0, charSequence.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public SpannableStringBuilder h1(CharSequence charSequence) {
        return hWithUnderLine(charSequence, scale_h1);
    }

    @Override
    public SpannableStringBuilder h2(CharSequence charSequence) {
        return hWithUnderLine(charSequence, scale_h2);
    }

    @Override
    public SpannableStringBuilder h3(CharSequence charSequence) {
        return h(charSequence, scale_h3, h1_text_color);
    }

    @Override
    public SpannableStringBuilder h4(CharSequence charSequence) {
        return h(charSequence, scale_h4, h1_text_color);
    }

    @Override
    public SpannableStringBuilder h5(CharSequence charSequence) {
        return h(charSequence, scale_h5, h1_text_color);
    }

    @Override
    public SpannableStringBuilder h6(CharSequence charSequence) {
        return h(charSequence, scale_h6, h6_text_color);
    }

    @Override
    public SpannableStringBuilder quota(CharSequence charSequence) {
        SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder.valueOf(charSequence);
        QuoteSpan span = new MarkDownQuoteSpan(quota_color);
        spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(quota_text_color), 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @Override
    public SpannableStringBuilder ul(CharSequence charSequence, int level) {
        SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder.valueOf(charSequence);
        BulletSpan bulletSpan = new MarkDownBulletSpan(level, h1_text_color, 0);
        spannableStringBuilder.setSpan(bulletSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @Override
    public SpannableStringBuilder ol(CharSequence charSequence, int level, int index) {
        SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder.valueOf(charSequence);
        BulletSpan bulletSpan = new MarkDownBulletSpan(level, h1_text_color, index);
        spannableStringBuilder.setSpan(bulletSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @Override
    public SpannableStringBuilder ul2(CharSequence charSequence, int quotaLevel, int bulletLevel) {
        SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder.valueOf(charSequence);
        QuotaBulletSpan bulletSpan = new QuotaBulletSpan(quotaLevel, bulletLevel, quota_color, quota_text_color, 0);
        spannableStringBuilder.setSpan(bulletSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(quota_text_color), 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @Override
    public SpannableStringBuilder ol2(CharSequence charSequence, int quotaLevel, int bulletLevel, int index) {
        SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder.valueOf(charSequence);
        QuotaBulletSpan bulletSpan = new QuotaBulletSpan(quotaLevel, bulletLevel, quota_color, quota_text_color, index);
        spannableStringBuilder.setSpan(bulletSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(quota_text_color), 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @Override
    public SpannableStringBuilder codeBlock(CharSequence... charSequence) {
        SpannableStringBuilder builder = SpannableStringBuilder.valueOf("$");
        CodeBlockSpan codeBlockSpan = new CodeBlockSpan(getTextViewRealWidth(), code_background_color, code_text_color, charSequence);
        builder.setSpan(codeBlockSpan, 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public SpannableStringBuilder codeBlock(String code) {
        return codeBlock((CharSequence[]) code.split("\n"));
    }

    @Override
    public SpannableStringBuilder link(CharSequence title, String link, String hint) {
        SpannableStringBuilder builder = SpannableStringBuilder.valueOf(title);
        LinkSpan linkSpan = new LinkSpan(link, link_color);
        builder.setSpan(linkSpan, 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public SpannableStringBuilder image(CharSequence title, String url, final String hint) {
        if (title == null || title.length() == 0) {
            title = "$";
        }
        SpannableStringBuilder builder = SpannableStringBuilder.valueOf(title);
        Drawable drawable = null;
        if (imageGetter != null) {
            drawable = imageGetter.getDrawable(url);
        }
        if (drawable == null) {
            drawable = new ColorDrawable(Color.TRANSPARENT);
//            builder.delete(0, builder.length());
//            return builder;
        }
        ImageSpan imageSpan = new ImageSpan(drawable, url);
        builder.setSpan(imageSpan, 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (!TextUtils.isEmpty(hint)) {
            builder.setSpan(new LongPressClickableSpan() {
                @Override
                public void onLongPress(View view) {
                    Toast.makeText(view.getContext(), hint, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onClick(View view) {
                }
            }, 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return builder;
    }

    @SuppressWarnings("WeakerAccess")
    protected SpannableStringBuilder h(CharSequence charSequence, float s, int color) {
        SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder.valueOf(charSequence);
        FontSpan fontSpan = new FontSpan(s, Typeface.BOLD, color);
        spannableStringBuilder.setSpan(fontSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    private SpannableStringBuilder hWithUnderLine(CharSequence charSequence, float s) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        int start = 0;
        FontSpan fontSpan = new FontSpan(s, Typeface.BOLD, h1_text_color);
        spannableStringBuilder.setSpan(fontSpan, 0, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Drawable underLine = new ColorDrawable(h_under_line_color);
        UnderLineSpan underLineSpan = new UnderLineSpan(underLine, getTextViewRealWidth(), 5);
        spannableStringBuilder.append('\n');
        start += charSequence.length() + 1;
        spannableStringBuilder.append("$");
        spannableStringBuilder.setSpan(underLineSpan, start, spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    private int getTextViewRealWidth() {
        TextView textView = textViewWeakReference.get();
        if (textView != null) {
            return textView.getWidth() - textView.getPaddingRight() - textView.getPaddingLeft();
        }
        return 0;
    }

    @Override
    public SpannableStringBuilder gap() {
        SpannableStringBuilder builder = new SpannableStringBuilder("$");
        Drawable underLine = new ColorDrawable(h_under_line_color);
        UnderLineSpan underLineSpan = new UnderLineSpan(underLine, getTextViewRealWidth(), 10);
        builder.setSpan(underLineSpan, 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }
}
