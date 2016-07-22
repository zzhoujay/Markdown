package com.zzhoujay.markdown.parser;

/**
 * Created by zhou on 16-7-10.
 */
public interface TagFinder {

    boolean find(int tag, Line line);

    boolean find(int tag, String line);

    int findCount(int tag,Line line,int group);

    int findCount(int tag,String line,int group);

}
