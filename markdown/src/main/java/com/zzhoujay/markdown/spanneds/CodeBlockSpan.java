package com.zzhoujay.markdown.spanneds;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.style.ReplacementSpan;

/**
 * Created by zhou on 16-7-2.
 */
public class CodeBlockSpan extends ReplacementSpan {

    public static final int FLAG_START = -1;
    public static final int FLAG_CENTER = 0;
    public static final int FLAG_END = 1;

    private static final float radius = 5;

    private static final float[] start_radii = {5, 5, 5, 5, 0, 0, 0, 0};
    private static final float[] end_radii = {0, 0, 0, 0, 5, 5, 5, 5};

    private int width;
    private int padding;
    private int flag;
    private Drawable drawable;

    public CodeBlockSpan(int width, int color, int flag) {
        this.width = width;
        this.flag = flag;
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        if (flag == FLAG_START) {
            g.setCornerRadii(start_radii);
        } else if (flag == FLAG_END) {
            g.setCornerRadii(end_radii);
        } else {
            g.setCornerRadius(radius);
        }
        drawable = g;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if (fm != null)
            padding = fm.bottom - fm.top;
        return width;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        drawable.setBounds((int) x, top, (int) x + width, bottom);
        drawable.draw(canvas);
        if (flag == FLAG_CENTER) {
            canvas.drawText(text, start, end, x + padding, y, paint);
        }
    }


}
