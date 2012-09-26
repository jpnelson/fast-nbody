package eric;

public class FBox extends Box {
	Particles particles;
	public FBox(Complex center) {
		super(center);
		particles = new Particles();
	}
	public void calmultiplefinest(int p) {
		mp = new MP(particles,p,center);
	}
	
	
	
	
}
