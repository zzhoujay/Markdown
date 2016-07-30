package com.zzhoujay.markdown.style;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.Parcel;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.QuoteSpan;
import android.widget.TextView;

import com.zzhoujay.markdown.util.NumberKit;

/**
 * Created by zhou on 16-7-30.
 */
public class QuotaBulletSpan extends QuoteSpan {

    private static final int tab = 40;
    private static final int mGapWidth = 40;
    private static final int BULLET_RADIUS = 6;


    private static final int STRIPE_WIDTH = 15;
    private static final int GAP_WIDTH = 40;

    private static Path circleBulletPath = null;
    private static Path rectBulletPath = null;

    private final String index;
    private int level = 0;
    private int bulletColor;
    private int margin;
    private TextView textView;
    private int quotaLevel;


    public QuotaBulletSpan(int quotaLevel, int level, int color, int bulletColor, int pointIndex, TextView textView) {
        super(color);
        this.quotaLevel = quotaLevel;
        this.level = level;
        if (pointIndex > 0) {
            if (level == 1) {
                this.index = NumberKit.toRomanNumerals(pointIndex);
            } else if (level >= 2) {
                this.index = NumberKit.toABC(pointIndex - 1);
            } else {
                this.index = pointIndex + "";
            }
        } else {
            index = null;
        }
        this.bulletColor = bulletColor;
        this.textView = textView;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
        // draw quota
        Paint.Style style = p.getStyle();
        int color = p.getColor();

        p.setStyle(Paint.Style.FILL);
        p.setColor(getColor());

        int i = 0;
        int quotaWidth = STRIPE_WIDTH + GAP_WIDTH;

        while (i <= quotaLevel) {
            int offset = i * quotaWidth;
            c.drawRect(x + offset, top, x + offset + dir * STRIPE_WIDTH, bottom, p);
            i++;
        }

        p.setStyle(style);
        p.setColor(color);

        // draw bullet
        if (((Spanned) text).getSpanStart(this) == start) {
            int oldcolor = 0;
            oldcolor = p.getColor();
            p.setColor(bulletColor);
            if (index != null) {
                c.drawText(index + '.', x - p.measureText(index) + margin - mGapWidth, baseline, p);
            } else {
                style = p.getStyle();
                if (level == 1) {
                    p.setStyle(Paint.Style.STROKE);
                } else {
                    p.setStyle(Paint.Style.FILL);
                }

                if (c.isHardwareAccelerated()) {
                    Path path;
                    if (level >= 2) {
                        if (rectBulletPath == null) {
                            rectBulletPath = new Path();
                            float w = 1.2f * BULLET_RADIUS;
                            rectBulletPath.addRect(-w, -w, w, w, Path.Direction.CW);
                        }
                        path = rectBulletPath;
                    } else {
                        if (circleBulletPath == null) {
                            circleBulletPath = new Path();
                            // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
                            circleBulletPath.addCircle(0.0f, 0.0f, 1.2f * BULLET_RADIUS, Path.Direction.CW);
                        }
                        path = circleBulletPath;
                    }

                    c.save();
                    c.translate(x + margin - mGapWidth, (top + bottom) / 2.0f);
                    c.drawPath(path, p);
                    c.restore();
                } else {
                    c.drawCircle(x + margin - mGapWidth, (top + bottom) / 2.0f, BULLET_RADIUS, p);
                }

                p.setStyle(style);
            }
            p.setColor(oldcolor);
        }


    }

    @Override
    public int getLeadingMargin(boolean first) {
        if (index != null) {
            margin = (int) (tab + (mGapWidth + textView.getPaint().measureText(index)) * (level + 1));
        } else {
            margin = (2 * BULLET_RADIUS + mGapWidth) * (level + 1) + tab;
        }
        int bulletMargin = (quotaLevel + 1) * (STRIPE_WIDTH + GAP_WIDTH);
        margin += bulletMargin;
        return margin;
    }
}
