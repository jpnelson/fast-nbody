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
	
	
	
	//Copies a matrix and possibly extends it's width
	public static double[][] copyMatrix(double[][] M, int newWidth)
	{
		double[][] newMatrix = new double[M[0].length][newWidth];
		for(int i = 0; i < M.length; i++)
		{
			newMatrix[i] = Arrays.copyOf(M[i], newWidth);
		}
		return newMatrix;
	}
}
