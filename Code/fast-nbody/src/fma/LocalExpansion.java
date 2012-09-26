package fma;

import java.util.ArrayList;

import math.Binomial;
import math.Complex;

public class LocalExpansion {
	private final int numberOfTerms;
	private final Complex center;
	private  Complex[] terms;
	public LocalExpansion(int numberOfTerms, Complex center)
	{
		this.center = center;
		this.numberOfTerms = numberOfTerms;
		terms = new Complex[numberOfTerms];
		for(int i = 0; i < numberOfTerms; i++)
		{
			terms[i] = Complex.zero;
		}
	}
	
	//For use in the add/shift method
	public LocalExpansion(ArrayList<Complex> initialTerms, Complex center)
	{
		this.center = center;
		numberOfTerms = initialTerms.size();
		terms = new Complex[numberOfTerms];
		for(int i = 0; i < initialTerms.size(); i++)
		{
			terms[i] = initialTerms.get(i);
		}
	}
	
	private static double minusoneto(int k)
	{
		return (k % 2 == 0? 1.0:-1.0);
	}
	public LocalExpansion(MultipoleExpansion multipoleExpansion, Complex center, int numberOfTerms)
	{
		this.numberOfTerms = numberOfTerms;
		this.center = center;
		terms = new Complex[numberOfTerms];
		terms[0] = Complex.zero;
		ArrayList<Complex> mpNumerators = multipoleExpansion.getNumerators();
		
		//(2.13) G&R
		for(int k = 1; k <= mpNumerators.size(); k++)
		{
			int kIndex = k-1; //mpNumerators[0] = a_1
			terms[0] = terms[0].add(mpNumerators.get(kIndex).div(center.power(k)).scale(minusoneto(k)));
		}
		terms[0] = terms[0].add(Complex.one.scale(multipoleExpansion.getChargeSum()).mult(center.scale(-1).ln()));
		
		//(2.14) G&R
		for(int l = 1; l < numberOfTerms; l++)
		{
			Complex termSum = Complex.zero;
			Complex subtractionTerm = Complex.one.scale(multipoleExpansion.getChargeSum()).div(center.power(l).scale(l));

			for(int k = 1; k <= mpNumerators.size(); k++)
			{
				int kIndex = k-1; //mpNumerators[0] = a_1
				termSum = termSum.add(mpNumerators.get(kIndex).div(center.power(k)).scale(Binomial.binomial(l+k-1,k-1)).scale(minusoneto(k)));
			}

			terms[l] = Complex.one.div(center.power(l)).mult(termSum).sub(subtractionTerm);
		}
	}
	
	public double potential(Complex z)
	{
		Complex p = Complex.zero;
		for(int l = 0; l < numberOfTerms; l++)
		{
			p = p.add(terms[l].mult(z.sub(center).power(l)));
		}
		return p.re();
	}
	
	public Complex getExpansionTerm(int i)
	{
		return terms[i];
	}
	
	public LocalExpansion add(LocalExpansion other)
	{
		ArrayList<Complex> initialTerms = new ArrayList<Complex>();
		for(int i = 0; i < numberOfTerms; i++)
		{
			initialTerms.add(other.getExpansionTerm(i).add(getExpansionTerm(i)));
		}
		return new LocalExpansion(initialTerms, center);
	}
	
	//Moves the multipole expansion's center (Lemma 2.5 G&R)
	public LocalExpansion shift(Complex newCenter)
	{
		ArrayList<Complex> shiftedTerms = new ArrayList<Complex>();
		Complex shiftBy = center.sub(newCenter);
		for(int l = 0; l < numberOfTerms; l++)
		{
			Complex secondSum = Complex.zero;
			for(int k = l; k < numberOfTerms; k++)
			{
				secondSum = secondSum.add(terms[k].scale(Binomial.binomial(k, l)).mult(shiftBy.scale(-1.0).power(k-l)));
			}
			shiftedTerms.add(secondSum);
		}
		return new LocalExpansion(shiftedTerms,newCenter);
	}
	
	public String toString()
	{
		String s = "LocalExpansion: ";
		for(int i = 0; i < numberOfTerms; i++)
		{
			s += terms[i] + ",";
		}
		return s;
	}
}
