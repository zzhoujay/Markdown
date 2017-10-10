package com.zzhoujay.markdown.method;

import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.zzhoujay.markdown.style.LongPressClickableSpan;

/**
 * Created by rikka on 2017/9/12.
 */

public class LongPressLinkMovementMethod extends LinkMovementMethod {

    private static final int LONG_PRESS = 0;

    private static LongPressLinkMovementMethod sInstance;

    public static MovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new LongPressLinkMovementMethod();
        }

        return sInstance;
    }

    private Handler mHandler;
    private boolean mIsLongPressed;
    private int mX;
    private int mY;

    public LongPressLinkMovementMethod() {
        super();

        mHandler = new LongPressHandler();
    }

    private class LongPressHandler extends Handler {
        LongPressHandler() {
            super();
        }

        LongPressHandler(Handler handler) {
            super(handler.getLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LONG_PRESS:
                    dispatchLongPress(
                            (LongPressClickableSpan)    ((Object[]) msg.obj)[0],
                            (TextView)                  ((Object[]) msg.obj)[1]);
                    break;
            }
        }
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer,
                                MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_DOWN
                || action == MotionEvent.ACTION_MOVE
                || action == MotionEvent.ACTION_CANCEL) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] clickableLinks = buffer.getSpans(off, off, ClickableSpan.class);
            LongPressClickableSpan[] longPressClickableLinks = buffer.getSpans(off, off, LongPressClickableSpan.class);

            switch (action) {
                case MotionEvent.ACTION_UP:
                    if (clickableLinks.length != 0) {
                        if (!mIsLongPressed) {
                            clickableLinks[0].onClick(widget);
                        }

                        mIsLongPressed = false;
                        mHandler.removeMessages(LONG_PRESS);
                    }

                    return true;

                case MotionEvent.ACTION_DOWN:
                    if (clickableLinks.length != 0) {
                        if (longPressClickableLinks.length == 0) {
                            Selection.setSelection(buffer,
                                    buffer.getSpanStart(clickableLinks[0]),
                                    buffer.getSpanEnd(clickableLinks[0]));
                        } else {
                            mX = (int) event.getX();
                            mY = (int) event.getY();

                            mHandler.removeMessages(LONG_PRESS);
                            mHandler.sendMessageAtTime(Message.obtain(mHandler, LONG_PRESS, new Object[]{longPressClickableLinks[0], widget}),
                                    event.getDownTime() + ViewConfiguration.getLongPressTimeout());
                        }
                    }

                    return true;

                case MotionEvent.ACTION_MOVE:
                    if (Math.abs(event.getX() - mX) > 50
                            && Math.abs(event.getX() - mY) > 50) {
                        mHandler.removeMessages(LONG_PRESS);
                    }

                    return super.onTouchEvent(widget, buffer, event);

                case MotionEvent.ACTION_CANCEL:
                    mHandler.removeMessages(LONG_PRESS);
                    return super.onTouchEvent(widget, buffer, event);
            }

            if (clickableLinks.length == 0) {
                Selection.removeSelection(buffer);
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }

    private void dispatchLongPress(LongPressClickableSpan span, TextView widget) {
        mIsLongPressed = true;
        span.onLongPress(widget);
    }
}
