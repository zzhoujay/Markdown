package com.zzhoujay.markdown.style;

import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.style.LineHeightSpan;

/**
 * Created by zhou on 16-7-2.
 * ScaleHeightSpan
 */
public class ScaleHeightSpan implements LineHeightSpan, Parcelable {

    private float scale;

    public ScaleHeightSpan(float scale) {
        this.scale = scale;
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
        fm.ascent *= scale;
        fm.top *= scale;
        fm.descent *= scale;
        fm.bottom *= scale;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.scale);
    }

    protected ScaleHeightSpan(Parcel in) {
        this.scale = in.readFloat();
    }

    public static final Parcelable.Creator<ScaleHeightSpan> CREATOR = new Parcelable.Creator<ScaleHeightSpan>() {
        @Override
        public ScaleHeightSpan createFromParcel(Parcel source) {
            return new ScaleHeightSpan(source);
        }

        @Override
        public ScaleHeightSpan[] newArray(int size) {
            return new ScaleHeightSpan[size];
        }
    };
}
