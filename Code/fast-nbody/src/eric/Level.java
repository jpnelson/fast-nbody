package eric;

import java.util.ArrayList;



public class Level {
    
	Box boxes[][];
	int l;
	int dim;
	Levels levels;
	
	public Level(int l, int n, Levels levels) {
		this.levels = levels;
		this.l = l;
		dim = (int) Math.round(Math.pow(2.0,l));
		boxes = new Box[dim][dim];
		for (int ibx = 0; ibx < dim; ibx++) {
			for (int iby = 0; iby < dim; iby++) {
				double xboxsize = levels.xdim/dim;
				double yboxsize = levels.ydim/dim;
				Complex center = new Complex(ibx*xboxsize+ 0.5*xboxsize, iby*yboxsize+ 0.5*yboxsize);
				boxes[ibx][iby] = (l == n ? new FBox(center) : new Box(center));
			}
		}
		
		
	}
	
	public int ibx(Complex c) {
		int ibx = (int) Math.floor(dim*(c.re()/levels.xdim));
		if (ibx == dim) ibx--;
		return ibx;
	}
	
	public int iby(Complex c) {
		int iby = (int) Math.floor(dim*(c.im()/levels.ydim));
		if (iby == dim) iby--;
		return iby;
	}

	public void addparticle(Particle p) {
		
		((FBox) boxes[ibx(p.pos)][iby(p.pos)]).particles.add(p);
	}

	public void calmultipolefinest() {
		for (int ibx = 0; ibx < dim; ibx++) {
			for (int iby = 0; iby < dim; iby++) {
				((FBox) boxes[ibx][iby]).calmultiplefinest(levels.p); 
			}
		}
	}

	
	
	public void addinteractionlist() {
		// TODO Auto-generated method stub
		for (int ibx = 0; ibx < dim; ibx++) {
			for (int iby = 0; iby < dim; iby++) {
				int pibx = ibx/2;
				int piby = iby/2;
				Box current = boxes[ibx][iby];
				current.interactionlist = new ArrayList<Box>();
				addboxes(current,ibx,iby,pibx-1,piby-1);
				addboxes(current,ibx,iby,pibx-1,piby);
				addboxes(current,ibx,iby,pibx-1,piby+1);
				addboxes(current,ibx,iby,pibx,piby-1);
				addboxes(current,ibx,iby,pibx,piby+1);
				addboxes(current,ibx,iby,pibx+1,piby-1);
				addboxes(current,ibx,iby,pibx+1,piby);
				addboxes(current,ibx,iby,pibx+1,piby+1);
				
			}
	    }
	}

	private void addboxes(Box current, int ibx, int iby, int px, int py) {
		if (px >= 0 && py >= 0 && px < dim/2 && py < dim/2) {
			addbox(current,ibx,iby,px*2,py*2);
			addbox(current,ibx,iby,px*2+1,py*2);
			addbox(current,ibx,iby,px*2,py*2+1);
			addbox(current,ibx,iby,px*2+1,py*2+1);
		}
		
	}

	private void addbox(Box current, int ibx, int iby, int x, int y) {
		if (Math.abs(ibx-x)>1 || Math.abs(iby-y)>1 ) {
			current.interactionlist.add(boxes[x][y]);
		}
	}

}
