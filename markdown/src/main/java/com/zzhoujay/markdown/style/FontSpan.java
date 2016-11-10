package com.zzhoujay.markdown.style;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/**
 * Created by zhou on 2016/11/10.
 * FontSpan
 */
public class FontSpan extends MetricAffectingSpan implements ParcelableSpan {

    private final float size;
    private final int style;
    private final int color;

    public FontSpan(float size, int style, int color) {
        this.size = size;
        this.style = style;
        this.color = color;
    }

    public FontSpan(Parcel parcel) {
        this(parcel.readFloat(), parcel.readInt(), parcel.readInt());
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
        dest.writeFloat(size);
        dest.writeInt(style);
        dest.writeInt(color);
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        apply(p, style);
        p.setTextSize(p.getTextSize() * size);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        updateMeasureState(tp);
        tp.setColor(color);
    }

    private static void apply(Paint paint, int style) {
        int oldStyle;

        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int want = oldStyle | style;

        Typeface tf;
        if (old == null) {
            tf = Typeface.defaultFromStyle(want);
        } else {
            tf = Typeface.create(old, want);
        }

        int fake = want & ~tf.getStyle();

        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
    }

    public static final Parcelable.Creator<FontSpan> CREATOR = new Creator<FontSpan>() {
        @Override
        public FontSpan createFromParcel(Parcel source) {
            return new FontSpan(source);
        }

        @Override
        public FontSpan[] newArray(int size) {
            return new FontSpan[size];
        }
    };
}
