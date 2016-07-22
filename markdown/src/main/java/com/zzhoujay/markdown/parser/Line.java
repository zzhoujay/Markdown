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

    private Line prev;
    private Line next;
    private Line parent;
    private Line child;

    private String source;
    private CharSequence style;
    private int type;
    private int count;
    private int attr;
	
	Line(){
		
	}


    public Line(String source) {
        this.source = source;
        count = 1;
        type = LINE_NORMAL;
    }
	
	private Line(Line line){
		this.source=line.source;
		this.count=line.count;
		this.attr=line.attr;
		this.style=new SpannableStringBuilder(line.style);
		this.type=line.type;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public String getSource()
	{
		return source;
	}

	public void setStyle(CharSequence style)
	{
		this.style = style;
	}

	public CharSequence getStyle()
	{
		return style;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public int getType()
	{
		return type;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public int getCount()
	{
		return count;
	}

	public void setAttr(int attr)
	{
		this.attr = attr;
	}

	public int getAttr()
	{
		return attr;
	}
	
	public Line get(){
		return this;
	}
	
	public Line nextLine(){
		return next;
	}
	
	public Line prevLine(){
		return prev;
	}
	
	public Line childLine(){
		return child;
	}
	
	public Line parentLine(){
		return parent;
	}

    public Line addNext(Line line) {
		if(line==null){
			next=null;
		}else{
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
			if(child!=null){
				child.addNext(line.child);
			}
		}
        return line;
    }

    public Line addPrev(Line line) {
		if(line==null){
			prev=null;
		}else{
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
			if(child!=null){
				child.addPrev(line.child);
			}
		}
        return line;
    }

    public Line add(Line line) {
        return addNext(line);
    }
	
	private void delete(){
		if(child!=null){
			child.delete();
		}
		if(prev!=null){
			prev.next=null;
		}
		prev=null;
		if(next!=null){
			next.prev=null;
		}
		next=null;
	}
	
	private void reduce(){
		if(child!=null){
			child.reduce();
		}
		if(prev!=null){
			prev.next=next;
		}
		if(next!=null){
			next.prev=prev;
		}
		next=null;
		prev=null;
	}

    public void remove() {
        if(parent==null){
			reduce();
		}else{
			delete();
		}
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
	
	public void addChild(Line line){
		if(child!=null){
			child.parent=null;
		}
		child=line;
		if(line.parent!=null){
			line.parent.child=null;
		}
		line.parent=this;
		attachChildToNext();
		attachChildToPrev();
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
	
	public void attachToParent(Line line){
		line.addChild(this);
	}
	

    public void unAttachFromParent() {
        if (parent != null) {
			delete();
            parent.child = null;
        }
        parent = null;
    }


    public Line createChild(String src) {
        Line c = new Line(src);
        addChild(c);
        return c;
    }

    public Line copyToNext(){
		Line p=null;
		if(parent!=null){
			p=parent.copyToNext();
		}
		Line line=new Line(this);
		if(p==null){
			line.next=next;
			if(next!=null){
				next.prev=line;
			}
			line.prev=this;
			next=line;
		}else{
			p.addChild(line);
		}
		return line;
	}
	
	public Line copyToPrev(){
		Line p=null;
		if(parent!=null){
			p=parent.copyToPrev();
		}
		Line line=new Line(this);
		if(p==null){
			line.prev=prev;
			if(prev!=null){
				prev.next=line;
			}
			line.next=this;
			prev=this;
		}else{
			p.addChild(line);
		}
		return line;
	}


    @Override
    public String toString() {
        return source;
    }

}
