package com.zzhoujay.markdown.parser;

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

    private final int lineNum;
    private String source;
    private boolean codeBlock;
    private CharSequence builder;
    private int type;
    private int typeCount;

    Line() {
        lineNum = 0;
    }

    public Line(int lineNum, String source) {
        this.lineNum = lineNum;
        this.source = source;
        codeBlock = false;
        typeCount = 1;
        type = LINE_NORMAL;
    }

    @Override
    public String toString() {
        return source;
    }

    public CharSequence getBuilder() {
        return builder;
    }

    public void setBuilder(CharSequence builder) {
        this.builder = builder;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isCodeBlock() {
        return codeBlock;
    }

    public void setCodeBlock(boolean codeBlock) {
        this.codeBlock = codeBlock;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTypeCount() {
        return typeCount;
    }

    public void setTypeCount(int typeCount) {
        this.typeCount = typeCount;
    }

    public int getLineNum() {
        return lineNum;
    }
}
