package com.zzhoujay.markdown.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.style.LineHeightSpan;
import android.text.style.ReplacementSpan;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhou on 16-7-2.
 * 代码块Span
 */
public class CodeBlockSpan extends ReplacementSpan implements LineHeightSpan {

    private static final float RADIUS = 10;
    private static final float PADDING = 16;
    private static final float TEXT_SIZE_SCALE = 0.92f;

    private int mWidth;
    private Drawable mBackground;
    private int mTextColor;
    private int mBaseline;
    private int mLineHeight;
    private CharSequence[] mLines;
    private List<Pair<Integer, Integer>> lines;

    public CodeBlockSpan(int width, int backgroundColor, int textColor, CharSequence... lines) {
        mWidth = width;
        GradientDrawable g = new GradientDrawable();
        g.setColor(backgroundColor);
        g.setCornerRadius(RADIUS);
        mBackground = g;
        mLines = lines;
        mTextColor = textColor;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        float size = paint.getTextSize();
        paint.setTextSize(size * TEXT_SIZE_SCALE);
        paint.setTypeface(Typeface.MONOSPACE);

        if (fm != null && lines == null) {
            lines = new ArrayList<>();
            for (CharSequence c : mLines) {
                lines.addAll(measureTextLine(c, 0, c.length(), paint));
            }
        }

        paint.setTextSize(size);
        return mWidth;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        float size = paint.getTextSize();
        paint.setTextSize(size * TEXT_SIZE_SCALE);
        paint.setTypeface(Typeface.MONOSPACE);

        int lineNum = 0;
        int height = mLineHeight;
        for (Pair<Integer, Integer> line : lines) {
            CharSequence t = mLines[lineNum];
            if (line.second >= t.length()) {
                lineNum++;
            }
            height += mLineHeight;
        }

        mBackground.setBounds((int) x, top, (int) x + mWidth, top + height);
        mBackground.draw(canvas);

        int color = paint.getColor();
        paint.setColor(mTextColor);

        lineNum = 0;
        x = x + PADDING;
        int i = mBaseline + mLineHeight / 2 + top;
        for (Pair<Integer, Integer> line : lines) {
            CharSequence t = mLines[lineNum];
            canvas.drawText(t, line.first, line.second, x + PADDING, i, paint);
            if (line.second >= t.length()) {
                lineNum++;
            }
            i += mLineHeight;
        }
        paint.setTextSize(size);
        paint.setColor(color);
    }


    private int getTextInLineLen(CharSequence text, int start, int end, Paint paint) {
        int e = start;
        while (paint.measureText(text, start, e) < mWidth - PADDING * 2) {
            e++;
            if (e > end) {
                break;
            }
        }
        return e - 1;
    }

    private int getTextInLineLenInRange(CharSequence text, int start, int end, int rs, int re, Paint paint) {
        int e = rs;
        if (rs > end) {
            return end;
        }
        while (paint.measureText(text, start, e) < mWidth - PADDING * 2) {
            e++;
            if (e > end || e > re) {
                break;
            }
        }
        return e - 1;
    }

    private List<Pair<Integer, Integer>> measureTextLine(CharSequence text, int start, int end, Paint paint) {
        List<Pair<Integer, Integer>> lines = new ArrayList<>();
        int l = getTextInLineLen(text, start, end, paint);
        int count = l;
        lines.add(new Pair<>(start, l));
        while (l < end) {
            int temp = l;
            l = getTextInLineLenInRange(text, l, end, l + count - 4, l + count + 4, paint);
            count = l - temp;
            lines.add(new Pair<>(temp, l));
        }
        return lines;
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
        int num = lines.size();
        mLineHeight = fm.bottom - fm.top;
        mBaseline = -fm.top;
        fm.ascent = fm.top;
        fm.bottom += num * mLineHeight;
        fm.descent = fm.bottom;
    }
}
