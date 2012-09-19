package fma;

import java.util.ArrayList;

import particles.NSquaredList;
import particles.Particle;

public class Cell {
	NSquaredList particles;
	MultipoleExpansion multipoleExpansion;
	
	//From G&R, psi are local expansions describing the field due to all particles in the system that are not in the cell / neighbours
	LocalExpansion psi;
	LocalExpansion psiBar; 
	private final int x,y;
	
	public Cell(int x, int y)
	{
		//Zero Local Expansions
		//psi = new LocalExpansion();
		//psiBar = new LocalExpansion();
		multipoleExpansion = new MultipoleExpansion();
		particles = new NSquaredList();
		this.x = x;
		this.y = y;
	}
	//Adds a particle to the cell
	public void add(Particle p)
	{
		particles.add(p);
	}
	
	public int getX()
	{
		return x;
	}
	public int getY()
	{
		return y;
	}
}
