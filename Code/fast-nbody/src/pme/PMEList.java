package pme;
/*REFEREFENCES:
 * -JChemPhys_103_3668.pdf: http://dx.doi.org/10.1063/1.470043			Petersen[95]
 * -JChemPhys_98_10089.pdf: http://dx.doi.org/10.1063/1.464397			Darden[93]
 * -9807099.pdf:			http://arxiv.org/pdf/cond-mat/9807099.pdf	Deserno[98]
*/
//
import gui.SpaceSize;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import jtransforms.DoubleFFT_2D;

import math.Complex;
import particles.Particle;
import particles.ParticleList;

public class PMEList extends ParticleList{
	static int CELL_SIDE_COUNT = 32; //N = 32
	static int ASSIGNMENT_SCHEME_ORDER = 2;
	static double BETA = 90; //Beta from Darden[93], describes the ratio of direct : reciprocal
	
	double[][] chargeAssignments; //Records the Q_H ^(p) results Petersen[95]
	Complex[][] recPairPotentialMatrix; //\Phi_{rec} from Darden[93]
	
	final double meshWidth; //H (Petersen[95])
	final double inverseMeshWidth; //H^-1 (required for the reciprocal lattice)
	final SpaceSize windowSize;
	
	private static double squared(double x){
		return x*x;
	}
	
	//Equation 3 of Darden[93]
	private Complex recPairPotentialFunction(double fractionalX, double fractionalY)
	{
		Complex returnValue = Complex.zero;
		double volume = windowSize.getWidth() * windowSize.getHeight();//FIXME: volume in two dimensions?
		double c = 1.0/(Math.PI * volume); 
		for(int x = 0; x < CELL_SIDE_COUNT; x++)
		{
			for(int y = 0; y < CELL_SIDE_COUNT; y++)
			{
				if(!(x==0 && y==0)){
					Complex thisTerm = Complex.zero;
					//Together these make m (Equation 3 Darden[93]) FIXME: is this really what it means by exp(-pi^2m^2/b^2)?
					double recXCoord = inverseMeshWidth * x; //We're dealing with reciprocal space, so we use the inverse mesh width. 
					double recYCoord = inverseMeshWidth * y;
					double mSquared = squared(recXCoord) + squared(recYCoord);
					thisTerm = thisTerm.add(new Complex(c * Math.exp(-squared(Math.PI) * mSquared / squared(BETA))/mSquared, 0));
					Complex part2 = new Complex(0,2 * Math.PI *(x*fractionalX + y*fractionalY));
					thisTerm = thisTerm.mult(part2);
					returnValue = returnValue.add(thisTerm);
				}
			}
		}
		return returnValue.scale(c);
	}
	//Eq. 3 of Darden[93]
	private void initPairPotentialMatrix()
	{
		recPairPotentialMatrix = new Complex[CELL_SIDE_COUNT][CELL_SIDE_COUNT];
		for(int x = 0; x < CELL_SIDE_COUNT; x++)
		{
			for(int y = 0; y < CELL_SIDE_COUNT; y++)
			{
				recPairPotentialMatrix[x][y] = recPairPotentialFunction((double)(x)/(double)(CELL_SIDE_COUNT),(double)(y)/(double)(CELL_SIDE_COUNT));
				System.out.print(recPairPotentialMatrix[x][y] + " ");
			}
			System.out.print("\n");
		}
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
	
	private void init()
	{
		initChargeMatrix();
		initPairPotentialMatrix();
		
		//Direct (DEBUG)
		double directEnergy = 0;
		for(Particle p : this)
		{
			for(Particle other : this)
			{
				if(!p.equals(other))
				{
					directEnergy += 0.5*p.getCharge() * other.getCharge() / (p.getPosition().sub(other.getPosition()).abs());
				}
			}
		}
		System.out.println("Direct energy: "+directEnergy);
				
		//Debug
		//DoubleFFT_2D fft = new DoubleFFT_2D(CELL_SIDE_COUNT,CELL_SIDE_COUNT);
		//fft.realForward(chargeAssignments);
//		for(int i = 0; i < CELL_SIDE_COUNT; i++)
//		{
//			for(int j = 0; j < CELL_SIDE_COUNT; j++)
//			{
//				System.out.print(chargeAssignments[i][j]+" ");
//			}
//			System.out.print("\n");
//		}
	}
	
	public PMEList(ArrayList<Particle> particles, SpaceSize windowSize) {
		super(particles);
		this.windowSize = windowSize;
		this.meshWidth = windowSize.getWidth() / CELL_SIDE_COUNT;
		this.inverseMeshWidth = 1.0 / this.meshWidth;
		init();

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
		double contribution = 0;

		for(Particle p : this)
		{
			contribution += chargeContribution(p,i,j);
		}
		if(contribution != 0)
		{
			//System.out.println("!");
		}
		return chargeAssignments[i][j];
		
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
