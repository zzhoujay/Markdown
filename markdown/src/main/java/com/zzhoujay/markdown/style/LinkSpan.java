package com.zzhoujay.markdown.style;

import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.URLSpan;

/**
 * Created by zhou on 16-7-2.
 * 链接Span
 */
public class LinkSpan extends URLSpan {

    private int color;

    public LinkSpan(String url, int color) {
        super(url);
        this.color = color;
    }

    private LinkSpan(Parcel src) {
        super(src);
        color = src.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(color);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(color);
        ds.setUnderlineText(false);
    }

    public static final Creator<LinkSpan> CREATOR = new Creator<LinkSpan>() {
        @Override
        public LinkSpan createFromParcel(Parcel source) {
            return new LinkSpan(source);
        }

        @Override
        public LinkSpan[] newArray(int size) {
            return new LinkSpan[size];
        }
    };
}
