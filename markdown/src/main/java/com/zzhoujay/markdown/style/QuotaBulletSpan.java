package com.zzhoujay.markdown.style;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.QuoteSpan;

import com.zzhoujay.markdown.util.NumberKit;

/**
 * Created by zhou on 16-7-30.
 */
public class QuotaBulletSpan extends QuoteSpan {

    private static final int TAB = 40;
    private static final int GAP_WIDTH = 40;
    private static final float BULLET_RADIUS = 7.2f;

    private static final Path CIRCLE_BULLET_PATH;
    private static final Path RECT_BULLET_PATH;

    static {
        float w = BULLET_RADIUS;

        RECT_BULLET_PATH = new Path();
        RECT_BULLET_PATH.addRect(-w, -w, w, w, Path.Direction.CW);

        CIRCLE_BULLET_PATH = new Path();
        // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
        CIRCLE_BULLET_PATH.addCircle(0.0f, 0.0f, w, Path.Direction.CW);
    }

    private static final int STRIPE_WIDTH = 15;

    private final int mBulletColor;
    private final String mIndex;
    private final int mLevel;

    private int mMargin;

    private int mQuotaLevel;

    public QuotaBulletSpan(int quotaLevel, int bulletLevel, int quotaColor, int bulletColor, int pointIndex) {
        super(quotaColor);

        mQuotaLevel = quotaLevel;
        mLevel = bulletLevel;
        if (pointIndex > 0) {
            if (bulletLevel == 1) {
                mIndex = NumberKit.toRomanNumerals(pointIndex) + '.';
            } else if (bulletLevel >= 2) {
                mIndex = NumberKit.toABC(pointIndex - 1) + '.';
            } else {
                mIndex = String.valueOf(pointIndex) + '.';
            }
        } else {
            mIndex = null;
        }

        mBulletColor = bulletColor;
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

        while (i <= mQuotaLevel) {
            int offset = i * quotaWidth;
            c.drawRect(x + offset, top, x + offset + dir * STRIPE_WIDTH, bottom, p);
            i++;
        }

        p.setStyle(style);
        p.setColor(color);

        // draw bullet
        if (((Spanned) text).getSpanStart(this) != start) {
            return;
        }

        int oldColor = p.getColor();
        p.setColor(mBulletColor);

        if (mIndex != null) {
            c.drawText(mIndex, x + mMargin - GAP_WIDTH - 2 * BULLET_RADIUS, baseline, p);
        } else {
            float dy = (p.getFontMetricsInt().descent - p.getFontMetricsInt().ascent) * 0.5f + top;

            Paint.Style oldStyle = p.getStyle();
            p.setStyle(mLevel == 1 ? Paint.Style.STROKE : Paint.Style.FILL);

            if (!c.isHardwareAccelerated()) {
                Path path = mLevel >= 2 ? RECT_BULLET_PATH : CIRCLE_BULLET_PATH;

                c.save();
                c.translate(x + mMargin - GAP_WIDTH, dy);
                c.drawPath(path, p);
                c.restore();
            } else {
                c.drawCircle(x + mMargin - GAP_WIDTH, dy, BULLET_RADIUS, p);
            }

            p.setStyle(oldStyle);
        }

        p.setColor(oldColor);
    }

    @Override
    public int getLeadingMargin(boolean first) {
        mMargin = (2 * (int) BULLET_RADIUS + GAP_WIDTH) * (mLevel + 1) + TAB;
        int bulletMargin = (mQuotaLevel + 1) * (STRIPE_WIDTH + GAP_WIDTH);
        mMargin += bulletMargin;
        return mMargin;
    }

}
