package particles;

import java.awt.Graphics2D;
import java.util.ArrayList;

import math.Complex;

public class FastMultipoleList extends ParticleList{
	static int expansionTerms = 6;
	public FastMultipoleList(ArrayList particles) {
		super(particles);
	}

	public void draw(Graphics2D g)
	{
		
		for(Particle p : this)
		{
			p.draw(g);
		}
	}

	@Override
	public double charge(Complex position) {
		int levelCount = (int) (Math.log(this.size()) / Math.log(4)); //log_4(N)
		return position.re();
	}
}
