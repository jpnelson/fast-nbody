package eric;

public class Complex {
	public static final Complex zero = new Complex(0.0,0.0);
	
	private final double re, im;
	
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
		if (k == 0) {
			return new Complex(1.0,0.0);
		} else if (k == 1) {
			return this;
		} else {
			return power(k-1).mult(this);
		}
	}
	
	 public Complex reciprocal() {
	        double s = re*re + im*im;
	        return new Complex(re / s, -im / s);
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
	

	public Complex neg() {
		
		return new Complex(-re,-im);
	}

}
