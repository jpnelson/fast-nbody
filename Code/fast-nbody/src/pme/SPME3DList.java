package pme;

import gui.SpaceSize;

import java.awt.Graphics;
import java.util.ArrayList;

import math.Complex;
import particles.Particle;
import particles.ParticleList;

public class SPME3DList extends ParticleList {
	static int PADDING = 1; //Make the space size this many times larger to reduce period issues
	static int CELL_SIDE_COUNT = 32; //N
	static int ASSIGNMENT_SCHEME_ORDER = 2;
	static double BETA = 90;
	
	double[][] recEnergyAtGridpoints;
	double[][] deltaXS;
	final double meshWidth; //H (Petersen[95])
	final double inverseMeshWidth; //H^-1 (required for the reciprocal lattice)
	final SpaceSize windowSize;
	
	//We have an array of 2D particles, but we assume the z component is 0
	public SPME3DList(ArrayList<Particle> particles, SpaceSize windowSize) {
		super(particles);
		this.windowSize = windowSize.scale(PADDING); //we make it empty around to help with period problems
		this.meshWidth = windowSize.getWidth() / CELL_SIDE_COUNT;
		this.inverseMeshWidth = 1.0 / this.meshWidth;
		init();
	}
	
	private void init()
	{
		
	}
	@Override
	public double charge(Complex position) {
		return 0;
	}

	@Override
	public void debugDraw(Graphics g) {
		
	}
	
	

}
