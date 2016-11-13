package com.zzhoujay.markdown.style;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.style.StyleSpan;

/**
 * Created by zhou on 2016/11/10.
 * FontSpan
 */
public class FontSpan extends StyleSpan implements ParcelableSpan {

    private final float size;
    private final int color;

    public FontSpan(float size, int style, int color) {
        super(style);
        this.size = size;
        this.color = color;
    }

    public FontSpan(Parcel parcel) {
        super(parcel);
        this.size = parcel.readFloat();
        this.color = parcel.readInt();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeFloat(size);
        dest.writeInt(color);
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        super.updateMeasureState(p);
        p.setTextSize(p.getTextSize() * size);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        super.updateDrawState(tp);
        updateMeasureState(tp);
        tp.setColor(color);
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
