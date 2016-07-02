package com.zzhoujay.markdown.parser;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhou on 16-7-2.
 */
public class LineQueue {

    private List<Line> lines;
    private int point;

    public LineQueue(List<Line> lines) {
        this.lines = lines;
        point = 0;
    }

    public boolean end() {
        return point == lines.size() - 1;
    }

    public boolean start() {
        return point == 0;
    }

    public Line nextLine() {
        if (!end()) {
            return lines.get(point + 1);
        }
        return null;
    }

    public Line prevLine() {
        if (!start()) {
            return lines.get(point - 1);
        }
        return null;
    }

    public Line get() {
        return lines.get(point);
    }

    public boolean next() {
        if (end()) {
            return false;
        }
        point++;
        return true;
    }
}
