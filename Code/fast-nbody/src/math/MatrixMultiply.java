package math;

public class MatrixMultiply {
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
	   
	  for(int i = 0; i < aRows; i++) { // aRow
	    for(int j = 0; j < bColumns; j++) { // bColumn
	      for(int k = 0; k < aColumns; k++) { // aColumn
	        resultant[i][j] = resultant[i][j].add(a[i][k].mult(b[k][j]));
	      }
	    }  
	  }
	   
	  return resultant;
	}
}
