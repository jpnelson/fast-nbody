package pme;

public class FastBSpline {
	public double[] c;
	final int order;
	public FastBSpline(int order)
	{
		this.order = order;
		c = new double[order+1];
	}
	public void fillBSpline(double w)
	{
		for(int i = 0; i <= order; i++){
			c[i] = 0; //clear it
		}
		init(w);
		for(int k=3; k <= order-1; ++k){
			onePass(w,k);
		}
		onePass(w,order);
	}
	private void init(double x)
	{
		c[order] = 0;
		c[2] = x;
		c[1] = 1-x;
	}
	
	private void onePass(double x, int k)
	{
		int j;
		double div;
		div = 1.0 / (k-1);
		c[k] = div * x * c[k-1];
		
		for(j=1; j<= (k-2); ++j)
		{
			c[k-j] = div * ((x+j) * c[k-j-1] + (k-j-x) * c[k-j]);
		}
		c[1] = div * (1-x) * c[1];
	}
}
