package pme;

import gui.SpaceSize;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import jtransforms.DoubleFFT_2D;

import math.Complex;
import math.ErrorFunction;
import math.MatrixOperations;
import math.Vector;
import particles.Particle;
import particles.ParticleList;

public class SPMEList extends ParticleList {
	static int CELL_SIDE_COUNT = 64; //K (Essman[95])
	static int ASSIGNMENT_SCHEME_ORDER = 6;

	final double ewaldCoefficient; //The ewald coefficient
	static double TOLERANCE = 1e-6;//Used to calculate ewaldCoefficient
	static double CUTOFF_DISTANCE = 0.25;//Used to calculate ewaldCoefficient. In unit cell dimensions
	final int directRange; //will be CUTOFF_DISTANCE / meshWidth. In mesh cells

	final ArrayList<Particle> nonUnitParticles; //We keep a copy of this for drawing

	ArrayList<Particle> cellList[][];

	double[][] Q; //the charge assignment matrix
	double[][] B; //The B Matrix (Essman[95])
	double[][] debugMatrix;
	Complex[][] convolutedMatrix;
	
	BSpline M;		//Order ASSIGNMENT_SCHEME_ORDER B spline
	final double meshWidth; //H (Petersen[95])
	final double inverseMeshWidth; //H^-1 (required for the reciprocal lattice)
	final SpaceSize windowSize;
	Vector[] reciprocalLatticeVectors; //Reciprocal lattice vectors

	//We have an array of 2D particles, but we assume the z component is 0
	public SPMEList(ArrayList<Particle> particles, SpaceSize windowSize) {

		//We work within the unit square
		ArrayList<Particle> unitParticles = new ArrayList<Particle>();
		for(Particle p : particles)
		{
			unitParticles.add(new Particle(p.getPosition().re() / (double)(windowSize.getWidth()),p.getPosition().im() / (double)(windowSize.getHeight()),
					p.getMass(),p.getCharge()));
		}
		this.addAll(unitParticles);
		
		this.nonUnitParticles = particles;
		this.windowSize = windowSize;
		this.meshWidth = 1.0 / (double)(CELL_SIDE_COUNT); //Working in the unit cell
		this.inverseMeshWidth = 1.0 / this.meshWidth;
		this.directRange = (int)Math.ceil(CUTOFF_DISTANCE/meshWidth);
		this.ewaldCoefficient = calculateEwaldCoefficient(CUTOFF_DISTANCE,TOLERANCE);
		init();
	}

	//subroutine ewaldcof from http://chem.skku.ac.kr/~wkpark/tutor/chem/tinker/source/kewald.f
	private double calculateEwaldCoefficient(double cutoffDistance, double tolerance)
	{
		int i,k;
		double x,xlo,xhi,y;
		double ratio;
		ratio = tolerance + 1.0;
		x = 0.5;
		i = 0;
		while (ratio >= tolerance){
			i = i + 1;
			x = 2.0 * x;
			y = x * cutoffDistance;
			ratio = ErrorFunction.erfc(y) / cutoffDistance;
		}
		//use a binary search to refine the coefficient
		k = i + 60;
		xlo = 0.0;
		xhi = x;
		for(i=0;i<k;i++){
			x = (xlo+xhi) / 2.0;
			y = x * cutoffDistance;
			ratio = ErrorFunction.erfc(y) / cutoffDistance;
			if (ratio >= tolerance){
				xlo = x;
			}
			else{
				xhi = x;
			}
		}
		System.out.println("Chose ewald coefficient of "+x);
		return x;
	}

	private void init()
	{
		initCellList();
		M = new BSpline(ASSIGNMENT_SCHEME_ORDER);
		initQMatrix();
		//initBMatrix();
		//initCMatrix();

		//Starting Eq 4.7 Essman[95]
//		double[][] BC = MatrixOperations.straightMultiply(B, C);
//		DoubleFFT_2D fft = new DoubleFFT_2D(CELL_SIDE_COUNT,CELL_SIDE_COUNT);
//		double[][] qInverseFT = MatrixOperations.copyMatrix(Q, 2*CELL_SIDE_COUNT);
//		fft.realInverseFull(qInverseFT, false);
//		Complex[][] product = MatrixOperations.straightMultiply(Complex.doubleToComplexArrayNoImaginaryPart(BC), Complex.doubleToComplexArray(qInverseFT));
//		double[][] wideProduct = Complex.complexToDoubleArray(product);
//		fft.complexForward(wideProduct);
//		convolution = Complex.doubleToComplexArray(wideProduct);
//
//		theta = MatrixOperations.copyMatrix(BC, 2*CELL_SIDE_COUNT);
//		fft.realForwardFull(theta);
//		complexTheta = Complex.doubleToComplexArray(theta);


		System.out.println("Reciprocal energy: "+getRecEnergy());
		System.out.println("Direct energy: "+getDirEnergy());
		System.out.println("Corrected energy: "+getCorEnergy());
		System.out.println("Actual energy: "+getActualEnergy());
		
		//Forces
		calculateForces();

	}


