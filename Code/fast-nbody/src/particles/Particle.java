package particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import math.Complex;
public class Particle{
	public static int DEFAULT_CHARGE = 1;
	public static int DEFAULT_MASS = 1;
	Complex position;
	double mass;
	double charge;
	public Particle(double x, double y, double mass, double charge) {
		position = new Complex(x,y);
		this.mass = mass;
		this.charge = charge;
	}
	
	public void draw(Graphics2D g)
	{
		if(charge>0)
			g.setColor(Color.BLUE);
		else
			g.setColor(Color.RED);
		g.drawOval((int)position.re(), (int)position.im(), 6, 6);
	}
	
	public Complex getPosition(){
		return position;
	}
	public double getCharge(){
		return charge;
	}
}
