package eric;

import java.awt.Color;
import java.awt.Graphics2D;


public class Particle {
	Complex pos;
	double q;
	double mass;
	
	public Particle(Complex p, double c, double m) {
		pos = p;
		q = c;
		mass = m;
	}

	public void draw(Graphics2D g) {
		if (q > 0.0) {
			g.setColor(Color.blue);
		} else {
			g.setColor(Color.red);
		}
		g.drawOval((int) pos.re() - 3, (int) pos.im() - 3, 6, 6);
	}

}
