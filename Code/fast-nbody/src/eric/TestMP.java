package eric;

public class TestMP {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Particles pts = new Particles();
		pts.add(new Particle(new Complex(11.0,12.0),2.0,3.0));
		pts.add(new Particle(new Complex(9.0,9.0),-1.0,3.0));
		pts.add(new Particle(new Complex(10.0,10.0),-1.0,3.0));
		
		Complex center = new Complex(10.0,10.0);
		Complex pos = new Complex(1.0,3.0);
		MP mp1 = new MP(pts,25, center);
		
		System.out.println(mp1);
		System.out.println("charge(pts) 1,3 : " + pts.charge(pos));
		System.out.println("charge(mp ) 1,3 : " + mp1.charge(pos.sub(center)));
		Complex newcenter = new Complex(13.0,10.0);
		MP mp2 = mp1.shift(newcenter.sub(center));
		System.out.println("charge(mps ) 1,3 : " + mp2.charge(pos.sub(newcenter)));
		PS ps1 = new PS(mp1,center,25);
		System.out.println("charge(ps ) 1,3 : " + ps1.charge(pos));
		Complex ps2center = new Complex(1.0,1.0);
		PS ps2 = ps1.shift(ps2center.neg());
		System.out.println("charge(pss) 1,3 : " + ps2.charge(pos.sub(ps2center)));

	}

}
