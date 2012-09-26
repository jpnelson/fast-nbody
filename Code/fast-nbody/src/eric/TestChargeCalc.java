package eric;

public class TestChargeCalc {

	
	public static void main(String[] args) {
		Particles pts = new Particles();
		pts.add(new Particle(new Complex(9.0,9.0),2.0,3.0));
		pts.add(new Particle(new Complex(9.0,7.0),-1.0,3.0));
		pts.add(new Particle(new Complex(1.0,2.0),-1.0,3.0));
		Complex pos = new Complex(1.0,1.1);
		System.out.println("charge(pts)     : " + pts.charge(pos));
		Levels levels = new Levels(5,10.0,10.0,20);
		levels.evaluate(pts);
		
		
		System.out.println("charge(levels)  : " + levels.charge(pos));
		
	}

}
