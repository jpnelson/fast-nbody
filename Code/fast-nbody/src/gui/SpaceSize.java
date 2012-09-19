package gui;

import fma.Pair;

public class SpaceSize {
	Pair<Double,Double> size;
	public SpaceSize(int width, int height) {
		size = new Pair<Double,Double>((double)width,(double)height);
	}
	public void Space(Double w, Double h)
	{
		size = new Pair<Double,Double>(w,h);
	}
	public Double getWidth()
	{
		return size.getFirst();
	}
	public Double getHeight()
	{
		return size.getFirst();
	}
}
