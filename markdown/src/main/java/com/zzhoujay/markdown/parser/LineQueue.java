package com.zzhoujay.markdown.parser;

/**
 * Created by zhou on 16-7-2.
 */
public class LineQueue extends Line{

    private Line root;
    private Line curr;
    private Line last;

    public LineQueue(Line root) {
        this.root = root;
        curr = root;
        last = root;
        while (last.nextLine() != null) {
            last = last.nextLine();
        }
    }

    private LineQueue(LineQueue queue, Line curr) {
        this.root = queue.root;
        this.last = queue.last;
        this.curr = curr;
    }

    public Line nextLine() {
        return curr.nextLine();
    }

    public Line prevLine() {
        return curr.prevLine();
    }

	@Override
	public Line get()
	{
		return curr;
	}
	

    public Line currLine() {
        return get();
    }

    public boolean next() {
        return (curr = curr.nextLine()) != null;
    }

    public boolean prev() {
        return (curr = curr.prevLine()) != null;
    }

    public boolean end() {
        return curr.nextLine() == null;
    }

    public boolean start() {
        return curr == root;
    }

    public void append(Line line) {
        last.add(line);
        last = line;
    }

    public void insert(Line line) {
        if (curr == last) {
            append(line);
        } else {
            curr.addNext(line);
        }
    }

    public Line removeCurrLine() {
        Line tmp;
        if (curr == last) {
            tmp = last.prevLine();
        } else {
            tmp = curr.nextLine();
        }
        curr.remove();
        Line r = curr;
        curr = tmp;
        return r;
    }

    public void removeNextLine() {
        if (curr.nextLine() == last) {
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
        return new LineQueue(this, curr.nextLine());
    }

    

    public void reset() {
        curr = root;
    }

	@Override
	public Line createChild(String src)
	{
		return curr.createChild(src);
	}

	@Override
	public Line addNext(Line line)
	{
		return curr.addNext(line);
	}

	@Override
	public Line addPrev(Line line)
	{
		return curr.addPrev(line);
	}

	@Override
	public Line add(Line line)
	{
		return curr.add(line);
	}
	
	

	@Override
	public void remove()
	{
		curr.remove();
	}

	@Override
	public void addChild(Line line)
	{
		curr.addChild(line);
	}

	@Override
	public Line removeNext()
	{
		return curr.removeNext();
	}

	@Override
	public Line removePrev()
	{
		return curr.removePrev();
	}

	@Override
	public void attachToParent(Line line)
	{
		curr.attachToParent(line);
	}

	@Override
	public void unAttachFromParent()
	{
		curr.unAttachFromParent();
	}

	@Override
	public Line copyToNext()
	{
		return curr.copyToNext();
	}

	@Override
	public Line copyToPrev()
	{
		return curr.copyToPrev();
	}

	@Override
	public Line childLine()
	{
		return curr.childLine();
	}

	@Override
	public Line parentLine()
	{
		return curr.parentLine();
	}

	@Override
	public void setType(int type)
	{
		curr.setType(type);
	}

	@Override
	public void setCount(int count)
	{
		curr.setCount(count);
	}

	@Override
	public void setAttr(int attr)
	{
		curr.setAttr(attr);
	}

	@Override
	public void setSource(String source)
	{
		curr.setSource(source);
	}

	@Override
	public void setStyle(CharSequence style)
	{
		curr.setStyle(style);
	}

	@Override
	public int getAttr()
	{
		return curr.getAttr();
	}

	@Override
	public CharSequence getStyle()
	{
		return curr.getStyle();
	}

	@Override
	public int getType()
	{
		return curr.getType();
	}

	@Override
	public int getCount()
	{
		return curr.getCount();
	}

	@Override
	public String getSource()
	{
		return curr.getSource();
	}
	
	

	
	
}
