package particles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import math.Complex;

public abstract class ParticleList extends ArrayList<Particle>{
	public ParticleList(ArrayList particles) {
		this.addAll(particles);
	}
	public ParticleList() {
		//Don't do anything
	}
	public void draw(Graphics2D g)
	{
		
		for(Particle p : this)
		{
			p.draw(g);
		}
	}
		
	public abstract double potential(Complex position);
	
	public abstract void debugDraw(Graphics g);

}
