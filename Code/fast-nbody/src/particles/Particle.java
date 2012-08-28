package particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import math.Complex;
public class Particle{
	Complex position;
	public Particle(double r, double i) {
		position = new Complex(r,i);
	}
	
	public void draw(Graphics2D g)
	{
		g.setColor(Color.BLUE);
		g.drawOval((int)position.re(), (int)position.im(), 6, 6);
	}
}
