package com.zzhoujay.markdown.style;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.text.style.ReplacementSpan;

/**
 * Created by zhou on 16-7-3.
 */
public class MarkDownInnerBulletSpan extends ReplacementSpan {

    private static final int BULLET_RADIUS = 6;
    private static final int tab = 40;

    private final int gap;
    private final int mColor;
    private final String index;
    private int margin;

    private static Path sBulletPath = null;


    public MarkDownInnerBulletSpan(int gap, int mColor, int index) {
        this.gap = gap;
        this.mColor = mColor;
        if (index > 0) {
            this.index = index + ".";
        } else {
            this.index = null;
        }
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if (index == null) {
            margin = tab + gap + BULLET_RADIUS * 2;
        } else {
            margin = (int) (tab + gap + paint.measureText(index));
        }
        return (int) (margin + paint.measureText(text, start, end));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        int oldcolor = paint.getColor();
        paint.setColor(mColor);
        // draw bullet
        if (index != null) {
            canvas.drawText(index, x + tab, y, paint);
        } else {
            Paint.Style style = paint.getStyle();

            paint.setStyle(Paint.Style.FILL);

            if (canvas.isHardwareAccelerated()) {
                if (sBulletPath == null) {
                    sBulletPath = new Path();
                    // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
                    sBulletPath.addCircle(0.0f, 0.0f, 1.2f * BULLET_RADIUS, Path.Direction.CW);
                }

                canvas.save();
                canvas.translate(x + 2 * BULLET_RADIUS + tab, (top + bottom) / 2.0f);
                canvas.drawPath(sBulletPath, paint);
                canvas.restore();
            } else {
                canvas.drawCircle(x + 2 * BULLET_RADIUS + tab, (top + bottom) / 2.0f, BULLET_RADIUS, paint);
            }
            paint.setStyle(style);
        }
        // drawText
        canvas.drawText(text, start, end, x + margin, y, paint);
        paint.setColor(oldcolor);
    }
}
