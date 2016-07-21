package com.zzhoujay.markdown.parser;

/**
 * Created by zhou on 16-7-2.
 */
public class LineQueue {

    private Line root;
    private Line curr;
    private Line last;

    public LineQueue(Line root) {
        this.root = root;
        curr = root;
        last = root;
        while (last.next != null) {
            last = last.next;
        }
    }

    private LineQueue(LineQueue queue, Line curr) {
        this.root = queue.root;
        this.last = queue.last;
        this.curr = curr;
    }

    public Line nextLine() {
        return curr.next;
    }

    public Line prevLine() {
        return curr.prev;
    }

    public Line currLine() {
        return curr;
    }

    public boolean next() {
        return (curr = curr.next) != null;
    }

    public boolean prev() {
        return (curr = curr.prev) != null;
    }

    public boolean end() {
        return curr.next == null;
    }

    public boolean start() {
        return curr == root;
    }

    public void append(Line line) {
        last.add(line);
        last = line;
    }

    public void insert(Line line) {
        if (curr == root) {
            append(line);
        } else {
            curr.addNext(line);
        }
    }

    public Line remove() {
        Line tmp;
        if (curr == last) {
            tmp = last.prev;
        } else {
            tmp = curr.next;
        }
        curr.remove();
        Line r = curr;
        curr = tmp;
        return r;
    }

    public void removeNext() {
        if (curr.next == last) {
            curr.removeNext();
            last = curr;
        } else {
            curr.removeNext();
        }
    }

    public LineQueue copy() {
        return new LineQueue(this, curr);
    }

    public LineQueue copyNext() {
        if (end()) {
            return null;
        }
        return new LineQueue(this, curr.next);
    }

    public Line get() {
        return curr;
    }

    public void reset() {
        curr = root;
    }

}
