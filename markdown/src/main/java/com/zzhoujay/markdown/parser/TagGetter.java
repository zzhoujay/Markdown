package com.zzhoujay.markdown.parser;

public interface TagGetter{
	
	CharSequence get(int tag,Line line,int group);
	
	CharSequence get(int tag,CharSequence line,int group);
}
