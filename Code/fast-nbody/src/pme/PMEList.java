package pme;
/*REFEREFENCES:
 * -JChemPhys_103_3668.pdf: http://dx.doi.org/10.1063/1.470043			Petersen[95]
 * -JChemPhys_98_10089.pdf: http://dx.doi.org/10.1063/1.464397			Darden[93]
 * -9807099.pdf:			http://arxiv.org/pdf/cond-mat/9807099.pdf	Deserno[98]
 * -JChemPhys_103_8577		http://dx.doi.org/10.1063/1.470117			Essman[95]
*/
//
import gui.SpaceSize;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;

import jtransforms.DoubleFFT_2D;

import math.Complex;
import math.ErrorFunction;
import math.MatrixOperations;
import particles.Particle;
import particles.ParticleList;

public class PMEList extends ParticleList{
	static int PADDING = 1; //Make the space size this many times larger to reduce period issues
	static int CELL_SIDE_COUNT = 32; //N
	static int ASSIGNMENT_SCHEME_ORDER = 2;
	static double BETA = 90;
	double[][] chargeAssignments; //Records the Q_H ^(p) results Petersen[95]
	Complex[][] cMatrix; //C from Essman[95]
	
	double[][] recEnergyAtGridpoints;
	double[][] deltaXS;
	final double meshWidth; //H (Petersen[95])
	final double inverseMeshWidth; //H^-1 (required for the reciprocal lattice)
	final SpaceSize windowSize;
	
	public PMEList(ArrayList<Particle> particles, SpaceSize windowSize) {
		super(particles);
		this.windowSize = windowSize.scale(PADDING); //we make it empty around to help with period problems
		this.meshWidth = windowSize.getWidth() / CELL_SIDE_COUNT;
		this.inverseMeshWidth = 1.0 / this.meshWidth;
		init();

	}
	
	private static double squared(double x){
		return x*x;
	}
	
	
	private void initChargeMatrix()
	{
		chargeAssignments = new double[CELL_SIDE_COUNT][CELL_SIDE_COUNT];
		//Set up the chargeAssignments array, as per Step 1 of Petersen[95]
		for(int i = 0; i < CELL_SIDE_COUNT; i++)
		{
			for(int j = 0; j < CELL_SIDE_COUNT; j++)
			{
				chargeAssignments[i][j] = 0;
				for(Particle p : this)
				{
					chargeAssignments[i][j] += chargeContribution(p,i,j);
				}
			}
		}
	}
	//Eq 3.9 Essman[95]
	private void initCMatrix()
	{
		cMatrix = new Complex[CELL_SIDE_COUNT][CELL_SIDE_COUNT];
		double V = windowSize.getWidth() * windowSize.getHeight();
		double c = 1.0 / (Math.PI * V);
		cMatrix[0][0] = Complex.zero;

		for(int x = 0; x < CELL_SIDE_COUNT; x++)
		{
			for(int y = 0; y < CELL_SIDE_COUNT; y++)
			{
				if(!(x==0 && y==0))
				{
					double mXPrime = (0 <= x && x <= CELL_SIDE_COUNT/2)? x : x - CELL_SIDE_COUNT;
					double mYPrime = (0 <= y && y <= CELL_SIDE_COUNT/2)? y : y - CELL_SIDE_COUNT;
					double m = mXPrime * inverseMeshWidth + mYPrime * inverseMeshWidth;
					if(m != 0){ //FIXME: What to do if m==0?
						cMatrix[x][y] = new Complex(c * (Math.exp(-squared(Math.PI)* squared(m) / squared(BETA))) / squared(m),0);
					}else{
						cMatrix[x][y] = Complex.zero;
					}
				}
			}
		}
	}
	
	
	
	//Equation 3.10 Essman[95]
	private double getReciprocalEnergy()
	{
		DoubleFFT_2D fft = new DoubleFFT_2D(CELL_SIDE_COUNT,CELL_SIDE_COUNT);
		double[][] chargeAssignmentsIFT = MatrixOperations.copyMatrix2D(chargeAssignments,CELL_SIDE_COUNT*2);
		fft.realInverseFull(chargeAssignmentsIFT, false);
		Complex[][] chargeAssignmentsIFTComplex = Complex.doubleToComplexArray2D(chargeAssignmentsIFT);
		Complex[][] convolutedMatrix = MatrixOperations.multiply(cMatrix, chargeAssignmentsIFTComplex);
		fft.complexForward(Complex.complexToDoubleArray2D(convolutedMatrix));
		double sum = 0;
		deltaXS = new double[CELL_SIDE_COUNT][CELL_SIDE_COUNT];
		//Get the reciprocal energy
		for(int x = 0; x < CELL_SIDE_COUNT; x++)
		{
			for(int y = 0; y < CELL_SIDE_COUNT; y++)
			{
				deltaXS[x][y] = (convolutedMatrix[(x+1)%CELL_SIDE_COUNT][y].re() - convolutedMatrix[x][y].re()) / meshWidth;
				sum += chargeAssignments[x][y] * convolutedMatrix[x][y].re();
			}
		}
		return 0.5*sum;
	}
	
