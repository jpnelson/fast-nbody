package eric;


import java.util.ArrayList;


public class MultipoleExpansion {
	private Complex[] expansionNumerators; //goes from a_1 to a_numberOfTerms-1
	private int numberOfTerms;
	private final Complex center;
	
	public MultipoleExpansion(ArrayList<Particle> particles, Complex center, int numberOfTerms)
	{
		this.center = center;
		this.numberOfTerms = numberOfTerms;
		expansionNumerators = new Complex[numberOfTerms+1];
		expansionNumerators[0] = Complex.zero;
		//Calculate the charge sum
		for(Particle p : particles)
		{
			expansionNumerators[0] = expansionNumerators[0].add(new Complex(1.0,0.0).scale(p.q));
		}
		
		//Calculate the numerators for the expansion
		for(int term = 1; term <= numberOfTerms; term++)
		{
			Complex thisNumerator = Complex.zero;
			for(Particle p : particles)
			{
				Complex numeratorDelta = p.pos.sub(center).power(term).scale(-p.q).scale(1.0/(double)(term));
				thisNumerator = thisNumerator.add(numeratorDelta);
			}
			expansionNumerators[term] = (thisNumerator);
		}
	}
	//Constructor used by the shifted method
	public MultipoleExpansion(Complex center, int numberOfTerms, Complex[] numerators)
	{
		this.center = center;
		this.numberOfTerms = numberOfTerms;
		this.expansionNumerators = numerators;

	}
	//This one provides an empty multipole expansion, will always return 0
	public MultipoleExpansion()
	{
		numberOfTerms = 0;
		center = Complex.zero;
	}
	
	//This one provides an empty multipole expansion, will always return 0. Has place holders of numberOfTerms size
	public MultipoleExpansion(int numberOfTerms, Complex center)
	{
		this.center = center;
		this.numberOfTerms = numberOfTerms;
		expansionNumerators = new Complex[numberOfTerms+1];
		for(int i = 0; i <= numberOfTerms; i++)
		{
			expansionNumerators[i] = Complex.zero;
		}
	}
	
	public double potential(Complex z)
	{
		Complex complexPotential = Complex.zero;
		complexPotential = z.sub(center).ln().scale(expansionNumerators[0].re()); //Qlog(z) (G&R) equivalent to a_0 log(z)
		for(int term = 1; term <= numberOfTerms; term++)
		{
			Complex numerator = expansionNumerators[term-1];//since it's 1 based
			complexPotential = complexPotential.add(numerator.div(z.sub(center).power(term)));
		}
		return complexPotential.re();
	}
	

	
	public MultipoleExpansion shift(Complex newCenter)
	{
		ArrayList<Complex> newNumerators = new ArrayList<Complex>();
		MultipoleExpansion res = new MultipoleExpansion(numberOfTerms,newCenter);
		Complex zs = newCenter.sub(center);
		zs = zs.neg();
		
		res.numberOfTerms = numberOfTerms;
		for (int l = 1; l <= res.numberOfTerms; l++) {
			Complex sum = zs.power(l).scale(-1.0*expansionNumerators[0].re()/((double) l));
			for (int k = 1; k <= l; k++) {
				sum = sum.add(zs.power(l-k).mult(expansionNumerators[k]).scale(Binomial.binomial(l-1,k-1)));		
			}
			res.expansionNumerators[l] = sum;
		}
		return res;
	}
	
	//Combine two multipole expansions
	public MultipoleExpansion add(MultipoleExpansion otherMP)
	{
		//The centers should be the same
		if(!otherMP.center.equals(center))
		{
			System.err.println("MultipoleExpansion.add: The centers should be the same");
			return null;
		}
		Complex[] newNumerators = new Complex[numberOfTerms+1];
		for(int i = 0; i <= otherMP.numberOfTerms; i++)
		{
			newNumerators[i] = (expansionNumerators[i].add(otherMP.expansionNumerators[i]));
		}
		MultipoleExpansion newMultipoleExpansion = new MultipoleExpansion(center,numberOfTerms,newNumerators);
		return newMultipoleExpansion;
	}
	public double getChargeSum()
	{
		return expansionNumerators[0].re();
	}
	
	public int getNumberOfTerms()
	{
		return numberOfTerms;
	}
	
	public Complex getCenter()
	{
		return center;
	}
	
	public Complex[] getNumerators()
	{
		return expansionNumerators;
	}
	
	@Override
	public String toString()
	{
		return "Expansion numerators: "+expansionNumerators.toString();
	}
}
