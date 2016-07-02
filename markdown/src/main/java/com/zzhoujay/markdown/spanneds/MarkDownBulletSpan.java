package com.zzhoujay.markdown.spanneds;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.Parcel;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.widget.TextView;

/**
 * Created by zhou on 16-6-25.
 */
public class MarkDownBulletSpan extends BulletSpan {

    private final int mGapWidth;
    private final boolean mWantColor;
    private final int mColor;
    private final String index;

    private static Path sBulletPath = null;

    private static final int BULLET_RADIUS = 6;

    private static final int tab = 40;
    public static final int STANDARD_GAP_WIDTH = 2;
    private TextView textView;

    public MarkDownBulletSpan(int gapWidth, int color, int pointIndex, TextView textView) {
        super(gapWidth, color);
        if (pointIndex > 0) {
            index = pointIndex + ".";
        } else {
            index = null;
        }
        mGapWidth = gapWidth;
        mWantColor = true;
        mColor = color;
        this.textView = textView;
    }

    public MarkDownBulletSpan(int gapWidth, int color, int pointIndex) {
        super(gapWidth, color);
        if (pointIndex > 0) {
            index = pointIndex + ".";
        } else {
            index = null;
        }
        mGapWidth = gapWidth;
        mWantColor = true;
        mColor = color;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        if (index != null) {
            return (int) (mGapWidth / 2 + tab + textView.getPaint().measureText(index));
        }
        return 2 * BULLET_RADIUS + mGapWidth + tab;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline, int bottom, CharSequence text, int start, int end, boolean first, Layout l) {
        if (((Spanned) text).getSpanStart(this) == start) {
            int oldcolor = 0;
            if (mWantColor) {
                oldcolor = p.getColor();
                p.setColor(mColor);
            }
            if (index != null) {
                c.drawText(index, x + tab, baseline, p);
            } else {
                Paint.Style style = p.getStyle();

                p.setStyle(Paint.Style.FILL);

                if (c.isHardwareAccelerated()) {
                    if (sBulletPath == null) {
                        sBulletPath = new Path();
                        // Bullet is slightly better to avoid aliasing artifacts on mdpi devices.
                        sBulletPath.addCircle(0.0f, 0.0f, 1.2f * BULLET_RADIUS, Path.Direction.CW);
                    }

                    c.save();
                    c.translate(x + dir * BULLET_RADIUS + tab, (top + bottom) / 2.0f);
                    c.drawPath(sBulletPath, p);
                    c.restore();
                } else {
                    c.drawCircle(x + dir * BULLET_RADIUS + tab, (top + bottom) / 2.0f, BULLET_RADIUS, p);
                }

                p.setStyle(style);
            }
            if (mWantColor) {
                p.setColor(oldcolor);
            }
        }
    }
}
