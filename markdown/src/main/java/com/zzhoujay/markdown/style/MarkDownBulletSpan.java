package com.zzhoujay.markdown.style;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.BulletSpan;

import com.zzhoujay.markdown.util.NumberKit;

/**
 * Created by zhou on 16-6-25.
 * 列表Span
 */
public class MarkDownBulletSpan extends BulletSpan {

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

    private final int mBulletColor;
    private final String mIndex;
    private final int mLevel;

    private int mMargin;

    public MarkDownBulletSpan(int level, int bulletColor, int pointIndex) {
        super(GAP_WIDTH, bulletColor);

        mLevel = level;
        if (pointIndex > 0) {
            if (level == 1) {
                mIndex = NumberKit.toRomanNumerals(pointIndex) + '.';
            } else if (level >= 2) {
                mIndex = NumberKit.toABC(pointIndex - 1) + '.';
            } else {
                mIndex = String.valueOf(pointIndex) + '.';
            }
        } else {
            mIndex = null;
        }

        mBulletColor = bulletColor;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        mMargin = TAB + (2 * (int) BULLET_RADIUS + GAP_WIDTH) * (mLevel + 1);
        return mMargin;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout l) {
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
}
