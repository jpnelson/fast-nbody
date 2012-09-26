package eric;

import java.util.ArrayList;


public class Box {
	MP mp;
	Complex center;
	ArrayList<Box> interactionlist;
	
	PS psibar;
	PS psi;
	
	public Box(Complex center) {
		this.center = center;
	}
	
	public String toString() {
		String ils="";
		for (Box b : interactionlist) {
			ils += b.center + " ";
		}
		
		return "Box c : " + center + " [" + ils + "]";
	}

}
