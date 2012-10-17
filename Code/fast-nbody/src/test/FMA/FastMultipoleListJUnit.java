package test.FMA;

import java.awt.Dimension;
import java.util.ArrayList;

import math.Complex;

import org.junit.Before;
import org.junit.Test;

import fma.FastMultipoleList;
import fma.MultipoleExpansion;
import gui.SpaceSize;

import particles.NSquaredList;
import particles.Particle;

public class FastMultipoleListJUnit {
	ArrayList<Particle> pts, pts2;
	Complex center, pos;
	MultipoleExpansion mp1,mp2;
	NSquaredList nsl;
	FastMultipoleList fml;
	@Before
    public void setUp() {
		center = new Complex(10.0,10.0);
		pos = new Complex(8.0,1.1);
		pts = new ArrayList<Particle>();
		pts2 = new ArrayList<Particle>();
		pts.add(new Particle(9.0,9.0,3.0,2.0));
		pts.add(new Particle(9.0,7.0,3.0,-1.0));
		pts.add(new Particle(1.0,2.0,3.0,-1.0));

		pts2.add(new Particle(13,11,3.0,2.5));
		pts2.add(new Particle(4,2,3.0,1.5));
		pts2.add(new Particle(5,1,3.0,-2.5));

		
		
    }
	
	@Test
	public void testChargeCalculation() {
		//Erics FMA
		eric.Levels lev = new eric.Levels(FastMultipoleList.LEVEL_COUNT,10.0,10.0,FastMultipoleList.EXPANSION_TERMS);
		eric.Particles ericPts = new eric.Particles();
		for(Particle p : pts)
		{
			ericPts.add(new eric.Particle(new eric.Complex(p.getPosition().re(),p.getPosition().im()),p.getCharge(),p.getMass()));
		}
		lev.evaluate(ericPts);
		
		fml = new FastMultipoleList(pts, new SpaceSize(10,10));
		nsl = new NSquaredList(pts);
		System.out.println("=========");
		System.out.println(lev.charge(complexToEricComplex(pos)));
		System.out.println(fml.potential(pos));
		System.out.println(nsl.potential(pos));
		
		
	}
	
	public eric.Complex complexToEricComplex(Complex c)
	{
		return new eric.Complex(c.re(),c.im());
	}
}
