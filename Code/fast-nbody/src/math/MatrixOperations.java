package math;

import java.util.Arrays;

public class MatrixOperations {
	//http://blog.ryanrampersad.com/2010/01/matrix-multiplication-in-java/
	public static Complex[][] multiply(Complex a[][], Complex b[][]) {
		   
		  int aRows = a.length,
		      aColumns = a[0].length,
		      bRows = b.length,
		      bColumns = b[0].length;
		   
		  if ( aColumns != bRows ) {
		    throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
		  }
		   
		  Complex[][] resultant = new Complex[aRows][bColumns];
		  for(int i = 0; i < aRows; i++)
		  {
			  for(int j=0;j<bColumns;j++)
			  {
				  resultant[i][j] = Complex.zero;
			  }
		  }
		  for(int i = 0; i < aRows; i++) { // aRow
		    for(int j = 0; j < bColumns; j++) { // bColumn
		      for(int k = 0; k < aColumns; k++) { // aColumn
		        resultant[i][j] = resultant[i][j].add(a[i][k].mult(b[k][j]));
		      }
		    }  
		  }
		   
		  return resultant;
	}
	
	public static double[][] multiply(double a[][], double b[][]) {
		   
		  int aRows = a.length,
		      aColumns = a[0].length,
		      bRows = b.length,
		      bColumns = b[0].length;
		   
		  if ( aColumns != bRows ) {
		    throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
		  }
		   
		  double[][] resultant = new double[aRows][bColumns];
		  for(int i = 0; i < aRows; i++) { // aRow
		    for(int j = 0; j < bColumns; j++) { // bColumn
		      for(int k = 0; k < aColumns; k++) { // aColumn
		        resultant[i][j] += (a[i][k]*(b[k][j]));
		      }
		    }  
		  }
		   
		  return resultant;
	}
	
	public static double[][] straightMultiply(double a[][], double b[][]) {
		   
		  int aRows = a.length,
		      aColumns = a[0].length,
		      bRows = b.length,
		      bColumns = b[0].length;
		   
		  if ( aColumns != bRows ) {
		    throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
		  }
		   
		  double[][] resultant = new double[aRows][bColumns];
		  for(int i = 0; i < aRows; i++) { // aRow
		    for(int j = 0; j < bColumns; j++) { // bColumn
		      resultant[i][j] = a[i][j]*b[i][j];
		    }  
		  }
		   
		  return resultant;
	}
	
	public static Complex[][] straightMultiply(Complex a[][], Complex b[][]) {
		   
		  int aRows = a.length,
		      aColumns = a[0].length,
		      bRows = b.length,
		      bColumns = b[0].length;
		   
		  if ( aColumns != bRows ) {
		    throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
		  }
		   
		  Complex[][] resultant = new Complex[aRows][bColumns];
		  for(int i = 0; i < aRows; i++) { // aRow
		    for(int j = 0; j < bColumns; j++) { // bColumn
		      resultant[i][j] = a[i][j].mult(b[i][j]);
		    }  
		  }
		   
		  return resultant;
	}
	
	public static Complex[] straightMultiply(Complex a[], Complex b[]) {
		   
		  int length = a.length;
		   
		   
		  Complex[] resultant = new Complex[length];
		  for(int i = 0; i < length; i++) {
			  resultant[i] = a[i].mult(b[i]);
		  }
		   
		  return resultant;
	}
	
	
	
	//Copies a matrix and possibly extends it's width
	public static double[][][] copyMatrix(double[][][] q, int newWidth)
	{
		double[][][] newMatrix = new double[q[0][0].length][q[0].length][newWidth];
		for(int i = 0; i < q.length; i++)
		{
			for(int j = 0; j < q.length; j++)
			{
				newMatrix[i][j] = Arrays.copyOf(q[i][j], newWidth);
			}
		}
		return newMatrix;
	}
	
	//Was for 1D stuff. TODO remove?
	public static double[] makeRowMajorVector(double[][] M)
	{
		double[] vector = new double[M.length * M[0].length];
		for(int row = 0; row < M.length; row++)
		{
			for(int col=0; col < M[0].length; col++)
			{
				vector[row*M.length + col] = M[row][col];
			}
		}
		return vector;
	}
	
	public static double[][] make2DMatrix(double[] vector, int rowLength)
	{
		double[][] M = new double[vector.length / rowLength][rowLength];
		for(int i = 0; i < vector.length; i++)
		{
			M[(i-i%rowLength)/rowLength][i%rowLength] = vector[i];
		}
		return M;
	}
	
	public static double[] copyVector(double[] vector, int newWidth)
	{
		return Arrays.copyOf(vector, newWidth);
	}
	
	public static Complex[][] make2DMatrix(Complex[] vector, int rowLength)
	{
		Complex[][] M = new Complex[vector.length / rowLength][rowLength];
		for(int i = 0; i < vector.length; i++)
		{
			M[(i-i%rowLength)/rowLength][i%rowLength] = vector[i];
		}
		return M;
	}
	
	public static Complex[] copyVector(Complex[] vector, int newWidth)
	{
		return Arrays.copyOf(vector, newWidth);
	}
}
