package com.zzhoujay.markdown.style;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;

/**
 * Created by zhou on 16-7-30.
 * EmailSpan
 */
public class EmailSpan extends URLSpan {

    private int color;

    public EmailSpan(String email, int color) {
        super(email);
        this.color = color;
    }

    private EmailSpan(Parcel src) {
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

    @Override
    public void onClick(View widget) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + getURL()));
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        try {
            widget.getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.w("URLSpan", "Actvity was not found for intent, " + intent.toString());
        }
    }

    public static final Creator<EmailSpan> CREATOR = new Creator<EmailSpan>() {
        @Override
        public EmailSpan createFromParcel(Parcel source) {
            return new EmailSpan(source);
        }

        @Override
        public EmailSpan[] newArray(int size) {
            return new EmailSpan[size];
        }
    };
}
