package com.zzhoujay.markdown.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.style.LineHeightSpan;
import android.text.style.ReplacementSpan;

import com.zzhoujay.markdown.util.FontKit;

/**
 * Created by zhou on 16-7-2.
 */
public class CodeBlockSpan extends ReplacementSpan implements LineHeightSpan {

    public static final int FLAG_START = -1;
    public static final int FLAG_CENTER = 0;
    public static final int FLAG_END = 1;

    private static final float radius = 10;
    private static final int padding = 40;

    private static final float[] start_radii = {radius, radius, radius, radius, 0, 0, 0, 0};
    private static final float[] end_radii = {0, 0, 0, 0, radius, radius, radius, radius};

    private int width;
    private int flag;
    private int lineCount;
    private Drawable drawable;
    private float textWidth;
    private int baseLine;
    private int lineHeight;

    private static Paint.FontMetricsInt normalFontMetrics;

    public CodeBlockSpan(int width, int color, int flag) {
        this.width = width;
        this.flag = flag;
        this.lineCount = 1;
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        if (flag == FLAG_START) {
            g.setCornerRadii(start_radii);
        } else if (flag == FLAG_END) {
            g.setCornerRadii(end_radii);
        } else {
            g.setCornerRadius(0);
        }
        drawable = g;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        textWidth = paint.measureText(text, start, end);
        if (normalFontMetrics == null && flag == FLAG_START) {
            Paint.FontMetricsInt temp = fm == null ? paint.getFontMetricsInt() : fm;
            normalFontMetrics = new Paint.FontMetricsInt();
            normalFontMetrics.bottom = temp.bottom;
            normalFontMetrics.top = temp.top;
            normalFontMetrics.ascent = temp.ascent;
            normalFontMetrics.descent = temp.descent;
            normalFontMetrics.leading = temp.leading;
        }
        return width;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        drawable.setBounds((int) x, top, (int) x + width, bottom);
        drawable.draw(canvas);
//        Paint.Style os = paint.getStyle();
//        paint.setStyle(Paint.Style.STROKE);
//        canvas.drawRect(x + 20, top, x + width - 20, bottom, paint);
//        paint.setStyle(os);
//        int baseLine=-paint.getFontMetricsInt().top;
        if (flag == FLAG_CENTER) {
            int e, s = start;
            for (int i = 0; i < lineCount; i++) {
//                canvas.drawLine(x, top + baseLine + lineHeight * i, width, top + baseLine + lineHeight * i, paint);
                e = findLine(paint, text, s, end);
                canvas.drawText(text, s, e, x + padding, top + baseLine + lineHeight * i, paint);
                s = e;
            }
        }
    }

    private int findLine(Paint paint, CharSequence text, int start, int end) {
        int e = end;
        while (paint.measureText(text, start, e) > width - padding * 2) {
            e--;
        }
        return e;
    }


    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
        if (flag == FLAG_START || flag == FLAG_END) {
            FontKit.scaleTo(normalFontMetrics, fm, 0.6f);
        } else {
            float tw = textWidth;
            if (lineCount == 1) {
                while (tw + padding > width) {
                    tw -= width - padding;
                    lineCount++;
                }
                FontKit.scaleTo(normalFontMetrics, fm, lineCount);
            }
        }
        baseLine = -fm.top;
        lineHeight = fm.bottom - fm.top;
    }
}
