package com.zzhoujay.markdown.spanneds;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.style.LineHeightSpan;
import android.text.style.ReplacementSpan;

/**
 * Created by zhou on 16-7-2.
 */
public class TestSpan extends ReplacementSpan implements LineHeightSpan {

    private int width;

    public TestSpan(int width) {
        this.width = width;
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
        fm.descent /= 2;
        fm.bottom /= 2;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return (int) paint.measureText(text, start, end);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        Drawable drawable = new ColorDrawable(Color.GRAY);
        drawable.setBounds((int) x, (top + bottom) / 2 - 5, (int) x + width, (bottom + top) / 2);
        drawable.draw(canvas);
    }
}