	//FIXME: this is O(n^2)
	//Equation 2.4 Essman[95]
	private double getDirectEnergy()
	{
		double sum = 0;
		for(Particle p : this){
			for(Particle q : this){
				if(!p.equals(q))
				{
					double term=0;
					double distance = p.getPosition().sub(q.getPosition()).mag();
					term += p.getCharge() * q.getCharge() * ErrorFunction.erfc(BETA * distance);
					term = term / distance;
					sum += term;
				}
			}
		}
		return sum;
	}
	//Equation 2.5 Essman[95]
	private double getCorrectedEnergy()
	{
		double chargeSquaredSum = 0;
		for(Particle p : this)
		{
			chargeSquaredSum += squared(p.getCharge());
		}
		return -(BETA / Math.sqrt(Math.PI)) * chargeSquaredSum;
	}
	
	private void init()
	{
		initChargeMatrix();
		initCMatrix();
		double recEnergy = getReciprocalEnergy();
		double dirEnergy = getDirectEnergy();
		double corrEnergy = getCorrectedEnergy();
		System.out.println("Reciprocal energy:\t"+recEnergy);
		System.out.println("Direct energy:\t\t"+dirEnergy);
		System.out.println("Corrected energy:\t"+corrEnergy);
		System.out.println("PME energy:\t\t"+(recEnergy+dirEnergy+corrEnergy));
		//Direct (DEBUG)
		double actualEnergy = 0;
		for(Particle p : this)
		{
			for(Particle other : this)
			{
				if(!p.equals(other))
				{
					actualEnergy += 0.5*p.getCharge() * other.getCharge() / (p.getPosition().sub(other.getPosition()).abs());
				}
			}
		}
		System.out.println("Actual energy: "+actualEnergy);
	}
	
	
	
	//P=3 from Appendix E of Deserno[98]
	double[][] getChargeAssignmentPolynomials()
	{
		double[][] polynomialArray = new double[3][3];
		polynomialArray[0] = new double[]{0,-1.0/(2*meshWidth),1.0/(2.0*meshWidth*meshWidth)}; //1,x,x^2
		polynomialArray[1] = new double[]{1,0,-1.0/(meshWidth*meshWidth)};
		polynomialArray[2] = new double[]{0,1.0/(2*meshWidth),1.0/(2.0*meshWidth*meshWidth)};
		return polynomialArray;
		
	}
	
	double evaluatePolynomial(double[] p, double x)
	{
		double sum=0;
		for(int i = 0; i < p.length; i++)
		{
			sum += p[i]*Math.pow(x, i);
		}
		return sum;
	}
	
	//S^p_H  in 3 of Petersen[95]
	private double[] nearestMeshPoints1D(double x, int points)
	{
		double[] nearestPoints = new double[points+1];
		int nextLowestIndex = (int)Math.floor(x/meshWidth);
		int nextHighestIndex = nextLowestIndex+1;
		nearestPoints[0] = nextHighestIndex * meshWidth;
		nearestPoints[1] = nextLowestIndex * meshWidth;
		int currentLowIndex = nextLowestIndex-1;
		int currentHighIndex = nextHighestIndex+1;
		for(int i = 2; i < (points+1); i++) //we've already found 2 / ASO+1
		{
			System.out.println(Math.abs(x - currentHighIndex*meshWidth) +" vs "+Math.abs(x - currentLowIndex*meshWidth));

			if(Math.abs(x - currentHighIndex*meshWidth) < Math.abs(x - currentLowIndex*meshWidth)) //if the highest is closer
			{
				nearestPoints[i] = currentHighIndex * meshWidth;
				currentHighIndex++;
			}else{
				nearestPoints[i] = currentLowIndex * meshWidth;
				currentLowIndex--;
			}
		}
		
		
		//Make sure they're all in bounds
		for(int i = 0; i < (points+1); i++)
		{
			if(nearestPoints[i] < 0)
			{
				nearestPoints[i] += windowSize.getWidth();
			}
			if(nearestPoints[i] > windowSize.getWidth())
			{
				nearestPoints[i] -= windowSize.getWidth();
			}
		}

		return nearestPoints;
	}
	
