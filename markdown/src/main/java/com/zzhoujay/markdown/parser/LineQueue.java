package com.zzhoujay.markdown.parser;

import java.util.List;
import java.util.Stack;

/**
 * Created by zhou on 16-7-2.
 */
public class LineQueue extends Line {

    private List<Line> lines;
    private int position;
    private Stack<Integer> status;

    public LineQueue(List<Line> lines) {
        this.lines = lines;
        position = 0;
        status = new Stack<>();
    }

    public boolean end() {
        return position == lines.size() - 1;
    }

    public boolean start() {
        return position == 0;
    }

    public Line nextLine() {
        if (!end()) {
            return lines.get(position + 1);
        }
        return null;
    }

    public Line prevLine() {
        if (!start()) {
            return lines.get(position - 1);
        }
        return null;
    }

    public Line get() {
        return lines.get(position);
    }

    public boolean next() {
        if (end()) {
            return false;
        }
        position++;
        return true;
    }

    public boolean prev() {
        if (start()) {
            return false;
        }
        position--;
        return true;
    }

    public void seek(int position) {
        this.position = position;
    }

    public void push() {
        status.push(position);
    }

    public void pop() {
        position = status.pop();
    }

    public void add(Line line) {
        lines.add(position + 1, line);
    }

    public Line remove() {
        return remove(position);
    }

    public Line removeNext() {
        return remove(position + 1);
    }

    public Line removePrev() {
        return remove(position - 1);
    }

    public Line remove(int position) {
        Line l = lines.remove(position);
        return l;
    }

    @Override
    public CharSequence getBuilder() {
        return get().getBuilder();
    }

    @Override
    public void setBuilder(CharSequence builder) {
        get().setBuilder(builder);
    }

    @Override
    public String getSource() {
        return get().getSource();
    }

    @Override
    public void setSource(String source) {
        get().setSource(source);
    }

    @Override
    public boolean isCodeBlock() {
        return get().isCodeBlock();
    }

    @Override
    public void setCodeBlock(boolean codeBlock) {
        get().setCodeBlock(codeBlock);
    }

    @Override
    public int getType() {
        return get().getType();
    }

    @Override
    public void setType(int type) {
        get().setType(type);
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

}
