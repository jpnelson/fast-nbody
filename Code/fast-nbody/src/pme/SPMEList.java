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
	public final double ewaldCoefficient; //The ewald coefficient
	static double TOLERANCE = 1e-8;//Used to calculate ewaldCoefficient
	static double CUTOFF_DISTANCE = 0.25;//Used to calculate ewaldCoefficient. In unit cell dimensions
	final int directRange; //will be CUTOFF_DISTANCE / meshWidth. In mesh cells

	final ArrayList<Particle> nonUnitParticles; //We keep a copy of this for drawing

	ArrayList<Particle> cellList[][];

	public double[][] Q; //the charge assignment matrix
	double[][] B; //The B Matrix (Essman[95])
	double[][] debugMatrix;
	Complex[][] convolutedMatrix;
	Complex[][] inverseFTQComplex;
	
	public BSpline M;		//Order ASSIGNMENT_SCHEME_ORDER B spline
	
	final double meshWidth; //H (Petersen[95])
	final double inverseMeshWidth; //H^-1 (required for the reciprocal lattice)
	final SpaceSize windowSize;
	Vector[] reciprocalLatticeVectors; //Reciprocal lattice vectors

	public SPMEList(ArrayList<Particle> particles, SpaceSize windowSize) {
		this.nonUnitParticles = particles;
		this.meshWidth = 1.0 / (double)(CELL_SIDE_COUNT); //Working in the unit cell
		this.windowSize = windowSize;
		this.inverseMeshWidth = 1.0 / this.meshWidth;
		this.directRange = (int)Math.ceil(CUTOFF_DISTANCE/meshWidth);
		this.ewaldCoefficient = calculateEwaldCoefficient(CUTOFF_DISTANCE,TOLERANCE);
		
		//We work within the unit square
		ArrayList<Particle> unitParticles = new ArrayList<Particle>();
		for(Particle p : particles)
		{
			unitParticles.add(new Particle(
					p.getPosition().re() / (double)(windowSize.getWidth()),
					p.getPosition().im() / (double)(windowSize.getHeight()),
					p.getMass(),p.getCharge()));
		}
		this.addAll(unitParticles);
		
		//init(); we'll call it when needed
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
		System.out.println("[SPMEList] Chose ewald coefficient of "+x);
		return x;
	}
	@Override
	public void init()
	{
		initialised = true;
		//Initialise the bspline
		M = new BSpline(ASSIGNMENT_SCHEME_ORDER);
		M.fillBSPMod(CELL_SIDE_COUNT);
		
		
		initCellList();
		initQMatrix();
		invertQMatrixFFT();
		getRecEnergy(); //Important part of the process, as it creates the convoluted matrix while calculating the energy.
		//initBMatrix();
		//initCMatrix();

		//Starting Eq 4.7 Essman[95]
		//The matrix approach (commented out currently)
		/*double[][] BC = MatrixOperations.straightMultiply(B, C);
		DoubleFFT_2D fft = new DoubleFFT_2D(CELL_SIDE_COUNT,CELL_SIDE_COUNT);
		double[][] qInverseFT = MatrixOperations.copyMatrix(Q, 2*CELL_SIDE_COUNT);
		fft.realInverseFull(qInverseFT, false);
		Complex[][] product = MatrixOperations.straightMultiply(Complex.doubleToComplexArrayNoImaginaryPart(BC), Complex.doubleToComplexArray(qInverseFT));
		double[][] wideProduct = Complex.complexToDoubleArray(product);
		fft.complexForward(wideProduct);
		convolution = Complex.doubleToComplexArray(wideProduct);

		theta = MatrixOperations.copyMatrix(BC, 2*CELL_SIDE_COUNT);
		fft.realForwardFull(theta);
		complexTheta = Complex.doubleToComplexArray(theta);*/

		
		//Useful properties for most simulations, but not for our purposes
		/*System.out.println("[SPMEList] Reciprocal energy: "+getRecEnergy());
		System.out.println("[SPMEList] Direct energy: "+getDirEnergy());
		System.out.println("[SPMEList] Self energy: "+getSelfEnergy());
		System.out.println("[SPMEList] Actual energy: "+getActualEnergy());*/
		
		//Forces
		/*calculateRecForces();
		calculateDirForces();
		System.out.println("[SPMEList] -------");
		System.out.println("[SPMEList] Actual forces");
		System.out.println("[SPMEList] -------");
		calculateActualForces();*/
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
	private void calculateRecForces(){
		//Perform a FFT on convolutedMatrix to make theta * Q from B C F^-1(Q)
		//Make IFTQ ourselves
		DoubleFFT_2D fft = new DoubleFFT_2D(CELL_SIDE_COUNT,CELL_SIDE_COUNT);
		double[][] convolutedDoubles = Complex.complexToDoubleArray2D(convolutedMatrix);
		fft.complexForward(convolutedDoubles);
		convolutedMatrix = Complex.doubleToComplexArray2D(convolutedDoubles); //F(B C F^-1(Q)) Pg. 182 Lee[05]
		
		for(Particle p : this){
			//Pg 183 Lee[05]
			//For each grid point that this particle has been interpolated to
			for(int dx = -ASSIGNMENT_SCHEME_ORDER; dx < ASSIGNMENT_SCHEME_ORDER; dx++)
			{
				for(int dy = -ASSIGNMENT_SCHEME_ORDER; dy < ASSIGNMENT_SCHEME_ORDER; dy++)
				{
					double uX = p.getPosition().re()/1.0 * CELL_SIDE_COUNT;//Scaled fractional coordinate
					double uY = p.getPosition().im()/1.0 * CELL_SIDE_COUNT;//Scaled fractional coordinate
					int particleCellX = (int)Math.round(p.getPosition().re()/1.0 * CELL_SIDE_COUNT);
					int particleCellY = (int)Math.round(p.getPosition().im()/1.0 * CELL_SIDE_COUNT);
					int thisX = particleCellX + dx;
					int thisY = particleCellY + dy;
					if(thisX >= 0 && thisY >= 0 && thisX < CELL_SIDE_COUNT && thisY < CELL_SIDE_COUNT)
					{
						double dQdx = p.getCharge() * M.evaluateDerivative(uX - thisX) * M.evaluate(uY - thisY);
						double dQdy = p.getCharge() * M.evaluate(uX - thisX) * M.evaluateDerivative(uY - thisY);
						p.addToForce(-dQdx * convolutedMatrix[thisX][thisY].re(), -dQdy * convolutedMatrix[thisX][thisY].re()); //FIXME .re()?
					}
				}
			}
		}
		
	}
	
	private void calculateDirForces(){
		for(Particle p : this)
		{
			for(Particle q : getNearParticles(p.getPosition(),directRange))
			{
				if(!p.equals(q)){
					Complex r = p.getPosition().sub(q.getPosition());
					if(r.mag() < CUTOFF_DISTANCE){ //in unit coordinates
						double d = r.mag();
						double fac = -q.getCharge() * p.getCharge() / (squared(d));
						Complex rHat = r.scale(1.0/d);
						Complex force = rHat.scale(fac);
						p.addToForce(force.re(), force.im());
					}
				}
			}
			//System.out.println("[SPMEList] Particle at "+p.getPosition()+" has force "+p.getForce());
		}
	}
	
	private void calculateActualForces(){
		Vector[] forces = new Vector[this.size()];
		int i = 0;
		for(Particle p : this)
		{
			forces[i] = new Vector(0,0);
			for(Particle q : this)
			{
				if(!p.equals(q)){
					Complex r = p.getPosition().sub(q.getPosition());
					double d = r.mag();
					double fac = -q.getCharge() * p.getCharge() / (squared(d));
					Complex rHat = r.scale(1.0/d);
					Complex force = rHat.scale(fac);
					forces[i].x+= force.re();
					forces[i].y+= force.im();
				}
			}
			//System.out.println("[SPMEList] Particle at "+p.getPosition()+" has force ("+forces[i].x + ","+forces[i].y+")");
			i++;
		}
	}
	
	private void invertQMatrixFFT(){
		//Make IFTQ ourselves
		DoubleFFT_2D fft = new DoubleFFT_2D(CELL_SIDE_COUNT,CELL_SIDE_COUNT);
		double[][] inverseFTQDoubles = MatrixOperations.copyMatrix2D(Q, CELL_SIDE_COUNT*2);
		fft.realInverseFull(inverseFTQDoubles, false);
		inverseFTQComplex = Complex.doubleToComplexArray2D(inverseFTQDoubles); //IFT of Q
	}
	
	//Requires Q to be initialised. C is implicitly calculated in here (part of eterm)
	//B matrix is also calculated elsewhere, in BSpline at the moment
	//Refer to page 191 of Lee[05]
	private double getRecEnergy(){
		invertQMatrixFFT();
		debugMatrix = new double[CELL_SIDE_COUNT][CELL_SIDE_COUNT];
		convolutedMatrix = new Complex[CELL_SIDE_COUNT][CELL_SIDE_COUNT];
		//initiliaze the whole convolutedMatrix array to zero
		for(int x = 0; x < CELL_SIDE_COUNT; x++)
		{
			for(int y = 0; y < CELL_SIDE_COUNT; y++)
			{
				convolutedMatrix[x][y] = Complex.zero;
			}
		}
		
		//Pg. 180 Lee[05]
		int indexTop = CELL_SIDE_COUNT * CELL_SIDE_COUNT;
		//Eq 19 Lee[05]
		//Also Eq 3.9 Essman[95]
		double sum = 0;
		int indtop = CELL_SIDE_COUNT * CELL_SIDE_COUNT;
		int midPoint = CELL_SIDE_COUNT / 2;
		if (midPoint << 1 < CELL_SIDE_COUNT) {
			++midPoint;
		}
		
		for (int ind = 1; ind <= (indtop - 1); ++ind) {
			
			int y = ind / CELL_SIDE_COUNT + 1;
			int x = ind - (y - 1) * CELL_SIDE_COUNT + 1;
//			double mXPrime = (0 <= x && x <= CELL_SIDE_COUNT/2)? x : x - CELL_SIDE_COUNT;
//			double mYPrime = (0 <= y && y <= CELL_SIDE_COUNT/2)? y : y - CELL_SIDE_COUNT;
			int mXPrime = x - 1;
			if (x > midPoint) {
				mXPrime = x - 1 - CELL_SIDE_COUNT;
			}
			int mYPrime = y - 1;
			if (y > midPoint) {
				mYPrime = y - 1 - CELL_SIDE_COUNT;
			}
			double m = mXPrime * 1.0 + mYPrime * 1.0; //Was inverseMeshWidth - theory is reciprocal lattice vectors are for the entire cell U rather than one cell
			double mSquared = squared(mXPrime * 1.0) + squared(mYPrime * 1.0);
			
			double V = 1; //working in the unit mesh
			double bterm = M.bspmod[x]*M.bspmod[y];
			double eterm = Math.exp(-squared(Math.PI/ewaldCoefficient)*mSquared) / (bterm * Math.PI * V * mSquared);
			//Section 3.2.8 Lee[05]
			double inverseQPart = (squared(inverseFTQComplex[x-1][y-1].re())+squared(inverseFTQComplex[x-1][y-1].im())); //Lee[05]
			double thisContribution = eterm * inverseQPart;
			convolutedMatrix[x-1][y-1] = inverseFTQComplex[x-1][y-1].scale(eterm); //Save this for the force calculation
			sum += thisContribution; //from the argument that F(Q(M))*F(Q(-M))=F-1(Q)^2
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
					sum += ErrorFunction.erfc(d * ewaldCoefficient)*p.getCharge()*q.getCharge()/d;
				}
			}
		}
		return 0.5*sum;
	}

	private double getSelfEnergy(){
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
					Complex r = p.getPosition().sub(q.getPosition());
					Complex s = r.ln().scale(p.getCharge() * q.getCharge());
					sum += s.re();
				}
			}
		}
		return 0.5*sum;
	}

	//Uses a cell list method
	private ArrayList<Particle> getNearParticles(Complex p, int range)
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
	
	//Using b spline interpolation, take a point and return the interpolated matrix's values
	private double interpolateMatrix(Complex fractionalCoordinate, Complex[][] Matrix)
	{
		int cellX = (int)(fractionalCoordinate.re() * CELL_SIDE_COUNT);
		int cellY = (int)(fractionalCoordinate.im() * CELL_SIDE_COUNT);
		double sum = 0;
		for(int dx= -ASSIGNMENT_SCHEME_ORDER; dx < ASSIGNMENT_SCHEME_ORDER; dx++)
		{
			for(int dy= -ASSIGNMENT_SCHEME_ORDER; dy < ASSIGNMENT_SCHEME_ORDER; dy++)
			{
				int thisCellX = cellX+dx;
				int thisCellY = cellY+dy;
				if(thisCellX >= 0 && thisCellY >= 0 && thisCellX < CELL_SIDE_COUNT && thisCellY < CELL_SIDE_COUNT){
					double xWeight = M.evaluate(fractionalCoordinate.re()*CELL_SIDE_COUNT - thisCellX);
					double yWeight = M.evaluate(fractionalCoordinate.im()*CELL_SIDE_COUNT - thisCellY);
					sum += Matrix[thisCellX][thisCellY].re() * xWeight * yWeight;
				}
			}
		}
		return sum;
	}




	@Override
	public double potential(Complex position) { //position is in coordinates out of the original dimensions given
		if(!initialised) init();
		int i = (int)(position.re() / (windowSize.getWidth()) / meshWidth); //Need to translate into unit coordinates, then find it's grid coordinate
		int j = (int)(position.im() / (windowSize.getHeight()) / meshWidth);
		Complex sum = Complex.zero;
		Complex fracPosition = position.scale(1.0/windowSize.getWidth());
		
		for (Particle particle : getNearParticles(fracPosition,directRange)) {
			Complex r = particle.getPosition().sub(fracPosition);
			double erfTerm = ErrorFunction.erfc(ewaldCoefficient * r.mag());
			sum = sum.add(r.ln().scale(erfTerm * particle.getCharge()));
		}
		return -interpolateMatrix(fracPosition,convolutedMatrix) + sum.re();
	}

	@Override
	public void debugDraw(Graphics g) {
		//Draw grid
		//Called once at the end of calculation
		for(int i = 0; i < CELL_SIDE_COUNT; i++)
		{
			for(int j = 0; j < CELL_SIDE_COUNT; j++)
			{
				//Place code for debug drawing here
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

