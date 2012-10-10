package test.PME;
//http://www.challenge.nm.org/archive/03-04/FinalReports/26/node15.html good for debugging
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pme.BSpline;

public class SplineTest {
	@Test
	public void testBSpline()
	{
		BSpline spline1 = new BSpline(2);
		BSpline spline2 = new BSpline(4);
		spline2.fillBSPMod(64);
		//System.out.println(spline2.bspmod[1]);
		assertTrue(spline1.evaluate(1)==1);
	}

}
