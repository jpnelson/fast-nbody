package particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import math.Complex;

public class ParticleList extends ArrayList<Particle>{
	
	public void draw(Graphics2D g)
	{
		
		for(Particle p : this)
		{
			p.draw(g);
		}
	}
	
	public double charge(Complex position)
	{
		Complex sum = Complex.zero;
		for (Particle particle : this) {
			   sum = sum.add(position.sub(particle.getPosition()).ln().scale(particle.getCharge()));
		}
		return sum.re();
	}
}
