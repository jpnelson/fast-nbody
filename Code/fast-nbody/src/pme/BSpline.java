package pme;

import java.util.Hashtable;

import math.Complex;

public class BSpline {
	final int order;
	public double[] bspmod;
	Hashtable<Double, Double> evaluateValues = new Hashtable<Double, Double>(); //used to prevent re evaluation
	public int hits=0;
	public int misses=0;//Debug variables
	public BSpline(int order)
	{
		this.order = order;
	}
	
	
	//TODO: this is pretty inefficient
	//Eq 4.1 Essman[95]
	public double evaluate(double x)
	{
		Double mx = evaluateValues.get(x);
		if(mx != null)
		{
			hits++;
			return mx;
		}else{
			misses++;
		}
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
		double value = (u / (n-1)) * lowerOrderSpline.evaluate(u) + ((n-u)/(n-1)) * lowerOrderSpline.evaluate(u-1);
		evaluateValues.put(x, value);
		return value;
	}
	
	//Eq 4.2 Essman[95]
	public double evaluateDerivative(double x)
	{
		BSpline lowerOrderSpline = new BSpline(order-1);
		return (lowerOrderSpline.evaluate(x) - lowerOrderSpline.evaluate(x-1));
	}
	
	//Eq 4.4 Essman[95]
	//3.2.2 Lee[05]
	//Replace with DFTmod from http://chem.skku.ac.kr/~wkpark/tutor/chem/tinker/source/kewald.f ?
	//Page 157 of Lee[05]
	public Complex b(int i, double mi, int K)
	{
		if(order%2==1 && (int)(2*mi) == K)
		{
			return Complex.zero;
		}
		Complex part1 = new Complex(0,2*Math.PI*(order-1)*mi / K).exp();
		Complex part2 = Complex.zero;
		for(int j = 0; j <= order-2; j++)
		{
			double splinePart = evaluate(j+1);
			Complex expPart = new Complex(0,2*Math.PI*mi*j/K).exp();
			part2 = part2.add(expPart.scale(splinePart));
		}
		return part1.mult(part2.reciprocal());
	}
	
	//From dftmod in Lee[05] (Pg 156)
	public void fillBSPMod(int K)
	{
		bspmod = new double[K+1];
	    /* Computes the modulus of the discrete fourier transform of bsp_arr, */
	    /* storing it into bsp_mod */
		double tiny = 1e-7;
		for(int i = 1; i <= K; i++)
		{
			double sum1=0;
			double sum2=0;
			for(int j=1; j <= K; j++)
			{
				double arg = Math.PI * 2 * (i-1) * (j-1) / K;
				sum1 += evaluate(j) * Math.cos(arg);
				sum2 += evaluate(j) * Math.sin(arg);
			}
			bspmod[i] = sum1*sum1+sum2*sum2;
		}
		for(int i = 1; i <= K; i++)
		{
			if(bspmod[i] < tiny)
			{
				bspmod[i] = (bspmod[i-1] + bspmod[i+1])*0.5;
			}
		}
		for(int i = 0; i < K; i++)
		{
			bspmod[i] = bspmod[i+1]; //Shift array back down
		}
//		for(int i = 0; i <= K; i++)
//		{
//			System.out.println("[Bspline] bsp_mod["+i+"]="+bspmod[i]);
//		}
	}
}
