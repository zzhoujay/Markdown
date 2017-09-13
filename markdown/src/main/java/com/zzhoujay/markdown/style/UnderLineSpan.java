package com.zzhoujay.markdown.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.LineHeightSpan;
import android.text.style.ReplacementSpan;

/**
 * Created by zhou on 16-7-2.
 */
public class UnderLineSpan extends ReplacementSpan implements LineHeightSpan {

    private static final int PADDING = 12;

    private int mHeight;
    private int mWidth;
    private Drawable mDrawable;

    public UnderLineSpan(Drawable drawable, int width, int height) {
        this.mHeight = height;
        this.mWidth = width;
        this.mDrawable = drawable;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return (int) paint.measureText(text, start, end);
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        mDrawable.setBounds((int) x, top + PADDING, (int) x + mWidth, top + mHeight + PADDING);
        mDrawable.draw(canvas);
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
        fm.top /= 3;
        fm.ascent /= 3;
        fm.bottom /= 3;
        fm.descent /= 3;
    }
}
