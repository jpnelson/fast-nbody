package eric;


public class Binomial {
	//Taken from MP.java by Eric McCreath.
	public static double binomial(int n, int k) {
		 if (k > n)
		      return 0;
		  if (k > n/2)
		        k = n-k; // faster
		   double accum = 1.0;
		    for (int i = 1; i <= k; i++) {
		         accum = accum * (n-k+i) / (double) i;
		 }
		 return accum;
	} 
}
