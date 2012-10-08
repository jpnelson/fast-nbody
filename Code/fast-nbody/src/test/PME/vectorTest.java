package test.PME;

import static org.junit.Assert.assertTrue;
import math.Complex;
import math.Vector;

import org.junit.Test;

public class VectorTest {
	@Test
	public void testCrossProduct()
	{
		Vector v = new Vector(1,0,0);
		Vector w = new Vector(0,1,0);
		assertTrue(v.cross(w).x == 0 && v.cross(w).y == 0 && v.cross(w).z == 1);
	}
	
	@Test
	public void testDotProduct()
	{
		Vector v = new Vector(1,0,0);
		Vector w = new Vector(0,1,0);
		assertTrue(v.dot(w) == 0);
	}
}
