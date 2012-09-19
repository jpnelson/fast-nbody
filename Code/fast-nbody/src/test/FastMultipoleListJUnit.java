package test;

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
		pos = new Complex(5.0,5.0);
		pts = new ArrayList<Particle>();
		pts2 = new ArrayList<Particle>();
		pts.add(new Particle(9.0,9.0,2.0,3.0));
		pts.add(new Particle(9.0,7.0,-1.0,3.0));
		pts.add(new Particle(1.0,2.0,-1.0,3.0));
		pts2.add(new Particle(13,11,3.0,2.5));
		pts2.add(new Particle(4,2,3.0,1.5));
		pts2.add(new Particle(5,1,3.0,-2.5));

		
		
    }
	
	@Test
	public void testChargeCalculation() {
		fml = new FastMultipoleList(pts, new SpaceSize(10,10));
		nsl = new NSquaredList(pts);
		System.out.println("=========");
		System.out.println(fml.charge(pos));
		System.out.println(nsl.charge(pos));
		
		
	}
}
