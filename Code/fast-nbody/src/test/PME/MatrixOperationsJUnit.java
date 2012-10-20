package test.PME;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import math.Complex;
import math.MatrixOperations;
import particles.NSquaredList;
import particles.Particle;
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
		double[][][] in = new double[4][4][8];
		for(int z = 0; z < 8; z++)
		{
			for(int y = 0; y < 4; y++)
			{
				for(int x = 0; x < 4; x++)
				{
					in[x][y][z] = (double) (x+y*8 + z*2*8); 
				}
			}
		}
		Complex[][][] out = Complex.doubleToComplexArray3D(in);
		assertTrue(out[2][2][2].re() == in[2][2][4]);
		assertTrue(out[0][0][0].im() == in[0][0][1]);

	}
	
	@Test
	public void testComplexToDoubleArray()
	{
		double[][][] in = new double[4][4][8];
		in[3][2][0] = 999;
		for(int x = 0; x < 4; x++)
			for(int y = 0; y < 4; y++)
				for(int z = 0; z < 8; z++)
					assertTrue(in[x][y][z] == Complex.complexToDoubleArray3D(Complex.doubleToComplexArray3D(in))[x][y][z]);
	}
	
	@Test
	public void testCopyMatrix()
	{
		double[][][] testMatrix = new double[3][3][6];
		testMatrix[1][2][3] = 1.0;
		double[][][] testMatrixCopy = MatrixOperations.copyMatrix3D(testMatrix, 6);
		assertTrue(testMatrix[1][2][3] == testMatrixCopy[1][2][3]);
		assertTrue(testMatrixCopy[0][0].length == 6);
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
