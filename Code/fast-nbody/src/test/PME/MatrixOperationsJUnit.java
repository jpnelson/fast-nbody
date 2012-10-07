package test.PME;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import math.Complex;
import particles.NSquaredList;
import particles.Particle;
import pme.PMEList;
import fma.MultipoleExpansion;


public class MatrixOperationsJUnit {
	private static <T> void printArray(T[][] array)
	{
		for(int i=0; i < array.length; i++)
		{
			for(int j = 0; j < array[0].length; j++)
			{
				System.out.print(array[i][j] + " ");
			}
			System.out.print("\n");
		}
	}
	@Test
	public void testDoubleToComplexArray()
	{
		double[][] in = new double[4][8];
		for(int y = 0; y < 4; y++)
		{
			for(int x = 0; x < 8; x++)
			{
				in[y][x] = (double) (x+y*8); 
			}
		}
		Complex[][] out = Complex.doubleToComplexArray(in);
		assertTrue(out[0][0].re() == in[0][0]);
		assertTrue(out[0][0].im() == in[0][1]);

	}
	
	@Test
	public void testComplexToDoubleArray()
	{
		double[][] in = new double[4][8];
		in[3][4] = 999;
		for(int x = 0; x < 4; x++)
			for(int y = 0; y < 8; y++)
				assertTrue(in[x][y] == Complex.complexToDoubleArray(Complex.doubleToComplexArray(in))[x][y]);

	}
	
	@Test
	public void testCopyMatrix()
	{
		double[][] testMatrix = new double[3][3];
		testMatrix[1][2] = 1.0;
		double[][] testMatrixCopy = PMEList.copyMatrix(testMatrix, 6);
		assertTrue(testMatrix[1][2] == testMatrixCopy[1][2]);
		assertTrue(testMatrixCopy[0].length == 6);
	}
	
}
