package eric;

public class TestStep2 {

	
	public static void main(String[] args) {
		Particles pts = new Particles();
		pts.add(new Particle(new Complex(9.0,6.0),2.0,3.0));
		pts.add(new Particle(new Complex(9.0,7.0),-1.0,3.0));
		pts.add(new Particle(new Complex(1.0,8.0),-1.0,3.0));
		Complex pos = new Complex(20.0,23.0);
		System.out.println("charge(pts) 1,3 : " + pts.charge(pos));
		Levels levels = new Levels(5,10.0,10.0,20);
		levels.evaluate(pts);
		Box main = levels.level[0].boxes[0][0];
		System.out.println("charge(mp) 1,3 : " + main.mp.charge(pos.sub(main.center)));
		
	}

}
