package fma;

import java.util.ArrayList;

import particles.Particle;
import particles.ParticleList;

import math.Complex;

public class MultipoleExpansion {
	private double chargeSum = 0; //Q (G&R)
	private ArrayList<Complex> expansionNumerators = new ArrayList<Complex>();
	private final int numberOfTerms;
	private final Complex center;
	
	public MultipoleExpansion(ArrayList<Particle> particles, Complex center, int numberOfTerms)
	{
		this.center = center;
		this.numberOfTerms = numberOfTerms;

		//Calculate the charge sum
		for(Particle p : particles)
		{
			chargeSum += p.getCharge();
		}
		
		//Calculate the numerators for the expansion
		for(int term = 1; term <= numberOfTerms; term++)
		{
			Complex thisNumerator = Complex.zero;
			for(Particle p : particles)
			{
				Complex numeratorDelta = p.getPosition().sub(center).power(term).scale(-p.getCharge()).scale(1.0/(double)(term));
				thisNumerator = thisNumerator.add(numeratorDelta);
			}
			expansionNumerators.add(thisNumerator);
		}
	}
	//Constructor used by the shifted method
	public MultipoleExpansion(Complex center, int numberOfTerms, double chargeSum, ArrayList<Complex> numerators)
	{
		this.chargeSum = chargeSum;
		this.center = center;
		this.numberOfTerms = numberOfTerms;
		this.expansionNumerators = numerators;

	}
	//This one provides an empty multipole expansion, will always return 0
	public MultipoleExpansion()
	{
		numberOfTerms = 0;
		chargeSum = 0;
		center = Complex.zero;
	}
	
	public double potential(Complex z)
	{
		Complex complexPotential = Complex.zero;
		complexPotential = z.sub(center).ln().scale(chargeSum); //Qlog(z) (G&R) equivalent to a_0 log(z)
		for(int term = 1; term <= numberOfTerms; term++)
		{
			Complex numerator = expansionNumerators.get(term-1);//since it's 1 based
			complexPotential = complexPotential.add(numerator.div(z.sub(center).power(term)));
		}
		return complexPotential.re();
	}
	
	//Taken from MP.java by Eric McCreath.
	static double binomial(int n, int k) {
		 if (k > n)
		      return 0;
		  if (k > n/2)
		        k = n-k; // faster
		   double accum = 1.0;
		    for (int i = 1; i <= k; i++) {
		         accum = accum * (n-k+i) / (double) i;
		 }
		 return accum;
	}
	
	public MultipoleExpansion shift(Complex newCenter)
	{
		ArrayList<Complex> newNumerators = new ArrayList<Complex>();
		Complex offset = center.sub(newCenter); //Shifting the coordinate system so it's origin is at newCenter, z_0 (G&R)
		for(int l = 1; l <= numberOfTerms; l++)
		{
			Complex thisTerm = Complex.zero;
			for(int k = 1; k <= l; k++)
			{
				int kIndex = k-1; //we start at 1, but our arrays are indexed at 0
				thisTerm = thisTerm.add(expansionNumerators.get(kIndex).mult(offset.power(l-k)).scale(binomial(l-1,k-1)).sub(offset.power(l).scale((double)(chargeSum)/(double)(l))));
			}
			
			newNumerators.add(thisTerm);
		}
		MultipoleExpansion shiftedExpansion = new MultipoleExpansion(newCenter,numberOfTerms,chargeSum,newNumerators);
		return shiftedExpansion;
	}
	
	@Override
	public String toString()
	{
		return "Expansion numerators: "+expansionNumerators.toString();
	}
}
