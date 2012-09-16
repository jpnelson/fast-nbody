package particles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import math.Complex;

public class NSquaredList extends ParticleList{
	

	public NSquaredList(ArrayList particles) {
		super(particles);
	}

	@Override
	public double charge(Complex position)
	{
		
		Complex sum = Complex.zero;
		for (Particle particle : this) {
			   sum = sum.add(position.sub(particle.getPosition()).ln().scale(particle.getCharge()));
		}
		return sum.re();
	}

	@Override
	public void debugDraw(Graphics g) {
		//No debug drawing for this one
	}
}
