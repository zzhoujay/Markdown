package com.zzhoujay.markdown.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.style.ReplacementSpan;

/**
 * Created by zhou on 16-7-2.
 * 代码Span
 */
public class CodeSpan extends ReplacementSpan {

    private static final float RADIUS = 10;
    private static final float PADDING_HORIZONTAL = 16;
    private static final float PADDING_VERTICAL = 2;
    private static final float MARGIN = 8;
    private static final float TEXT_SIZE_SCALE = 0.92f;

    private Drawable mBackground;
    private int mWidth;
    private int mHeight;
    private int mTextColor;

    public CodeSpan(int backgroundColor, int textColor) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(backgroundColor);
        d.setCornerRadius(RADIUS);
        mBackground = d;

        mTextColor = textColor;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        mHeight = paint.getFontMetricsInt().descent - paint.getFontMetricsInt().ascent;

        float size = paint.getTextSize();
        paint.setTextSize(size * TEXT_SIZE_SCALE);
        paint.setTypeface(Typeface.MONOSPACE);

        mWidth = (int) (paint.measureText(text, start, end) + PADDING_HORIZONTAL * 2 + MARGIN * 2);
        if (fm != null) {
            fm.top -= PADDING_VERTICAL;
            fm.bottom += PADDING_VERTICAL;
        }

        paint.setTextSize(size);
        return mWidth;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        float size = paint.getTextSize();
        paint.setTextSize(size * TEXT_SIZE_SCALE);
        paint.setTypeface(Typeface.MONOSPACE);

        mBackground.setBounds((int) (x + MARGIN), (int) (top - PADDING_VERTICAL), (int) (x + mWidth - MARGIN), (int) (top + mHeight + PADDING_VERTICAL));
        mBackground.draw(canvas);

        int color = paint.getColor();
        paint.setColor(mTextColor);
        canvas.drawText(text, start, end, x + MARGIN + PADDING_HORIZONTAL, y - mHeight * (1 - TEXT_SIZE_SCALE) * 0.5f, paint);
        paint.setColor(color);

        paint.setTextSize(size);
    }
}
