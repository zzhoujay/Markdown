package com.zzhoujay.markdown.parser;

import java.util.List;
import java.util.Stack;

/**
 * Created by zhou on 16-7-2.
 */
public class LineQueue extends Line {

    private List<Line> lines;
    private int point;
    private Line currLine;
    private Stack<Integer> status;

    public LineQueue(List<Line> lines) {
        this.lines = lines;
        point = 0;
        currLine = get();
        status = new Stack<>();
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
        currLine = get();
        return true;
    }

    public boolean prev() {
        if (start()) {
            return false;
        }
        point--;
        currLine = get();
        return true;
    }

    public void seek(int position) {
        point = position;
        currLine = get();
    }

    public void push() {
        status.push(point);
    }

    public void pop() {
        point = status.pop();
        currLine = get();
    }

    public void add(Line line) {
        lines.add(point + 1, line);
    }

    public Line remove() {
        return remove(point);
    }

    public Line removeNext() {
        return remove(point + 1);
    }

    public Line removePrev() {
        return remove(point - 1);
    }

    public Line remove(int position) {
        Line l = lines.remove(position);
        currLine = get();
        return l;
    }

    @Override
    public CharSequence getBuilder() {
        return currLine.getBuilder();
    }

    @Override
    public void setBuilder(CharSequence builder) {
        currLine.setBuilder(builder);
    }

    @Override
    public String getSource() {
        return currLine.getSource();
    }

    @Override
    public void setSource(String source) {
        currLine.setSource(source);
    }

    @Override
    public boolean isCodeBlock() {
        return currLine.isCodeBlock();
    }

    @Override
    public void setCodeBlock(boolean codeBlock) {
        currLine.setCodeBlock(codeBlock);
    }

    @Override
    public int getType() {
        return currLine.getType();
    }

    @Override
    public void setType(int type) {
        currLine.setType(type);
    }

    @Override
    public int getTypeCount() {
        return super.getTypeCount();
    }

    @Override
    public void setTypeCount(int typeCount) {
        super.setTypeCount(typeCount);
    }

    @Override
    public int getLineNum() {
        return super.getLineNum();
    }

    public int position() {
        return point;
    }
}
