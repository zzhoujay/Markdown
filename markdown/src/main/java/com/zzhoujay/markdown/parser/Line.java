package com.zzhoujay.markdown.parser;

import android.text.SpannableStringBuilder;

/**
 * Created by zhou on 16-6-28.
 */
public class Line {

    public final int lineNum;
    private String source;

    private boolean codeBlock;

    private CharSequence builder;

    public Line(int lineNum, String source) {
        this.lineNum = lineNum;
        this.source = source;
        codeBlock = false;
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
}
