package com.zzhoujay.markdown.style;

import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by rikka on 2017/9/12.
 */

public abstract class LongPressClickableSpan extends ClickableSpan {

    public abstract void onLongPress(View view);
}
