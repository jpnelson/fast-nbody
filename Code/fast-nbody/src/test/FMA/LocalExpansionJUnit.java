package test.FMA;

import static org.junit.Assert.*;

import java.util.ArrayList;

import math.Complex;

import org.junit.Before;
import org.junit.Test;

import particles.NSquaredList;
import particles.Particle;
import eric.MP;
import eric.PS;
import eric.Particles;
import fma.LocalExpansion;
import fma.MultipoleExpansion;

public class LocalExpansionJUnit {
	ArrayList<Particle> pts, pts2;
	eric.Particles ericPts;
	Complex center, pos;
	MultipoleExpansion mp1,mp2;
	NSquaredList pl1;
	@Before
    public void setUp() {
		center = new Complex(10.0,10.0);
		pos = new Complex(2.0,3.0);
		pts = new ArrayList<Particle>();
		pts2 = new ArrayList<Particle>();
		pts.add(new Particle(11.0,12.0,3.0,2.0));
		pts.add(new Particle(9.0,9.0,3.0,-1.0));
		pts.add(new Particle(10.0,10.0,3.0,-1.0));
		pts2.add(new Particle(13,11,3.0,2.5));
		pts2.add(new Particle(4,2,3.0,1.5));
		pts2.add(new Particle(5,1,3.0,-2.5));

		ericPts = new eric.Particles();
		for(Particle p : pts)
		{
			ericPts.add(new eric.Particle(new eric.Complex(p.getPosition().re(),p.getPosition().im()),p.getCharge(),p.getMass()));
		}
		
		mp1 = new MultipoleExpansion(pts,center,25);
		mp2 = new MultipoleExpansion(pts2,center,25);
		pl1 = new NSquaredList(pts);
		
    }
	@Test
	public void testCreation()
	{
		LocalExpansion psi = new LocalExpansion(mp1,center,25);
		
		eric.MP mp = new eric.MP(ericPts,25,complexToEricComplex(center));
		eric.PS PSpsi = new eric.PS(mp,complexToEricComplex(center),25);
		for(int i = 0; i < 25; i++)
		{
			//System.out.println(psi.getExpansionTerm(i));
			//System.out.println(PSpsi.b[i]);
		}
	}
	
	@Test
	public void testEval()
	{
		mp1 = new MultipoleExpansion(pts,center,26);
		LocalExpansion psi = new LocalExpansion(mp1,center,26);
		
		eric.MP mp = new eric.MP(ericPts,25,new eric.Complex(center.re(),center.im()));
		eric.PS PSpsi = new eric.PS(mp,new eric.Complex(center.re(),center.im()),25);
		System.out.println(PSpsi.charge(complexToEricComplex(pos.sub(center))));
		System.out.println(psi.potential(pos));
		System.out.println("----------------");
		assertTrue(Math.abs(PSpsi.charge(complexToEricComplex(pos.sub(center))) - psi.potential(pos)) <= 0.00001);
		
		
		Complex shiftedCenter = new Complex(23.0,14.0);
		PSpsi = PSpsi.shift(complexToEricComplex(center.sub(shiftedCenter)));
		psi = psi.shift(shiftedCenter);
		System.out.println(PSpsi.charge(complexToEricComplex(pos.sub(shiftedCenter))));
		System.out.println(psi.potential(pos));
		assertTrue(Math.abs(PSpsi.charge(complexToEricComplex(pos.sub(shiftedCenter))) - psi.potential(pos)) <= 0.00001);

	}
	
	public eric.Complex complexToEricComplex(Complex c)
	{
		return new eric.Complex(c.re(),c.im());
	}
	
	@Test
	public void testAdd()
	{
		LocalExpansion psi1 = new LocalExpansion(mp1,center,25);
		LocalExpansion psi2 = new LocalExpansion(mp2,center,25);
		double separatePotential = psi1.potential(pos)+(psi2.potential(pos));
		double combinedPotential = psi1.add(psi2).potential(pos);
		assertTrue(separatePotential-combinedPotential <= 0.000001);
	}
	
	@Test
	public void testShift()
	{
		Complex newCenter = new Complex(2.0,8.0);
		LocalExpansion psi = new LocalExpansion(mp1,center,25);
		LocalExpansion psiShifted = psi.shift(newCenter);
		//System.out.println("Before shift: " + psi.potential(pos));
		//System.out.println("After shift: " + psiShifted.potential(pos));
		assertTrue(Math.abs(psi.potential(pos)-psiShifted.potential(pos)) <= 0.0001);
	}
}
