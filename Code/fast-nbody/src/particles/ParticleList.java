package particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class ParticleList extends ArrayList<Particle>{
	
	public void draw(Graphics2D g)
	{
		
		for(Particle p : this)
		{
			p.draw(g);
		}
	}
}
