package test.FMA;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import math.Complex;
import particles.NSquaredList;
import particles.Particle;
import fma.MultipoleExpansion;


public class MultipoleExpansionJUnit {
	ArrayList<Particle> pts, pts2;
	Complex center, pos;
	MultipoleExpansion mp1,mp2;
	NSquaredList pl1;
	@Before
    public void setUp() {
		center = new Complex(10.0,10.0);
		pos = new Complex(1.0,3.0);
		pts = new ArrayList<Particle>();
		pts2 = new ArrayList<Particle>();
		pts.add(new Particle(11.0,12.0,3.0,2.0));
		pts.add(new Particle(19.0,9.0,3.0,-1.0));
		pts.add(new Particle(10.0,10.0,3.0,-1.0));
		pts2.add(new Particle(13,11,3.0,2.5));
		pts2.add(new Particle(4,2,3.0,1.5));
		pts2.add(new Particle(5,1,3.0,-2.5));

		mp1 = new MultipoleExpansion(pts,center,25);
		mp2 = new MultipoleExpansion(pts2,center,25);
		pl1 = new NSquaredList(pts);
		
    }
	@Test
	public void testCharge()
	{
		
		assertTrue(pl1.potential(pos)-mp1.potential(pos) < 0.0001);
		
	}
	@Test
	public void testShift()
	{
		Complex newcenter = new Complex(83.0,53.0);
		MultipoleExpansion mpShifted = mp1.shift(newcenter);
		System.out.println(mpShifted.potential(pos));
		System.out.println(mp1.potential(pos));
		assertTrue(Math.abs(mpShifted.potential(pos) - mp1.potential(pos)) < 0.0001);
	}
	@Test
	public void testAdd()
	{
		MultipoleExpansion mpSum = mp2.add(mp1);
		double separateSum = mp2.potential(pos) + mp1.potential(pos);
		double addedSum = mpSum.potential(pos);
		//System.out.println(mpSum.getNumerators() + "=\n"+mp1.getNumerators()+"+\n"+mp2.getNumerators());
		assertTrue(Math.abs(separateSum - addedSum) < 0.0001);
	}
	
}
