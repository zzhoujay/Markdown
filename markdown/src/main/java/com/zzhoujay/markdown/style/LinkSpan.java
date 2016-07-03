package com.zzhoujay.markdown.style;

import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.URLSpan;

/**
 * Created by zhou on 16-7-2.
 */
public class LinkSpan extends URLSpan {

    private int color;

    public LinkSpan(String url, int color) {
        super(url);
        this.color = color;
    }

    public LinkSpan(Parcel src) {
        super(src);
        color = src.readInt();
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(color);
        ds.setUnderlineText(false);
    }
}
