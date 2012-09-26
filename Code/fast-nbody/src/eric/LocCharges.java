package eric;

import java.util.ArrayList;


public class LocCharges extends ArrayList<LocCharge> {

	
	
	Complex chargeat(Complex z) {
		Complex sum = Complex.zero;
		for (LocCharge lc : this) {
		   sum = sum.add(z.sub(lc.pos).ln().scale(-1.0*lc.q));
		}
		return sum;
	}
	
	
	public static void main(String[] args) {

	}

}
