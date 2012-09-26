package eric;

public class MP {
	
	Complex a[];  // a[0] is the sum of the charges
	
	int p;
	
	public MP(int p) {
		this.p = p;
		a = new Complex[p+1];
		for (int i = 0; i <= p; i++) {
			a[i] = Complex.zero;
		}
	}
	
	public MP(Particles pts, int p, Complex center) {
		
		this.p = p;
		a = new Complex[p+1];
		double q = 0.0;
		for (Particle pt : pts) {
			q += pt.q;
		}
		a[0] = new Complex(q,0.0);
		for (int i = 1; i <= p; i++) {
			Complex sum = Complex.zero;
			for (Particle pa : pts) {
				sum = sum.add(pa.pos.sub(center).power(i).scale(-pa.q/(i*1.0)));
			}
			a[i] = sum;
		}
	}
	
	public MP() {	
	}
	
	public MP shift(Complex zs) {
		MP res = new MP();
		zs = zs.neg();
		
		res.p = p;
		
		res.a = new Complex[res.p+1];
		res.a[0] = a[0];
		for (int l = 1; l <= res.p; l++) {
			Complex sum = zs.power(l).scale(-1.0*a[0].re()/((double) l));
			for (int k = 1; k <= l; k++) {
				sum = sum.add(zs.power(l-k).mult(a[k]).scale(binomial(l-1,k-1)));		
			}
			res.a[l] = sum;
		}
		return res;
	}
	
	public MP add(MP mp) {
		MP res = new MP();
		res.p = p;
		res.a = new Complex[res.p+1];
		for (int l = 0; l <= res.p; l++) {
			res.a[l] = a[l].add(mp.a[l]);
		}
		return res;
	}
	
	
	 static double binomial(int n, int k) {
		 if (k > n)
		      return 0;
		  if (k > n/2)
		        k = n-k; // faster

		   double accum = 1.0;
		    for (int i = 1; i <= k; i++) {
		         accum = accum * (n-k+i) / (double) i;
		 }
		 return accum;
	}

	public double charge(Complex pos) {
		Complex z = pos;
		Complex res = z.ln().scale(a[0].re());
		for (int i = 1; i <= p; i++) {
			res = res.add(a[i].div(z.power(i)));
		}
		return res.re();
	}
	
	
	public String toString() {
		String res = "";
		for (int i = 0; i < a.length; i++) {
			res += (i==0? "": ", ") + a[i];
		}
		return "MP#  "  + "a [" + res + "]" ;
	}
	
	
	
}
