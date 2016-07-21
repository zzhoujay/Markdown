package com.zzhoujay.markdown.parser;

import android.text.SpannableStringBuilder;

/**
 * Created by zhou on 16-6-28.
 */
public class Line {

    public static final int LINE_NORMAL = 0;
    public static final int LINE_TYPE_QUOTA = 1;
    public static final int LINE_TYPE_UL = 2;
    public static final int LINE_TYPE_OL = 3;
    public static final int LINE_TYPE_H1 = 4;
    public static final int LINE_TYPE_H2 = 5;
    public static final int LINE_TYPE_H3 = 6;
    public static final int LINE_TYPE_H4 = 7;
    public static final int LINE_TYPE_H5 = 8;
    public static final int LINE_TYPE_H6 = 9;
    public static final int LINE_TYPE_CODE_BLOCK_2 = 10;
    public static final int LINE_TYPE_CODE_BLOCK_1 = 11;
    public static final int LINE_TYPE_GAP = 12;

    public Line prev;
    public Line next;
    public Line parent;
    public Line child;

    public String source;
    public CharSequence style;
    public int type;
    public int count;
    public int attr;


    public Line(String source) {
        this.source = source;
        count = 1;
        type = LINE_NORMAL;
    }

    public Line addNext(Line line) {
        if (line.next != null) {
            line.next.prev = null;
        }
        line.next = next;
        if (next != null) {
            next.prev = line;
        }
        if (line.prev != null) {
            line.prev.next = null;
        }
        line.prev = this;
        next = line;
        return line;
    }

    public Line addPrev(Line line) {
        if (line.prev != null) {
            line.prev.next = null;
        }
        line.prev = prev;
        if (prev != null) {
            prev.next = line;
        }
        if (line.next != null) {
            line.next.prev = null;
        }
        line.next = this;
        prev = line;
        return line;
    }

    public Line addNextAndChild(Line line) {
        addNext(line);
        if (child != null) {
            child.addNextAndChild(line.child);
        }
        return line;
    }

    public Line addPrevAndChild(Line line) {
        addPrev(line);
        if (child != null) {
            child.addPrevAndChild(line.child);
        }
        return line;
    }

    public Line add(Line line) {
        return addNext(line);
    }

    public void remove() {
        if (child != null) {
            child.remove();
        }
        if (prev != null) {
            prev.next = next;
        }
        if (next != null) {
            next.prev = prev;
        }
        next = null;
        prev = null;
    }

    public Line removeNext() {
        if (next != null) {
            next.remove();
        }
        return this;
    }

    public Line removePrev() {
        if (prev != null) {
            prev.remove();
        }
        return this;
    }

    public void attachChildToNext() {
        if (child != null && next != null) {
            if (child.next != null) {
                child.next.prev = null;
            }
            child.next = next.child;
            if (next.child != null) {
                if (next.child.prev != null) {
                    next.child.prev.next = null;
                }
                next.child.prev = child;
            }
            child.attachChildToNext();
        }
    }

    public void attachChildToPrev() {
        if (child != null && prev != null) {
            if (child.prev != null) {
                child.prev.next = null;
            }
            child.prev = prev.child;
            if (prev.child != null) {
                if (prev.child.next != null) {
                    prev.child.next.prev = null;
                }
                prev.child.next = child;
            }
            child.attachChildToPrev();
        }
    }

    public void attachAsChild(Line line) {
        if (line.child != null) {
            line.child.parent = null;
        }
        line.child = this;
        if (parent != null) {
            parent.child = null;
        }
        parent = line;
    }

    public void unAttach() {
        if (next != null) {
            next.prev = null;
        }
        next = null;
        if (prev != null) {
            prev.next = null;
        }
        prev = null;
    }

    public void unAttachAndChild() {
        if (child != null) {
            child.unAttach();
        }
        unAttach();
    }

    public void unAttachFromParent() {
        if (parent != null) {
            parent.child = null;
        }
        parent = null;
    }


    public Line createChild(String src) {
        Line c = new Line(src);
        c.parent = this;
        if (child != null) {
            child.parent = null;
        }
        child = c;
        return c;
    }

    public Line copy() {
        Line line = new Line(source);
        line.attr = attr;
        line.type = type;
        line.count = count;
        line.style = new SpannableStringBuilder(style);
        return line;
    }

    public Line copyParent() {
        return null;
    }


    @Override
    public String toString() {
        return source;
    }

}
