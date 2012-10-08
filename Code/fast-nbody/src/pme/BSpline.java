package pme;

import math.Complex;

public class BSpline {
	int order;
	public BSpline(int order)
	{
		this.order = order;
	}
	
	//TODO: this is pretty inefficient
	//Eq 4.1 Essman[95]
	public double evaluate(double x)
	{
		double u = (double)x;
		double n = (double)order;
		//Base case
		if(order==2){
			if(0 <= u && u <= 2){
				return 1-Math.abs(u-1);
			}else{
				return 0;
			}
		}
		BSpline lowerOrderSpline = new BSpline(order-1);
		return (u / (n-1)) * lowerOrderSpline.evaluate(u) + ((n-u)/(n-1)) * lowerOrderSpline.evaluate(u-1);
	}
	
	//Eq 4.2 Essman[95]
	public double evaluateDerivative(double x)
	{
		BSpline lowerOrderSpline = new BSpline(order-1);
		return lowerOrderSpline.evaluate(x) - lowerOrderSpline.evaluate(x-1);
	}
	
	//Eq 4.4 Essman[95]
	public Complex b(int i, double mi, int K)
	{
		if(order%2==1 && (int)(2*mi) == K)
		{
			return Complex.zero;
		}
		Complex part1 = new Complex(0,2*Math.PI*(order-1)*mi / K);
		Complex part2 = Complex.zero;
		for(int j = 0; j < order-2; j++)
		{
			double splinePart = evaluate(j+1);
			Complex expPart = new Complex(0,2*Math.PI*mi*j/K).exp();
			part2 = part2.add(expPart.scale(splinePart));
		}
		return part1.mult(part2.reciprocal());
	}
}
