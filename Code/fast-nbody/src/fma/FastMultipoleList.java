package fma;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import particles.Particle;
import particles.ParticleList;


import math.Complex;

public class FastMultipoleList extends ParticleList{
	static int EXPANSION_TERMS = 6;
	static int LEVEL_COUNT = 4; //should be about log_4(N) (G&R)
	Dimension windowSize;
	Mesh lowestLevelMesh;
	
	public FastMultipoleList(ArrayList particles, Dimension windowSize) {
		super(particles);
		this.windowSize = windowSize;
		
		init();

	}
	
	public void init()
	{
		int lowestLevelBoxCount = (int)Math.pow(4.0, LEVEL_COUNT);
		//2^n is the number of times we've split horizontally. (2^n)^2 = 4^n
		int boxesOnSide = (int)Math.pow(2.0, LEVEL_COUNT);
		
		//Allocate each particle to it's appropriate lowest level box
		lowestLevelMesh = new Mesh(boxesOnSide,LEVEL_COUNT,windowSize);
		for(Particle p : this)
		{
			lowestLevelMesh.add(p);
		}
		
		//Form multipole expansions
		lowestLevelMesh.formMultipoleExpansions(EXPANSION_TERMS);
		//Get the next highest multipole expansion
	}
	
	public void debugDraw(Graphics g)
	{
		lowestLevelMesh.draw(g);
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
		//Algorithm from Greengard and Rokhlin
		//Form a p-term multipole expansion \phi_{n,ibox} by using Theorem 2.1
		
		return 0;
	}
}
