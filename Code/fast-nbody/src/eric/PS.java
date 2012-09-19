package eric;

import math.Binomial;
import math.Complex;

public class PS {
	public Complex b[];  
	int p;
	
	
	public PS() {
	}
	
	public PS(int p) {
        this.p = p;	
		b = new Complex[p+1];
		for (int l = 0; l <= p; l++) {
			b[l] = Complex.zero;
		}
	}
	
	public PS(MP mp, Complex z0,  int p) {
		this.p = p;
		
		b = new Complex[p+1];
		
		Complex sum = z0.neg().ln().mult(mp.a[0]);
		for (int k = 1; k <= mp.p; k++) {
			sum = sum.add(mp.a[k].div(z0.power(k)).scale(minusoneto(k)));
		}
		b[0] = sum;
		
		for (int l = 1; l <= p; l++) {
			sum = Complex.zero;
			for (int k = 1; k <= mp.p; k++) {
				sum = sum.add(mp.a[k].div(z0.power(k)).scale(Binomial.binomial(l+k-1,k-1) * minusoneto(k) )  );
			}
			sum = sum.div(z0.power(l));
			sum = sum.sub(mp.a[0].div(z0.power(l).scale(l)));
			b[l] = sum;
		}
	}

	private double minusoneto(int k) {
		
		return k%2 == 0 ? 1.0 : -1.0;
	}
	
	public double charge(Complex z) {
		Complex res = Complex.zero;
		for (int i = 0; i <= p; i++) {
			res = res.add(b[i].mult(z.power(i)));
		}
		return res.re();
	}
	
	public PS shift(Complex z0) {
		PS res = new PS();
		
		
		res.p = p;
		res.b = new Complex[p+1];
	
		for (int l = 0; l <= res.p; l++) {
			Complex sum = Complex.zero;
			for (int k = l; k <= res.p; k++) {
				sum = sum.add(z0.neg().power(k-l).mult(b[k]).scale(Binomial.binomial(k,l)));		
			}
			res.b[l] = sum;
		}
		return res;
	}

	public PS add(PS ps) {
		PS res = new PS();
		res.p = p;
		res.b = new Complex[res.p+1];
		for (int l = 0; l <= res.p; l++) {
			res.b[l] = b[l].add(ps.b[l]);
		}
		return res;
	}
	

}