	//Eq 4.6 Essman[95]
	private void initQMatrix()
	{
		Q = new double[CELL_SIDE_COUNT][CELL_SIDE_COUNT];
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
						//Just before Eq 3.1 Essman[95]
						double uX = (CELL_SIDE_COUNT) * (particleX / 1.0);//Unit cell already in fractional coordinates
						double uY = (CELL_SIDE_COUNT) * (particleY / 1.0);
						//Removed periodic images? Seems to be off by a grid cell in x/y? FIXME?
						double a = M.evaluate(uX-x);
						double b = M.evaluate(uY-y);
						sum += p.getCharge() * a * b;
					}
					Q[x][y] = sum;

				}
			}
	}

	//Eq 4.8 Essman[95]
	private void initBMatrix()
	{
		B = new double[CELL_SIDE_COUNT][CELL_SIDE_COUNT];
		for(int x = 0; x < CELL_SIDE_COUNT; x++)
		{
			for(int y = 0; y < CELL_SIDE_COUNT; y++)
			{
				B[x][y] = squared(M.b(1, x, CELL_SIDE_COUNT).mag())*squared(M.b(2, y, CELL_SIDE_COUNT).mag());
			}
		}
	}

	private void initCellList(){
		cellList = new ArrayList[CELL_SIDE_COUNT][CELL_SIDE_COUNT];
		for(int i = 0; i < CELL_SIDE_COUNT; i++)
		{
			for(int j = 0; j < CELL_SIDE_COUNT; j++)
			{
				cellList[i][j] = new ArrayList<Particle>();
			}
		}
		for(Particle p : this)
		{
			int cellX = (int)Math.floor(p.getPosition().re() / meshWidth);
			int cellY = (int)Math.floor(p.getPosition().im() / meshWidth);
			cellList[cellX][cellY].add(p);
		}
	}


	private static double squared(double x){
		return x*x;
	}
	
	
	//Requires getRecEnergy to have been called, which fills the convolutedMatrix
	public void calculateForces(){
		//Perform a FFT on convolutedMatrix to make theta * Q from B C F^-1(Q)
		//Make IFTQ ourselves
		DoubleFFT_2D fft = new DoubleFFT_2D(CELL_SIDE_COUNT,CELL_SIDE_COUNT);
		double[][] convolutedDoubles = Complex.complexToDoubleArray(convolutedMatrix);
		fft.complexForward(convolutedDoubles);
		convolutedMatrix = Complex.doubleToComplexArray(convolutedDoubles); //F(B C F^-1(Q)) Pg. 182 Lee[05]
		
		for(Particle p : this){
			//TODO left off at uni here 183
		}
		
	}
	
	//Requires Q to be initialised. C is implicitly calculated in here (part of eterm)
	//B matrix is also calculated elsewhere, in BSpline at the moment
	//Refer to page 191 of Lee[05]
	private double getRecEnergy(){
		//Fill the bspmod array
		M.fillBSPMod(CELL_SIDE_COUNT);
		
		//Make IFTQ ourselves
		DoubleFFT_2D fft = new DoubleFFT_2D(CELL_SIDE_COUNT,CELL_SIDE_COUNT);
		double[][] inverseFTQDoubles = MatrixOperations.copyMatrix(Q, CELL_SIDE_COUNT*2);
		fft.realInverseFull(inverseFTQDoubles, false);
		Complex[][] inverseFTQComplex = Complex.doubleToComplexArray(inverseFTQDoubles); //IFT of Q
		
		debugMatrix = new double[CELL_SIDE_COUNT][CELL_SIDE_COUNT];
		convolutedMatrix = new Complex[CELL_SIDE_COUNT][CELL_SIDE_COUNT];
		//Eq 19 Lee[05]
		//Also Eq 3.9 Essman[95]
		double sum = 0;
		for(int x = 0; x < CELL_SIDE_COUNT; x++)
		{
			for(int y = 0; y < CELL_SIDE_COUNT; y++)
			{
				double mXPrime = (0 <= x && x <= CELL_SIDE_COUNT/2)? x : x - CELL_SIDE_COUNT;
				double mYPrime = (0 <= y && y <= CELL_SIDE_COUNT/2)? y : y - CELL_SIDE_COUNT;
				double m = mXPrime * 1.0 + mYPrime * 1.0; //Was inverseMeshWidth - theory is reciprocal lattice vectors are for the entire cell U rather than one cell
				double mSquared = squared(mXPrime * 1.0) + squared(mYPrime * 1.0);
				if(m!=0){
					double V = 1; //working in the unit mesh
					double bterm = M.bspmod[x]*M.bspmod[y];
					double eterm = Math.exp(-squared(Math.PI/ewaldCoefficient)*mSquared) / (bterm * Math.PI * V * mSquared);
					//Section 3.2.8 Lee[05]
					double thisContribution = eterm * (squared(inverseFTQComplex[x][y].re())+squared(inverseFTQComplex[x][y].im()));
					convolutedMatrix[x][y] = inverseFTQComplex[x][y].scale(eterm); //Save this for the force calculation
					sum += thisContribution; //from the argument that F(Q(M))*F(Q(-M))-F-1(Q)^2
					debugMatrix[x][y] = Q[x][y]; //Save this for the force calculation
				}else{
					convolutedMatrix[x][y] = Complex.zero;
				}
			}
		}
		return 0.5*sum;
	}

	private double getDirEnergy(){
		double sum = 0;
		for(Particle p : this)
		{
			for(Particle q : getNearParticles(p.getPosition(),directRange))
			{
				if(!p.equals(q)){
					double d = p.getPosition().sub(q.getPosition()).mag();
					sum += p.getCharge()*q.getCharge()/d;
				}
			}
		}
		return 0.5*sum;
	}

	private double getCorEnergy(){
		double sum = 0;
		for(Particle p : this)
		{
			sum += squared(p.getCharge());
		}
		return -ewaldCoefficient/Math.sqrt(Math.PI) * sum;
	}

	private double getActualEnergy(){
		double sum = 0;
		for(Particle p : this)
		{
			for(Particle q : this)
			{
				if(!p.equals(q)){
					double d = p.getPosition().sub(q.getPosition()).mag();
					sum += p.getCharge()*q.getCharge()/d;
				}
			}
		}
		return 0.5*sum;
	}

	//Uses a cell list method
	public ArrayList<Particle> getNearParticles(Complex p, int range)
	{
		int cellX = (int)Math.floor(p.re() / meshWidth);
		int cellY = (int)Math.floor(p.im() / meshWidth);
		ArrayList<Particle> nearParticles = new ArrayList<Particle>();
		for(int dx= -range; dx < range; dx++)
		{
			for(int dy= -range; dy < range; dy++)
			{
				int thisX = (cellX + dx);
				int thisY = (cellY + dy);
				if(thisX >= 0 && thisY >= 0 && thisX < CELL_SIDE_COUNT && thisY < CELL_SIDE_COUNT){
					nearParticles.addAll(cellList[thisX][thisY]);
				}
			}
		}
		return nearParticles;

	}




	@Override
	public double charge(Complex position) {
		int i = (int)((position.re() / windowSize.getWidth()) / meshWidth); //Need to translate into unit coordinates, then find it's grid coordinate
		int j = (int)((position.im() / windowSize.getHeight()) / meshWidth);
		return convolutedMatrix[i][j].mag();
	}

	@Override
	public void debugDraw(Graphics g) {
		//Draw grid
		//Called once at the end of calculation
		for(int i = 0; i < CELL_SIDE_COUNT; i++)
		{
			for(int j = 0; j < CELL_SIDE_COUNT; j++)
			{
				//g.setColor(Color.gray);
				//g.drawRect((int)(i*512.0/CELL_SIDE_COUNT), (int)(j*512.0/CELL_SIDE_COUNT), (int)512.0/CELL_SIDE_COUNT, (int)512.0/CELL_SIDE_COUNT);
			}
		}
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		
		for(Particle p : nonUnitParticles)
		{
			p.draw(g);
		}
	}



}

