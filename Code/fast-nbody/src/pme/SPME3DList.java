package pme;

import gui.SpaceSize;

import java.awt.Graphics;
import java.util.ArrayList;

import math.Complex;
import math.Vector;
import particles.Particle;
import particles.ParticleList;

public class SPME3DList extends ParticleList {
	static int PADDING = 1; //Make the space size this many times larger to reduce period issues
	static int CELL_SIDE_COUNT = 64; //K (Essman[95])
	static int ZPLANE=0; //We just remember that particles are at this Z coordinate always
	static int ASSIGNMENT_SCHEME_ORDER = 4;
	static double BETA = 90;
	
	double[][][] Q; //the charge assignment matrix
	double[][][] B; //The B Matrix (Essman[95])
	double[][][] C; //The C Matrix (Essman[95])
	BSpline M;		//Order ASSIGNMENT_SCHEME_ORDER B spline
	final double meshWidth; //H (Petersen[95])
	final double inverseMeshWidth; //H^-1 (required for the reciprocal lattice)
	final SpaceSize windowSize;
	Vector[] reciprocalLatticeVectors; //Reciprocal lattice vectors
	
	//We have an array of 2D particles, but we assume the z component is 0
	public SPME3DList(ArrayList<Particle> particles, SpaceSize windowSize) {
		super(particles);
		this.windowSize = windowSize.scale(PADDING); //we make it empty around to help with period problems
		this.meshWidth = windowSize.getWidth() / CELL_SIDE_COUNT;
		this.inverseMeshWidth = 1.0 / this.meshWidth;
		this.reciprocalLatticeVectors = new Vector[3];
		this.reciprocalLatticeVectors[0] = new Vector(inverseMeshWidth,0,0); //X
		this.reciprocalLatticeVectors[1] = new Vector(0,inverseMeshWidth,0); //Y
		this.reciprocalLatticeVectors[2] = new Vector(0,0,inverseMeshWidth); //Z
		init();
	}
	
	private void init()
	{
		M = new BSpline(ASSIGNMENT_SCHEME_ORDER);
		initQMatrix();
		initBMatrix();
		initCMatrix();
		
	}
	//Eq 4.6 Essman[95]
	private void initQMatrix()
	{
		Q = new double[CELL_SIDE_COUNT][CELL_SIDE_COUNT][CELL_SIDE_COUNT];
		for(int z = 0; z < CELL_SIDE_COUNT; z++)
		{
			for(int x = 0; x < CELL_SIDE_COUNT; x++)
			{
				for(int y = 0; y < CELL_SIDE_COUNT; y++)
				{
						double sum = 0;
						for(Particle p : this)
						{
							double particleX,particleY,particleZ;
							particleX = p.getPosition().re();
							particleY = p.getPosition().im();
							particleZ = ZPLANE*meshWidth; //we're working on a plane
							Vector positionVector = new Vector(particleX,particleY,particleZ);
							//Just before Eq 3.1 Essman[95]
							double uX = (CELL_SIDE_COUNT) * (particleX / (double)(windowSize.getWidth()));
							double uY = (CELL_SIDE_COUNT) * (particleY / (double)(windowSize.getWidth()));
							double uZ = (CELL_SIDE_COUNT) * (particleZ / (double)(windowSize.getWidth()));
							//Removed periodic images? Made z Bspline 1? Seems to be off by a grid cell in x/y? FIXME?
							double a = M.evaluate(uX-x);
							double b = M.evaluate(uY-y);
							double c = M.evaluate(uZ-z);
							sum += p.getCharge() * a * b * 1; //FIXME: zero when c is used. ZPLANE-1, -2 contains values if c is used?
						}
						Q[x][y][z] = sum;
						
					}
				}
				//System.out.println(M.evaluate((CELL_SIDE_COUNT) * (ZPLANE*meshWidth / (double)(windowSize.getWidth()))-z));
			}
		System.out.println("!");
	}
	
	//Eq 4.8 Essman[95]
	private void initBMatrix()
	{
		B = new double[CELL_SIDE_COUNT][CELL_SIDE_COUNT][CELL_SIDE_COUNT];
		for(int z = 0; z < CELL_SIDE_COUNT; z++)
		{
			for(int x = 0; x < CELL_SIDE_COUNT; x++)
			{
				for(int y = 0; y < CELL_SIDE_COUNT; y++)
				{
					B[x][y][z] = squared(M.b(1, x, CELL_SIDE_COUNT).abs())*squared(M.b(2, y, CELL_SIDE_COUNT).abs())*squared(M.b(3, z, CELL_SIDE_COUNT).abs());
					//System.out.print(B[x][y][z]+"\t");
				}
				//System.out.print("\n");
			}
			//System.out.print("----------\n");
		}
	}
	
	//Eq 3.9 Essman[95]
	private void initCMatrix()
	{
		C = new double[CELL_SIDE_COUNT][CELL_SIDE_COUNT][CELL_SIDE_COUNT];
		double V = windowSize.getWidth() * windowSize.getHeight();
		double c = 1.0 / (Math.PI * V);
		C[0][0][0] = 0;

		for(int x = 0; x < CELL_SIDE_COUNT; x++)
		{
			for(int y = 0; y < CELL_SIDE_COUNT; y++)
			{
				for(int z = 0; z < CELL_SIDE_COUNT; z++)
				{
					if(!(x==0 && y==0 && z==0))
					{
						double mXPrime = (0 <= x && x <= CELL_SIDE_COUNT/2)? x : x - CELL_SIDE_COUNT;
						double mYPrime = (0 <= y && y <= CELL_SIDE_COUNT/2)? y : y - CELL_SIDE_COUNT;
						double mZPrime = (0 <= z && z <= CELL_SIDE_COUNT/2)? z : z - CELL_SIDE_COUNT;
						double m = mXPrime * inverseMeshWidth + mYPrime * inverseMeshWidth + mZPrime * inverseMeshWidth;
						if(m != 0){ //FIXME: What to do if m==0?
							C[x][y][z] = c * (Math.exp(-squared(Math.PI)* squared(m) / squared(BETA))) / squared(m);
						}else{
							C[x][y][z] = 0;
						}
					}
				}
			}
		}
	}
	
	private static double squared(double x){
		return x*x;
	}
	
	
	@Override
	public double charge(Complex position) {
		int i = (int)(position.re() / meshWidth);
		int j = (int)(position.im() / meshWidth);
		return B[i][j][ZPLANE];
	}

	@Override
	public void debugDraw(Graphics g) {
		
	}
	
	

}
