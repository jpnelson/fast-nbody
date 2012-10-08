package test.PME;

import static org.junit.Assert.assertTrue;

import gui.SpaceSize;

import java.util.ArrayList;

import math.Vector;

import org.junit.Test;

import particles.Particle;
import pme.SPMEList;

public class NearParticlesJUnit {
	@Test
	public void testNearParticles()
	{
		ArrayList<Particle> list = new ArrayList<Particle>();
		Particle p1 = new Particle(0, 0, 1, 1);
		Particle p2 = new Particle(312, 0, 1, 1);

		list.add(p1);
		list.add(new Particle(1, 0, 1, 1));
		list.add(new Particle(0, 1, 1, 1));
		list.add(p2);
		list.add(new Particle(0, 222, 1, 1));
		list.add(new Particle(313, 0, 1, 1));
		SPMEList p = new SPMEList(list, new SpaceSize(512,512));
		assertTrue(p.getNearParticles(p1, 3).size()==3);
		assertTrue(p.getNearParticles(p2, 3).size()==2);

	}
}
