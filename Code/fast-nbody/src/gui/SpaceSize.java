package gui;

import fma.Pair;

public class SpaceSize {
	Pair<Integer,Integer> size;
	public SpaceSize(int width, int height) {
		size = new Pair<Integer,Integer>((int)width,(int)height);
	}
	public void Space(Integer w, Integer h)
	{
		size = new Pair<Integer,Integer>(w,h);
	}
	public Integer getWidth()
	{
		return size.getFirst();
	}
	public Integer getHeight()
	{
		return size.getFirst();
	}
	
	public SpaceSize scale(int c)
	{
		return new SpaceSize(size.getFirst() * c, size.getSecond() * c);
	}
}
