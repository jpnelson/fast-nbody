package test.PME;

import gui.SpaceSize;

import java.util.ArrayList;

import jtransforms.DoubleFFT_2D;

import math.Complex;
import math.MatrixOperations;

import org.junit.Test;

import particles.Particle;
import pme.BSpline;
import pme.SPMEList;

public class ScalarSumTest {
	static int CELL_SIDE_COUNT = 64;
	@Test
	public void testLeeCode()
	{
		ArrayList<Particle> particles = new ArrayList<Particle>();
		particles.add(new Particle(0,0,1,1));
		particles.add(new Particle(24,24,1,1));
		SPMEList pmeList = new SPMEList(particles, new SpaceSize(512,512));
		pmeList.initCellList();
		pmeList.initQMatrix();
		double ewald = pmeList.ewaldCoefficient;
		
		//Make IFTQ ourselves
		DoubleFFT_2D fft = new DoubleFFT_2D(CELL_SIDE_COUNT,CELL_SIDE_COUNT);
		double[][] inverseFTQDoubles = MatrixOperations.copyMatrix(pmeList.Q, CELL_SIDE_COUNT*2);
		fft.realInverseFull(inverseFTQDoubles, false);
		Complex[][] inverseFTQComplex = Complex.doubleToComplexArray(inverseFTQDoubles); //IFT of Q
		
		
		int indtop = CELL_SIDE_COUNT * CELL_SIDE_COUNT;
		double pi = 3.14159265358979323846;
		double fac = pi*pi / (ewald * ewald);
		int nff = CELL_SIDE_COUNT * CELL_SIDE_COUNT;
		int nf1 = CELL_SIDE_COUNT / 2;
		if (nf1 << 1 < CELL_SIDE_COUNT) {
			++nf1;
		}
		int nf2 = CELL_SIDE_COUNT / 2;
		if (nf2 << 1 < CELL_SIDE_COUNT) {
			++nf2;
		}
		double energy = 0.;

		for (int ind = 1; ind <= (indtop - 1); ++ind) {
			/* get k1,k2,k3 from the relationship: */
			/* ind = (k1-1) + (k2-1)*nfft1 */
			/* Also shift the C array index */
			int k2 = ind / CELL_SIDE_COUNT + 1;
			int k1 = ind - (k2 - 1) * CELL_SIDE_COUNT + 1;
			int m1 = k1 - 1;
			if (k1 > nf1) {
				m1 = k1 - 1 - CELL_SIDE_COUNT;
			}
			int m2 = k2 - 1;
			if (k2 > nf2) {
				m2 = k2 - 1 - CELL_SIDE_COUNT;
			}
			//System.out.println(m1+" "+m2);
			
			int mhat1 = m1;
			int mhat2 = m2 ;
			int msq = mhat1 * mhat1 + mhat2 * mhat2;
			double bpart = pmeList.M.bspmod[k1] * pmeList.M.bspmod[k2];
			double denom = pi * 1 * bpart * msq;
			double eterm = Math.exp(-fac * msq) / denom;
			double d_1 = inverseFTQComplex[k1-1][k2-1].re();
			double d_2 = inverseFTQComplex[k1-1][k2-1].im();
			double struc2 = d_1 * d_1 + d_2 * d_2;
			energy += eterm * struc2;
			if(k1==35 && k2==1)
			{
				System.out.println("ASDF");
			}
			System.out.println("SST: "+k1+" "+k2+" "+energy);
		}
		System.out.println("ENERGY "+energy * .5);
		} /* scalar_sum */
	}