	//The two dimensional version of the above, providing all the closest grid points
	private Complex[] nearestMeshPoints2D(double x, double y, int proximity)
	{
		double[] nearXS = nearestMeshPoints1D(x,proximity);
		double[] nearYS = nearestMeshPoints1D(y,proximity);
		if(nearXS.length != nearYS.length){
			System.err.println("nearestMeshPoints2D: 1D of x,y not of equal length");
			return null;
		}
		int arrayLength = nearXS.length;
		Complex[] nearPoints = new Complex[nearXS.length*nearYS.length];
		for(int i = 0; i < nearXS.length; i++)
		{
			for(int j = 0; j < nearYS.length; j++)
			{
				nearPoints[i*arrayLength+j] = new Complex(nearXS[i],nearYS[j]);
			}
		}
		return nearPoints;
	}
	
	private boolean meshPointsClose(int meshX, int meshY, int otherX, int otherY)
	{
		Complex[] nearList = nearestMeshPoints2D(meshX*meshWidth,meshY*meshWidth,ASSIGNMENT_SCHEME_ORDER);
		for(int i = 0; i < nearList.length; i++)
		{
			if((int)(nearList[i].re()/meshWidth)==otherX && (int)(nearList[i].im()/meshWidth)==otherY)
			{
				return true;
			}
		}
		return false;
		
	}
	
	double chargeContribution(Particle particle, int meshX, int meshY)
	{
		Complex meshPosition = new Complex(meshX * meshWidth,meshY * meshWidth);
		Complex chargePosition = particle.getPosition();
		Complex nearestGridPoint = nearestMeshPoints2D(chargePosition.re(),chargePosition.im(),1)[0];
		int nearestMeshX = (int)(nearestGridPoint.re() / meshWidth); //should always go evenly if nearestGridPoint is a mesh point
		int nearestMeshY = (int)(nearestGridPoint.im() / meshWidth); //should always go evenly if nearestGridPoint is a mesh point

		
		//Return 0 if the mesh point is too far away
		//Was (!meshPointsClose(meshX,meshY,nearestMeshX,nearestMeshY)) (just a square) which limits it to the nearest grid points
		//FIXME: introduces a small error but is much faster (implementation problem)
		//(in the range of the charge assignment order scheme)
		if((Math.abs(meshX - nearestMeshX) > 1 || Math.abs(meshY - nearestMeshY) > 1))
		{
			return 0;
		}
		
		double[][] polynomials = getChargeAssignmentPolynomials(); // the -H,0,H grid polynomials

		//x is the relative position to the nearest grid point (for p even, which it is)
		
		double distanceToNearestX = chargePosition.re() - nearestGridPoint.re();
		double distanceToNearestY = chargePosition.im() - nearestGridPoint.im();
		

		
		double[] assignmentPolynomialX = polynomials[meshX - nearestMeshX + 1];
		double[] assignmentPolynomialY = polynomials[meshY - nearestMeshY + 1];
		
		double assignmentX = evaluatePolynomial(assignmentPolynomialX,distanceToNearestX);
		double assignmentY = evaluatePolynomial(assignmentPolynomialY,distanceToNearestY);
		return particle.getCharge() * assignmentX * assignmentY;
	}
	
	
	@Override
	public double charge(Complex position) {
		int i = (int)(position.re() / meshWidth);
		int j = (int)(position.im() / meshWidth);
		
		return deltaXS[i][j];
		
		//return contribution;
	}

	@Override
	public void debugDraw(Graphics g) {
//		int x = 500; int y = 256;
//		Complex[] nearPoints = nearestMeshPoints2D(x,y);
//		g.setColor(Color.magenta);
//		g.drawRect(x, y, 1, 1);
//		g.setColor(Color.white);
//		for(int i = 0; i < nearPoints.length; i++)
//		{
//			g.drawRect((int)nearPoints[i].re(), (int)nearPoints[i].im(), 1, 1);
//		}
		//Draw grid
		//Called once at the end of calculation
//		for(int i = 0; i < windowSize.getWidth() / meshWidth; i++)
//		{
//			for(int j = 0; j < windowSize.getHeight() / meshWidth; j++)
//			{
//				g.setColor(Color.gray);
//				g.drawRect((int)(i*meshWidth), (int)(j*meshWidth), (int)meshWidth, (int)meshWidth);
//			}
//		}
		//Draw the nearest charge
//		for(Particle p : this)
//		{
//			Complex nearestGridPoint = nearestMeshPoints2D(p.getPosition().re(),p.getPosition().im(),1)[0];
//			g.setColor(new Color(255,255,255));
//			g.drawRect((int)(nearestGridPoint.re()), (int)(nearestGridPoint.im()), 1, 1);
//		}
		
		
		
		
	}
	
//	public  void main(String[] args)
//	{
//		//PMEList p = new PMEList(new ArrayList<Particle>(), new SpaceSize(512,512));
//		
//	}
}
