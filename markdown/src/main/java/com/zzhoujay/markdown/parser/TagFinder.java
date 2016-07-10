package com.zzhoujay.markdown.parser;

/**
 * Created by zhou on 16-7-10.
 */
public interface TagFinder {

    boolean find(int tag, Line line);

    boolean find(int tag, String line);

}
