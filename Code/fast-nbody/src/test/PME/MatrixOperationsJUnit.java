package test.PME;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import math.Complex;
import math.MatrixOperations;
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
	private static <T> void printArray(T[] array)
	{
		for(int i=0; i < array.length; i++)
		{
			System.out.print(array[i]+" ");
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
		double[][] testMatrixCopy = MatrixOperations.copyMatrix(testMatrix, 6);
		assertTrue(testMatrix[1][2] == testMatrixCopy[1][2]);
		assertTrue(testMatrixCopy[0].length == 6);
	}
	
	@Test
	public void toFromRowMajor()
	{
		double[][] testMatrix = new double[3][3];
		for(int i=0;i<9;i++){
			testMatrix[i/3][i%3] = 0.0;
		}
		testMatrix[0][0] = 1.0;
		testMatrix[1][1] = 2.0;
		testMatrix[2][2] = 3.0;
		for(int i=0;i<9;i++){
			assertTrue(testMatrix[i/3][i%3] == MatrixOperations.make2DMatrix(MatrixOperations.makeRowMajorVector(testMatrix), 3)[i/3][i%3]);
		}

	}
	
}
