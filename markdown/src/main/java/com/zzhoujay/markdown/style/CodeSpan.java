package com.zzhoujay.markdown.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.style.ReplacementSpan;

/**
 * Created by zhou on 16-7-2.
 * 代码Span
 */
public class CodeSpan extends ReplacementSpan implements ParcelableSpan {

    private static final float radius = 10;

    private Drawable drawable;
    private float padding;
    private int width;
    private int color;

    public CodeSpan(int color) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        this.color = color;
        d.setCornerRadius(radius);
        drawable = d;
    }

    public CodeSpan(Parcel parcel) {
        this(parcel.readInt());
    }


    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        padding = paint.measureText("t");
        width = (int) (paint.measureText(text, start, end) + padding * 2);
        return width;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        drawable.setBounds((int) x, top, (int) x + width, bottom);
        drawable.draw(canvas);
        canvas.drawText(text, start, end, x + padding, y, paint);
    }

    @Override
    public int getSpanTypeId() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(color);
    }

    public static final Creator<CodeSpan> CREATOR = new Creator<CodeSpan>() {
        @Override
        public CodeSpan createFromParcel(Parcel source) {
            return new CodeSpan(source);
        }

        @Override
        public CodeSpan[] newArray(int size) {
            return new CodeSpan[size];
        }
    };
}
