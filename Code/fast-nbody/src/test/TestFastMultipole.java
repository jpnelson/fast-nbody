package test;

import java.util.ArrayList;

import fma.MultipoleExpansion;

import math.Complex;

import particles.NSquaredList;
import particles.Particle;
import particles.ParticleList;

public class TestFastMultipole {
	
public static void main(String[] args) {
		
		ArrayList<Particle> pts = new ArrayList<Particle>();
		pts.add(new Particle(11.0,12.0,3.0,2.0));
		pts.add(new Particle(9.0,9.0,3.0,-1.0));
		pts.add(new Particle(10.0,10.0,3.0,-1.0));
		
		Complex center = new Complex(10.0,10.0);
		Complex pos = new Complex(1.0,3.0);
		MultipoleExpansion mp1 = new MultipoleExpansion(pts,center,25);
		NSquaredList pl1 = new NSquaredList(pts);
		System.out.println(mp1);
		System.out.println("charge(nsq) 1,3 : " + pl1.charge(pos));
		System.out.println("charge(fma) 1,3 : " + mp1.potential(pos));
		Complex newcenter = new Complex(13.0,10.0);
		MultipoleExpansion mp2 = mp1.shift(newcenter);
		System.out.println("charge(fma) 1,3 : " + mp2.potential(pos));

	}
	

}
