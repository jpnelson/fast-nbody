package particles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import math.Complex;

public abstract class ParticleList extends ArrayList<Particle>{
	protected boolean initialised = false;
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
	//Don't put this in the constructor, as initialisation may take time that we wish to measure, so we call it when we want
	//Will set initialised to true at the start
	public abstract void init();
		
	public abstract double potential(Complex position);
	
	public abstract void debugDraw(Graphics g);

}
