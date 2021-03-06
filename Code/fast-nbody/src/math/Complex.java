package math;
/**
 * 
 * @author Eric McCreath
 * GPL,2008
 */
public class Complex {
	public static final Complex zero = new Complex(0.0,0.0);
	public static final Complex one = new Complex(1.0,0.0);

	private final double re;

	private final double im;
	
	
	public Complex(double r, double i) {
		re = r;
		im = i;
	}
	
	public double re() { 
		return re; 
	}
	
    public double im() { 
    	return im; 
    }
	
	public double phase() {
		return Math.atan2(im, re);
	}
	
	public double abs() {
		return Math.hypot(re, im);
	}
	
	public double mag() {
		return Math.sqrt(re*re + im*im);
	}
	
	public Complex add(Complex a) {
		return new Complex(re+a.re,im+a.im);
	}
	
	public Complex sub(Complex a) {
		return new Complex(re-a.re,im-a.im);
	}
	
	public Complex mult(Complex a) {
		return new Complex(re*a.re-im*a.im,re*a.im+im*a.re);
	}
	
	public Complex scale(double d) {
		return new Complex(d*re,d*im);
	}
	
	public Complex power(int k) {
		if (k > 1) {
			return power(k-1).mult(this);
		} else if (k == 1) {
			return this;
		} else {
			return new Complex(1.0,0.0);
		}
	}
	
	 public Complex reciprocal() {
        double s = re*re + im*im;
        return new Complex(re / s, -im / s);
    }

    public Complex conjugate() {
    	return new Complex(re,-im);
    }
	    

	  
    public Complex div(Complex a) {
    	double s = a.re*a.re + a.im*a.im;
    	double rar = a.re/s;
    	double rai = -a.im/s;
        return new Complex(re*rar-im*rai,re*rai+im*rar);
    }

 
    public Complex exp() {
        return new Complex(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im));
    }

    public Complex sin() {
        return new Complex(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im));
    }

   
    public Complex cos() {
        return new Complex(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im));
    }

   
    public Complex tan() {
        return sin().div(cos());
    }

    public Complex ln() {
    	return new Complex(0.5*Math.log(re*re+im*im), Math.atan2(im, re));
    }
	    
	public String toString() {
		return re + " + " + im + "i" ;
	}
	
	public static void main(String[] args) {
		Complex z = new Complex(10.0,200000.0);
		Complex z0 = new Complex(1.0,1.0);
		System.out.println((z.sub(z0)).ln().scale(-1.0).re());		
	}

	public Complex neg() {
		
		return new Complex(-re,-im);
	}
	
	//Takes a rectangular matrix which has the format of the output from a jtransforms FFT
	//Returns a width halved complex matrix
	public static Complex[][][] doubleToComplexArray3D(double[][][] in)
	{
		int depth = in[0][0].length;
		int width = in[0].length;
		int height = in.length;

		Complex[][][] out = new Complex[width][height][depth/2];
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
			{
				for(int z = 0; z <= depth/2-1; z++) // -1 here since we look ahead once
				{
					out[x][y][z] = new Complex(in[x][y][2*z],in[x][y][2*z+1]);
				}
			}
		}
		return out;
	}
	
	//Opposite of the above
	public static double[][][] complexToDoubleArray3D(Complex[][][] in)
	{
		int depth = in[0][0].length;
		int width = in[0].length;
		int height = in.length;

		double[][][] out = new double[width][height][depth*2];
		for(int z = 0; z < depth; z++)
		{
			for(int y = 0; y < height; y++)
			{
				for(int x = 0; x < width; x++) // -1 here since we look ahead once
				{
					out[x][y][2*z] = in[x][y][z].re();
					out[x][y][2*z+1] = in[x][y][z].im();
				}
			}
		}
		return out;
	}
	
	//Takes a rectangular matrix which has the format of the output from a jtransforms FFT
	//Returns a width halved complex matrix
	public static Complex[][] doubleToComplexArray2D(double[][] in)
	{
		int width = in[0].length;
		int height = in.length;

		Complex[][] out = new Complex[height][width/2];
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x <= width/2-1; x++) // -1 here since we look ahead once
			{
				out[y][x] = new Complex(in[y][2*x],in[y][2*x+1]);
			}
		}
		return out;
	}

	//Opposite of the above
	public static double[][] complexToDoubleArray2D(Complex[][] in)
	{
		int width = in[0].length;
		int height = in.length;

		double[][] out = new double[height][width*2];
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++) // -1 here since we look ahead once
			{
				out[y][2*x] = in[y][x].re();
				out[y][2*x+1] = in[y][x].im();
			}
		}
		return out;
	}
	
	
	//Just makes imaginary parts zero
	public static Complex[][] doubleToComplexArrayNoImaginaryPart2D(double[][] in)
	{
		int width = in[0].length;
		int height = in.length;

		Complex[][] out = new Complex[height][width];
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++) // -1 here since we look ahead once
			{
				out[x][y] = new Complex(in[x][y],0);
			}
		}
		return out;
	}
	
	public static double[] complexToDoubleVector(Complex[] in)
	{
		double[] vector = new double[in.length*2];
		for(int i = 0; i < in.length; i++)
		{
			vector[2*i] = in[i].re();
			vector[2*i+1] = in[i].im();
		}
		return vector;
	}
	
	//The in format is [re,im,re,im,re,...]
	public static Complex[] doubleToComplexVector(double[] in)
	{
		Complex[] vector = new Complex[in.length/2];
		for(int i = 0; i < in.length/2; i++)
		{
			vector[i] = new Complex(in[2*i],in[2*i+1]);
		}
		return vector;
	}
	
	//Just makes imaginary parts zero
		public static Complex[] doubleToComplexVectorNoImaginaryPart(double[] in)
		{
			int length = in.length;

			Complex[] out = new Complex[length];
			for(int i = 0; i < length; i++)
			{
				out[i] = new Complex(in[i],0);
			}
			return out;
		}

		public Vector toVector() {
			return new Vector(re,im,0);
		}
	


}
