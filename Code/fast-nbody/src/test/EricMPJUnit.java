package test;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import math.Complex;
import particles.NSquaredList;
import particles.Particle;
import eric.MP;
import fma.MultipoleExpansion;
public class EricMPJUnit {
	ArrayList<Particle> pts, pts2;
	Complex center, pos;
	MP mp1,mp2;
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

		mp1 = new MP(pts,25,center);
		mp2 = new MP(pts2,25,center);
		pl1 = new NSquaredList(pts);
		
    }
	@Test
	public void testCharge()
	{
		
		assertTrue(pl1.charge(pos)-mp1.charge(pos.sub(center)) < 0.0001);

	}
	@Test
	public void testShift()
	{
		Complex newcenter = new Complex(83.0,53.0);
		MP mpShifted = mp1.shift(newcenter.sub(center));
		System.out.println(mpShifted.charge(pos.sub(newcenter)));
		System.out.println(mp1.charge(pos.sub(center)));
		//assertTrue(Math.abs(mpShifted.charge(pos.sub(center)) - mp1.charge(pos.sub(center))) < 0.1);
	}
	@Test
	public void testAdd()
	{
		MP mpSum = mp2.add(mp1);
		double separateSum = mp2.charge(pos.sub(center)) + mp1.charge(pos.sub(center));
		double addedSum = mpSum.charge(pos.sub(center));
		assertTrue(Math.abs(separateSum - addedSum) < 0.0001);
	}
		
	}

