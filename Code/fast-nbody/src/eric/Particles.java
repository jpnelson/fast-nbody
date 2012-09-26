package eric;

import java.awt.Graphics2D;
import java.util.ArrayList;


public class Particles extends ArrayList<Particle> {

	
	double totalq;
	ArrayList<Complex> moments;
	
	@Override
	public boolean add(Particle e) {
		boolean res = super.add(e);
		calcMoments(10);
		return res;
	}
	
	public void calcMoments(int p) {
		totalq = 0.0;
		for (Particle pa : this ) {
			totalq += pa.q;
		}
		moments = new ArrayList<Complex>();
		for (int k = 1; k <= p; k++) {
			Complex sum = Complex.zero;
			for (Particle pa : this ) {
				sum = sum.add(pa.pos.power(k).scale(-pa.q/(k*1.0)));
			}
			moments.add(sum);	
		}
	}
	
	public void draw(Graphics2D g) {
		for (Particle p : this) {
			p.draw(g);
		}
	}
	
	
	public double mchargeat(Complex z) {
		Complex res = z.ln().scale(totalq);
		int k = 1;
		for (Complex m : moments) {
			   res = res.add(m.div(z.power(k)));
			   k++;
			}
		return res.re();
	}
	
	public double charge(Complex z) {
		Complex sum = Complex.zero;
		for (Particle lc : this) {
		   sum = sum.add(z.sub(lc.pos).ln().scale(1.0*lc.q));
		}
		return sum.re();
	}
}
