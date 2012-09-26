package eric;

public class Play {

	
	static public Complex form21(Complex z0, Complex z, double q, int kmax) {
		if (kmax == 0) {
			return z.sub(z0).ln().scale(q);
		} else {
			Complex sum = new Complex(0.0,0.0);
			for (int k = 1; k < kmax; k++) {
				//System.out.println(sum + " : " + z0.div(z).power(k));
				sum = sum.add(z0.div(z).power(k).scale(1.0/(k*1.0)));
				//System.out.println(sum);
			}
			
			sum = z.ln().sub(sum);
			return sum.scale(q);
		}
	}
	
	public static void main(String[] args) {
for (int i = 0; i < 999; i++) {
	System.out.println(String.format("%d %s", i, form21(new Complex(10.0,10000.0),new Complex(10001.0,10.0),1.0,i)));
	
}
		
		
	}

}
