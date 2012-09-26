package eric;

public class Levels {
	Level level[]; // mesh levels indexed from 0. n-1 is the finest level. 
	int n;  // number of mesh levels
	double xdim, ydim;
	int p;
	public Levels(int n, double xdim, double ydim, int p) {
		this.xdim = xdim;
		this.ydim = ydim;
		this.n = n;
		this.p = p;
		level = new Level[n+1]; 
		for (int l = 0; l <= n; l++) {
			level[l] = new Level(l,n,this);
		}
		
		// add interaction lists
		for (int l = 0; l <= n; l++) {
			level[l].addinteractionlist();
		}
		
	}
	
	public void evaluate(Particles pts) {
		
		// step 1 - add particles cal multipole at finest level
		for (Particle part : pts) {
			level[n].addparticle(part);
		}
		level[n].calmultipolefinest();
		
		// step 2 - cal multipole for all levels
		for (int l = n-1; l>=0; l-- ) {
			for (int ibx = 0; ibx < level[l].dim; ibx++) {
				for (int iby = 0; iby < level[l].dim; iby++) {
					Box parent = level[l].boxes[ibx][iby];
					Box kid0 = level[l+1].boxes[ibx*2][iby*2];
					Box kid1 = level[l+1].boxes[ibx*2+1][iby*2];
					Box kid2 = level[l+1].boxes[ibx*2][iby*2+1];
					Box kid3 = level[l+1].boxes[ibx*2+1][iby*2+1];
					
					MP mpsum = kid0.mp.shift(parent.center.sub(kid0.center));
					mpsum = mpsum.add(kid1.mp.shift(parent.center.sub(kid1.center)));
					mpsum = mpsum.add(kid2.mp.shift(parent.center.sub(kid2.center)));
					mpsum = mpsum.add(kid3.mp.shift(parent.center.sub(kid3.center)));
					parent.mp = mpsum; 
				}
			}
		}
		
		// step 3
		level[1].boxes[0][0].psibar = new PS(p);
		level[1].boxes[1][0].psibar = new PS(p);
		level[1].boxes[0][1].psibar = new PS(p);
		level[1].boxes[1][1].psibar = new PS(p);
		for (int l = 1; l < n; l++) {
			for (int ibx = 0; ibx < level[l].dim; ibx++) {
				for (int iby = 0; iby < level[l].dim; iby++) {
					Box current = level[l].boxes[ibx][iby];
					PS sum = new PS(p);
					
					for (Box box : current.interactionlist) {
						PS boxps = new PS(box.mp,box.center.sub(current.center),p);
						sum = sum.add(boxps);
						if(ibx==3 && iby==0 && l==2) //1,0 lvl 1 | 3,0 lvl 2 | 6,0 lvl 3| 12,1 lvl 4 | 25,3 lvl 5
						{
							//System.out.println(box.center);
						}
					}
					
					
					sum = sum.add(current.psibar);
					
					current.psi = sum;
					
					
					
				}
			}
			for (int ibx = 0; ibx < level[l].dim; ibx++) {
				for (int iby = 0; iby < level[l].dim; iby++) {
					Box current = level[l].boxes[ibx][iby];
					Box kid0 = level[l+1].boxes[ibx*2][iby*2];
					Box kid1 = level[l+1].boxes[ibx*2+1][iby*2];
					Box kid2 = level[l+1].boxes[ibx*2][iby*2+1];
					Box kid3 = level[l+1].boxes[ibx*2+1][iby*2+1];
					
					kid0.psibar = current.psi.shift(current.center.sub(kid0.center));
					kid1.psibar = current.psi.shift(current.center.sub(kid1.center));
					kid2.psibar = current.psi.shift(current.center.sub(kid2.center));
					kid3.psibar = current.psi.shift(current.center.sub(kid3.center));
					
//					if(ibx==3 && iby==0 && l==2)
//					{
//						System.out.println(current.psi);
//					}
				}
			}
		}
		
		// step 4
		for (int ibx = 0; ibx < level[n].dim; ibx++) {
			for (int iby = 0; iby < level[n].dim; iby++) {
				Box current = level[n].boxes[ibx][iby];
				PS sum = new PS(p);
				for (Box box : current.interactionlist) {
					PS boxps = new PS(box.mp,box.center.sub(current.center),p);
					sum = sum.add(boxps);
				}
				sum = sum.add(current.psibar);
				current.psi = sum;

			}
		}
		
		// step 5 and 6
	/*	double totalpe = 
		for (int ibx = 0; ibx < level[n].dim; ibx++) {
			for (int iby = 0; iby < level[n].dim; iby++) {
				FBox current = (FBox) level[n].boxes[ibx][iby];
				double charge;
				for (Particle particle : current.particles) {
					
				}
				
				
			}
		}*/
		
		
		
		
		
	}
	
	public double charge(Complex pos) {
		int ibx = level[n].ibx(pos);
		int iby = level[n].iby(pos);
		FBox box = (FBox) level[n].boxes[ibx][iby];
		double charge = box.psi.charge(pos.sub(box.center));
		for(int i = -1; i <= 1 ; i++)
			for (int j = -1; j <= 1; j++)
		        charge += chargefrombox(ibx+i,iby+j,pos);
		return charge;
	}

	private double chargefrombox(int i, int j, Complex pos) {
		if (i >=0 && j >= 0 && j < level[n].dim && i < level[n].dim) {
		FBox box = (FBox) level[n].boxes[i][j];
		return box.particles.charge(pos);
		
	} else {
		return 0.0;
	}
	}
	
	

}
